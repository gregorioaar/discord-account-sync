package com.gregorioandrade.ds.accsync;

import com.gregorioandrade.ds.accsync.data.DataConnector;
import com.gregorioandrade.ds.accsync.data.connectors.MySQLConnector;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RequestHandler {

    private final DataConnector connector = new MySQLConnector();
    private final ScheduledExecutorService  scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private final int minutesToPurge = 5;

    {
        scheduledExecutor.scheduleAtFixedRate(
                () -> {
                    connector.purgeOldRequests(minutesToPurge);
                }, 1, 1, TimeUnit.MINUTES);
    }

    public Mono<String> tryRequest(long id){
        return connector.hasRequest(id)
                .flatMap(has -> {
                    if (has){
                        return Mono.just("Ya tienes una verificación en proceso. Revisa tus mensajes privados!");
                    } else {
                        return DiscordBot.CLIENT.getUserById(Snowflake.of(id))
                                .flatMap(User::getPrivateChannel)
                                .map(channel -> {
                                    String tokenString = Utils.generateRandomSixDigitString();
                                    int token = Integer.parseInt(tokenString);
                                    connector.createRequest(id, token);
                                    return channel.createMessage("Tu código es: "+token).subscribe();
                                })
                                .flatMap(user -> Mono.just("Código enviado. Revisa tus mensajes privados!"));
                    }
                });
    }

    public void shutdown(){
        scheduledExecutor.shutdown();
        connector.disconnect();
    }

}

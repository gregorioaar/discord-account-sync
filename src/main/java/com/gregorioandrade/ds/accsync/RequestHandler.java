package com.gregorioandrade.ds.accsync;

import com.gregorioandrade.ds.accsync.data.DataConnector;
import com.gregorioandrade.ds.accsync.data.connectors.MySQLConnector;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class RequestHandler {

    private final DataConnector connector = new MySQLConnector();

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

}

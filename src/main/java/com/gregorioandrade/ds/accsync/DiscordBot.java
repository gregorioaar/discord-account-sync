package com.gregorioandrade.ds.accsync;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

public class DiscordBot {

    public static void main(String[] args){
        GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
                .build()
                .login()
                .block();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> System.out.println("Now I'm the man on the inside looking out."));

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .filter(event -> event.getGuildId().isPresent()) // So it can only be used in guild channels
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().equalsIgnoreCase("!verificar"))
                .filterWhen(message -> message.getChannel().map(channel -> {
                    channel.createMessage("Código enviado, revisa tus mensajes privados.").subscribe();
                    return true;
                }))
                .map(message -> message.getAuthor().get())
                .flatMap(User::getPrivateChannel)
                .flatMap(channel -> channel.createMessage("Tu código es: "+Utils.generateRandomSixDigitString()))
                .subscribe();

        client.onDisconnect().block();
    }

}

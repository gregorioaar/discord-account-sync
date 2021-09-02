package com.gregorioandrade.ds.accsync;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class DiscordBot {

    public static GatewayDiscordClient CLIENT;

    public static void main(String[] args){
        CLIENT = DiscordClientBuilder.create(args[0])
                .build()
                .login()
                .block();

        CLIENT.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> System.out.println("Now I'm the man on the inside looking out."));

        RequestHandler requestHandler = new RequestHandler();

        CLIENT.getEventDispatcher().on(MessageCreateEvent.class)
                .filter(event -> event.getGuildId().isPresent()) // So it can only be used in guild channels
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().equalsIgnoreCase("!verificar"))
                .flatMap(message -> requestHandler.tryRequest(message.getAuthor().get().getId().asLong())
                        .flatMap(result -> message.getChannel().flatMap(ch -> ch.createMessage(result)))
                )
                .subscribe();

        CLIENT.onDisconnect().block();
    }

}

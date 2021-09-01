package com.gregorioandrade.ds.accsync;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;

public class DiscordBot {

    public static void main(String[] args){
        GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
                .build()
                .login()
                .block();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> System.out.println("Now I'm the man on the inside looking out."));

        client.onDisconnect().block();
    }

}

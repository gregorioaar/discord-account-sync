package com.gregorioandrade.ds.accsync.data;

import reactor.core.publisher.Mono;

public interface DataConnector {

    Mono<Void> createRequest(long discordId, short verificationToken);
    Mono<Boolean> hasRequest(long discordId);
    Mono<Void> deleteRequest(long discordId);
}
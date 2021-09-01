package com.gregorioandrade.ds.accsync.data;

import reactor.core.publisher.Mono;

public interface DataConnector {

    Mono<Void> createRequest(String discordId, String verificationToken);
    Mono<Boolean> hasRequest(String discordId);
    Mono<Void> deleteRequest(String discordId);
}

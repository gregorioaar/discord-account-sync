package com.gregorioandrade.ds.accsync.data;

import reactor.core.publisher.Mono;

public interface DataConnector {

    Mono<Void> createRequest(long discordId, int verificationToken);
    Mono<Boolean> hasRequest(long discordId);
    Mono<Void> deleteRequest(long discordId);
    Mono<Void> purgeOldRequests(int minutesOld);

    void disconnect();
}

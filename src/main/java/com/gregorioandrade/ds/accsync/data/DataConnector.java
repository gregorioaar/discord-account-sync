package com.gregorioandrade.ds.accsync.data;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface DataConnector {

    Mono<Void> createRequest(long discordId, int verificationToken);
    Mono<Boolean> hasRequest(long discordId);
    Mono<Void> deleteRequest(long discordId);
    Mono<Void> purgeOldRequests(int minutesOld);
    Mono<Boolean> isSynced(long discordId);
    Mono<Map<String, String>> getData(long discordId);

    void disconnect();
}

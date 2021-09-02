package com.gregorioandrade.ds.accsync.data.connectors;

import com.gregorioandrade.ds.accsync.data.DataConnector;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class MySQLConnector implements DataConnector {

    private final DataSource dataSource;
    private final ExecutorService executorService;

    public MySQLConnector(){
        HikariConfig config = new HikariConfig();
        String url = "jdbc:mysql://localhost:3306/dsaccsync"; // TODO below values should be configurable
        config.setJdbcUrl(url);
        config.setUsername("root");
        config.setPassword(System.getProperty("mysqlPassword"));
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMinimumIdle(4);
        config.setMaximumPoolSize(4);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts","true");
        config.addDataSourceProperty("useLocalSessionState","true");

        this.dataSource = new HikariDataSource(config);
        this.executorService = Executors.newFixedThreadPool(2);

        String createTableStatement = "CREATE TABLE IF NOT EXISTS `active_requests` (" +
                "`discord_id` BIGINT NOT NULL," +
                "`token` SMALLINT NOT NULL," +
                "`created_timestamp` BIGINT NOT NULL," +
                "PRIMARY KEY (`discord_id`)" +
                ")";

        try {
            dataSource.getConnection().prepareStatement(createTableStatement).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Mono<Void> createRequest(long discordId, short verificationToken) {
        Runnable runnable = () -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("REPLACE INTO `active_requests` (discord_id, token, created_timestamp) VALUES (?, ?, ?);")){
                statement.setLong(1, discordId);
                statement.setInt(2, verificationToken);
                statement.setLong(3, System.currentTimeMillis());
                statement.execute();
            } catch (SQLException exception){
                exception.printStackTrace();
            }
        };
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, executorService);
        return Mono.fromFuture(future);
    }

    @Override
    public Mono<Boolean> hasRequest(long discordId) {
        Supplier<Boolean> runnable = () -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM `active_requests` WHERE discord_id = ?;")){
                statement.setLong(1, discordId);
                try (ResultSet set = statement.executeQuery()){
                    return set.next();
                }
            } catch (SQLException exception){
                exception.printStackTrace();
            }
            return false;
        };
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(runnable, executorService);
        return Mono.fromFuture(future);
    }

    @Override
    public Mono<Void> deleteRequest(long discordId) {
        Runnable runnable = () -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE * FROM `active_requests` WHERE discord_id = ?;")){
                statement.setLong(1, discordId);
                statement.execute();
            } catch (SQLException exception){
                exception.printStackTrace();
            }
        };
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, executorService);
        return Mono.fromFuture(future);
    }
}

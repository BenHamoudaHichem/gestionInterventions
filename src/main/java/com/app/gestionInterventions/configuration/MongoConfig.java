package com.app.gestionInterventions.configuration;

import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.SocketUtils;

@Configuration
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class })

public class MongoConfig {

    private static final String CONNECTION_STRING = "mongodb://manager:xyz123@127.0.0.1:27017/gestioninterventions_db?authSource=gestioninterventions_db";
    private static final String HOST = "localhost";

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {

        int randomPort = SocketUtils.findAvailableTcpPort();

        ImmutableMongodConfig mongoDbConfig = MongodConfig.builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(HOST, randomPort, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodExecutable mongodExecutable = starter.prepare(mongoDbConfig);
        mongodExecutable.start();
        return new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, HOST, randomPort)), "gestioninterventions_db");
    }
}

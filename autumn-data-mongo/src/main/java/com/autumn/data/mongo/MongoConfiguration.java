package com.autumn.data.mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;

@Slf4j
@Configuration
public class MongoConfiguration {

    @Resource
    private MongoDatabaseFactory mongoDatabaseFactory;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDatabaseFactory);
    }

    @Bean
    public GridFSBucket getGridFSBuckets() {
        MongoDatabase db = mongoDatabaseFactory.getMongoDatabase();
        return GridFSBuckets.create(db);
    }

    @Bean
    public GridFsTemplate gridFsTemplate(MongoTemplate mongoTemplate) {
        return new GridFsTemplate(mongoDatabaseFactory, mongoTemplate.getConverter());
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory() {
        //TODO mongodb 作为第二数据源

        MongoDatabaseFactory mongoDbFactory =
                new SimpleMongoClientDatabaseFactory(
                        "mongodb://military:N3Xx3tXOmnjYDlLv@172.16.0.7:28016,172.16.0.7:28018/military?slaveOk=true&replicaSet=dataearth&write=1&readPreference=secondaryPreferred&connectTimeoutMS=300000");
        return mongoDbFactory;
    }

}

package org.homework.utils;

import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoUtil {

    @Resource
    private MongoTemplate mongoTemplate;


    public <T> void insert(T object, String collectionName) {
        mongoTemplate.insert(object, collectionName);
    }

    public <T> void insertMany(List<T> objectList, String collectionName) {
        mongoTemplate.insert(objectList, collectionName);
    }

    public <T> List<T> findList(String collectionName, Query query, Class<T> tClass) {
        return mongoTemplate.find(query, tClass, collectionName);
    }

    public <T> List<T> findAllAsList(String collectionName, Class<T> tClass) {
        return mongoTemplate.findAll(tClass, collectionName);
    }

    public <T> T findOne(String collectionName, Query query, Class<T> tClass) {
        return mongoTemplate.findOne(query, tClass, collectionName);
    }

    public long totalData(String collectionName, Query query) {
        return mongoTemplate.count(query, collectionName);
    }


}

/*
 * Copyright: Philipp Emmermacher 2020
 */

package de.emmermacher.springboot.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.bson.Document;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author ubuntu
 *
 */
@Service
@ComponentScan("de.snap2life.*")
public class MongoServiceImpl implements MongoService {

  private static Map<String, MongoClient> mongoClients = new HashMap<String, MongoClient>();

  @PreDestroy
  public void preDestroy() {
    for (MongoClient mongoClient : mongoClients.values()) {
      mongoClient.close();
    }
    mongoClients.clear();
  }

  @Override
  public MongoCollection<Document> getMongoCollection(ServerAddress serverAddress, String database, String collection, MongoCredential mongoCredential, MongoClientOptions options) {
    if (serverAddress == null) {
      throw new IllegalArgumentException("serverAddress cannot be null");
    } else if (database == null || database.isEmpty()) {
      throw new IllegalArgumentException("database cannot be null or empty");
    } else if (collection == null || collection.isEmpty()) {
      throw new IllegalArgumentException("collection cannot be null or empty");
    }

    if (options == null) {
      options = new MongoClientOptions.Builder().build();
    }

    if (!mongoClients.containsKey(serverAddress.getHost() + ":" + serverAddress.getPort())) {
      if (mongoCredential != null) {
        List<MongoCredential> mongoCredentials = new ArrayList<MongoCredential>();
        mongoCredentials.add(mongoCredential);
        mongoClients.put(serverAddress.getHost() + ":" + serverAddress.getPort(), new MongoClient(serverAddress, mongoCredentials, options));
      } else {
        mongoClients.put(serverAddress.getHost() + ":" + serverAddress.getPort(), new MongoClient(serverAddress, options));
      }
    }
    
    MongoDatabase mongoDatabase = mongoClients.get(serverAddress.getHost() + ":" + serverAddress.getPort()).getDatabase(database);
    return mongoDatabase.getCollection(collection);
  }
}

/*
 * Copyright: Philipp Emmermacher 2020
 */

package de.emmermacher.springboot.mongo;

import org.bson.Document;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

/**
 * @author ubuntu
 *
 */
public interface MongoService {

  //MongoCollection<Document> getMongoCollection(String host, Integer port, String database, String collection);

  MongoCollection<Document> getMongoCollection(ServerAddress serverAddress, String database, String collection, MongoCredential mongoCredential, MongoClientOptions options);

  //MongoCollection<Document> getMongoCollection(String host, Integer port, String database, String collection, MongoClientOptions options);

  //Map<String, MongoClient> getClients();

}

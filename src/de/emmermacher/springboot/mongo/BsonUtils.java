/*
 * Copyright: Philipp Emmermacher 2020
 */

package de.emmermacher.springboot.mongo;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;

/**
 * @author ubuntu
 *
 */
public class BsonUtils {

  public static String getString(Bson bson) {
    return bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toString();
  }
  
}

/*
 * Copyright: Philipp Emmermacher 2020
 */

package de.emmermacher.springboot.mongo;

import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;

/**
 * @author emmermacherp
 */
public interface GenericDAO<E extends GenericEntity> {

  E findById(ObjectId _id);

  E create(E e);

  E update(E e);

  List<E> find(Bson filter, Bson sort, int skip, int limit);

  List<E> find(Bson filter);

  List<E> find(Bson filter, Bson sort, Pageable pageable);

  List<E> findAll();
  
  E findFirst(Bson filter);
  
  <R> R findFirstValue(Bson filter, String key);

  long count(Bson filter);

  long countAll();

  DeleteResult deleteById(ObjectId id);

  MongoCollection<Document> getCollection();
  
}

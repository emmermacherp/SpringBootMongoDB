/*
 * Copyright: Philipp Emmermacher 2020
 */

package de.emmermacher.springboot.mongo;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;

/**
 * @author emmermacherp
 */
@SuppressWarnings("unchecked")
public abstract class GenericDAOImpl<E extends GenericEntity> implements GenericDAO<E> {

  private static final Logger                  LOG = Logger.getLogger(GenericDAOImpl.class);

  @Autowired
  private MongoService                         mongoService;

  protected MongoCollection<Document>          collection;

  private Constructor<? extends GenericEntity> documentConstructor;

  @Override
  public E findById(ObjectId _id) {
    try {
      Document doc = collection.find(Filters.eq(GenericEntity.ID, _id)).first();
      return doc != null ? (E) documentConstructor.newInstance(doc) : null;
    } catch (Exception e) {
      LOG.error("findById()", e);
      return null;
    }
  }

  @Override
  public E create(E e) {
    Document doc = e.getDocument();
    collection.insertOne(doc);
    try {
      e = (E) documentConstructor.newInstance(doc);
    } catch (Exception ex) {
      LOG.error("create()", ex);
      return null;
    }
    return e;
  }

  @Override
  public E update(E e) {
    Document doc = e.getDocument();
    doc.remove(GenericEntity.ID);
    getCollection()
        .updateOne(new Document(GenericEntity.ID, e.getId()),
            new Document("$set", doc));
    return findById(e.getId());
  }

  @Override
  public E findFirst(Bson filter) {
    try {
      Document doc = collection.find(filter).first();
      return doc != null ? (E) documentConstructor.newInstance(doc) : null;
    } catch (Exception e) {
      LOG.error("findFirst()", e);
      return null;
    }
  }

  @Override
  public <R> R findFirstValue(Bson filter, String key) {
    try {
      Document doc = collection.find(filter).first();
      return doc != null ? (R) doc.get(key) : null;
    } catch (Exception e) {
      LOG.error("findFirstValue()", e);
      return null;
    }
  }
  
  
  public List<E> find(Bson filter) {
    List<E> entities = new ArrayList<E>();
    MongoCursor<Document> cursor = null;

    // use filter if not null
    if (filter != null) {
      cursor = collection.find(filter).iterator();
    } else {
      cursor = collection.find().iterator();
    }

    while (cursor.hasNext()) {
      try {
        entities.add((E) documentConstructor.newInstance(cursor.next()));
      } catch (Exception e) {
        LOG.error("find()", e);
      }
    }
    return entities;
  }
  
  @Override
  public List<E> find(Bson filter, Bson sort, int skip, int limit) {
    List<E> entities = new ArrayList<E>();
    MongoCursor<Document> cursor = null;

    // use filter if not null
    if (filter != null) {
      if (sort != null) {
        cursor = collection.find(filter).sort(sort).skip(skip).limit(limit).iterator();
      } else {
        cursor = collection.find(filter).skip(skip).limit(limit).iterator();
      }
    } else {
      if (sort != null) {
        cursor = collection.find().sort(sort).skip(skip).limit(limit).iterator();
      } else {
        cursor = collection.find().skip(skip).limit(limit).iterator();
      }
    }

    while (cursor.hasNext()) {
      try {
        entities.add((E) documentConstructor.newInstance(cursor.next()));
      } catch (Exception e) {
        LOG.error("find()", e);
      }
    }
    return entities;
  }

  @Override
  public List<E> find(Bson filter, Bson sort, Pageable pageable) {
    return find(filter, sort, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
  }

  @Override
  public List<E> findAll() {
    return find(null, null, 0, 0);
  }

  @Override
  public long count(Bson filter) {
    if (filter != null) {
      return collection.count(filter);
    } else {
      return collection.count();
    }
  }

  @Override
  public long countAll() {
    return collection.count();
  }

  @PostConstruct
  public void postConstruct() {
    MongoCredential mongoCredential = null;
    String username = getDatabaseUsername();
    username = username.isEmpty() ? null : username;
    String password = getDatabasePassword();
    password = password.isEmpty() ? null : password;
    String authenticationDatabase = getAuthenticationDatabase();
    authenticationDatabase = authenticationDatabase.isEmpty() ? null : authenticationDatabase;
    authenticationDatabase = authenticationDatabase != null ? authenticationDatabase : getDatabaseName();
    
    if (username != null && password != null) {      
      mongoCredential = MongoCredential.createCredential(username, authenticationDatabase, password.toCharArray());      
    }
    
    this.collection = mongoService.getMongoCollection(new ServerAddress(getHost(), getPort()), getDatabaseName(), getCollectionName(), mongoCredential, null);
    
    try {
      this.documentConstructor = getEntityClass().getConstructor(new Class[] { Document.class });
    } catch (Exception e) {
      LOG.error("postConstruct()", e);
    }
  }

  @Override
  public DeleteResult deleteById(ObjectId id) {
    return collection.deleteOne(new Document().append(GenericEntity.ID, id));
  }

  @Override
  public MongoCollection<Document> getCollection() {
    return collection;
  }

  protected void createHashedIndex(String key) {
    MongoCursor<Document> cursor = collection.listIndexes().iterator();
    boolean hasIndex = false;
    while (cursor.hasNext()) {
      if (cursor.next().getString("name").equals("_" + key + "_")) {
        hasIndex = true;
        break;
      }
    }
    if (!hasIndex) {
      collection.createIndex(new Document().append(key, "hashed"), new IndexOptions().name("_" + key + "_"));
    }
  }

  protected void createCompoundIndex(Map<String, Boolean> keys, boolean unique) {
    String name = "_";
    for (Iterator<String> iter = keys.keySet().iterator(); iter.hasNext();) {
      name += iter.next() + (iter.hasNext() ? "_" : "");
    }
    name += "_";
    MongoCursor<Document> cursor = collection.listIndexes().iterator();
    boolean hasIndex = false;
    while (cursor.hasNext()) {
      if (cursor.next().getString("name").equals(name)) {
        hasIndex = true;
        break;
      }
    }
    Document doc = new Document();
    for (String key : keys.keySet()) {
      doc.append(key, keys.get(key) ? 1 : 0);
    }
    if (!hasIndex) {
      collection.createIndex(doc, new IndexOptions().unique(unique).name(name));
    }
  }

  protected void createIndex(String key, boolean asc, boolean unique) {
    MongoCursor<Document> cursor = collection.listIndexes().iterator();
    boolean hasIndex = false;
    while (cursor.hasNext()) {
      if (cursor.next().getString("name").equals("_" + key + "_")) {
        hasIndex = true;
        break;
      }
    }
    if (!hasIndex) {
      collection.createIndex(new Document().append(key, asc ? 1 : -1), new IndexOptions().unique(unique).name("_" + key + "_"));
    }
  }

  protected abstract String getCollectionName();

  protected abstract Class<? extends GenericEntity> getEntityClass();

  protected abstract String getHost();

  protected abstract Integer getPort();

  protected abstract String getDatabaseName();
  
  protected abstract String getDatabaseUsername();
  
  protected abstract String getDatabasePassword();
  
  protected abstract String getAuthenticationDatabase();
  
}

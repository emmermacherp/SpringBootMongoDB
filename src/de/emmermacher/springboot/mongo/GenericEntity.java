/*
 * Copyright: Philipp Emmermacher 2020
 */

package de.emmermacher.springboot.mongo;

import java.io.Serializable;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * @author emmermacherp
 */
public abstract class GenericEntity implements Serializable {

  private static final long  serialVersionUID = 1L;

  public static final String ID               = "_id";
  public static final String CREATED          = "created";
  public static final String UPDATED          = "updated";

  protected ObjectId         _id;
  protected Date             created;
  protected Date             updated;

  public GenericEntity() {
  }

  protected GenericEntity(Document doc) throws EntityException {
    this._id = getFieldOrThrow(doc, ID);
    this.created = getFieldOrThrow(doc, CREATED);
    this.updated = getFieldOrThrow(doc, UPDATED);
  }

  @SuppressWarnings("unchecked")
  public static <E> E getFieldOrThrow(Document doc, String key) throws EntityException {
    if (doc == null || key == null || key.isEmpty()) {
      throw new IllegalArgumentException("invalid arguments");
    }
    if (!doc.containsKey(key)) {
      throw new EntityException("key \"" + key + "\" does not exist in document with id " + doc.getObjectId(ID));
    }
    try {
      return (E) doc.get(key);
    } catch (Exception e) {
      throw new EntityException("could not get \"" + key + "\" because (" + e.getMessage() + ") from document with id " + doc.getObjectId(ID));
    }
  }

  @SuppressWarnings("unchecked")
  public static <E> E getField(Document doc, String key) {
    if (doc == null || key == null || key.isEmpty()) {
      throw new IllegalArgumentException("invalid arguments");
    }
    if (!doc.containsKey(key)) {
      return null;
    }
    try {
      return (E) doc.get(key);
    } catch (Exception e) {
      return null;
    }
  }

  public ObjectId getId() {
    return _id;
  }

  public String getIdAsString() {
    return _id != null ? _id.toString() : null;
  }

  public void setId(ObjectId _id) {
    this._id = _id;
  }

  public ObjectId get_id() {
    return _id;
  }

  public void set_id(ObjectId _id) {
    this._id = _id;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public Document getDocument() {
    Document doc = new Document();

    if (_id != null) {
      doc.append(ID, _id);
    }

    if (created == null) {
      created = new Date();
    }

    updated = new Date();

    doc.append(CREATED, created);
    doc.append(UPDATED, updated);

    return doc;
  }

  @Override
  public String toString() {
    return new Document()
        .append(ID, getIdAsString())
        .append(CREATED, created)
        .append(UPDATED, updated)
        .toJson();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_id == null) ? 0 : _id.hashCode());
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((updated == null) ? 0 : updated.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GenericEntity other = (GenericEntity) obj;
    if (_id == null) {
      if (other._id != null)
        return false;
    } else if (!_id.equals(other._id))
      return false;
    if (created == null) {
      if (other.created != null)
        return false;
    } else if (!created.equals(other.created))
      return false;
    if (updated == null) {
      if (other.updated != null)
        return false;
    } else if (!updated.equals(other.updated))
      return false;
    return true;
  }

}

/*
 * Copyright: Philipp Emmermacher 2020
 */
 
 package de.emmermacher.springboot.mongo;

public class EntityException extends Exception {

  private static final long serialVersionUID = 1L;

  public EntityException(String message) {
    super(message);
  }  

}

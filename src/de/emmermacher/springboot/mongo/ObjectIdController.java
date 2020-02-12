/*
 * Copyright: Philipp Emmermacher 2020
 */

package de.emmermacher.springboot.mongo;

import java.beans.PropertyEditorSupport;

import org.bson.types.ObjectId;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author ubuntu
 *
 */
public abstract class ObjectIdController {
 
  @InitBinder
  public void initBinder(final WebDataBinder binder) {
    binder.registerCustomEditor(ObjectId.class, new ObjectIdEditor());
  }  
  
  public static class ObjectIdEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
      try {
        ObjectId objectId = new ObjectId(text);
        this.setValue(objectId);
      } catch (Exception e) {
        // ignore
      }
    }
  }
}

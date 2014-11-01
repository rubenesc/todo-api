/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todo.api.resources.ext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.HttpMethod;

/**
 *
 * @author ruben
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
public @interface PATCH {
}
package tech.ayot.ticket.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to check user roles
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CheckRole {

    /**
     * The role
     */
    String role() default "";

    /**
     * The root role
     * <p>
     *     root means this user has this role for all products
     * </p>
     */
    String rootRole() default "";
}

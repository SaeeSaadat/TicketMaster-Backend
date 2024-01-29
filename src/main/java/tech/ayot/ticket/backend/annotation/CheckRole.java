package tech.ayot.ticket.backend.annotation;

import tech.ayot.ticket.backend.model.enumuration.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to check user roles.
 * <p>
 *     If you want to use this annotation on an endpoint with {@link CheckRole#role} parameter,
 *     the endpoint should have
 *     {@value tech.ayot.ticket.backend.configuration.WebMvcConfiguration#PRODUCT_ID_PATH_VARIABLE_NAME}
 *     path variable.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CheckRole {

    /**
     * The role
     */
    Role role() default Role.GUEST;

    /**
     * The root role
     * <p>
     *     root means this user has this role for all products
     * </p>
     */
    Role rootRole() default Role.GUEST;
}

package tech.ayot.ticket.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import tech.ayot.ticket.backend.annotation.CheckRole;
import tech.ayot.ticket.backend.configuration.WebMvcConfiguration;
import tech.ayot.ticket.backend.model.enumuration.Role;
import tech.ayot.ticket.backend.service.auth.RoleService;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * This class checks access to the called method using [CheckRole] annotations declared on the method
 */
@Component
public class RoleCheckerInterceptor implements HandlerInterceptor {

    private final RoleService roleService;

    public RoleCheckerInterceptor(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public boolean preHandle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler
    ) {
        if (!(handler instanceof HandlerMethod)) return true;

        // Get CheckRole annotation
        Method method = ((HandlerMethod) handler).getMethod();
        if (!method.isAnnotationPresent(CheckRole.class)) return true;
        CheckRole checkRoleAnnotation = method.getAnnotation(CheckRole.class);

        boolean hasRole = true;
        if (!(checkRoleAnnotation.role() == Role.GUEST)) {
            // Get product ID from request path variables
            Map<String, String> pathVariablesMap;
            try {
                //noinspection unchecked
                pathVariablesMap = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            } catch (ClassCastException e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
            String productIdString = pathVariablesMap.get(WebMvcConfiguration.PRODUCT_ID_PATH_VARIABLE_NAME);
            if (productIdString == null) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
            long productId = Integer.parseInt(productIdString);

            hasRole &= roleService.userHasRole(productId, checkRoleAnnotation.role());
        }
        if (!(checkRoleAnnotation.rootRole() == Role.GUEST)) {
            hasRole &= roleService.userHasRole(null, checkRoleAnnotation.rootRole());
        }

        if (!hasRole) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }

        return hasRole;
    }
}
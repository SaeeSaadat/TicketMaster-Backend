package tech.ayot.ticket.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.ayot.ticket.backend.interceptor.RoleCheckerInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    public static final String PRODUCT_ID_PATH_VARIABLE_NAME = "productId";

    private final RoleCheckerInterceptor roleCheckerInterceptor;

    public WebMvcConfiguration(RoleCheckerInterceptor roleCheckerInterceptor) {
        this.roleCheckerInterceptor = roleCheckerInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleCheckerInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedHeaders("*");
        registry.addMapping("/**").allowedMethods("POST, GET, DELETE, PUT");
        registry.addMapping("/**").allowedOrigins("*");
    }
}

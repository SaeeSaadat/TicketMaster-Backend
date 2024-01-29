package tech.ayot.ticket.backend.unit.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tech.ayot.ticket.backend.BackendApplication;
import tech.ayot.ticket.backend.dto.auth.UserDto;
import tech.ayot.ticket.backend.model.enumuration.Role;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.model.user.UserProduct;
import tech.ayot.ticket.backend.service.auth.RoleService;
import tech.ayot.ticket.backend.unit.BaseUnitTest;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BackendApplication.class})
public class RoleServiceUnitTest extends BaseUnitTest {

    private final RoleService roleService;

    public RoleServiceUnitTest(RoleService roleService) {
        this.roleService = roleService;
    }


    @Test
    public void userHasRoleShouldReturnFalseIfUserIsNull() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        boolean hasRole = roleService.userHasRole(null, null);

        // Assert
        Assertions.assertFalse(hasRole);
    }

    @Test
    public void userHasRoleShouldReturnTrueIfUserHasRole() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Create product
        Product product = new Product();
        product.setId(1L);
        product.setName("product");

        // Create user product
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(product);
        userProduct.setRole(Role.ADMIN);
        user.setUserProducts(List.of(userProduct));

        // Mock authentication
        UserDto userDto = new UserDto(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        boolean hasRole = roleService.userHasRole(product.getId(), Role.USER);

        // Assert
        Assertions.assertTrue(hasRole);
    }

    @Test
    public void userHasRoleShouldReturnFalseIfUserDoesNotHaveRole() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Create product
        Product product = new Product();
        product.setId(1L);
        product.setName("product");

        // Create user product
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(product);
        userProduct.setRole(Role.USER);
        user.setUserProducts(List.of(userProduct));

        // Mock authentication
        UserDto userDto = new UserDto(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        boolean hasRole = roleService.userHasRole(product.getId(), Role.ADMIN);

        // Assert
        Assertions.assertFalse(hasRole);
    }

    @Test
    public void userHasRootRoleShouldReturnTrueIfUserHasRootRole() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Create user product
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(null);
        userProduct.setRole(Role.ADMIN);
        user.setUserProducts(List.of(userProduct));

        // Mock authentication
        UserDto userDto = new UserDto(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        boolean hasRole = roleService.userHasRole(null, Role.USER);

        // Assert
        Assertions.assertTrue(hasRole);
    }

    @Test
    public void userHasRootRoleShouldReturnTrueIfUserDoesNotHaveRootRole() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Create user product
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(null);
        userProduct.setRole(Role.USER);
        user.setUserProducts(List.of(userProduct));

        // Mock authentication
        UserDto userDto = new UserDto(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        boolean hasRole = roleService.userHasRole(null, Role.ADMIN);

        // Assert
        Assertions.assertFalse(hasRole);
    }

    @Test
    public void userHasRoleShouldReturnTrueIfUserHasRootRole() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Create user product
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(null);
        userProduct.setRole(Role.ADMIN);
        user.setUserProducts(List.of(userProduct));

        // Mock authentication
        UserDto userDto = new UserDto(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        boolean hasRole = roleService.userHasRole(1L, Role.USER);

        // Assert
        Assertions.assertTrue(hasRole);
    }

    @Test
    public void userHasRoleShouldReturnTrueIfUserDoesNotHaveRootRole() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Create user product
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(null);
        userProduct.setRole(Role.USER);
        user.setUserProducts(List.of(userProduct));

        // Mock authentication
        UserDto userDto = new UserDto(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        boolean hasRole = roleService.userHasRole(1L, Role.ADMIN);

        // Assert
        Assertions.assertFalse(hasRole);
    }
}

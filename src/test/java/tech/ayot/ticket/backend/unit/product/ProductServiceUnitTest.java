package tech.ayot.ticket.backend.unit.product;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.BackendApplication;
import tech.ayot.ticket.backend.dto.auth.enumuration.Role;
import tech.ayot.ticket.backend.dto.product.request.CreateProductRequest;
import tech.ayot.ticket.backend.dto.product.request.UpdateProductRequest;
import tech.ayot.ticket.backend.dto.product.response.CreateProductResponse;
import tech.ayot.ticket.backend.dto.product.response.ViewProductResponse;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.model.user.UserProduct;
import tech.ayot.ticket.backend.repository.product.ProductRepository;
import tech.ayot.ticket.backend.repository.user.UserProductRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;
import tech.ayot.ticket.backend.service.product.ProductService;
import tech.ayot.ticket.backend.unit.BaseUnitTest;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {BackendApplication.class})
public class ProductServiceUnitTest extends BaseUnitTest {

    @MockBean
    private final AuthenticationService authenticationService;

    @MockBean
    private final ProductRepository productRepository;

    @MockBean
    private final UserProductRepository userProductRepository;

    private final ProductService productService;

    public ProductServiceUnitTest(
        AuthenticationService authenticationService,
        ProductRepository productRepository,
        UserProductRepository userProductRepository,
        ProductService productService
    ) {
        this.authenticationService = authenticationService;
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
        this.productService = productService;
    }


    @Test
    public void createShouldCreateANewProduct() {
        // Create request
        CreateProductRequest request = new CreateProductRequest(
            "name",
            "description",
            UUID.randomUUID()
        );

        // Mock authentication
        User user = new User();
        user.setUsername("username");
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Mock product repository
        when(productRepository.existsProductByName(request.name())).thenReturn(false);
        when(productRepository.save(any(Product.class)))
            .thenAnswer((Answer<Product>) invocation -> {
                Product product = invocation.getArgument(0, Product.class);
                product.setId(1L);
                return product;
            });

        // Act
        ResponseEntity<CreateProductResponse> response = productService.create(request);

        // Assert
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1L, response.getBody().id());

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        Assertions.assertEquals(request.name(), savedProduct.getName());
        Assertions.assertEquals(request.description(), savedProduct.getDescription());
        Assertions.assertEquals(request.imageId(), savedProduct.getImageId());

        ArgumentCaptor<UserProduct> userProductArgumentCaptor = ArgumentCaptor.forClass(UserProduct.class);
        verify(userProductRepository).save(userProductArgumentCaptor.capture());
        UserProduct userProduct = userProductArgumentCaptor.getValue();
        Assertions.assertEquals(1L, userProduct.getProduct().getId());
        Assertions.assertEquals(user.getUsername(), userProduct.getUser().getUsername());
        Assertions.assertEquals(Role.ADMIN, userProduct.getRole());
    }

    @Test
    public void createShouldReturn409IfNameIsNotUnique() {
        // Create request
        CreateProductRequest request = new CreateProductRequest(
            "name",
            "description",
            UUID.randomUUID()
        );

        // Mock product repository
        when(productRepository.existsProductByName(request.name())).thenReturn(true);

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> productService.create(request)
        );
        Assertions.assertEquals(HttpStatus.CONFLICT, responseStatusException.getStatusCode());
        Assertions.assertEquals("Product with this name already exists", responseStatusException.getReason());
    }

    @Test
    public void createShouldReturnIfUserAlreadyIsProductAdmin() {
        // Create request
        CreateProductRequest request = new CreateProductRequest(
            "name",
            "description",
            UUID.randomUUID()
        );

        // Mock authentication
        User user = new User();
        user.setUsername("username");
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(new Product());
        userProduct.setRole(Role.ADMIN);
        user.setUserProducts(List.of(userProduct));
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Mock product repository
        when(productRepository.existsProductByName(request.name())).thenReturn(false);

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> productService.create(request)
        );
        Assertions.assertEquals(HttpStatus.CONFLICT, responseStatusException.getStatusCode());
        Assertions.assertEquals("This user has already a product", responseStatusException.getReason());
    }

    @Test
    public void viewShouldReturnProductResponse() {
        // Create product
        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setDescription("description");
        product.setImageId(UUID.randomUUID());

        // Mock product repository
        when(productRepository.findProductById(product.getId())).thenReturn(product);

        // Act
        ResponseEntity<ViewProductResponse> response = productService.view(product.getId());

        // Assert
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(product.getVersion(), response.getBody().version());
        Assertions.assertEquals(product.getName(), response.getBody().name());
        Assertions.assertEquals(product.getDescription(), response.getBody().description());
        Assertions.assertEquals(product.getImageId(), response.getBody().imageId());
    }

    @Test
    public void viewShouldReturn404IfProductDoesNotExists() {
        // Mock product repository
        Long productId = 1L;
        when(productRepository.findProductById(productId)).thenReturn(null);

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> productService.view(productId)
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
        Assertions.assertEquals("There is no product with this id", responseStatusException.getReason());
    }

    @Test
    public void updateShouldUpdateProduct() {
        // Create product
        Product product = new Product();
        product.setId(1L);
        product.setVersion(0L);
        product.setName("name");
        product.setDescription("description");
        product.setImageId(UUID.randomUUID());

        // Mock product repository
        when(productRepository.findProductById(product.getId())).thenReturn(product);

        // Create request
        UpdateProductRequest request = new UpdateProductRequest(
            product.getVersion(),
            "new-description",
            UUID.randomUUID()
        );

        // Act
        productService.update(product.getId(), request);

        // Assert
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());
        Product savedProduct = productArgumentCaptor.getValue();
        Assertions.assertEquals(request.description(), savedProduct.getDescription());
        Assertions.assertEquals(request.imageId(), savedProduct.getImageId());
    }

    @Test
    public void updateShouldReturn404IfProductDoesNotExists() {
        // Mock product repository
        Long productId = 1L;
        when(productRepository.findProductById(productId)).thenReturn(null);

        // Create request
        UpdateProductRequest request = new UpdateProductRequest(
            0L,
            "new-description",
            UUID.randomUUID()
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> productService.update(productId, request)
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
        Assertions.assertEquals("There is no product with this id", responseStatusException.getReason());
    }

    @Test
    public void updateShouldReturn406IfVersionDoesNotMatch() {
        // Create product
        Product product = new Product();
        product.setId(1L);
        product.setVersion(1L);
        product.setName("name");
        product.setDescription("description");
        product.setImageId(UUID.randomUUID());

        // Mock product repository
        Long productId = 1L;
        when(productRepository.findProductById(productId)).thenReturn(product);

        // Create request
        UpdateProductRequest request = new UpdateProductRequest(
            product.getVersion() - 1,
            "new-description",
            UUID.randomUUID()
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> productService.update(productId, request)
        );
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, responseStatusException.getStatusCode());
        Assertions.assertEquals("Version is not correct", responseStatusException.getReason());
    }

    @Test
    public void deleteShouldDeleteProduct() {
        // Mock product repository
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);

        // Act
        productService.delete(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
        verify(userProductRepository, times(1)).deleteAllByProductId(productId);
    }

    @Test
    public void deleteShouldReturn404IfProductDoesNotExists() {
        // Mock product repository
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> productService.delete(productId)
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
        Assertions.assertEquals("There is no product with this id", responseStatusException.getReason());
    }
}

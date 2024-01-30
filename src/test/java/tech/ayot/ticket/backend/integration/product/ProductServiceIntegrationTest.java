package tech.ayot.ticket.backend.integration.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import tech.ayot.ticket.backend.dto.product.request.CreateProductRequest;
import tech.ayot.ticket.backend.dto.product.request.UpdateProductRequest;
import tech.ayot.ticket.backend.dto.product.response.CreateProductResponse;
import tech.ayot.ticket.backend.dto.product.response.ViewProductResponse;
import tech.ayot.ticket.backend.integration.BaseIntegrationTest;
import tech.ayot.ticket.backend.model.enumuration.Role;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.model.user.UserProduct;
import tech.ayot.ticket.backend.repository.product.ProductRepository;
import tech.ayot.ticket.backend.repository.user.UserProductRepository;
import tech.ayot.ticket.backend.repository.user.UserRepository;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductServiceIntegrationTest extends BaseIntegrationTest {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final UserProductRepository userProductRepository;

    public ProductServiceIntegrationTest(
        UserRepository userRepository,
        ProductRepository productRepository,
        UserProductRepository userProductRepository
    ) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
    }


    @AfterEach
    public void cleanUp() {
        List<Product> products = productRepository.findAll();
        products.forEach(product -> userProductRepository.deleteAllByProductId(product.getId()));
        productRepository.deleteAll(products);
    }


    @Test
    public void createShouldCreateANewProduct() throws Exception {
        // Create request
        CreateProductRequest request = new CreateProductRequest(
            "name",
            "description",
            "image"
        );

        // Act
        MockMvcResponse<CreateProductResponse> mockMvcResponse = sendRequest(
            HttpMethod.POST,
            "/api/product",
            MediaType.APPLICATION_JSON,
            request,
            status().isOk(),
            CreateProductResponse.class
        );

        // Assert
        Product product = productRepository.findProductById(mockMvcResponse.body().id());
        Assertions.assertEquals(request.name(), product.getName());
        Assertions.assertEquals(request.description(), product.getDescription());
        Assertions.assertEquals(request.imageId(), product.getImageId());

        List<UserProduct> userProducts = userProductRepository.findAll();
        Assertions.assertTrue(
            userProducts.stream().anyMatch(
                userProduct -> ADMIN_USER.equals(userProduct.getUser().getUsername())
                    && userProduct.getProduct() != null
                    && product.getId().equals(userProduct.getProduct().getId())
                    && userProduct.getRole() == Role.ADMIN
            )
        );
    }

    @Test
    public void createShouldReturn409IfNameIsNotUnique() throws Exception {
        // Create product
        Product product = new Product();
        product.setName("name");
        productRepository.save(product);

        // Create request
        CreateProductRequest request = new CreateProductRequest(
            "name",
            "description",
            "image"
        );

        // Act & Assert
        sendRequest(
            HttpMethod.POST,
            "/api/product",
            MediaType.APPLICATION_JSON,
            request,
            status().isConflict()
        );
    }

    @Test
    public void createShouldReturnIfUserAlreadyIsProductAdmin() throws Exception {
        // Create product
        Product product = new Product();
        product.setName("old-product");
        product = productRepository.save(product);

        // Create user product
        User user = userRepository.findUserByUsername(ADMIN_USER);
        UserProduct userProduct = new UserProduct();
        userProduct.setUser(user);
        userProduct.setProduct(product);
        userProduct.setRole(Role.ADMIN);
        userProductRepository.save(userProduct);

        // Create request
        CreateProductRequest request = new CreateProductRequest(
            "name",
            "description",
            "image"
        );

        // Act & Assert
        sendRequest(
            HttpMethod.POST,
            "/api/product",
            MediaType.APPLICATION_JSON,
            request,
            status().isConflict()
        );
    }

    @Test
    public void viewShouldReturnProductResponse() throws Exception {
        // Create product
        Product product = new Product();
        product.setName("name");
        product = productRepository.save(product);

        // Act
        MockMvcResponse<ViewProductResponse> mockMvcResponse = sendRequest(
            HttpMethod.GET,
            "/api/product/" + product.getId(),
            MediaType.APPLICATION_JSON,
            null,
            status().isOk(),
            ViewProductResponse.class
        );

        // Assert
        ViewProductResponse response = mockMvcResponse.body();
        Assertions.assertEquals(product.getVersion(), response.version());
        Assertions.assertEquals(product.getName(), response.name());
        Assertions.assertEquals(product.getDescription(), response.description());
        Assertions.assertEquals(product.getImageId(), response.imageId());
    }

    @Test
    public void viewShouldReturn404IfProductDoesNotExists() throws Exception {
        // Act & Assert
        sendRequest(
            HttpMethod.GET,
            "/api/product/1",
            MediaType.APPLICATION_JSON,
            null,
            status().isNotFound()
        );
    }

    @Test
    public void updateShouldUpdateProduct() throws Exception {
        // Create product
        Product product = new Product();
        product.setName("name");
        product.setDescription("description");
        product.setImageId("image");
        product = productRepository.save(product);

        // Create request
        UpdateProductRequest request = new UpdateProductRequest(
            product.getVersion(),
            "new-description",
            "image"
        );

        // Act
        sendRequest(
            HttpMethod.POST,
            "/api/product/" + product.getId() + "/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isOk()
        );

        // Assert
        Product updatedProduct = productRepository.findProductById(product.getId());
        Assertions.assertEquals(request.description(), updatedProduct.getDescription());
        Assertions.assertEquals(request.imageId(), updatedProduct.getImageId());
    }

    @Test
    public void updateShouldReturn404IfProductDoesNotExists() throws Exception {
        // Create request
        UpdateProductRequest request = new UpdateProductRequest(
            0L,
            "new-description",
            "image"
        );

        // Act & Assert
        sendRequest(
            HttpMethod.POST,
            "/api/product/1/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isNotFound()
        );
    }

    @Test
    public void updateShouldReturn406IfVersionDoesNotMatch() throws Exception {
        // Create product
        Product product = new Product();
        product.setName("name");
        product = productRepository.save(product);

        // Create request
        UpdateProductRequest request = new UpdateProductRequest(
            product.getVersion() - 1,
            "new-description",
            "image"
        );

        // Act & Assert
        sendRequest(
            HttpMethod.POST,
            "/api/product/" + product.getId() + "/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isNotAcceptable()
        );
    }

    @Test
    public void deleteShouldDeleteProduct() throws Exception {
        // Create product
        Product product = new Product();
        product.setName("name");
        product.setDescription("description");
        product.setImageId("image");
        product = productRepository.save(product);

        // Create user product
        User user = userRepository.findUserByUsername(ADMIN_USER);
        UserProduct userProduct = new UserProduct();
        userProduct.setProduct(product);
        userProduct.setUser(user);
        userProduct.setRole(Role.ADMIN);
        userProductRepository.save(userProduct);

        // Act
        sendRequest(
            HttpMethod.DELETE,
            "/api/product/" + product.getId(),
            MediaType.APPLICATION_JSON,
            null,
            status().isOk()
        );

        // Assert
        Assertions.assertFalse(productRepository.existsById(product.getId()));
        List<UserProduct> userProducts = userProductRepository.findAll();
        Product finalProduct = product;
        Assertions.assertTrue(
            userProducts.stream().noneMatch(
                up -> ADMIN_USER.equals(up.getUser().getUsername())
                    && up.getProduct() != null
                    && finalProduct.getId().equals(up.getProduct().getId())
                    && up.getRole() == Role.ADMIN
            )
        );
    }

    @Test
    public void deleteShouldReturn404IfProductDoesNotExists() throws Exception {
        // Act & Assert
        sendRequest(
            HttpMethod.DELETE,
            "/api/product/1",
            MediaType.APPLICATION_JSON,
            null,
            status().isNotFound()
        );
    }
}

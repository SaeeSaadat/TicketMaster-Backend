package tech.ayot.ticket.backend.service.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.annotation.CheckRole;
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
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

import static tech.ayot.ticket.backend.configuration.WebMvcConfiguration.PRODUCT_ID_PATH_VARIABLE_NAME;

@RestController
@RequestMapping("/api/product")
public class ProductService {

    private final AuthenticationService authenticationService;

    private final ProductRepository productRepository;

    private final UserProductRepository userProductRepository;

    private final UserRepository userRepository;

    public ProductService(
        AuthenticationService authenticationService,
        ProductRepository productRepository,
        UserProductRepository userProductRepository,
        UserRepository userRepository
    ) {
        this.authenticationService = authenticationService;
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
        this.userRepository = userRepository;
    }


    /**
     * Creates a new product
     *
     * @param request The create request
     * @return ID of the created product
     */
    @Transactional
    @PostMapping(value = {""}, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CreateProductResponse> create(@RequestBody @Valid CreateProductRequest request) {
        if (productRepository.existsProductByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product with this name already exists");
        }

        User user = authenticationService.getCurrentUser();
        if (user.getUserProducts().stream().anyMatch(userProduct -> userProduct.getProduct() != null)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This user has already a product");
        }

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setImageId(request.imageId());

        product = productRepository.save(product);

        UserProduct userProduct = new UserProduct();
        userProduct.setProduct(product);
        userProduct.setUser(user);
        userProduct.setRole(Role.ADMIN);
        user.getUserProducts().add(userProduct);
        userRepository.save(user);

        CreateProductResponse response = new CreateProductResponse(product.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @param productId The product's ID
     * @return View product response
     */
    @GetMapping(value = {"/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}"}, produces = {"application/json"})
    public ResponseEntity<ViewProductResponse> view(@PathVariable Long productId) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with this id not found");
        }

        ViewProductResponse viewProductResponse = new ViewProductResponse(
            product.getVersion(),
            product.getName(),
            product.getDescription(),
            product.getImageId()
        );
        return new ResponseEntity<>(viewProductResponse, HttpStatus.OK);
    }

    /**
     * Updates an existing product
     *
     * @param productId The product's ID
     * @param request   The update request
     */
    @Transactional
    @PutMapping(
        value = {"/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}"},
        consumes = {"application/json"},
        produces = {"application/json"}
    )
    @CheckRole(role = Role.ADMIN)
    public ResponseEntity<Long> update(
        @PathVariable Long productId,
        @RequestBody @Valid UpdateProductRequest request
    ) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with this id not found");
        }

        if (!request.version().equals(product.getVersion())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Version is not correct");
        }

        product.setDescription(request.description());
        product.setImageId(request.imageId());

        productRepository.save(product);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes a product
     *
     * @param productId The product's I
     */
    @Transactional
    @DeleteMapping(value = {"/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}"})
    @CheckRole(role = Role.ADMIN)
    public ResponseEntity<Void> delete(@PathVariable long productId) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with this id not found");
        }

        productRepository.deleteById(productId);
        userProductRepository.deleteAllByProductId(productId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

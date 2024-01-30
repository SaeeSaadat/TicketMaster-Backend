package tech.ayot.ticket.backend.service.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.annotation.CheckRole;
import tech.ayot.ticket.backend.model.enumuration.Role;
import tech.ayot.ticket.backend.dto.product.request.CreateProductRequest;
import tech.ayot.ticket.backend.dto.product.request.UpdateProductRequest;
import tech.ayot.ticket.backend.dto.product.response.CreateProductResponse;
import tech.ayot.ticket.backend.dto.product.response.ViewProductResponse;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.model.user.UserProduct;
import tech.ayot.ticket.backend.repository.product.ProductRepository;
import tech.ayot.ticket.backend.repository.ticket.TicketRepository;
import tech.ayot.ticket.backend.repository.user.UserProductRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

import static tech.ayot.ticket.backend.configuration.WebMvcConfiguration.PRODUCT_ID_PATH_VARIABLE_NAME;

@RestController
@RequestMapping("/api/product")
public class ProductService {

    private final AuthenticationService authenticationService;

    private final ProductRepository productRepository;

    private final UserProductRepository userProductRepository;

    private final TicketRepository ticketRepository;

    public ProductService(
        AuthenticationService authenticationService,
        ProductRepository productRepository,
        UserProductRepository userProductRepository,
        TicketRepository ticketRepository
    ) {
        this.authenticationService = authenticationService;
        this.productRepository = productRepository;
        this.userProductRepository = userProductRepository;
        this.ticketRepository = ticketRepository;
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
        boolean userIsProductAdmin = user.getUserProducts().stream().anyMatch(
            userProduct -> userProduct.getProduct() != null && userProduct.getRole().getLevel() >= Role.ADMIN.getLevel()
        );
        if (userIsProductAdmin) {
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
        userProductRepository.save(userProduct);

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no product with this id");
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
    @CheckRole(role = Role.ADMIN)
    @PostMapping(
        value = {"/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}/update"},
        consumes = {"application/json"}
    )
    public ResponseEntity<Long> update(
        @PathVariable Long productId,
        @RequestBody @Valid UpdateProductRequest request
    ) {
        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no product with this id");
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
    @CheckRole(role = Role.ADMIN)
    @DeleteMapping(value = {"/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}"})
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no product with this id");
        }
        if (ticketRepository.existsByProductId(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot delete a product which has a ticket");
        }

        userProductRepository.deleteAllByProductId(productId);
        productRepository.deleteById(productId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

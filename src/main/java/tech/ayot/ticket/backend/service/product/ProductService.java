package tech.ayot.ticket.backend.service.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.dto.auth.enumuration.Role;
import tech.ayot.ticket.backend.dto.product.request.CreateProductRequest;
import tech.ayot.ticket.backend.dto.product.request.UpdateProductRequest;
import tech.ayot.ticket.backend.dto.product.response.ViewProductResponse;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.model.user.UserProduct;
import tech.ayot.ticket.backend.repository.product.ProductRepository;
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

import static tech.ayot.ticket.backend.configuration.WebMvcConfiguration.PRODUCT_ID_PATH_VARIABLE_NAME;

@RestController
@RequestMapping("/api/product")
public class ProductService {

    private final ProductRepository productRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    public ProductService(ProductRepository productRepository, AuthenticationService authenticationService, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping(value = {""}, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long> create(@RequestBody CreateProductRequest request) {
        if (productRepository.existsProductByName(request.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product with this name already exists");
        }

        User user = authenticationService.getCurrentUser();
        if (!user.getUserProducts().stream().filter(userProduct -> userProduct.getProduct() != null).toList().isEmpty()) {
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

        return new ResponseEntity<>(product.getId(), HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = {"/update"}, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long> update(@RequestBody UpdateProductRequest request) {
        Product product = productRepository.findProductById(request.productId());
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with this id not found");
        }

        User user = authenticationService.getCurrentUser();
        Product finalProduct = product;
        //TODO test
        if (user.getUserProducts().stream().filter(userProduct -> userProduct.getProduct() == finalProduct).toList().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The user doesn't have access to edit this product");
        }

        product.setDescription(request.description());
        product.setImageId(request.imageId());

        product = productRepository.save(product);

        return new ResponseEntity<>(product.getId(), HttpStatus.OK);
    }

    @GetMapping(value = "/{" + PRODUCT_ID_PATH_VARIABLE_NAME + "}/view", produces = {"application/json"})
    public ResponseEntity<?> view(@PathVariable long productId) {

        Product product = productRepository.findProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with this id not found");
        }

        ViewProductResponse viewProductResponse = new ViewProductResponse(
            product.getName(),
            product.getDescription(),
            product.getImageId()
        );
        return new ResponseEntity<>(viewProductResponse, HttpStatus.OK);
    }


}

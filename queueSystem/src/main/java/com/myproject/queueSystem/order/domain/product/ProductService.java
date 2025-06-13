package com.myproject.queueSystem.order.domain.product;

import com.myproject.queueSystem.order.domain.order.item.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemRepository itemRepository;


    public ResponseEntity<Product> createProduct(Product data) {
        productRepository.save(data);
        return ResponseEntity.ok(productRepository.getReferenceById(data.getId()));
    }

    public ResponseEntity<List<Product>> listProducts(ProductType TYPE) {
        List<Product> products = new ArrayList<>();
        if (TYPE != null){
            products = productRepository.findAllByType(TYPE);
        }else {
            products = productRepository.findAll();
        }
        return ResponseEntity.ok(products);
    }

    public ResponseEntity<Product> updateProduct(Long id, Product data) {
        Product product = productRepository.getReferenceById(id);

        if (data.getName() != null) {
            product.setName(data.getName());
        }
        if (data.getDescription() != null) {
            product.setDescription(data.getDescription());
        }
        if (data.getPrice() != null) {
            product.setPrice(data.getPrice());
        }
        if (data.getType() != null) {
            product.setType(data.getType());
        }

        productRepository.save(product);
        return ResponseEntity.ok(product);
    }


    @Transactional
    public ResponseEntity<?> deleteProduct(Long id) {
        itemRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        return ResponseEntity.ok(true);
    }
}

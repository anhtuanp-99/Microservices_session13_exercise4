package com.pharmacy.identityservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        log.info(">>> [IDENTITY-SERVICE] Nhận request GET /api/products");

        List<Map<String, Object>> products = List.of(
                Map.of("id", 1, "name", "Paracetamol 500mg",
                        "price", new BigDecimal("50000"), "stock", 100),
                Map.of("id", 2, "name", "Amoxicillin 250mg",
                        "price", new BigDecimal("80000"), "stock", 50),
                Map.of("id", 3, "name", "Vitamin C 1000mg",
                        "price", new BigDecimal("120000"), "stock", 200)
        );

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        log.info(">>> [IDENTITY-SERVICE] Nhận request GET /api/products/{}", id);

        return ResponseEntity.ok(Map.of(
                "id", id,
                "name", "Paracetamol 500mg",
                "price", new BigDecimal("50000"),
                "stock", 100,
                "source", "identity-service"
        ));
    }
}
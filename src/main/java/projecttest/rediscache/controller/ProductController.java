package projecttest.rediscache.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projecttest.rediscache.entity.Product;
import projecttest.rediscache.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 제품 저장
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    // 단일 제품 조회 (캐시 사용)
    @GetMapping("/cache/{id}")
    public ResponseEntity<Map<String, Object>> getProductWithCache(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        Product product = productService.getProductByIdWithCache(id).orElse(null);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("duration", duration);
        response.put("source", "cache");

        return ResponseEntity.ok(response);
    }

    // 단일 제품 조회 (DB 직접)
    @GetMapping("/db/{id}")
    public ResponseEntity<Map<String, Object>> getProductDirectDb(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();

        Product product = productService.getProductByIdDirectDb(id).orElse(null);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("duration", duration);
        response.put("source", "database");

        return ResponseEntity.ok(response);
    }

    // 모든 제품 조회 (캐시 사용)
    @GetMapping("/cache/all")
    public ResponseEntity<Map<String, Object>> getAllProductsWithCache() {
        long startTime = System.currentTimeMillis();

        List<Product> products = productService.getAllProductsWithCache();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("duration", duration);
        response.put("source", "cache");
        response.put("count", products.size());

        return ResponseEntity.ok(response);
    }

    // 모든 제품 조회 (DB 직접)
    @GetMapping("/db/all")
    public ResponseEntity<Map<String, Object>> getAllProductsDirectDb() {
        long startTime = System.currentTimeMillis();

        List<Product> products = productService.getAllProductsDirectDb();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("duration", duration);
        response.put("source", "database");
        response.put("count", products.size());

        return ResponseEntity.ok(response);
    }

    // 성능 비교 (단일 제품)
    @GetMapping("/compare/{id}")
    public ResponseEntity<Map<String, Object>> comparePerformance(@PathVariable Long id) {
        // 캐시 조회
        long cacheStartTime = System.currentTimeMillis();
        Product cacheProduct = productService.getProductByIdWithCache(id).orElse(null);
        long cacheEndTime = System.currentTimeMillis();
        long cacheDuration = cacheEndTime - cacheStartTime;

        // DB 직접 조회
        long dbStartTime = System.currentTimeMillis();
        Product dbProduct = productService.getProductByIdDirectDb(id).orElse(null);
        long dbEndTime = System.currentTimeMillis();
        long dbDuration = dbEndTime - dbStartTime;

        Map<String, Object> response = new HashMap<>();
        response.put("product", cacheProduct);
        response.put("cacheDuration", cacheDuration);
        response.put("dbDuration", dbDuration);
        response.put("speedup",
                dbDuration > 0 ? String.format("%.2f", (double) dbDuration / cacheDuration) + "x" : "N/A");

        return ResponseEntity.ok(response);
    }

    // 성능 비교 (모든 제품)
    @GetMapping("/compare/all")
    public ResponseEntity<Map<String, Object>> compareAllPerformance() {
        // 캐시 조회
        long cacheStartTime = System.currentTimeMillis();
        List<Product> cacheProducts = productService.getAllProductsWithCache();
        long cacheEndTime = System.currentTimeMillis();
        long cacheDuration = cacheEndTime - cacheStartTime;

        // DB 직접 조회
        long dbStartTime = System.currentTimeMillis();
        List<Product> dbProducts = productService.getAllProductsDirectDb();
        long dbEndTime = System.currentTimeMillis();
        long dbDuration = dbEndTime - dbStartTime;

        Map<String, Object> response = new HashMap<>();
        response.put("products", cacheProducts);
        response.put("count", cacheProducts.size());
        response.put("cacheDuration", cacheDuration);
        response.put("dbDuration", dbDuration);
        response.put("speedup",
                dbDuration > 0 ? String.format("%.2f", (double) dbDuration / cacheDuration) + "x" : "N/A");

        return ResponseEntity.ok(response);
    }
}

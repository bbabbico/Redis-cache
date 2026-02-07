package projecttest.rediscache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projecttest.rediscache.entity.Product;
import projecttest.rediscache.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    // Write Around: 저장 시 캐시에 저장하지 않음
    @Transactional
    public Product saveProduct(Product product) {
        log.info("Saving product to DB: {}", product);
        return productRepository.save(product);
    }

    // Look Aside: 캐시에서 먼저 조회, 없으면 DB 조회 후 캐시에 저장
    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductByIdWithCache(Long id) {
        log.info("Fetching product from DB (cache miss): id = {}", id);
        // 캐시 미스 시에만 DB 조회
        return productRepository.findById(id);
    }

    // DB 직접 조회 (캐시 사용 안 함)
    @Transactional(readOnly = true)
    public Optional<Product> getProductByIdDirectDb(Long id) {
        log.info("Fetching product directly from DB: id = {}", id);
        return productRepository.findById(id);
    }

    // 모든 제품 조회 (캐시 사용)
    @Cacheable(value = "allProducts")
    public List<Product> getAllProductsWithCache() {
        log.info("Fetching all products from DB (cache miss)");
        return productRepository.findAll();
    }

    // 모든 제품 조회 (DB 직접)
    @Transactional(readOnly = true)
    public List<Product> getAllProductsDirectDb() {
        log.info("Fetching all products directly from DB");
        return productRepository.findAll();
    }
}

package com.omniflow.service;

import com.omniflow.model.Category;
import com.omniflow.model.Product;
import com.omniflow.model.Supplier;
import com.omniflow.repository.CategoryRepository;
import com.omniflow.repository.ProductRepository;
import com.omniflow.repository.SupplierRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final AuditService auditService;

    public ProductService(ProductRepository productRepository, 
                          CategoryRepository categoryRepository, 
                          SupplierRepository supplierRepository, 
                          AuditService auditService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.auditService = auditService;
    }

    @PostConstruct
    public void seedInitialCatalog() {
        if (categoryRepository.count() == 0) {
            Category electronics = categoryRepository.save(new Category("Electronics & Sensors", "ELEC", "Industrial sensors, IoT beacons, and controllers"));
            Category coldChain = categoryRepository.save(new Category("Cold Chain Pharmaceuticals", "COLD", "Temperature-controlled medical supplies and vaccines"));
            Category packaging = categoryRepository.save(new Category("Eco Packaging & Materials", "PACK", "Biodegradable containers and insulated wraps"));
            Category heavyHardware = categoryRepository.save(new Category("Heavy Hardware & Fasteners", "HARD", "Alloy bolts, structural brackets, and tools"));

            Supplier sup1 = supplierRepository.save(new Supplier("Nexus Sensor Technologies", "Alex Vance", "contact@nexussensors.com", "+1-415-555-0143", "Industrial Zone 4, Fremont CA", 5));
            Supplier sup2 = supplierRepository.save(new Supplier("CryoMed Biologics Inc.", "Dr. Elena Rostova", "orders@cryomed.bio", "+1-617-555-0182", "Biotech Boulevard, Boston MA", 3));
            Supplier sup3 = supplierRepository.save(new Supplier("Apex Heavy Manufacturing", "Marcus Sterling", "sales@apexheavy.com", "+1-312-555-0199", "Steel Mill Road, Chicago IL", 10));

            createDemoProduct("SKU-IOT-901", "IoT Ultra-Low Latency Telemetry Gateway", electronics, sup1, 45.00, 89.99, 140, 30, 15, 3.2, "PCS", "Aisle 1 - Bin E4");
            createDemoProduct("SKU-SENS-404", "Optical Temperature & Humidity Sensor probe", electronics, sup1, 12.50, 24.99, 8, 25, 10, 4.5, "PCS", "Aisle 1 - Bin B2");
            createDemoProduct("SKU-MED-VAC7", "mRNA Ultra-Cold Storage Vials (Pack of 50)", coldChain, sup2, 120.00, 240.00, 15, 20, 10, 2.8, "BOX", "Cold Room B - Tier 3");
            createDemoProduct("SKU-MED-INS9", "Insulated Polyurethane Bio-Transport Box", packaging, sup2, 18.00, 39.50, 4, 15, 8, 2.1, "PCS", "Aisle 3 - Floor Bay");
            createDemoProduct("SKU-FAST-880", "Titanium Reinforced Flange Fasteners M12", heavyHardware, sup3, 2.20, 5.80, 500, 100, 50, 12.0, "BOX", "Heavy Rack C-9");
            createDemoProduct("SKU-PACK-010", "Biodegradable Honeycomb Cushioning Wrap 100m", packaging, sup3, 14.50, 29.00, 60, 20, 10, 3.0, "ROLL", "Aisle 2 - Pallet 4");

            auditService.log("INITIAL_CATALOG_SEED", "Catalog", "All", "INFO", "Demonstration catalog initialized with products and suppliers.");
        }
    }

    private void createDemoProduct(String sku, String name, Category cat, Supplier sup, 
                                   double cost, double price, int stock, int threshold, int safety, 
                                   double velocity, String uom, String location) {
        Product p = new Product();
        p.setSku(sku);
        p.setName(name);
        p.setCategory(cat);
        p.setSupplier(sup);
        p.setUnitCost(BigDecimal.valueOf(cost));
        p.setSellingPrice(BigDecimal.valueOf(price));
        p.setQuantityInStock(stock);
        p.setReorderThreshold(threshold);
        p.setSafetyStock(safety);
        p.setDailyVelocity(velocity);
        p.setUnitOfMeasure(uom);
        p.setWarehouseLocation(location);
        p.setStatus("ACTIVE");
        productRepository.save(p);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public long getLowStockCount() {
        return productRepository.countLowStockProducts();
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(keyword.trim());
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Transactional
    public Product saveProduct(Product product) {
        boolean isNew = (product.getId() == null);
        Product saved = productRepository.save(product);
        String action = isNew ? "CREATE_PRODUCT" : "UPDATE_PRODUCT";
        auditService.log(action, "Product", saved.getId().toString(), "INFO", 
                "Product saved: " + saved.getName() + " (SKU: " + saved.getSku() + ", Stock: " + saved.getQuantityInStock() + ")");
        return saved;
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        productRepository.delete(p);
        auditService.log("DELETE_PRODUCT", "Product", id.toString(), "WARNING", "Product removed: " + p.getName() + " (" + p.getSku() + ")");
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }
}


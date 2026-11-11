-- ===================================================================
-- OmniFlow Enterprise Database Schema (MySQL 8.0)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS omniflow_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE omniflow_db;

-- 1. Users & Authentication
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

-- 2. Categories
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- 3. Suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(50),
    address VARCHAR(255),
    lead_time_days INT NOT NULL DEFAULT 7
);

-- 4. Products Catalog
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    category_id BIGINT,
    supplier_id BIGINT,
    unit_cost DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    selling_price DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    reorder_threshold INT NOT NULL DEFAULT 10,
    safety_stock INT NOT NULL DEFAULT 5,
    daily_velocity DOUBLE NOT NULL DEFAULT 1.0,
    unit_of_measure VARCHAR(30) DEFAULT 'PCS',
    warehouse_location VARCHAR(100) DEFAULT 'Zone A',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME,
    updated_at DATETIME,
    CONSTRAINT fk_prod_cat FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_prod_sup FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);

-- 5. Orders & Consignments
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(150) NOT NULL,
    customer_email VARCHAR(100),
    customer_phone VARCHAR(50),
    shipping_address VARCHAR(255),
    order_date DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    verification_hash VARCHAR(128),
    qr_code_base64 LONGTEXT,
    notes VARCHAR(500),
    created_by VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME
);

-- 6. Order Items
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 7. Stock Movements (Inventory Ledger)
CREATE TABLE IF NOT EXISTS stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    movement_type VARCHAR(30) NOT NULL,
    quantity INT NOT NULL,
    balance_after INT NOT NULL,
    reference_number VARCHAR(50),
    reason VARCHAR(255),
    performed_by VARCHAR(50),
    timestamp DATETIME NOT NULL,
    CONSTRAINT fk_mov_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 8. Compliance Audit Trail
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    entity_name VARCHAR(50),
    entity_id VARCHAR(50),
    performed_by VARCHAR(50) NOT NULL,
    user_role VARCHAR(30),
    ip_address VARCHAR(50),
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    details VARCHAR(1000),
    timestamp DATETIME NOT NULL
);

-- 9. Dynamic System Settings & Theming
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(50) NOT NULL UNIQUE,
    setting_value VARCHAR(500) NOT NULL,
    description VARCHAR(255),
    category VARCHAR(50)
);

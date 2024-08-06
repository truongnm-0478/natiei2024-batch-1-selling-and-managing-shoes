CREATE DATABASE IF NOT EXISTS Shoeshop
USE Shoeshop
CREATE TABLE accounts(
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    display_name VARCHAR(500) COLLATE utf8mb4_unicode_ci,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER', 'SELLER') NOT NULL,
    full_name VARCHAR(500) COLLATE utf8mb4_unicode_ci,
    date_of_birth DATE,
    address VARCHAR(500) COLLATE utf8mb4_unicode_ci,
    phone_number VARCHAR(20),
    gender BOOLEAN,
    avatar_url VARCHAR(255),
    is_activated BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME
);

CREATE INDEX idx_email ON accounts (email);

CREATE TABLE constants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    value VARCHAR(500) COLLATE utf8mb4_unicode_ci,
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME
);
CREATE TABLE refresh_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    token VARCHAR(255),
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    origin_price INT,
    discount INT,
    name VARCHAR(500) COLLATE utf8mb4_unicode_ci,
    gender ENUM('MALE', 'FEMALE', 'UNISEX'),
    description JSON,
    images VARCHAR(255),
    style_id INT,
	category_id INT,
    material_id INT,
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME,
    deleted_at DATETIME,
    FOREIGN KEY (style_id) REFERENCES constants(id),
    FOREIGN KEY (category_id) REFERENCES constants(id),
    FOREIGN KEY (material_id) REFERENCES constants(id)
);

CREATE INDEX idx_product_name ON products (name);

CREATE TABLE product_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantity INT,
    price INT,
    product_id INT,
    color_id INT,
    size_id INT,
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME,
    deleted_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (color_id) REFERENCES constants(id),
    FOREIGN KEY (size_id) REFERENCES constants(id)
);
CREATE TABLE product_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    url VARCHAR(300),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
CREATE TABLE shopping_carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    product_detail_id INT,
    quantity INT,
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME,
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (product_detail_id) REFERENCES product_details(id)
);
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    total_price DECIMAL,
    phone_number VARCHAR(20),
    address VARCHAR(500),
    status ENUM('WAIT', 'CONFIRM', 'REJECT', 'CANCEL', 'RECEIVED', 'DONE'),
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
CREATE TABLE order_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_detail_id INT,
    price INT,
    count INT,
    created_at DATETIME DEFAULT (now()),
    updated_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_detail_id) REFERENCES product_details(id)
);
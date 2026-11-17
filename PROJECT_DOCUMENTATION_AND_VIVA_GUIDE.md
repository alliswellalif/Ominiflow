# OmniFlow - Enterprise Intelligent Supply Chain & Cryptographically Verifiable Logistics ERP

> **Project Type:** Production-Grade Enterprise Web Application  
> **Backend Architecture:** Java 17/21/26 + Spring Boot 3.3.4 (Spring Data JPA, Spring Security 6, Thymeleaf, Actuator, HikariCP, ZXing QR Engine)  
> **Database:** MySQL 8.0 (with Zero-Config H2 In-Memory Fallback)  
> **Frontend:** Responsive HTML5, Bootstrap 5.3, Bootstrap Icons, FontAwesome, Chart.js, Custom CSS Dynamic Theming Engine  
> **Deployment Ready:** Docker, Docker Compose, Render.com, Railway.app, Heroku/AWS Procfile  

---

## 1. Executive Summary & Problem Solved
Traditional inventory and logistics management software typically suffers from three major flaws:
1. **Passive Stockouts:** Basic CRUD systems only record stock after it is already depleted, causing supply chain halts and lost revenue.
2. **Document Forgery & Tampering:** Paper or simple digital PDF invoices and delivery notes can be easily modified or faked without detection.
3. **Static, Inflexible Branding:** Changes to company logos, brand names, color schemes, or currencies require editing source code and redeploying the entire software.

**OmniFlow** provides an all-in-one, enterprise-grade solution combining:
- **Heuristic & AI Predictive Health Engine:** Calculates real-time daily burn rates, lead times, and optimal Economic Order Quantities (EOQ) before stock runs out.
- **Cryptographic SHA-256 Verification & Public QR Passes:** Every invoice and dispatch order generates a tamper-evident digital signature and scannable QR pass that can be validated publicly by anyone in real time without login.
- **Dynamic White-Label Brand & Theming Engine:** Allows administrators to modify application names, logos, color themes, and currencies on the fly from the database.

---

## 2. Standout Unique & Innovative Features

### Unique Feature 1: Predictive AI & Heuristic Reorder Matrix
Unlike simple CRUD apps that only notify when quantity $\le$ threshold:
- **Daily Velocity Analysis:** Continuously evaluates outbound sales and shipments to establish the item's daily consumption rate:
  $$\text{Daily Velocity} = \frac{\sum \text{Outbound Units}}{\Delta \text{Days}}$$
- **Days Until Stockout (Run-Out Date):**
  $$\text{Days Remaining} = \frac{\text{Current Stock Level}}{\text{Daily Velocity}}$$
- **Economic Order Quantity (EOQ):** Calculates the cost-optimal batch size to reorder from suppliers:
  $$EOQ = \sqrt{\frac{2 \cdot D \cdot S}{H}}$$
  where $D$ is Annual Demand, $S$ is Order Setup Cost, and $H$ is Holding Cost.
- **Urgency Scoring (0–100):** If days remaining are less than the supplier transit lead time, the item automatically spikes to **CRITICAL** (Urgency $\ge 90$) with an emergency order recommendation.
- **1-Click Automated Requisition:** Staff can click "Order EOQ" to instantly replenish stock.

### Unique Feature 2: Cryptographic Tamper-Proof SHA-256 Ledger & Public QR Pass
- When an order is placed or dispatched, a cryptographic SHA-256 digest is generated across its core payload:
  $$\text{Hash} = \text{SHA-256}(\text{OrderNumber} \parallel \text{TotalAmount} \parallel \text{CustomerEmail} \parallel \text{OrderDate} \parallel \text{SecretSalt})$$
- Generates a **2D QR Code image** embedded directly into printable official invoices and delivery challans.
- **Public Verification Endpoint (`/verify` and `/verify/{hash}`):** Anyone (auditors, delivery drivers, customers) can scan the QR code with their mobile phone or enter the hash to verify authenticity. If even 1 cent of the invoice is modified or tampered in the database, the system immediately flags:
  `WARNING: TAMPERING DETECTED / RECORD INTEGRITY COMPROMISED`.

### Unique Feature 3: Immutable Compliance Audit Trail & Live Node Telemetry
- **Audit Logging:** Every stock movement, price modification, order dispatch, status advance, and configuration change is logged into an immutable audit table capturing:
  - Actor Username & Granted Role
  - Real Client IP Address (supporting proxies and forwarders)
  - Action Classification (`CREATE`, `UPDATE`, `DISPATCH`, `STOCK_ADJUSTMENT`, `SETTINGS_UPDATE`)
  - Severity Level (`INFO`, `WARNING`, `CRITICAL`)
  - Snapshot Diff of the operation
- **Real-Time Telemetry Hub (`/telemetry`):** Visualizes JVM heap memory allocation, CPU thread count, HikariCP database pool health, and server uptime.

### Unique Feature 4: Live White-Label Branding & Theming Engine
- Accessible by Admins via `/settings`.
- Changes are persisted in MySQL and propagated globally across all views:
  - **App Title & Tagline:** Changes the software brand name across headers, footers, and page titles.
  - **Color Themes:** Switch between **Indigo Modern**, **Emerald Forest**, **Royal Violet**, **Crimson Ember**, and **Midnight Dark**.
  - **Custom Logo:** Switch between dynamic image URL or built-in modern SVG vector logo.
  - **Currency Symbol:** Live switch between `$`, `€`, `৳`, `£`, `₹`, etc.
  - **Company Profile:** Update registered address, hotline, and support email displayed on printable invoices.
  - **Maintenance Mode Banner:** Toggle system maintenance warnings with 1 click.

---

## 3. Technology Stack & Architecture

| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Language & SDK** | Java 17 / 21 / 26 (SE Runtime) | High-performance compiled object-oriented core |
| **Framework** | Spring Boot 3.3.4 | Dependency injection, MVC architecture, embedded Tomcat |
| **Security** | Spring Security 6 + BCrypt | RBAC (Admin, Manager, Staff), CSRF protection, secure session |
| **Persistence** | Spring Data JPA / Hibernate 6 | Automated ORM, query optimization, dynamic DDL |
| **Databases** | MySQL 8.0 (Primary) / H2 (Dev Fallback) | Relational SQL ledger with HikariCP connection pooling |
| **QR Generation**| Google ZXing 3.5.3 | Server-side cryptographic QR code image matrix generation |
| **View Engine** | Thymeleaf 3 + Spring Security Dialect | Server-side reactive HTML rendering |
| **Frontend UI** | Bootstrap 5.3 + Custom CSS Themes | Responsive mobile-friendly UI, CSS custom properties |
| **Analytics** | Chart.js 4.4 | Real-time interactive revenue and stock health charts |
| **Packaging** | Apache Maven 3.9.x | Build lifecycle, dependency resolution, executable JAR packaging |
| **Container** | Docker & Docker Compose | Multi-stage production containerization |

---

## 4. Default User Credentials (Pre-Seeded)

The system automatically initializes 3 role-tiered accounts upon first boot:

| Role | Username | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin` | `admin123` | Full system access, system settings, live theming, telemetry, audit logs, catalog deletion |
| **Logistics Manager** | `manager` | `manager123` | Order approval, stock adjustments, audit log viewing, product management |
| **Warehouse Staff** | `staff` | `staff123` | Order creation, packing, catalog browsing, stock intake |

> **Password Updates:** Users can update their display name, email, and password at any time via the `/profile` page. Passwords are salted and hashed using BCrypt.

---

## 5. How to Run the Application Locally

### Option A: Quick Standalone Dev Mode (Zero Configuration H2 Mode)
You can run the application immediately without configuring MySQL:
```powershell
cd C:\Users\HP\.gemini\antigravity\scratch\omniflow
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```
OR run the pre-built JAR:
```powershell
java -jar target/omniflow-platform-1.0.0.jar --spring.profiles.active=dev
```
Then open your browser at:
`http://localhost:8080`

### Option B: Running with MySQL 8.0
1. Open your MySQL client / MySQL Workbench and create the database (or let Spring Boot create it automatically):
   ```sql
   CREATE DATABASE IF NOT EXISTS omniflow_db;
   ```
2. Open `src/main/resources/application.properties` and enter your MySQL root password:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/omniflow_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
   ```
3. Run the application:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   OR run the JAR:
   ```powershell
   java -jar target/omniflow-platform-1.0.0.jar
   ```

---

## 6. How to Publish to a Live Public URL (Free Cloud Hosting)

### Method 1: Deploy to Render.com (Recommended - 100% Free)
1. Push this repository to GitHub.
2. Log in to [Render.com](https://render.com) and click **New +** -> **Web Service**.
3. Connect your GitHub repository.
4. Set:
   - **Environment:** `Docker`
   - **Plan:** `Free`
5. Click **Create Web Service**. Render will automatically detect the `Dockerfile`, compile the Spring Boot application, and generate a live URL (e.g. `https://omniflow-platform.onrender.com`).

### Method 2: Deploy to Railway.app
1. Install the Railway CLI or connect via GitHub on [Railway.app](https://railway.app).
2. Click **New Project** -> **Deploy from GitHub repo**.
3. Add a **MySQL** plugin service in Railway.
4. Railway will automatically link `DB_HOST`, `DB_PORT`, `DB_USER`, and `DB_PASSWORD` and provide a live public HTTPS domain.

---

## 7. Viva / Defense Preparation: Key Questions & Answers

### Q1: What problem does your project solve that other standard systems don't?
**Answer:** Most basic inventory projects are simple CRUD databases that only tell you what is already gone. OmniFlow solves the **Stockout Risk Problem** using a **Predictive AI Heuristic Matrix** that calculates daily velocity and days of inventory remaining against supplier lead times. Additionally, it solves **Document Fraud** using cryptographic SHA-256 digital signatures and real-time public QR code verification.

### Q2: How does the cryptographic verification work?
**Answer:** When an order is issued, our `VerificationService` calculates a SHA-256 hash using the order's immutable fields: `OrderNumber`, `TotalAmount`, `CustomerEmail`, `OrderDate`, and an internal server salt. This hash is encoded into a 2D QR Code. When anyone visits `/verify/{hash}`, the system fetches the database record, dynamically recalculates the hash from the live data, and compares it to the stored hash. If anyone altered the total amount in the database directly, the hashes mismatch and the system flags a tampering alert.

### Q3: How is dynamic theming implemented?
**Answer:** The themes are managed dynamically via a database table `system_settings` and cached in memory using `SystemSettingService`. A `@ControllerAdvice` bean (`GlobalModelAttributes`) automatically injects the primary theme class, app title, logo, and currency symbol into every Thymeleaf template. Changing the theme in `/settings` updates CSS variables in `custom.css` without requiring any code modification or server restarts.

### Q4: How is security and role-based access handled?
**Answer:** We implemented Spring Security 6 with `SecurityFilterChain` and `BCryptPasswordEncoder`. We enforce strict Role-Based Access Control (RBAC):
- `ROLE_ADMIN`: Can modify system settings, live themes, view telemetry, and delete catalog items.
- `ROLE_MANAGER`: Can approve/dispatch orders, adjust stock, and inspect audit logs.
- `ROLE_STAFF`: Can create orders and view catalog.
Every request passes through authentication providers and includes CSRF protection.


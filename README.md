# OmniFlow Enterprise Logistics & Supply Chain ERP

A production-grade, feature-rich Java & MySQL enterprise software solution equipped with predictive stockout AI heuristics, cryptographic SHA-256 tamper-evident verification, immutable compliance audit logging, and dynamic white-label branding.

---

## Quick Start (1-Minute Run)

### 1. Run in Zero-Config Dev Mode (H2 In-Memory)
```powershell
cd C:\Users\HP\.gemini\antigravity\scratch\omniflow
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```
Or execute the standalone production JAR:
```powershell
java -jar target/omniflow-platform-1.0.0.jar --spring.profiles.active=dev
```
Open browser at: **`http://localhost:8080`**

### 2. Run with MySQL 8.0
1. Create the database: `CREATE DATABASE omniflow_db;`
2. Update your password in `src/main/resources/application.properties`:
   `spring.datasource.password=YOUR_PASSWORD`
3. Run:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

---

## Default Login Credentials

- **Admin Account:** `admin` / `admin123` (Full control, settings, themes, telemetry)
- **Manager Account:** `manager` / `manager123` (Order approvals, audit logs, stock adjustments)
- **Staff Account:** `staff` / `staff123` (Orders & catalog operations)

*Users can also update their passwords and display names anytime at `/profile`.*

---

## Live URL Publishing
- **Render.com:** Uses included multi-stage `Dockerfile` and `render.yaml`.
- **Railway.app:** Uses included `railway.json`.
- **Docker Compose:** Run `docker-compose up -d` to launch both MySQL 8 and Spring Boot.

See `PROJECT_DOCUMENTATION_AND_VIVA_GUIDE.md` for complete viva defense notes and architecture documentation.


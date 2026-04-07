# Inventory Management Service

A Spring Boot REST application demonstrating **OAuth2**, **JWT authentication**, and **Role-Based Access Control (RBAC)** for educational purposes.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Features

- **JWT Token Authentication** - Stateless authentication using signed JWT tokens
- **OAuth2 Authorization Server** - Issues JWT tokens after user authentication  
- **RBAC (Role-Based Access Control)** - Three roles with different permissions
- **HTTPS with Self-Signed Certificate** - Secure communication setup
- **H2 In-Memory Database** - Zero-configuration database for development
- **Spring Data JPA** - Repository pattern for data access
- **Comprehensive Error Handling** - Centralized exception handling

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.2.4 | Application framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring OAuth2 Authorization Server | 1.x | Token issuance |
| JJWT | 0.12.5 | JWT token handling |
| H2 Database | 2.x | In-memory database |
| Spring Data JPA | 3.x | Data persistence |
| Lombok | 1.18.x | Boilerplate reduction |

## RBAC Roles

| Role | Permissions | Use Case |
|------|-------------|----------|
| **ADMIN** | Full CRUD access to all endpoints | System administrators |
| **OPERATOR** | Read materials, update quantities | Warehouse operators |
| **AUDITOR** | Read-only access | Compliance auditors |

## Prerequisites

- **Java 17** or higher
- **Git** (for cloning the repository)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/inventory-management.git
cd inventory-management
```

### 2. Generate SSL Certificate (Required for HTTPS)

The application uses HTTPS with a self-signed certificate. Generate it before running:

#### Option A: Using keytool (Included in JDK)

```bash
# Create SSL directory
mkdir -p ssl

# Generate self-signed certificate
keytool -genkeypair \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore ssl/keystore.p12 \
  -validity 3650 \
  -alias inventory \
  -storepass changeit \
  -keypass changeit \
  -dname "CN=localhost, OU=Education, O=Example, L=City, ST=State, C=US"
```

#### Option B: Windows (PowerShell)

```powershell
mkdir ssl
keytool -genkeypair -keyalg RSA -keysize 2048 -storetype PKCS12 `
  -keystore ssl/keystore.p12 -validity 3650 -alias inventory `
  -storepass changeit -keypass changeit `
  -dname "CN=localhost, OU=Education, O=Example, L=City, ST=State, C=US"
```

#### Certificate Parameters Explained

| Parameter | Description |
|-----------|-------------|
| `-keyalg RSA` | Algorithm for key generation (RSA is widely supported) |
| `-keysize 2048` | Key length in bits (2048 minimum recommended) |
| `-storetype PKCS12` | Keystore format (PKCS12 is standard) |
| `-keystore ssl/keystore.p12` | Output file path |
| `-validity 3650` | Certificate validity in days (10 years) |
| `-alias inventory` | Alias for the certificate entry |
| `-storepass` / `-keypass` | Passwords for keystore and key |
| `-dname` | Distinguished Name (certificate subject) |

> **Note:** The certificate is self-signed and will trigger browser warnings. This is expected for development/educational purposes. For production, use a CA-signed certificate (e.g., Let's Encrypt).

### 3. Run the Application

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

The application starts on **https://localhost:8080**

### 4. Default Users

| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| admin | password | ROLE_ADMIN | Full access |
| operator | password | ROLE_OPERATOR | Read + update quantities |
| auditor | password | ROLE_AUDITOR | Read-only |

> All passwords are BCrypt-hashed in the database.

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | Login and get JWT token | No |
| GET | `/api/auth/me` | Get current user info | Yes |

### Materials (Inventory)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/materials` | List all materials | ADMIN, OPERATOR, AUDITOR |
| GET | `/api/materials/{id}` | Get material by ID | ADMIN, OPERATOR, AUDITOR |
| GET | `/api/materials/sku/{sku}` | Get material by SKU | ADMIN, OPERATOR, AUDITOR |
| POST | `/api/materials` | Create new material | ADMIN, OPERATOR |
| PUT | `/api/materials/{id}` | Update material | ADMIN, OPERATOR |
| PATCH | `/api/materials/{id}/quantity` | Update quantity only | ADMIN, OPERATOR |
| DELETE | `/api/materials/{id}` | Delete material | ADMIN only |

### Admin

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| GET | `/api/admin/stats` | System statistics | ADMIN only |

## Usage Examples

### 1. Login to Get JWT Token

```bash
curl -k -X POST https://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "admin"
}
```

### 2. Access Protected Endpoint

```bash
curl -k -X GET https://localhost:8080/api/materials \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Create a Material (ADMIN or OPERATOR)

```bash
curl -k -X POST https://localhost:8080/api/materials \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "MAT-006",
    "name": "New Material",
    "description": "A new inventory item",
    "quantity": 100,
    "unit": "pieces"
  }'
```

### 4. Update Quantity (OPERATOR Role)

```bash
curl -k -X PATCH https://localhost:8080/api/materials/1/quantity \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 500,
    "reason": "Stock replenishment from warehouse"
  }'
```

### 5. Test RBAC - Access Denied Example

```bash
# Login as auditor (read-only role)
curl -k -X POST https://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"auditor","password":"password"}'

# Try to delete a material (should fail with 403 Forbidden)
curl -k -X DELETE https://localhost:8080/api/materials/1 \
  -H "Authorization: Bearer AUDITOR_TOKEN_HERE"
```

## H2 Database Console

Access the H2 console for debugging:

```
URL: https://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:inventorydb
Username: sa
Password: (leave empty)
```

> Note: Accept the browser security warning for the self-signed certificate.

## Project Structure

```
src/main/java/com/example/inventory/
├── InventoryManagementApplication.java  # Main entry point
├── config/
│   ├── SecurityConfig.java              # RBAC security rules
│   └── OAuth2AuthorizationServerConfig.java
├── controller/
│   ├── AuthController.java              # Authentication endpoints
│   ├── MaterialController.java          # Inventory CRUD
│   ├── AdminController.java             # Admin-only endpoints
│   └── GlobalExceptionHandler.java      # Error handling
├── dto/                                  # Data Transfer Objects
├── entity/                               # JPA Entities
├── repository/                           # Spring Data Repositories
├── security/
│   ├── JwtTokenProvider.java            # JWT generation/validation
│   ├── CustomUserDetailsService.java    # User loading
│   └── JwtAuthenticationFilter.java     # Request filtering
└── service/
    └── DataInitializer.java             # Seed data on startup
```

## Security Architecture

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Client    │────▶│  JwtAuthFilter   │────▶│  SecurityConfig │
└─────────────┘     └──────────────────┘     └─────────────────┘
       │                      │                        │
       │ 1. POST /login       │                        │
       │─────────────────────▶│                        │
       │                      │                        │
       │ 2. JWT Token         │                        │
       │◀─────────────────────│                        │
       │                      │                        │
       │ 3. Request + Token   │                        │
       │─────────────────────▶│ Validate Token         │
       │                      │───────────────────────▶│ Check RBAC Rules
       │                      │                        │
       │ 4. Protected Response│                        │
       │◀─────────────────────│────────────────────────│
```

## Configuration

Key configuration in `application.properties`:

```properties
# Server
server.port=8080
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=changeit

# JWT Settings
app.jwt.secret=mySecretKeyForJwtTokenGeneration...
app.jwt.expiration=3600000

# H2 Database
spring.datasource.url=jdbc:h2:mem:inventorydb
spring.jpa.hibernate.ddl-auto=create-drop
```

## Files NOT to Commit to GitHub

The `.gitignore` file excludes these sensitive files:

| File/Pattern | Reason |
|--------------|--------|
| `ssl/keystore.p12` | Contains private key - security risk |
| `src/main/resources/keystore.p12` | Copy of private key |
| `application-local.properties` | May contain secrets |
| `*.secret`, `*.key` | Sensitive configuration |
| `.env` files | Environment secrets |
| `build/`, `.gradle/` | Build artifacts |

> **Important:** Always run `git status` before committing to ensure no sensitive files are included.

## Production Considerations

This is an **educational example**. For production:

1. **SSL Certificate**: Use a CA-signed certificate (e.g., Let's Encrypt)
2. **JWT Secrets**: Store in secure vault (HashiCorp Vault, AWS Secrets Manager)
3. **Database**: Use persistent database (PostgreSQL, MySQL) instead of H2
4. **Password Policy**: Implement stronger password requirements
5. **Token Refresh**: Add refresh token mechanism
6. **Rate Limiting**: Add request rate limiting
7. **CORS**: Configure proper CORS policies
8. **Logging**: Sanitize logs to avoid leaking sensitive data
9. **HTTPS Redirect**: Force HTTP to HTTPS redirect
10. **Security Headers**: Add HSTS, CSP, X-Frame-Options

## Troubleshooting

### Certificate Issues

```bash
# Regenerate certificate if needed
rm ssl/keystore.p12
keytool -genkeypair -keyalg RSA -keysize 2048 -storetype PKCS12 \
  -keystore ssl/keystore.p12 -validity 3650 -alias inventory \
  -storepass changeit -dname "CN=localhost, OU=Education, O=Example, L=City, ST=State, C=US"
```

### Port Already in Use

```bash
# Change port in application.properties
server.port=8081
```

### Build Errors

```bash
# Clean and rebuild
gradlew.bat clean build

# Or on Linux/Mac
./gradlew clean build
```

## License

MIT License - See LICENSE file for details.

## Author

Educational project for learning Spring Security, OAuth2, and JWT.

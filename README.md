# Shopping API - Spring Boot Backend

Một Web API hoàn chỉnh được phát triển bằng Spring Boot 4.0.6 và Java 21 để hỗ trợ ứng dụng Android shopping được viết bằng Kotlin + Jetpack Compose với Retrofit.

## 🎯 Tính Năng Chính

### 1. **Authentication & Authorization**
- Đăng ký (Register) và Đăng nhập (Login)
- JWT Token-based authentication
- Hỗ trợ Role-based access control (User, Admin)
- Password encryption với BCrypt

### 2. **Product Management**
- Xem danh sách sản phẩm (có phân trang)
- Tìm kiếm sản phẩm
- Lọc sản phẩm theo danh mục
- Xem chi tiết sản phẩm
- Quản lý sản phẩm (Create, Update, Delete - Admin only)

### 3. **Category Management**
- Xem danh sách danh mục
- Tạo, cập nhật, xóa danh mục

### 4. **Shopping Cart**
- Thêm sản phẩm vào giỏ hàng
- Xem giỏ hàng
- Cập nhật số lượng sản phẩm
- Xóa sản phẩm khỏi giỏ hàng
- Xóa hết giỏ hàng

### 5. **Order Management**
- Tạo đơn hàng từ giỏ hàng
- Xem danh sách đơn hàng
- Xem chi tiết đơn hàng
- Cập nhật trạng thái đơn hàng (Admin only)

### 6. **Review System**
- Thêm đánh giá/nhận xét về sản phẩm
- Xem đánh giá của sản phẩm
- Xóa đánh giá (Admin only)

## 📂 Cấu Trúc Thư Mục

```
springboot-backend/
├── src/main/java/backend/
│   ├── Application.java                    # Main application entry point
│   ├── config/
│   │   ├── JwtAuthenticationFilter.java   # JWT filter cho Spring Security
│   │   ├── SecurityConfig.java            # Security configuration
│   │   └── WebConfig.java                 # CORS configuration
│   ├── controller/
│   │   ├── AuthController.java            # Authentication endpoints
│   │   ├── ProductController.java         # Product endpoints
│   │   ├── CategoryController.java        # Category endpoints
│   │   ├── CartController.java            # Shopping cart endpoints
│   │   ├── OrderController.java           # Order endpoints
│   │   └── ReviewController.java          # Review endpoints
│   ├── entity/
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Cart.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── Review.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── CartRepository.java
│   │   ├── OrderRepository.java
│   │   ├── OrderItemRepository.java
│   │   └── ReviewRepository.java
│   ├── services/                          # Service interfaces
│   │   ├── AuthService.java
│   │   ├── ProductService.java
│   │   ├── CategoryService.java
│   │   ├── CartService.java
│   │   ├── OrderService.java
│   │   └── ReviewService.java
│   ├── service/impl/                      # Service implementations
│   │   ├── AuthServiceImpl.java
│   │   ├── ProductServiceImpl.java
│   │   ├── CategoryServiceImpl.java
│   │   ├── CartServiceImpl.java
│   │   ├── OrderServiceImpl.java
│   │   └── ReviewServiceImpl.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── ProductRequest.java
│   │   │   └── CartRequest.java
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── ProductResponse.java
│   │       ├── OrderResponse.java
│   │       └── ApiResponse.java
│   ├── exception/
│   │   ├── ResourceNotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   └── until/
│       └── JwtTokenProvider.java          # JWT utility
├── src/main/resources/
│   └── application.properties             # Configuration file
├── pom.xml                                # Maven dependencies
└── database.sql                           # Database schema
```

## 🚀 Getting Started

### Prerequisites
- **Java 21+** 
- **MySQL 8.0+**
- **Maven 3.6+**
- **Git** (optional)

### 1. Setup Database

```bash
# Kết nối với MySQL
mysql -u root -p

# Chạy file database.sql
mysql -u root -p < database.sql
```

### 2. Configure Application

Edit file `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/finalterm?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password

# Server Configuration
server.port=8080
spring.application.name=shopping-api

# JWT Configuration
app.jwtSecret=mySecretKeyForJWTAuthenticationTokenGenerationAndValidationShoppingAPI
app.jwtExpirationInMs=86400000
```

### 3. Build Project

```bash
cd springboot-backend
mvn clean install
```

### 4. Run Application

```bash
mvn spring-boot:run
```

Server sẽ chạy trên: `http://localhost:8080`

## 🔌 API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Đăng ký
- `POST /api/v1/auth/login` - Đăng nhập

### Products
- `GET /api/v1/products` - Lấy danh sách sản phẩm
- `GET /api/v1/products/{id}` - Lấy chi tiết sản phẩm
- `GET /api/v1/products/category/{categoryId}` - Lấy sản phẩm theo danh mục
- `GET /api/v1/products/search?keyword=...` - Tìm kiếm sản phẩm
- `POST /api/v1/products` - Tạo sản phẩm (Admin)
- `PUT /api/v1/products/{id}` - Cập nhật sản phẩm (Admin)
- `DELETE /api/v1/products/{id}` - Xóa sản phẩm (Admin)

### Categories
- `GET /api/v1/categories` - Lấy danh sách danh mục
- `GET /api/v1/categories/{id}` - Lấy chi tiết danh mục
- `POST /api/v1/categories` - Tạo danh mục (Admin)
- `PUT /api/v1/categories/{id}` - Cập nhật danh mục (Admin)
- `DELETE /api/v1/categories/{id}` - Xóa danh mục (Admin)

### Cart
- `POST /api/v1/cart/{userId}/add` - Thêm vào giỏ hàng
- `GET /api/v1/cart/{userId}` - Xem giỏ hàng
- `PUT /api/v1/cart/{userId}/update` - Cập nhật giỏ hàng
- `DELETE /api/v1/cart/{userId}/remove` - Xóa khỏi giỏ hàng
- `DELETE /api/v1/cart/{userId}/clear` - Xóa hết giỏ hàng

### Orders
- `POST /api/v1/orders/{userId}/create` - Tạo đơn hàng
- `GET /api/v1/orders/{id}` - Lấy chi tiết đơn hàng
- `GET /api/v1/orders/user/{userId}` - Lấy danh sách đơn hàng
- `PUT /api/v1/orders/{id}/status` - Cập nhật trạng thái (Admin)

### Reviews
- `POST /api/v1/reviews` - Thêm đánh giá
- `GET /api/v1/reviews/product/{productId}` - Lấy đánh giá sản phẩm
- `DELETE /api/v1/reviews/{id}` - Xóa đánh giá (Admin)

📖 **Chi tiết API:** Xem file [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 🔐 Authentication

API sử dụng JWT (JSON Web Tokens) cho authentication. Sau khi login, bạn sẽ nhận được token:

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjg0Mzk2Mjk4LCJleHAiOjE2ODQ0ODI2OTh9...."
}
```

Sử dụng token này trong header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 🏗️ Architecture

### Layers
1. **Controller Layer** - Xử lý HTTP requests và responses
2. **Service Layer** - Chứa business logic
3. **Repository Layer** - Tương tác với database
4. **Entity Layer** - Định nghĩa model dữ liệu

### Design Patterns
- **DTO Pattern** - Tách biệt data transfer objects khỏi entities
- **Service Layer Pattern** - Tập trung business logic
- **Repository Pattern** - Trừu tượng hóa data access
- **JWT Authentication** - Bảo mật API

## 📦 Dependencies

Key dependencies:
- `Spring Boot 4.0.6` - Framework
- `Spring Security` - Authentication & Authorization
- `Spring Data JPA` - ORM
- `JWT (JJWT)` - Token generation & validation
- `MySQL Connector` - Database driver
- `Lombok` - Reduce boilerplate code
- `Validation` - Input validation

## 🔄 Database Schema

### Tables
- **users** - Người dùng
- **categories** - Danh mục sản phẩm
- **products** - Sản phẩm
- **cart** - Giỏ hàng
- **orders** - Đơn hàng
- **order_items** - Chi tiết đơn hàng
- **reviews** - Đánh giá sản phẩm

Mối quan hệ:
```
users (1) ──→ (N) orders
users (1) ──→ (N) cart
users (1) ──→ (N) reviews
categories (1) ──→ (N) products
products (1) ──→ (N) cart
products (1) ──→ (N) orders (through order_items)
products (1) ──→ (N) reviews
orders (1) ──→ (N) order_items
```

## 📱 Android Integration

### Retrofit Setup Example

```kotlin
// Create API Service
interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>
    
    @GET("products")
    suspend fun getProducts(@Query("page") page: Int, @Query("size") size: Int): Response<ApiResponse<PagedResponse<ProductResponse>>>
    
    @POST("cart/{userId}/add")
    suspend fun addToCart(@Path("userId") userId: Int, @Body request: CartRequest): Response<ApiResponse<Unit>>
}

// Create Retrofit Instance
object RetrofitClient {
    private const val BASE_URL = "http://your-server-ip:8080/api/v1/"
    
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// Use in ViewModel
viewModelScope.launch {
    val response = RetrofitClient.apiService.getProducts(0, 10)
    if (response.isSuccessful) {
        val products = response.body()?.data?.content
    }
}
```

## 🧪 Testing API

### Using cURL

```bash
# Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "phone": "0901234567",
    "address": "123 Test Street"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# Get Products
curl -X GET http://localhost:8080/api/v1/products?page=0&size=10

# Add to Cart (with token)
curl -X POST http://localhost:8080/api/v1/cart/1/add \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### Using Postman
1. Import endpoints từ API_DOCUMENTATION.md
2. Set base URL: `http://localhost:8080/api/v1`
3. Thêm Authorization token vào Postman Environment
4. Test các endpoints

## 🐛 Troubleshooting

### 1. Database Connection Error
```
Error: Cannot connect to database
```
**Solution:** 
- Kiểm tra MySQL đang chạy
- Kiểm tra thông tin connection trong application.properties
- Kiểm tra database 'finalterm' đã được tạo

### 2. Port Already in Use
```
Error: Address already in use
```
**Solution:**
- Đổi port trong application.properties: `server.port=8081`
- Hoặc kill process đang sử dụng port 8080

### 3. JWT Token Invalid
```
Error: Token validation failed
```
**Solution:**
- Kiểm tra token được gửi đúng format: `Bearer {token}`
- Kiểm tra token chưa hết hạn
- Kiểm tra `app.jwtSecret` trong application.properties

## 📝 Sample Data

Database đã được khởi tạo với sample data:
- **Users:** 2 users + 1 admin
- **Categories:** 5 danh mục
- **Products:** 8 sản phẩm
- **Orders:** 2 đơn hàng
- **Reviews:** 10 đánh giá

Credentials:
```
Email: user1@example.com
Password: password (hashed)

Email: admin@example.com
Password: password (hashed)
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Guide](https://jwt.io/)
- [Retrofit Documentation](https://square.github.io/retrofit/)

## 📄 License

Dự án này được tạo cho mục đích học tập.

## 👤 Author

Tạo bởi: Your Name
Ngày tạo: 2026-05-18

---

**Happy Coding! 🚀**

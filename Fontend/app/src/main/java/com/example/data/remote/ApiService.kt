package com.example.data.remote

import com.example.data.dto.*
import com.example.data.dto.request.LoginRequest
import com.example.data.dto.request.RegisterRequest
import com.example.data.dto.request.UpdateProfileRequest
import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.AuthResponse
import com.example.data.dto.response.CartItemResponse
import com.example.data.dto.response.CategoryResponse
import com.example.data.dto.response.NotificationResponse
import com.example.data.dto.response.OrderResponse
import com.example.data.dto.response.PaginatedProductResponse
import com.example.data.dto.response.ProductResponse
import com.example.data.dto.response.ReviewResponse
import com.example.data.dto.response.UserResponse
import retrofit2.Response // ✅ Chắc chắn có import này để bọc phản hồi mạng
import retrofit2.http.*

interface ApiService {

    // --- AUTHENTICATION ---
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    // --- PRODUCTS ---

    @GET("api/v1/products")
    suspend fun getProducts(): Response<ApiResponse<PaginatedProductResponse>>

    @GET("api/v1/products/{id}")
    suspend fun getProductDetail(
        @Path("id") id: Long
    ): Response<ApiResponse<ProductResponse>>
    @GET("api/v1/products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 15
    ): Response<ApiResponse<PaginatedProductResponse>>

    //  (CATEGORIES)
    @GET("api/v1/categories")
    suspend fun getAllCategories(): Response<ApiResponse<List<CategoryResponse>>>


    @GET("api/v1/products/category/{categoryId}")
    suspend fun getProductsByCategoryId(
        @Path("categoryId") categoryId: Long
    ): Response<ApiResponse<PaginatedProductResponse>>

    // --- CART ---
    @GET("api/v1/cart/{userId}")
    suspend fun getCartItems(@Path("userId") userId: Long): Response<ApiResponse<List<CartItemResponse>>>

    @POST("api/v1/cart/{userId}/add")
    suspend fun addToCart(
        @Path("userId") userId: Long,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<Void>>


    @PUT("api/v1/cart/{userId}/update")
    suspend fun updateCartQuantity(
        @Path("userId") userId: Long,
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int
    ): Response<ApiResponse<Void>>

    @DELETE("api/v1/cart/{userId}/remove")
    suspend fun deleteCartItem(
        @Path("userId") userId: Long,
        @Query("productId") productId: Long
    ): Response<ApiResponse<Void>>

    // --- ORDERS ---
    @POST("api/v1/orders/{userId}/place")
    suspend fun placeOrder(
        @Path("userId") userId: Long,
        @Body request: com.example.data.dto.request.OrderRequest
    ): Response<ApiResponse<OrderResponse>>

    @GET("api/orders/user/{userId}")
    suspend fun getUserOrders(
        @Path("userId") userId: Long,
        @Query("status") status: String
    ): Response<ApiResponse<List<OrderResponse>>>

    // chi tiết đơn đã đặt hàng
    @GET("api/orders/{id}")
    suspend fun getOrderById(
        @Path("id") id: Long
    ): Response<ApiResponse<OrderResponse>>

    @PUT("api/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") orderId: Long,
        @Query("status") status: String
    ): Response<ApiResponse<Void>>

    @GET("api/v1/orders/user/{userId}")
    suspend fun getMyOrders(@Path("userId") userId: Long): Response<ApiResponse<List<OrderResponse>>>

    // --- REVIEWS ---
    @GET("api/reviews/product/{productId}")
    suspend fun getProductReviews(
        @Path("productId") productId: Long
    ): Response<ApiResponse<List<ReviewResponse>>>

    @POST("api/reviews/add")
    suspend fun addReview(
        @Body review: com.example.data.dto.request.ReviewRequest
    ): Response<ApiResponse<ReviewResponse>>

    // --- USER PROFILE ---
    @GET("api/v1/users/{id}")
    suspend fun getUserProfile(
        @Path("id") userId: Long
    ): Response<ApiResponse<UserResponse>>

    @PUT("api/v1/users/{id}")
    suspend fun updateUserProfile(
        @Path("id") userId: Long,
        @Body updateRequest: UpdateProfileRequest
    ): Response<ApiResponse<Void>>

    // --- NOTIFICATIONS ---
    @GET("api/v1/notifications/user/{userId}")
    suspend fun getUserNotifications(
        @Path("userId") userId: Long
    ): Response<ApiResponse<List<NotificationResponse>>>

    @PUT("api/v1/notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") notificationId: Long
    ): Response<ApiResponse<Void>>
}
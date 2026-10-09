package com.example.ui.features.product

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dto.response.CategoryResponse
import com.example.data.dto.response.ProductResponse
import com.example.data.dto.response.ReviewResponse
import com.example.data.repository.ProductRepository
import com.example.data.repository.ReviewRepository
import kotlinx.coroutines.launch

class ProductViewModel(
    application: Application,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : AndroidViewModel(application) {

    private val _productDetail = mutableStateOf<ProductResponse?>(null)
    val productDetail: State<ProductResponse?> = _productDetail

    private val _isDetailLoading = mutableStateOf(false)
    val isDetailLoading: State<Boolean> = _isDetailLoading

    private val _detailError = mutableStateOf<String?>(null)
    val detailError: State<String?> = _detailError

    private val _reviewsList = mutableStateOf<List<ReviewResponse>>(emptyList())
    val reviewsList: State<List<ReviewResponse>> = _reviewsList

    private val _isReviewsLoading = mutableStateOf(false)
    val isReviewsLoading: State<Boolean> = _isReviewsLoading
    var selectedFilterRating = mutableStateOf(0)

    // =========================================================================
    // 🟢 THÊM MỚI: CÁC BIẾN QUẢN LÝ TRẠNG THÁI DANH MỤC (CATEGORIES)
    // =========================================================================
    private val _categoriesList = mutableStateOf<List<CategoryResponse>>(emptyList())
    val categoriesList: State<List<CategoryResponse>> = _categoriesList

    private val _isCategoriesLoading = mutableStateOf(false)
    val isCategoriesLoading: State<Boolean> = _isCategoriesLoading

    // State lưu giữ ID danh mục đang click (Mặc định ban đầu là 0L tương đương nút "Tất cả")
    private val _selectedCategoryId = mutableStateOf<Long>(0L)
    val selectedCategoryId: State<Long> = _selectedCategoryId

    /**
     * Tải danh sách toàn bộ danh mục từ MySQL Backend về máy
     */
    fun fetchCategories() {
        if (_isCategoriesLoading.value) return

        viewModelScope.launch {
            _isCategoriesLoading.value = true
            try {
                val response = productRepository.getAllCategories()
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        _categoriesList.value = apiResponse.data ?: emptyList()
                    }
                } else {
                    android.util.Log.e("API_ERROR", "Không thể lấy danh mục: ${response.message()}")
                }
            } catch (e: Exception) {
                // In mã lỗi ra Logcat để dễ dàng theo dõi nếu đường truyền mạng có vấn đề
                android.util.Log.e("API_EXCEPTION", "Lỗi kết nối danh mục: ${e.localizedMessage}")
                e.printStackTrace()
            } finally {
                _isCategoriesLoading.value = false
            }
        }
    }

    /**
     * Xử lý hành động khi người dùng nhấp chọn vào nút vòng tròn Category
     */
    fun selectCategory(id: Long) {
        _selectedCategoryId.value = id
        // 💡 Gợi ý: Khi bạn làm tiếp tính năng lọc sản phẩm, bạn chỉ cần gọi thêm hàm
        // fetchProductsByCategory(id) ngay tại đây để cập nhật danh sách sản phẩm trang chủ!
    }

    // --- PRODUCT DETAILS ---
    fun fetchProductDetail(productId: Long) {
        viewModelScope.launch {
            _isDetailLoading.value = true
            _detailError.value = null
            try {
                val response = productRepository.getProductById(productId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        _productDetail.value = apiResponse.data
                    } else {
                        _detailError.value = apiResponse.message ?: "Không thể tải chi tiết sản phẩm"
                    }
                } else {
                    _detailError.value = "Lỗi hệ thống: Phản hồi không hợp lệ"
                }
            } catch (e: Exception) {
                _detailError.value = "Không thể kết nối đến Server. Vui lòng kiểm tra mạng!"
            } finally {
                _isDetailLoading.value = false
            }
        }
    }

    // --- REVIEWS ---
    fun loadReviews(productId: Long) {
        viewModelScope.launch {
            _isReviewsLoading.value = true
            try {
                val response = reviewRepository.getProductReviews(productId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        _reviewsList.value = apiResponse.data ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isReviewsLoading.value = false
            }
        }
    }

    fun setFilterRating(rating: Int) {
        selectedFilterRating.value = rating
    }

    // Hàm gửi bình luận mới
    fun sendComment(productId: Long, userId: Long, name: String, stars: Int, text: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val request = com.example.data.dto.request.ReviewRequest(
                    productId = productId,
                    userId = userId,
                    userName = name,
                    rating = stars,
                    comment = text
                )
                val response = reviewRepository.addReview(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    loadReviews(productId)
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
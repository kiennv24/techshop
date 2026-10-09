package com.example.ui.features.home

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dto.response.ProductResponse
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.getApiService(application)

    private val _productsList = mutableStateOf<List<ProductResponse>>(emptyList())
    val productsList: State<List<ProductResponse>> = _productsList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private var loaded = false

    fun fetchProductsIfNeeded() {
        if (loaded) return
        loaded = true
        fetchProducts(categoryId = 0L) // Mặc định ban đầu load tất cả (0L)
    }

    // =========================================================================
    // 🟢 ĐÃ CẬP NHẬT: Thêm tham số categoryId để truyền bộ lọc lên Spring Boot API
    // =========================================================================
    fun fetchProducts(categoryId: Long = 0L) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = if (categoryId == 0L) {
                    apiService.getProducts() // API lấy toàn bộ sản phẩm mặc định của bạn
                } else {
                    apiService.getProductsByCategoryId(categoryId)
                }

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.success == true) {
                        _productsList.value = apiResponse.data?.content ?: emptyList()
                    } else {
                        _errorMessage.value = apiResponse.message ?: "Không thể tải danh sách sản phẩm"
                    }
                } else {
                    _errorMessage.value = "Không thể kết nối đến máy chủ hệ thống"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = e.localizedMessage ?: "Lỗi kết nối mạng cục bộ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================================================================
    // 🟢 THÊM MỚI HOÀN CHỈNH: Hàm gọi API tìm kiếm sản phẩm phân trang theo từ khóa
    // =========================================================================
    fun searchProducts(keyword: String) {
        if (keyword.isBlank()) {
            // Nếu ô tìm kiếm trống, tự động tải lại toàn bộ sản phẩm mặc định (Tất cả - 0L)
            fetchProducts(0L)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Gọi API searchProducts từ apiService đã fix đồng bộ Response ở bước trước
                val response = apiService.searchProducts(keyword = keyword, page = 0, size = 20)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.success == true) {
                        // Đổ danh sách mảng nội dung (.content) tìm được vào lưới sản phẩm hiển thị
                        _productsList.value = apiResponse.data?.content ?: emptyList()
                    } else {
                        _productsList.value = emptyList()
                        _errorMessage.value = apiResponse.message ?: "Không tìm thấy sản phẩm phù hợp"
                    }
                } else {
                    _productsList.value = emptyList()
                    _errorMessage.value = "Không thể kết nối kết quả tìm kiếm từ máy chủ"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _productsList.value = emptyList()
                _errorMessage.value = e.localizedMessage ?: "Lỗi kết nối mạng khi tìm kiếm"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
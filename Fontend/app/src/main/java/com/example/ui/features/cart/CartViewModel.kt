package com.example.ui.features.cart

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dto.response.CartItemResponse
import com.example.data.repository.CartRepository
import com.example.untils.TokenManager
import kotlinx.coroutines.launch

class CartViewModel(
    application: Application,
    private val repository: CartRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val tokenManager = TokenManager(context)

    var cartItems = mutableStateOf<List<CartItemResponse>>(emptyList())
        private set
    var isLoading = mutableStateOf(false)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    var buyNowItem = mutableStateOf<CartItemResponse?>(null)
        private set

    /**
     * Tải danh sách sản phẩm trong giỏ hàng từ Server về
     */
    fun loadCart() {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            errorMessage.value = "Bạn cần đăng nhập để xem giỏ hàng"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = repository.getCart(userId)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        cartItems.value = apiResponse.data ?: emptyList()
                    } else {
                        errorMessage.value = apiResponse.message ?: "Không thể tải giỏ hàng"
                    }
                } else {
                    errorMessage.value = "Lỗi kết nối Server mạng"
                }
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage ?: "Không thể tải giỏ hàng"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng - ĐÃ TỐI ƯU MƯỢT MÀ
     */
    fun updateQuantity(productId: Long, quantity: Int) {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            errorMessage.value = "Bạn cần đăng nhập để cập nhật giỏ hàng"
            return
        }

        // 1. Cập nhật cục bộ trên bộ nhớ RAM bằng cách so sánh ".productId"
        val originalList = cartItems.value
        cartItems.value = originalList.map { item ->
            // 🟢 ĐÃ ĐỒNG BỘ: Dùng item.productId để khớp với key { it.productId } bên giao diện UI
            if (item.productId == productId) item.copy(quantity = quantity) else item
        }

        viewModelScope.launch {
            try {
                val response = repository.updateQuantity(userId, productId, quantity)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success != true) {
                        // Nếu server báo lỗi, hoàn tác (rollback) dữ liệu cũ tránh lệch dòng tiền
                        cartItems.value = originalList
                        errorMessage.value = apiResponse.message ?: "Không thể cập nhật số lượng"
                    }
                } else {
                    cartItems.value = originalList
                    errorMessage.value = "Lỗi kết nối mạng khi cập nhật"
                }
            } catch (e: Exception) {
                cartItems.value = originalList
                errorMessage.value = e.localizedMessage ?: "Không thể cập nhật số lượng"
            }
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng (Không reload trang)
     */
    fun removeItem(productId: Long) {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            errorMessage.value = "Bạn cần đăng nhập để thao tác giỏ hàng"
            return
        }

        // 1. Lọc bỏ ngay lập tức item bị xóa trên RAM để card biến mất mượt mà
        val originalList = cartItems.value
        // 🟢 ĐÃ ĐỒNG BỘ: Sử dụng it.productId để lọc chính xác thẻ cần xóa
        cartItems.value = originalList.filter { it.productId != productId }

        viewModelScope.launch {
            try {
                val response = repository.deleteItem(userId, productId)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success != true) {
                        // Nếu server xóa thất bại, nạp lại danh sách cũ
                        cartItems.value = originalList
                        errorMessage.value = apiResponse.message ?: "Không thể xóa sản phẩm"
                    }
                } else {
                    cartItems.value = originalList
                    errorMessage.value = "Lỗi kết nối mạng khi xóa sản phẩm"
                }
            } catch (e: Exception) {
                cartItems.value = originalList
                errorMessage.value = e.localizedMessage ?: "Không thể xóa sản phẩm"
            }
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    fun addToCart(productId: Long, quantity: Int) {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            errorMessage.value = "Vui lòng đăng nhập để thêm vào giỏ hàng"
            return
        }

        viewModelScope.launch {
            try {
                val requestBody = mapOf(
                    "productId" to productId,
                    "quantity" to quantity
                )

                val response = repository.addToCart(userId, requestBody)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        loadCart() // Khi thêm mới từ trang chi tiết thì vẫn gọi load để đồng bộ số lượng tổng
                    } else {
                        errorMessage.value = apiResponse.message ?: "Không thể thêm vào giỏ hàng"
                    }
                } else {
                    errorMessage.value = "Server từ chối yêu cầu (Lỗi: ${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage ?: "Lỗi kết nối mạng khi thêm giỏ hàng"
            }
        }
    }

    fun setBuyNowItem(productId: Long, productName: String?, productImage: String?, price: Double?) {
        buyNowItem.value = CartItemResponse(
            id = 0L,
            productId = productId,
            productName = productName ?: "",
            productImage = productImage ?: "",
            price = price ?: 0.0,
            quantity = 1
        )
    }

    fun clearBuyNowItem() {
        buyNowItem.value = null
    }
}
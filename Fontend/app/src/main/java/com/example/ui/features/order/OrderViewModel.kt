package com.example.ui.features.order

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dto.response.CartItemResponse
import com.example.data.dto.response.OrderResponse
import com.example.data.repository.OrderRepository
import com.example.untils.TokenManager
import kotlinx.coroutines.launch

class OrderViewModel(
    application: Application,
    private val orderRepository: OrderRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val tokenManager = TokenManager(context)

    var orderList = mutableStateOf<List<OrderResponse>>(emptyList())
    var isLoading = mutableStateOf(false)
    var errorMsg = mutableStateOf<String?>(null)

    var address = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var paymentMethod = mutableStateOf("COD")
    var orderStatusMsg = mutableStateOf<String?>(null)
    var checkoutSuccess = mutableStateOf(false)

    // 🟢 THÊM STATE: Lưu trữ thông tin chi tiết của một đơn hàng cụ thể
    var orderDetail = mutableStateOf<OrderResponse?>(null)
        private set

    /**
     * Lấy danh sách lịch sử đơn hàng của tôi PHÂN LOẠI THEO TAB TRẠNG THÁI
     * tabIndex: 0 -> "pending", 1 -> "delivered", 2 -> "cancelled"
     */
    fun fetchOrders(tabIndex: Int) {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            errorMsg.value = "Bạn cần đăng nhập để xem lịch sử mua hàng"
            return
        }

        // 🟢 CẢI TIẾN: Bộ chặn chống lặp vô hạn gây Skipped Frames (Nghẽn Main Thread)
        if (isLoading.value) return

        // Ánh xạ vị trí bấm Tab sang chuỗi chữ thường khớp chính xác với MySQL Database Backend
        val status = when (tabIndex) {
            0 -> "pending"
            1 -> "delivered"
            2 -> "cancelled"
            else -> "pending"
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMsg.value = null
            try {
                val response = orderRepository.getUserOrders(userId, status)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        orderList.value = apiResponse.data ?: emptyList()
                    } else {
                        errorMsg.value = apiResponse.message ?: "Không thể tải danh sách đơn hàng"
                    }
                } else {
                    errorMsg.value = "Không thể kết nối đến máy chủ cổng mạng"
                }
            } catch (e: Exception) {
                errorMsg.value = e.localizedMessage ?: "Lỗi kết nối hệ thống"
                orderList.value = emptyList() // Tránh treo giao diện khi lỗi mạng
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 🟢 ĐÃ FIX LỖI: Hàm xử lý tác vụ hủy đơn hàng từ phía Client App
     */
    fun cancelOrder(orderId: Long) {
        isLoading.value = true
        errorMsg.value = null

        viewModelScope.launch {
            try {
                val response = orderRepository.updateOrderStatus(orderId, "cancelled")

                if (response.isSuccessful) {
                    isLoading.value = false
                    loadOrderDetail(orderId)
                } else {
                    isLoading.value = false
                    errorMsg.value = "Không thể hủy đơn hàng vào lúc này"
                }
            } catch (e: Exception) {
                isLoading.value = false
                errorMsg.value = "Lỗi kết nối: ${e.localizedMessage}"
            }
        }
    }

    /**
     * 🟢 THÊM HÀM: Tải thông tin chi tiết của một đơn hàng dựa trên ID
     */
    fun loadOrderDetail(orderId: Long) {
        if (isLoading.value) return

        viewModelScope.launch {
            isLoading.value = true
            errorMsg.value = null
            try {
                val response = orderRepository.getOrderById(orderId)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        orderDetail.value = apiResponse.data
                    } else {
                        errorMsg.value = apiResponse.message ?: "Không thể tải chi tiết đơn hàng"
                    }
                } else {
                    errorMsg.value = "Lỗi kết nối đến máy chủ"
                }
            } catch (e: Exception) {
                errorMsg.value = e.localizedMessage ?: "Sự cố kết nối mạng"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Tiến hành đặt hàng thành công
     */
    fun placeOrder(cartItems: List<CartItemResponse>) {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            orderStatusMsg.value = "Vui lòng đăng nhập để tiến hành đặt hàng"
            return
        }
        if (address.value.isBlank()) {
            orderStatusMsg.value = "Vui lòng nhập địa chỉ giao hàng"
            return
        }
        if (phoneNumber.value.isBlank()) {
            orderStatusMsg.value = "Vui lòng cập nhật số điện thoại tài khoản"
            return
        }
        if (cartItems.isEmpty()) {
            orderStatusMsg.value = "Giỏ hàng trống, không có sản phẩm để thanh toán"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            orderStatusMsg.value = null
            try {
                val requestItems = cartItems.map { responseItem ->
                    com.example.data.dto.request.CartItemRequest(
                        productId = responseItem.productId,
                        quantity = responseItem.quantity
                    )
                }

                val response = orderRepository.placeOrder(
                    userId = userId,
                    shippingAddress = address.value.trim(),
                    phoneNumber = phoneNumber.value.trim(),
                    paymentMethod = paymentMethod.value,
                    cartItems = requestItems
                )

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    orderStatusMsg.value = apiResponse.message

                    if (apiResponse.success == true) {
                        checkoutSuccess.value = true
                    }
                } else {
                    orderStatusMsg.value = "Đặt hàng thất bại. Phản hồi mạng không hợp lệ!"
                }
            } catch (e: Exception) {
                orderStatusMsg.value = e.localizedMessage ?: "Đặt hàng thất bại do sự cố mạng"
            } finally {
                isLoading.value = false
            }
        }
    }

    // 🟢 THÊM HÀM: Dọn dẹp dữ liệu chi tiết khi người dùng quay lại màn hình trước
    fun clearOrderDetail() {
        orderDetail.value = null
    }

    // 🟢 THÊM HÀM: Xóa bộ nhớ tạm danh sách đơn hàng khi người dùng chuyển Tab
    fun clearOrdersBeforeTabChange() {
        orderList.value = emptyList()
    }

    fun resetCheckoutState() {
        checkoutSuccess.value = false
        orderStatusMsg.value = null
    }
}
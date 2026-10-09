package com.example.ui.features.notification

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dto.response.NotificationResponse
import com.example.data.repository.NotificationRepository
import com.example.untils.TokenManager
import kotlinx.coroutines.launch

class NotificationViewModel(
    application: Application,
    private val repository: NotificationRepository
) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)

    var notificationList = mutableStateOf<List<NotificationResponse>>(emptyList())
    var isLoading = mutableStateOf(false)
    var errorMsg = mutableStateOf<String?>(null)

    // Tải danh sách thông báo mới nhất
    fun loadNotifications() {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            errorMsg.value = "Vui lòng đăng nhập để kiểm tra thông báo"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMsg.value = null
            try {
                val response = repository.getUserNotifications(userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true) {
                        notificationList.value = apiResponse.data ?: emptyList()
                    } else {
                        errorMsg.value = apiResponse.message
                    }
                } else {
                    errorMsg.value = "Không thể tải dữ liệu từ máy chủ mạng"
                }
            } catch (e: Exception) {
                errorMsg.value = e.localizedMessage ?: "Lỗi kết nối mạng hệ thống"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Đánh dấu đã xem thông báo
    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.markNotificationAsRead(notificationId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Cập nhật trạng thái hiển thị cục bộ ngay trên bộ nhớ UI mà không cần fetch lại toàn bộ danh sách
                    notificationList.value = notificationList.value.map {
                        if (it.id == notificationId) it.copy(isRead = true) else it
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
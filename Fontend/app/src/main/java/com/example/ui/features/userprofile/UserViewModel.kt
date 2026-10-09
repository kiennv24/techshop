package com.example.ui.features.userprofile

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dto.request.UpdateProfileRequest
import com.example.data.dto.response.UserResponse
import com.example.data.repository.ProfileRepository
import com.example.untils.TokenManager
import kotlinx.coroutines.launch

class UserViewModel(
    application: Application,
    private val profileRepository: ProfileRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val tokenManager = TokenManager(context)

    // Các biến trạng thái đồng bộ dữ liệu với UI Form
    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var phone = mutableStateOf("")
    var address = mutableStateOf("")

    // ✅ THÊM MỚI BIẾN NÀY: Quản lý trạng thái URL ảnh đại diện trên Form chỉnh sửa
    var avatar = mutableStateOf("")

    var isLoading = mutableStateOf(false)
    var isSaving = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var saveSuccessMsg = mutableStateOf<String?>(null)

    var currentUserName = mutableStateOf("Khách")

    var userProfile = mutableStateOf<UserResponse?>(null)
        private set

    /**
     * Tải thông tin hồ sơ cá nhân của người dùng hiển thị lên UI
     */
    fun loadUserProfile() {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            currentUserName.value = "Khách"
            userProfile.value = null
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val response = profileRepository.getUserProfile(userId)

                // BÓC TÁCH LỚP VỎ RETROFIT RESPONSE TẠI ĐÂY
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.success == true && apiResponse.data != null) {
                        val user = apiResponse.data

                        // Gán dữ liệu thô vào các ô nhập Form trên giao diện
                        name.value = user.name ?: ""
                        email.value = user.email ?: ""
                        phone.value = user.phone ?: ""
                        address.value = user.address ?: ""

                        // ✅ CẬP NHẬT THÊM: Đổ link ảnh hiện tại vào ô xử lý form
                        avatar.value = user.avatar ?: ""

                        currentUserName.value = user.name ?: "Người dùng"

                        // Đồng bộ Object dữ liệu về State để giao diện Cá Nhân hiển thị
                        userProfile.value = user
                    } else {
                        errorMessage.value =
                            apiResponse.message ?: "Không thể tải thông tin tài khoản"
                    }
                } else {
                    errorMessage.value = "Không thể kết nối đến máy chủ hệ thống"
                }
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage ?: "Lỗi kết nối mạng cục bộ"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Gửi yêu cầu cập nhật thay đổi thông tin cá nhân lên Server
     */
    fun updateUserProfile(onSuccess: () -> Unit) {
        val userId = tokenManager.getUserId()
        if (userId <= 0) {
            errorMessage.value = "Vui lòng đăng nhập để thực hiện"
            return
        }
        if (name.value.isBlank()) {
            errorMessage.value = "Họ và tên không được để trống"
            return
        }

        val updateRequest = UpdateProfileRequest(
            name = name.value.trim(),
            phone = phone.value.trim(),
            address = address.value.trim(),
            avatar = avatar.value.trim(),
            currentPassword = null,
            newPassword = null
        )

        viewModelScope.launch {
            isSaving.value = true
            errorMessage.value = null
            saveSuccessMsg.value = null
            try {
                val response = profileRepository.updateProfile(userId, updateRequest)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.success == true) {
                        saveSuccessMsg.value =
                            apiResponse.message ?: "Cập nhật thông tin thành công!"
                        currentUserName.value = name.value.trim()

                        userProfile.value = userProfile.value?.copy(
                            name = name.value.trim(),
                            phone = phone.value.trim(),
                            address = address.value.trim(),
                            avatar = avatar.value.trim()
                        )

                        // ✅ THÊM DÒNG NÀY: Kích hoạt callback thông báo cho giao diện quay về trang trước
                        onSuccess()

                    } else {
                        errorMessage.value = apiResponse.message ?: "Cập nhật thất bại"
                    }
                } else {
                    errorMessage.value = "Lỗi phản hồi mạng khi lưu dữ liệu!"
                }
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage ?: "Lỗi kết nối khi lưu dữ liệu"
            } finally {
                isSaving.value = false
            }
        }
    }
}
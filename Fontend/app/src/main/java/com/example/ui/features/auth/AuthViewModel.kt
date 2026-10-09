package com.example.ui.features.auth

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.dto.request.LoginRequest
import com.example.data.dto.request.RegisterRequest
import com.example.data.repository.AuthRepository
import com.example.untils.TokenManager
import kotlinx.coroutines.launch

// ✅ HAI DÒNG IMPORT NÀY LÀ BẮT BUỘC ĐỂ DIỆT LỖI ĐỎ .onSuccess VÀ .onFailure
import kotlin.Result
import kotlin.runCatching

class AuthViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val tokenManager by lazy { TokenManager(context) }

    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var loginSuccess = mutableStateOf(false)
    var registerSuccess = mutableStateOf(false)

    fun login(email: String, javaPasswordTxt: String) {
        val loginRequest = LoginRequest(email = email, password = javaPasswordTxt)

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = authRepository.login(loginRequest)

            result.onSuccess { apiResponse ->
                if (apiResponse.success && apiResponse.data != null) {
                    val authData = apiResponse.data
                    tokenManager.saveToken(authData.token)
                    tokenManager.saveUserId(authData.id)
                    loginSuccess.value = true
                } else {
                    errorMessage.value = apiResponse.message ?: "Đăng nhập thất bại"
                }
            }.onFailure { exception ->
                errorMessage.value = exception.message
            }
            isLoading.value = false
        }
    }

    fun register(name: String, email: String, phone: String, password: String, address: String) {
        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            phone = phone,
            password = password,
            address = address.trim()
        )

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            authRepository.register(registerRequest)
                .onSuccess { apiResponse ->
                    if (apiResponse.success == true) {
                        registerSuccess.value = true
                        errorMessage.value = apiResponse.message ?: "Đăng ký thành công! Vui lòng đăng nhập."
                    } else {
                        errorMessage.value = apiResponse.message ?: "Đăng ký thất bại"
                    }
                }
                .onFailure { exception ->
                    errorMessage.value = exception.message ?: "Có lỗi xảy ra, vui lòng thử lại"
                }

            isLoading.value = false
        }
    }

    fun setError(message: String) {
        errorMessage.value = message
    }

    fun resetLoginState() {
        loginSuccess.value = false
    }

    fun resetRegisterState() {
        registerSuccess.value = false
    }
}
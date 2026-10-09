package com.example.data.repository

import com.example.data.dto.request.LoginRequest
import com.example.data.dto.request.RegisterRequest // ✅ Đã thêm import cho Đăng ký
import com.example.data.dto.response.ApiResponse
import com.example.data.dto.response.AuthResponse
import com.example.data.remote.ApiService
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException

class AuthRepository(private val apiService: ApiService) {


    suspend fun login(loginRequest: LoginRequest): Result<ApiResponse<AuthResponse>> {
        return try {
            val response = apiService.login(loginRequest)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseErrorMessage(e)))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Kết nối timeout - vui lòng kiểm tra kết nối mạng"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Không thể kết nối tới server - vui lòng kiểm tra URL"))
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi hệ thống: ${e.localizedMessage}"))
        }
    }


    suspend fun register(registerRequest: RegisterRequest): Result<ApiResponse<AuthResponse>> {
        return try {
            val response = apiService.register(registerRequest)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseErrorMessage(e)))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Kết nối mạng quá chậm, vui lòng kiểm tra lại mạng"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Không thể kết nối tới server - vui lòng kiểm tra URL"))
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi hệ thống đăng ký: ${e.localizedMessage}"))
        }
    }

    /**
     * Hàm phụ trợ dùng chung để bóc tách key "message" thông minh từ Backend Spring Boot trả về
     */
    private fun parseErrorMessage(e: HttpException): String {
        val errorBody = e.response()?.errorBody()?.string()
        val backendMessage = try {
            val json = Gson().fromJson(errorBody, JsonObject::class.java)
            if (json != null && json.has("message")) json.get("message").asString else null
        } catch (_: Exception) {
            null
        }

        return backendMessage ?: when (e.code()) {
            401 -> "Sai email hoặc mật khẩu"
            404 -> "Không tìm thấy tài khoản"
            400 -> "Tài khoản hoặc thông tin đăng ký đã tồn tại trong hệ thống"
            else -> "Thao tác thất bại (${e.code()})"
        }
    }
}
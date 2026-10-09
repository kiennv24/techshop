package com.example.ui.features.auth

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.remote.RetrofitClient
import com.example.data.repository.AuthRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: AuthViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val authRepository = AuthRepository(apiService)
            AuthViewModelFactory(application, authRepository)
        }
    )

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value
    val registerSuccess = viewModel.registerSuccess.value

    // ✅ TỐI ƯU LUỒNG ĐIỀU HƯỚNG: Hiện Toast thành công công khai trước khi rút màn hình về Login
    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            Toast.makeText(context, "🎉 Đăng ký tài khoản mới thành công!", Toast.LENGTH_LONG).show()
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
            viewModel.resetRegisterState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        PurpleLightBg.copy(alpha = 0.4f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tech",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = PurpleMain,
                        fontSize = 32.sp,
                        letterSpacing = (-1).sp
                    )
                )
                Text(
                    text = "Shop",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Light,
                        color = Color.Black,
                        fontSize = 32.sp,
                        letterSpacing = (-1).sp
                    )
                )
            }

            Text(
                text = "TẠO TÀI KHOẢN MỚI",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextGrayDark,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // --- HỌ VÀ TÊN ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ và Tên") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (name.isNotEmpty()) PurpleMain else TextGrayDark.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleMain,
                    focusedLabelColor = PurpleMain,
                    unfocusedContainerColor = BgGrayLight,
                    focusedContainerColor = BgGrayLight
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- EMAIL ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Địa chỉ Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = if (email.isNotEmpty()) PurpleMain else TextGrayDark.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleMain,
                    focusedLabelColor = PurpleMain,
                    unfocusedContainerColor = BgGrayLight,
                    focusedContainerColor = BgGrayLight
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- SỐ ĐIỆN THOẠI ---
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số Điện Thoại") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = if (phone.isNotEmpty()) PurpleMain else TextGrayDark.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleMain,
                    focusedLabelColor = PurpleMain,
                    unfocusedContainerColor = BgGrayLight,
                    focusedContainerColor = BgGrayLight
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- MẬT KHẨU ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật Khẩu") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (password.isNotEmpty()) PurpleMain else TextGrayDark.copy(alpha = 0.6f)
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleMain,
                    focusedLabelColor = PurpleMain,
                    unfocusedContainerColor = BgGrayLight,
                    focusedContainerColor = BgGrayLight
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- ĐỊA CHỈ ---
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Địa Chỉ ") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = if (address.isNotEmpty()) PurpleMain else TextGrayDark.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleMain,
                    focusedLabelColor = PurpleMain,
                    unfocusedContainerColor = BgGrayLight,
                    focusedContainerColor = BgGrayLight
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- LOADING / BUTTONS XỬ LÝ ---
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PurpleMain, strokeWidth = 3.5.dp)
                }
            } else {
                Button(
                    onClick = {
                        val emailError = when {
                            email.isBlank() -> "Vui lòng nhập email"
                            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email không đúng định dạng"
                            else -> null
                        }

                        val validationError = when {
                            name.isBlank() -> "Vui lòng nhập họ và tên"
                            emailError != null -> emailError
                            password.isBlank() -> "Vui lòng nhập mật khẩu"
                            password.length < 6 -> "Mật khẩu phải chứa ít nhất 6 ký tự"
                            phone.isBlank() -> "Vui lòng nhập số điện thoại"
                            address.isBlank() -> "Vui lòng cập nhật địa chỉ mặc định"
                            else -> null
                        }

                        if (validationError != null) {
                            viewModel.setError(validationError)
                        } else {
                            viewModel.register(name.trim(), email.trim(), phone.trim(), password, address.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleMain),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Đăng Ký",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate("login") }
                ) {
                    Text(
                        text = "Đã có tài khoản? Đăng nhập ngay",
                        color = PurpleMain,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // --- KHỐI ĐỎ BÁO LỖI HỆ THỐNG / TRÙNG EMAIL ---
            errorMessage?.let {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (registerSuccess) PurpleLightBg else Color(0xFFFDE8E8)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it,
                            // ✅ ĐÃ SỬA: Ép chữ màu đỏ sẫm để dễ quan sát công khai thay vì màu mờ cũ
                            color = if (registerSuccess) PurpleMain else Color(0xFFE53E3E),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
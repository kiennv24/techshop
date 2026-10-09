package com.example.ui.features.userprofile

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.remote.RetrofitClient
import com.example.data.repository.ProfileRepository
import com.example.ui.theme.BgGrayLight
import com.example.ui.theme.PurpleLightBg
import com.example.ui.theme.PurpleMain
import com.example.untils.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileUserScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val tokenManager = remember { TokenManager(context) }

    // 1. Khởi tạo UserViewModel thông qua Factory sạch sẽ
    val userViewModel: UserViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val profileRepository = ProfileRepository(apiService)
            UserViewModelFactory(application, profileRepository)
        }
    )

    // 2. Lấy dữ liệu Realtime từ ViewModel
    val currentUserName by userViewModel.currentUserName
    val currentUserProfile by userViewModel.userProfile

    // Tự động bảo ViewModel nạp dữ liệu Profile mới nhất khi mở màn hình Cá Nhân lên
    LaunchedEffect(Unit) {
        userViewModel.loadUserProfile()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cá Nhân", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 👤 3. HIỂN THỊ ẢNH ĐẠI DIỆN REALTIME
            val avatarUrl = currentUserProfile?.avatar
            if (avatarUrl != null && avatarUrl.isNotBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier // ✅ ĐÃ SỬA: Chuỗi liên kết hàm viết liền dòng chuẩn cú pháp Jetpack Compose
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(PurpleLightBg),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(PurpleLightBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        tint = PurpleMain,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✍️ 4. HIỂN THỊ TÊN NGƯỜI DÙNG REALTIME
            Text(
                text = if (currentUserName.isNotEmpty()) currentUserName else "Đang tải...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ==================== KHỐI 1: QUẢN LÝ TÀI KHOẢN ====================
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Quản lí tài khoản", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(10.dp))
            ProfileMenuRow(
                icon = Icons.Default.AccountCircle,
                title = "Thông tin cá nhân",
                onClick = { navController.navigate("personalinfo") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ==================== KHỐI 2: QUẢN LÝ ĐƠN HÀNG ====================
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Quản lí đơn hàng", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgGrayLight)
            ) {
                Column {
                    ProfileMenuInsideCard(icon = Icons.Default.ShoppingCart, title = "Đơn Hàng", onClick = { navController.navigate("ordermanagement") })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    ProfileMenuInsideCard(icon = Icons.Default.List, title = "Lịch sử mua hàng", onClick = { navController.navigate("order_history") })
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ==================== KHỐI 3: ĐĂNG XUẤT TÀI KHOẢN ====================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Xóa sạch Token và UserId để bảo mật dữ liệu đăng nhập cũ
                        tokenManager.clearSession()
                        Toast.makeText(context, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show()

                        // Xóa sạch toàn bộ lịch sử stack điều hướng chuẩn Jetpack Compose
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgGrayLight)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFC62828))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đăng xuất tài khoản", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    }
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgGrayLight)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = PurpleMain)
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun ProfileMenuInsideCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = PurpleMain)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}
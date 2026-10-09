package com.example.ui.features.order

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.remote.RetrofitClient
import com.example.data.repository.OrderRepository
import com.example.ui.components.LoadingView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: OrderViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val orderRepository = OrderRepository(apiService)
            OrderViewModelFactory(application, orderRepository)
        }
    )
    val orderList by viewModel.orderList
    val isLoading by viewModel.isLoading
    val errorMsg by viewModel.errorMsg

    // 🟢 ĐÃ TỐI ƯU: Giải phóng kho lưu trữ đệm cũ trước khi gọi nạp dữ liệu real từ MySQL,
    // đảm bảo dữ liệu mới nhất luôn được cập nhật mà không bị dính cơ chế chặn đơ nghẽn.
    LaunchedEffect(Unit) {
        viewModel.resetCheckoutState() // Đảm bảo dọn dẹp các thông báo cũ (nếu có)
        viewModel.orderList.value = emptyList() // Ép xóa danh sách đệm để load mới hoàn toàn
        viewModel.fetchOrders(0) // Nạp đơn hàng mặc định lên màn hình
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Lịch Sử Đơn Hàng",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black // Ép cứng màu đen tránh lỗi lóa chữ khi bật Dark Mode hệ thống
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA)) // Thêm màu nền xám nhạt để các Card trắng nổi bật hơn
        ) {
            when {
                isLoading -> {
                    LoadingView()
                }

                errorMsg != null -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = errorMsg!!,
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                orderList.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Bạn chưa có đơn hàng nào!",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(orderList) { order ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White), // Đổi sang nền trắng sạch sẽ
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Mã Đơn: #${order.id}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.Black
                                        )

                                        val currentStatus = order.status ?: "pending"
                                        val isDelivered = currentStatus.lowercase() == "delivered"
                                        val badgeBgColor = if (isDelivered) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                        val badgeTextColor = if (isDelivered) Color(0xFF2E7D32) else Color(0xFFE65100)

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(badgeBgColor)
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = currentStatus.uppercase(),
                                                color = badgeTextColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Xử lý chuỗi ISO ngày tháng của Java LocalDateTime gửi sang
                                    Text(
                                        text = "Ngày Đặt: ${order.createdAt?.replace("T", " ")?.substringBefore(".") ?: "Chưa rõ"}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )

                                    Text(
                                        text = "Địa Chỉ: ${order.shippingAddress ?: "Chưa có"}",
                                        fontSize = 14.sp,
                                        color = Color.Black.copy(alpha = 0.8f),
                                        maxLines = 1
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        color = Color.LightGray.copy(alpha = 0.4f)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Tổng thanh toán:",
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${String.format("%,.0f", order.totalPrice)} đ",
                                            color = PurpleMain,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.ui.features.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.components.LoadingView
import com.example.ui.theme.PurpleMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: Long,
    orderViewModel: OrderViewModel
) {
    // Quan sát trạng thái từ ViewModel
    val order by orderViewModel.orderDetail
    val isLoading by orderViewModel.isLoading
    val errorMsg by orderViewModel.errorMsg

    // 🟢 Tự động tải dữ liệu chi tiết khi mở màn hình
    LaunchedEffect(orderId) {
        orderViewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA), // Nền xám nhạt sang trọng
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chi Tiết Đơn Hàng", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        orderViewModel.clearOrderDetail() // Dọn dẹp dữ liệu khi quay về
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
        ) {
            when {
                isLoading -> {
                    LoadingView() // Component loading xoay tròn của bạn
                }
                errorMsg != null -> {
                    Text(
                        text = errorMsg!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                order != null -> {
                    val currentOrder = order!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- 1. THẺ TRẠNG THÁI ĐƠN HÀNG ---
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Mã đơn hàng", fontSize = 12.sp, color = Color.Gray)
                                        Text("#${currentOrder.id}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                                    }

                                    val statusStr = currentOrder.status ?: "pending"

                                    // 🟢 ĐÃ CẬP NHẬT LOGIC MÀU SẮC: Thêm màu đỏ/hồng nhạt cho trạng thái ĐÃ HỦY (cancelled)
                                    val badgeBgColor = when (statusStr.lowercase()) {
                                        "delivered" -> Color(0xFFE8F5E9)  // Xanh lá nhạt
                                        "cancelled" -> Color(0xFFFFEBEE)  // Đỏ hồng nhạt
                                        else -> Color(0xFFFFF3E0)         // Cam nhạt (pending)
                                    }

                                    val badgeTextColor = when (statusStr.lowercase()) {
                                        "delivered" -> Color(0xFF2E7D32)  // Xanh lá sẫm
                                        "cancelled" -> Color(0xFFC62828)  // Đỏ sẫm
                                        else -> Color(0xFFFF9800)         // Cam sẫm (pending)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(badgeBgColor)
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = when(statusStr.lowercase()) {
                                                "pending" -> "CHỜ XÁC NHẬN"
                                                "delivered" -> "ĐÃ GIAO"
                                                "cancelled" -> "ĐÃ HỦY"
                                                else -> statusStr.uppercase()
                                            },
                                            color = badgeTextColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        // --- 2. THÔNG TIN GIAO HÀNG & THANH TOÁN ---
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PurpleMain, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Thông tin nhận hàng", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    DetailInfoRow("Địa chỉ", currentOrder.shippingAddress ?: "N/A")
                                    DetailInfoRow("Thanh toán", currentOrder.paymentMethod ?: "COD")
                                    DetailInfoRow("Ngày đặt", currentOrder.createdAt?.replace("T", " ")?.substringBefore(".") ?: "N/A")
                                }
                            }
                        }

                        // --- 3. DANH SÁCH SẢN PHẨM TRONG ĐƠN ---
                        item {
                            Text("Sản phẩm đã mua", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(start = 4.dp))
                        }

                        items(currentOrder.items ?: emptyList()) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val fullImageUrl = item.productImage ?: ""

                                    coil.compose.AsyncImage(
                                        model = if (fullImageUrl.isNotEmpty()) fullImageUrl else "https://via.placeholder.com/150",
                                        contentDescription = "Ảnh sản phẩm",
                                        modifier = Modifier
                                            .size(55.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF0F0F0)),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                        error = painterResource(id = android.R.drawable.ic_menu_report_image)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.productName ?: "Sản phẩm", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, maxLines = 1)
                                        Text("Số lượng: x${item.quantity}", fontSize = 12.sp, color = Color.Gray)
                                    }

                                    Text(
                                        text = "${String.format("%,.0f", item.price)} đ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        // --- 4. TỔNG KẾT TIỀN ---
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PurpleMain.copy(alpha = 0.05f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Tổng thanh toán", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                                    Text(
                                        text = "${String.format("%,.0f", currentOrder.totalPrice)} đ",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = PurpleMain
                                    )
                                }
                            }
                        }

                        // =========================================================================
                        // 🟢 ĐÃ THÊM MỚI: NÚT HỦY ĐƠN HÀNG (Chỉ hiển thị khi trạng thái là PENDING)
                        // =========================================================================
                        val currentStatus = currentOrder.status ?: "pending"
                        if (currentStatus.lowercase() == "pending") {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        // Gửi mã đơn hàng lên ViewModel để gọi API cập nhật trạng thái hủy
                                        orderViewModel.cancelOrder(currentOrder.id)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFD32F2F), // Màu đỏ đậm bắt mắt chuyên cho tác vụ hủy đơn
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        text = "HỦY ĐƠN HÀNG",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        } else {
                            // Tạo khoảng cách chân trang nếu không có nút Hủy đơn hàng
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
    }
}
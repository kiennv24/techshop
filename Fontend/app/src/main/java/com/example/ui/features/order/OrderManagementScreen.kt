package com.example.ui.features.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.dto.response.OrderResponse
import com.example.ui.theme.PurpleMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(
    navController: NavController,
    orderViewModel: OrderViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Chờ xác nhận", "Đã giao", "Đã hủy")

    val orders by orderViewModel.orderList
    val isLoading by orderViewModel.isLoading

    // 🟢 CỐT LÕI: Tự động gọi API tải dữ liệu real từ MySQL khi người dùng đổi Tab
    LaunchedEffect(selectedTabIndex) {
        orderViewModel.fetchOrders(selectedTabIndex)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text("Quản lý đơn hàng", fontWeight = FontWeight.Bold, color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- 🅰️ THANH ĐIỀU HƯỚNG TAB ROW ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = PurpleMain,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = PurpleMain
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            // 🟢 ĐÃ TỐI ƯU: Giải phóng danh sách cũ trước khi đổi tab để tránh xung đột dữ liệu đệm gây đơ luồng
                            orderViewModel.clearOrdersBeforeTabChange()
                            selectedTabIndex = index
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (selectedTabIndex == index) PurpleMain else Color.Gray
                            )
                        }
                    )
                }
            }

            // --- 🅱️ NỘI DUNG HIỂN THỊ DANH SÁCH ĐƠN HÀNG BIẾN ĐỘNG ---
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PurpleMain
                    )
                } else if (orders.isEmpty()) {
                    Text(
                        text = "Không có đơn hàng nào trong mục này.",
                        color = Color.Gray,
                        fontSize = 15.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(orders) { order ->
                            // 🟢 ĐÃ ĐỒNG BỘ: Truyền navController vào item để thực hiện chuyển hướng màn hình chi tiết
                            OrderCardItem(order = order, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(order: OrderResponse, navController: NavController) {
    // Thuật toán bóc tách chuỗi hiển thị tên từ danh sách sản phẩm lồng nhau
    val firstItemName = order.items?.firstOrNull()?.productName ?: "Sản phẩm hệ thống"
    val displayTitle = if ((order.items?.size ?: 0) > 1) {
        "$firstItemName và ${(order.items?.size ?: 1) - 1} sản phẩm khác"
    } else {
        firstItemName
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PurpleMain,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = displayTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }

                // Nhãn hiển thị trạng thái đơn hàng góc trên
                val currentStatus = order.status ?: "pending"
                val isDelivered = currentStatus.lowercase() == "delivered"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDelivered) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when(currentStatus.lowercase()) {
                            "pending" -> "Chờ xác nhận"
                            "delivered" -> "Đã giao"
                            "cancelled" -> "Đã hủy"
                            else -> currentStatus
                        },
                        color = if (isDelivered) Color(0xFF2E7D32) else Color(0xFFFF9800),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tổng thanh toán", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "${String.format("%,.0f", order.totalPrice)} đ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleMain
                    )
                }

                Button(
                    onClick = {
                        navController.navigate("order_detail/${order.id}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.7f))
                ) {
                    Text("Chi tiết", color = Color.DarkGray, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
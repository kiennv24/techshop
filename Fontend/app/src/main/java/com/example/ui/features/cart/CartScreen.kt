package com.example.ui.features.cart

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ui.theme.PurpleMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel
) {
    val context = LocalContext.current

    val cartItems by viewModel.cartItems
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    // Bộ quản lý danh sách ID các sản phẩm được người dùng tích chọn
    var selectedItemIds by remember { mutableStateOf(setOf<Long>()) }

    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }

    // Tự động tích chọn tất cả sản phẩm khi giỏ hàng vừa được tải xong lần đầu
    LaunchedEffect(cartItems) {
        if (cartItems.isNotEmpty() && selectedItemIds.isEmpty()) {
            selectedItemIds = cartItems.map { it.productId }.toSet()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ Hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PurpleMain)
            } else if (cartItems.isEmpty()) {
                Text(
                    text = "Giỏ hàng của bạn đang trống",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        // Khóa cố định luồng render theo từng ID sản phẩm giúp cuộn siêu mượt
                        items(
                            items = cartItems,
                            key = { it.productId }
                        ) { item ->
                            val isChecked = selectedItemIds.contains(item.productId)

                            CartItemRow(
                                item = item,
                                isChecked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedItemIds = if (checked) {
                                        selectedItemIds + item.productId
                                    } else {
                                        selectedItemIds - item.productId
                                    }
                                },
                                onQuantityChanged = { newQty ->
                                    viewModel.updateQuantity(item.productId, newQty)
                                },
                                onRemoveClick = {
                                    viewModel.removeItem(item.productId)
                                }
                            )
                        }
                    }

                    // --- KHỐI TÍNH TỔNG TIỀN & ĐIỀU HƯỚNG ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tổng cộng:", fontSize = 16.sp, color = Color.Gray)

                                // Tính tổng tiền tự động nhảy số theo các sản phẩm đang được tích chọn
                                val totalAmount = cartItems
                                    .filter { selectedItemIds.contains(it.productId) }
                                    .sumOf { (it.price ?: 0.0) * (it.quantity ?: 1) }

                                Text(
                                    text = "${String.format("%,.0f", totalAmount)} đ",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleMain
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (selectedItemIds.isEmpty()) {
                                        Toast.makeText(context, "Vui lòng tích chọn ít nhất 1 sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        navController.navigate("checkout")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
                            ) {
                                Text(" THANH TOÁN", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🟢 COMPONENT CÔ LẬP TRẠNG THÁI: Tối ưu hóa phản hồi tăng/giảm và cân đối layout
@Composable
fun CartItemRow(
    item: com.example.data.dto.response.CartItemResponse,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    onRemoveClick: () -> Unit
) {
    // Ép kiểu dữ liệu an toàn để ép buộc LaunchedEffect hoặc remember nhận diện sự thay đổi trạng thái
    val currentQty = item.quantity ?: 1
    var localQuantity by remember(currentQty) { mutableStateOf(currentQty) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Ảnh sản phẩm bo góc
            AsyncImage(
                model = item.productImage ?: "https://via.placeholder.com/80",
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Khu vực thông tin Tên, Giá, và Nút bấm tăng giảm số lượng
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName ?: "Sản phẩm",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Hiển thị tổng giá tiền động nhảy số theo localQuantity cực mượt
                val displayPrice = (item.price ?: 0.0) * localQuantity
                Text(
                    text = "${String.format("%,.0f", displayPrice)} đ",
                    fontWeight = FontWeight.SemiBold,
                    color = PurpleMain
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Bộ điều khiển số lượng
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color(0xFFE0E0E0)), RoundedCornerShape(8.dp))
                        .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
                        .height(30.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (localQuantity > 1) {
                                localQuantity--
                                onQuantityChanged(localQuantity)
                            } else {
                                onRemoveClick()
                            }
                        },
                        modifier = Modifier.width(32.dp).fillMaxHeight()
                    ) {
                        Text("-", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .border(BorderStroke(0.5.dp, Color(0xFFE0E0E0)))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$localQuantity", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    }

                    IconButton(
                        onClick = {
                            localQuantity++
                            onQuantityChanged(localQuantity)
                        },
                        modifier = Modifier.width(32.dp).fillMaxHeight()
                    ) {
                        Text("+", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                    }
                }
            }

            // 3. Khối điều khiển bên phải (Cân đối lại bố cục bằng chiều cao cố định để giao diện cân xứng)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(80.dp)
                    .padding(start = 4.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = PurpleMain,
                        uncheckedColor = Color.Gray
                    )
                )

                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
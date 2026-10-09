package com.example.ui.features.order

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.remote.RetrofitClient
import com.example.data.repository.OrderRepository
import com.example.data.repository.ProfileRepository
import com.example.ui.features.cart.CartViewModel
import com.example.ui.features.cart.CartItemRow // Gọi component dùng chung
import com.example.ui.theme.BgGrayLight
import com.example.ui.theme.PurpleLightBg
import com.example.ui.theme.PurpleMain
import com.example.ui.theme.TextGrayDark
import com.example.untils.TokenManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val orderRepository = remember { OrderRepository(RetrofitClient.getApiService(context)) }
    val profileRepository = remember { ProfileRepository(RetrofitClient.getApiService(context)) }

    // Nhận diện chế độ "Mua ngay" hay mua từ Giỏ hàng
    val isBuyNowMode = cartViewModel.buyNowItem.value != null

    // Đồng bộ nguồn danh sách hiển thị
    val cartItems = if (isBuyNowMode) {
        remember(cartViewModel.buyNowItem.value) {
            listOfNotNull(cartViewModel.buyNowItem.value)
        }
    } else {
        cartViewModel.cartItems.value
    }

    var address by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("COD") }
    var isSaving by remember { mutableStateOf(false) }
    var orderStatusMsg by remember { mutableStateOf<String?>(null) }
    var isFetchingAddress by remember { mutableStateOf(false) }

    // Tính toán số tiền tự động (Sẽ tự động chạy lại khi cartItems thay đổi số lượng trên RAM)
    val subtotal = cartItems.sumOf { (it.price ?: 0.0) * (it.quantity ?: 1) }
    val deliveryFee = if (subtotal > 0) 30000.0 else 0.0
    val totalAmount = subtotal + deliveryFee

    // Nạp dữ liệu mặc định từ tài khoản người dùng
    LaunchedEffect(Unit) {
        if (!isBuyNowMode) {
            cartViewModel.loadCart()
        }

        val userId = tokenManager.getUserId()
        if (userId > 0) {
            isFetchingAddress = true
            try {
                val response = profileRepository.getUserProfile(userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success == true && apiResponse.data != null) {
                        val userAddress = apiResponse.data.address
                        if (!userAddress.isNullOrBlank()) {
                            address = userAddress
                        }

                        val userPhone = apiResponse.data.phone
                        if (!userPhone.isNullOrBlank()) {
                            phoneNumber = userPhone
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isFetchingAddress = false
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thanh Toán", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isBuyNowMode) {
                            cartViewModel.clearBuyNowItem()
                        }
                        navController.popBackStack()
                    }) {
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
                .background(Brush.verticalGradient(colors = listOf(Color.White, PurpleLightBg.copy(alpha = 0.3f))))
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ==================== PHẦN 1: THÔNG TIN NHẬN HÀNG ====================
            Text("Thông tin nhận hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgGrayLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = PurpleMain, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thông tin mặc định của tài khoản", fontSize = 14.sp, color = TextGrayDark, fontWeight = FontWeight.Medium)
                        }
                        if (isFetchingAddress) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PurpleMain)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (address.trim().isNotEmpty()) {
                        Text(
                            text = "Địa chỉ: $address",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Số điện thoại: ${if(phoneNumber.isNotBlank()) phoneNumber else "Chưa cập nhật"}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    } else if (!isFetchingAddress) {
                        Text(
                            text = "Tài khoản của bạn chưa cập nhật địa chỉ và số điện thoại nhận hàng. Vui lòng thiết lập thông tin trong trang cá nhân!",
                            fontSize = 14.sp,
                            color = Color.Red.copy(alpha = 0.8f),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== PHẦN 2: DANH SÁCH SẢN PHẨM PHẢN HỒI ĐỘNG ====================
            Text("Sản phẩm đơn hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cartItems.forEach { item ->
                    CartItemRow(
                        item = item,
                        isReadOnly = false,
                        isChecked = true,
                        onQuantityChanged = { newQty ->
                            if (isBuyNowMode) {
                                cartViewModel.setBuyNowItem(item.productId, item.productName, item.productImage, item.price)
                                cartViewModel.buyNowItem.value = cartViewModel.buyNowItem.value?.copy(quantity = newQty)
                            } else {
                                // ✅ ĐÃ FIX: Sửa đổi từ viewModel sang cartViewModel
                                cartViewModel.updateQuantity(item.productId, newQty)
                            }
                        },
                        onRemoveClick = {
                            if (!isBuyNowMode) {
                                // ✅ ĐÃ FIX: Sửa đổi từ viewModel sang cartViewModel
                                cartViewModel.removeItem(item.productId)
                            } else {
                                cartViewModel.clearBuyNowItem()
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== PHẦN 3: PHƯƠNG THỨC THANH TOÁN ====================
            Text("Phương thức thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgGrayLight)
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    PaymentOptionRow(
                        title = "Thanh toán khi nhận hàng (COD)",
                        isSelected = paymentMethod == "COD",
                        onClick = { paymentMethod = "COD" }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp), thickness = 0.5.dp)
                    PaymentOptionRow(
                        title = "Thẻ tín dụng / Chuyển khoản",
                        isSelected = paymentMethod == "CARD",
                        onClick = { paymentMethod = "CARD" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==================== PHẦN 4: TÓM TẮT THANH TOÁN ====================
            Text("Tóm tắt thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BgGrayLight)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tiền hàng (Subtotal)", color = TextGrayDark, fontSize = 14.sp)
                        Text("${String.format("%,.0f", subtotal)} đ", fontSize = 14.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Phí vận chuyển (Delivery)", color = TextGrayDark, fontSize = 14.sp)
                        Text("${String.format("%,.0f", deliveryFee)} đ", fontSize = 14.sp)
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.6f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Tổng thanh toán (Total)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("${String.format("%,.0f", totalAmount)} đ", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PurpleMain)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ==================== PHẦN 5: NÚT XÁC NHẬN ĐẶT HÀNG ====================
            if (isSaving) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PurpleMain)
                }
            } else {
                Button(
                    onClick = {
                        if (address.trim().isEmpty()) {
                            Toast.makeText(context, "Vui lòng cập nhật địa chỉ giao hàng cụ thể!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (phoneNumber.trim().isEmpty()) {
                            Toast.makeText(context, "Vui lòng bổ sung số điện thoại tài khoản trước khi đặt hàng!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (cartItems.isEmpty()) {
                            Toast.makeText(context, "Đơn hàng trống, không có sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        coroutineScope.launch {
                            isSaving = true
                            orderStatusMsg = null
                            try {
                                val userId = tokenManager.getUserId()
                                if (userId <= 0) {
                                    Toast.makeText(context, "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                val requestItems = cartItems.map { responseItem ->
                                    com.example.data.dto.request.CartItemRequest(
                                        productId = responseItem.productId,
                                        quantity = responseItem.quantity
                                    )
                                }

                                val response = orderRepository.placeOrder(
                                    userId = userId,
                                    shippingAddress = address.trim(),
                                    phoneNumber = phoneNumber.trim(),
                                    paymentMethod = paymentMethod,
                                    cartItems = requestItems
                                )

                                if (response.isSuccessful && response.body() != null) {
                                    val apiResponse = response.body()!!

                                    if (apiResponse.success == true) {
                                        Toast.makeText(context, "🎉 Đặt hàng thành công!", Toast.LENGTH_LONG).show()

                                        if (isBuyNowMode) {
                                            cartViewModel.clearBuyNowItem()
                                        } else {
                                            cartViewModel.loadCart()
                                        }

                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    } else {
                                        orderStatusMsg = "Lỗi hệ thống: ${apiResponse.message}"
                                    }
                                } else {
                                    orderStatusMsg = "Hệ thống máy chủ gặp sự cố phản hồi! (Mã: ${response.code()})"
                                }
                            } catch (e: Exception) {
                                orderStatusMsg = "Lỗi kết nối mạng: ${e.localizedMessage}"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("XÁC NHẬN ĐẶT HÀNG", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.White)
                }
            }

            orderStatusMsg?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = PurpleLightBg)) {
                    Text(text = msg, modifier = Modifier.padding(16.dp), color = PurpleMain, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PurpleMain)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PurpleMain else Color.Black
        )
    }
}
package com.example.ui.features.product

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.remote.RetrofitClient
import com.example.data.repository.ProductRepository
import com.example.data.repository.ReviewRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: Long,
    cartViewModel: com.example.ui.features.cart.CartViewModel
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val productViewModel: ProductViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val productRepository = ProductRepository(apiService)
            val reviewRepository = ReviewRepository(apiService)
            ProductViewModelFactory(application, productRepository, reviewRepository)
        }
    )

    val productDetail by productViewModel.productDetail
    val isLoading by productViewModel.isDetailLoading
    val errorMessage by productViewModel.detailError

    val reviewsList by productViewModel.reviewsList
    val isReviewsLoading by productViewModel.isReviewsLoading

    // State cho việc thêm bình luận mới
    var newCommentText by remember { mutableStateOf("") }
    var newCommentRating by remember { mutableStateOf(5) }

    val coroutineScope = rememberCoroutineScope()

    // Tự động kéo cả chi tiết sản phẩm và danh sách review về cùng lúc khi vào màn hình
    LaunchedEffect(productId) {
        productViewModel.fetchProductDetail(productId)
        productViewModel.loadReviews(productId)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PurpleMain)
                }
                errorMessage != null -> {
                    Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
                productDetail != null -> {
                    val product = productDetail!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState()), // Cuộn mượt từ trên xuống dưới bao gồm cả review
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- 1. ẢNH SẢN PHẨM ---
                        AsyncImage(
                            model = product.image ?: "https://via.placeholder.com/320",
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(BgGrayLight),
                            contentScale = ContentScale.Crop
                        )

                        // --- 2. TÊN & GIÁ ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = product.name ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(text = "${String.format("%,.0f", product.price ?: 0.0)} đ", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
                        }

                        // =========================================================================
                        // 🟢 TỐI ƯU MỚI: 3. ĐÁNH GIÁ SỐ SAO TỔNG & SỐ LƯỢNG ĐÃ BÁN THỜI GIAN THỰC
                        // =========================================================================
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            // Cụm hiển thị điểm số sao trung bình kiểu Float
                            val avgRating = product.rating ?: 5.0f
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating tổng",
                                tint = Color(0xFFFFB300), // Đổi sang màu vàng cam chuẩn thương mại điện tử
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", avgRating),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color.Black
                            )

                            // Thanh thẳng phân cách dọc mờ
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .width(1.2.dp)
                                    .height(14.dp)
                                    .background(Color.LightGray.copy(alpha = 0.7f))
                            )

                            // Cụm hiển thị số lượng bán động kết nối cơ sở dữ liệu
                            val soldCount = product.sold ?: 0
                            Text(
                                text = "Đã bán: ",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = if (soldCount >= 1000) "${String.format("%.1f", soldCount / 1000.0)}k" else "$soldCount",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }

                        HorizontalDivider(color = BgGrayLight)

                        // --- 4. MÔ TẢ SẢN PHẨM ---
                        Text(text = product.description ?: "Chưa có mô tả.", fontSize = 15.sp, lineHeight = 22.sp, textAlign = TextAlign.Justify)

                        HorizontalDivider(color = BgGrayLight)

                        // =========================================================================
                        // 5. KHU VỰC HIỂN THỊ ĐÁNH GIÁ TRỰC TIẾP (CUỘN XUỐNG LÀ THẤY)
                        // =========================================================================
                        Text(
                            text = "Đánh giá sản phẩm (${reviewsList.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )

                        // --- 🅰️ THANH BỘ LỌC SỐ SAO ---
                        val filterRating by productViewModel.selectedFilterRating
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(0, 5, 4, 3, 2, 1).forEach { rating ->
                                val isSelected = filterRating == rating
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) PurpleMain else BgGrayLight)
                                        .clickable { productViewModel.setFilterRating(rating) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (rating == 0) "Tất cả" else "$rating ★",
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Lọc danh sách review trực tiếp trên giao diện dựa theo State Filter chọn ở trên
                        val filteredReviews = if (filterRating == 0) reviewsList else reviewsList.filter { it.rating == filterRating }

                        // --- 🅱️ HIỂN THỊ DANH SÁCH COMMENT ---
                        if (isReviewsLoading) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = PurpleMain, modifier = Modifier.size(24.dp))
                            }
                        } else if (filteredReviews.isEmpty()) {
                            Text(
                                text = "Không có đánh giá nào phù hợp số sao đã chọn.",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            // Render tĩnh tuần tự từng phần tử trong Column, không dùng LazyColumn lồng nhau gây xung đột scroll
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                filteredReviews.forEach { review ->
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = review.userName ?: "Khách hàng", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                repeat(review.rating) {
                                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = review.comment ?: "", fontSize = 14.sp, color = Color.DarkGray, lineHeight = 18.sp)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.4f))
                                    }
                                }
                            }
                        }

                        // --- 🅲 KHUNG VIẾT BÌNH LUẬN MỚI ---
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = BgGrayLight.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Để lại đánh giá của bạn:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Chọn số sao nhanh
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    repeat(5) { index ->
                                        val starIndex = index + 1
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (starIndex <= newCommentRating) Color(0xFFFFB300) else Color.LightGray,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable { newCommentRating = starIndex }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = newCommentText,
                                    onValueChange = { newCommentText = it },
                                    placeholder = { Text("Nhập nội dung nhận xét về sản phẩm...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 3,
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurpleMain)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        if (newCommentText.trim().isEmpty()) {
                                            Toast.makeText(context, "Vui lòng nhập nội dung đánh giá!", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        productViewModel.sendComment(
                                            productId = product.id,
                                            userId = 1L, // Thay ID động nếu có TokenManager
                                            name = "Nguyễn Văn Kiên",
                                            stars = newCommentRating,
                                            text = newCommentText.trim()
                                        ) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Cảm ơn bạn đã đánh giá sản phẩm!", Toast.LENGTH_SHORT).show()
                                                newCommentText = "" // Reset ô nhập
                                            } else {
                                                Toast.makeText(context, "Lỗi: Không thể gửi bình luận!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PurpleMain),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Gửi đánh giá", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // --- 6. HÀNG NÚT MUA HÀNG ĐÁY TRANG ---
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            cartViewModel.addToCart(product.id, 1)
                                            Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Lỗi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.size(54.dp).clip(RoundedCornerShape(14.dp)).background(PurpleLightBg)
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Thêm vào giỏ hàng", tint = PurpleMain)
                            }

                            Button(
                                onClick = {
                                    cartViewModel.setBuyNowItem(
                                        productId = product.id,
                                        productName = product.name,
                                        productImage = product.image,
                                        price = product.price
                                    )
                                    navController.navigate("checkout")
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
                            ) {
                                Text(text = "Mua ngay", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
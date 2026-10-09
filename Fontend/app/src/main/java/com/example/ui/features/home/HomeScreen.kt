package com.example.ui.features.home

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.remote.RetrofitClient
import com.example.data.repository.ProfileRepository
import com.example.data.repository.NotificationRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.ReviewRepository
import com.example.ui.components.ProductCard
import com.example.ui.features.product.ProductViewModel
import com.example.ui.features.product.ProductViewModelFactory
import com.example.ui.features.userprofile.UserViewModel
import com.example.ui.features.userprofile.UserViewModelFactory
import com.example.ui.features.notification.NotificationViewModel
import com.example.ui.features.notification.NotificationViewModelFactory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val userViewModel: UserViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val profileRepository = ProfileRepository(apiService)
            UserViewModelFactory(application, profileRepository)
        }
    )
    val currentUserName by userViewModel.currentUserName

    val homeFactory = remember { HomeViewModelFactory(application) }
    val homeViewModel: HomeViewModel = viewModel(factory = homeFactory)

    val products by homeViewModel.productsList
    val isProductsLoading by homeViewModel.isLoading
    val productsError by homeViewModel.errorMessage

    // Khởi tạo ProductViewModel để lấy danh sách Categories động
    val productViewModel: ProductViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val productRepository = ProductRepository(apiService)
            val reviewRepository = ReviewRepository(apiService)
            ProductViewModelFactory(application, productRepository, reviewRepository)
        }
    )
    val categories by productViewModel.categoriesList
    val selectedCatId by productViewModel.selectedCategoryId

    val notiViewModel: NotificationViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val repository = NotificationRepository(apiService)
            NotificationViewModelFactory(application, repository)
        }
    )
    val notifications by notiViewModel.notificationList

    LaunchedEffect(Unit) {
        userViewModel.loadUserProfile()
        homeViewModel.fetchProductsIfNeeded()
        notiViewModel.loadNotifications()
        productViewModel.fetchCategories()
    }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BgGray,
        bottomBar = {
            // Thanh điều hướng phẳng phẳng cân xứng 4 nút tính năng
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // NÚT 1: HOME
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate("home") }
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = PurpleMain)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Home", fontSize = 11.sp, color = PurpleMain, fontWeight = FontWeight.Medium)
                    }

                    // NÚT 2: THÔNG BÁO
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate("notifications") }
                    ) {
                        val unreadCount = notifications.count { !it.isRead }
                        if (unreadCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                                        Text(unreadCount.toString())
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = TextGray)
                            }
                        } else {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = TextGray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Thông báo", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    }

                    // NÚT 3: GIỎ HÀNG
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate("cart") }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TextGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Giỏ hàng", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    }

                    // NÚT 4: TÔI (CÁ NHÂN)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate("profileuser") }
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TextGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tôi", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 0.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ITEM 1: Header màu tím kèm thanh Search
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(PurpleMain)
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Xin Chào ", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                                    Text(text = currentUserName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { newValue ->
                                    searchQuery = newValue

                                    if (newValue.isNotBlank() && selectedCatId != 0L) {
                                        productViewModel.selectCategory(0L)
                                    }

                                    homeViewModel.searchProducts(newValue)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search anything...", color = TextGray, fontSize = 15.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black) },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Transparent
                                ),
                                singleLine = true
                            )
                        }
                    }
                }

                // ITEM 2: Mục Tiêu đề Categories
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Loại Sản Phẩm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 12.dp)
                    )
                }

                // ITEM 3: HIỂN THỊ DANH MỤC SẢN PHẨM (LAZYROW)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            val isSelected = selectedCatId == 0L
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    searchQuery = ""
                                    productViewModel.selectCategory(0L)
                                    homeViewModel.fetchProducts(0L)
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) PurpleMain else PurpleLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else PurpleMain
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tất cả",
                                    fontSize = 12.sp,
                                    color = if (isSelected) PurpleMain else Color.Black,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }

                        items(categories) { cat ->
                            val isSelected = selectedCatId == cat.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(75.dp)
                                    .clickable {
                                        searchQuery = ""
                                        productViewModel.selectCategory(cat.id)
                                        homeViewModel.fetchProducts(cat.id)
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) PurpleMain else PurpleLight)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!cat.icon.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = cat.icon,
                                            contentDescription = cat.name,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else PurpleMain.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = cat.name,
                                    fontSize = 12.sp,
                                    color = if (isSelected) PurpleMain else Color.Black,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // ITEM 4: Tiêu đề Mục Popular Product
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 4.dp, top = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Sản Phẩm Phổ Biến", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }

                // XỬ LÝ TRẠNG THÁI LOADING / ERROR / DANH SÁCH SẢN PHẨM
                if (isProductsLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PurpleMain)
                        }
                    }
                } else if (productsError != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = productsError!!, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                if (searchQuery.isBlank()) {
                                    homeViewModel.fetchProducts(selectedCatId)
                                } else {
                                    homeViewModel.searchProducts(searchQuery)
                                }
                            }, colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)) {
                                Text("Thử lại")
                            }
                        }
                    }
                } else {
                    if (products.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isBlank()) "Không có sản phẩm nào thuộc danh mục này" else "Không tìm thấy sản phẩm phù hợp",
                                    color = TextGray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        items(products) { product ->
                            Box(modifier = Modifier.padding(horizontal = 2.dp)) {
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        navController.navigate("product_detail/${product.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.data.remote.RetrofitClient
import com.example.data.repository.CartRepository
import com.example.data.repository.NotificationRepository
import com.example.data.repository.OrderRepository
import com.example.ui.features.auth.LoginScreen
import com.example.ui.features.auth.RegisterScreen
import com.example.ui.features.cart.CartScreen
import com.example.ui.features.cart.CartViewModel
import com.example.ui.features.cart.CartViewModelFactory
import com.example.ui.features.home.HomeScreen
import com.example.ui.features.order.CheckoutScreen
import com.example.ui.features.order.OrderHistoryScreen
import com.example.ui.features.order.OrderManagementScreen
import com.example.ui.features.order.OrderDetailScreen // 🟢 ĐÃ THÊM IMPORT MÀN HÌNH CHI TIẾT
import com.example.ui.features.order.OrderViewModel
import com.example.ui.features.order.OrderViewModelFactory
import com.example.ui.features.userprofile.PersonalInfoScreen
import com.example.ui.features.notification.NotificationScreen
import com.example.ui.features.notification.NotificationViewModel
import com.example.ui.features.notification.NotificationViewModelFactory
import com.example.ui.features.product.ProductDetailScreen
import com.example.ui.features.userprofile.ProfileUserScreen

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // Share Giỏ hàng dùng chung giữa HomeScreen, ProductDetail và Checkout
    val sharedCartViewModel: CartViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val cartRepository = CartRepository(apiService)
            CartViewModelFactory(application, cartRepository)
        }
    )

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        // Màn hình giỏ hàng
        composable("cart") {
            CartScreen(
                navController = navController,
                viewModel = sharedCartViewModel
            )
        }

        // Màn hình đặt hàng / thanh toán
        composable("checkout") {
            CheckoutScreen(
                navController = navController,
                cartViewModel = sharedCartViewModel
            )
        }

        composable("order_history") {
            OrderHistoryScreen(navController = navController)
        }

        composable("profileuser") {
            ProfileUserScreen(navController = navController)
        }

        // Màn hình quản lý đơn hàng phân loại theo Tab
        composable("ordermanagement") {
            val orderViewModel: OrderViewModel = viewModel(
                factory = remember(application) {
                    val apiService = RetrofitClient.getApiService(application)
                    val orderRepository = OrderRepository(apiService)
                    OrderViewModelFactory(application, orderRepository)
                }
            )
            OrderManagementScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }

        // =========================================================================
        // 🟢 ĐÃ THÊM MỚI: Định tuyến màn hình Chi Tiết Đơn Hàng nhận tham số {orderId}
        // =========================================================================
        composable(
            route = "order_detail/{orderId}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L

            val orderViewModel: OrderViewModel = viewModel(
                factory = remember(application) {
                    val apiService = RetrofitClient.getApiService(application)
                    val orderRepository = OrderRepository(apiService)
                    OrderViewModelFactory(application, orderRepository)
                }
            )

            OrderDetailScreen(
                navController = navController,
                orderId = orderId,
                orderViewModel = orderViewModel
            )
        }

        // 🔔 Màn hình hộp thư thông báo
        composable(route = "notifications") {
            val notificationViewModel: NotificationViewModel = viewModel(
                factory = remember(application) {
                    val apiService = RetrofitClient.getApiService(application)
                    val repository = NotificationRepository(apiService)
                    NotificationViewModelFactory(application, repository)
                }
            )
            NotificationScreen(
                navController = navController,
                viewModel = notificationViewModel
            )
        }

        composable("personalinfo") {
            PersonalInfoScreen(navController = navController)
        }

        // Màn hình chi tiết sản phẩm
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L

            ProductDetailScreen(
                navController = navController,
                productId = productId,
                cartViewModel = sharedCartViewModel
            )
        }
    }
}
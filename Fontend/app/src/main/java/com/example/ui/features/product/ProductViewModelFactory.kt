package com.example.ui.features.product

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.ProductRepository
import com.example.data.repository.ReviewRepository

class ProductViewModelFactory(
    private val application: Application,
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(application, productRepository, reviewRepository) as T
        }
        throw IllegalArgumentException("Không tìm thấy lớp ViewModel tương thích")
    }
}
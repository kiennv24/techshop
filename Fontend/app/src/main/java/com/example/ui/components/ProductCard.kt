package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.dto.response.ProductResponse

@Composable
fun ProductCard(product: ProductResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = product.image ?: "https://via.placeholder.com/150",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                // 1. Tên Sản Phẩm
                Text(
                    text = product.name ?: "Sản phẩm chưa có tên",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Hiển thị tên danh mục lấy từ Object lồng nhau
                Text(
                    text = product.category?.name ?: "Chưa phân loại",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))


                val formattedPrice = product.price?.let {
                    try {
                        String.format("%,.0f", it)
                    } catch (e: Exception) {
                        it.toString()
                    }
                } ?: "0"

                Text(
                    text = "$formattedPrice đ",
                    color = Color(0xFFFF5722),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Đánh giá & Số lượng đã bán
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "⭐ ${product.rating ?: 0.0}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(text = "Đã bán ${product.sold ?: 0}", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
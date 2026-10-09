package com.example.ui.features.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.dto.response.CartItemResponse
import com.example.ui.theme.BgGrayLight
import com.example.ui.theme.PurpleMain
import com.example.ui.theme.TextGrayDark

@Composable
fun CartItemRow(
    item: CartItemResponse,
    isChecked: Boolean = true,
    isReadOnly: Boolean = false,
    onQuantityChanged: (Int) -> Unit = {},
    onRemoveClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {}
) {
    // Cô lập trạng thái số lượng tại riêng ô này để tránh hiện tượng giật/load lại cả trang
    var localQuantity by remember(item.quantity) { mutableStateOf(item.quantity ?: 1) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🗳️ 1. Ô CHECKBOX (Chỉ hiển thị ở chế độ sửa tại Giỏ hàng)
            if (!isReadOnly) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = PurpleMain,
                        uncheckedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // 2. Hình ảnh sản phẩm bọc góc bo tròn từ API
            AsyncImage(
                model = item.productImage ?: "https://via.placeholder.com/80",
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 3. Khu vực hiển thị thông tin Text và Nút bấm
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productName ?: "Sản phẩm",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Giá tiền động tính theo: Đơn giá * Số lượng thay đổi local tại ô
                val displayPrice = (item.price ?: 0.0) * localQuantity
                Text(
                    text = "${String.format("%,.0f", displayPrice)} đ",
                    fontWeight = FontWeight.Bold,
                    color = PurpleMain,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ⚙️ PHÂN TÁCH GIAO DIỆN SỐ LƯỢNG DỰA TRÊN CHẾ ĐỘ READONLY:
                if (isReadOnly) {
                    // 🟢 Nếu ở màn hình CHECKOUT: Chỉ hiển thị text tĩnh, không cho bấm sửa
                    Text(
                        text = "Số lượng: $localQuantity",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    // 🔴 Nếu ở màn hình GIỎ HÀNG: Hiển thị bộ nút bấm tăng giảm thiết kế liền mạch siêu đẹp
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
                            // ✅ ĐÃ FIX: Đổi từ "mountaineer" sang "fontWeight" và đổi hiển thị thành dấu trừ "-" chuẩn logic
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
                            // ✅ ĐÃ FIX: Đổi thuộc tính sai "mountaineer" thành "fontWeight" chuẩn cấu trúc Jetpack Compose
                            Text("+", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // 🗑️ 4. NÚT THÙNG RÁC XÓA (Chỉ hiển thị ở chế độ sửa tại Giỏ hàng)
            if (!isReadOnly) {
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
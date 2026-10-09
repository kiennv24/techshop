package com.example.ui.features.userprofile

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.remote.RetrofitClient
import com.example.data.repository.ProfileRepository
import com.example.ui.theme.*
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val userViewModel: UserViewModel = viewModel(
        factory = remember(application) {
            val apiService = RetrofitClient.getApiService(application)
            val profileRepository = ProfileRepository(apiService)
            UserViewModelFactory(application, profileRepository)
        }
    )

    var name by userViewModel.name
    var email by userViewModel.email
    var phone by userViewModel.phone
    var address by userViewModel.address
    var avatarUrlForm by userViewModel.avatar

    val isLoading by userViewModel.isLoading
    val isSaving by userViewModel.isSaving
    val errorMessage by userViewModel.errorMessage
    val saveSuccessMsg by userViewModel.saveSuccessMsg

    // Trạng thái cục bộ để quản lý vòng xoay tiến trình khi đang upload ảnh lên Cloudinary
    var isUploadingImage by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val config = mapOf(
                "cloud_name" to "db1geruuv",
                "secure" to true
            )
            com.cloudinary.android.MediaManager.init(context, config)
        } catch (e: Exception) {
        }

        userViewModel.loadUserProfile()
    }

    // 🟢 BỘ PHÓNG ĐÒNG MỞ THƯ VIỆN ẢNH ĐIỆN THOẠI VÀ XỬ LÝ UPLOAD
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploadingImage = true

            // 🟢 ĐÃ FIX: Chỉ định gói com.cloudinary.android tường minh để tránh lỗi biên dịch Kotlin lambda
            com.cloudinary.android.MediaManager.get().upload(uri)
                .unsigned("Unsigned Upload Presets") // Cấu hình Preset Name chính xác của bạn
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        // Trích xuất lấy chuỗi URL chữ an toàn (HTTPS) do Cloudinary trả về
                        val secureUrl = resultData?.get("secure_url") as? String ?: ""

                        // Cập nhật giá trị link trực tuyến này vào Form dữ liệu của ViewModel
                        avatarUrlForm = secureUrl
                        isUploadingImage = false
                        Toast.makeText(context, "Đã nạp ảnh đại diện mới!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        isUploadingImage = false
                        Toast.makeText(context, "Tải ảnh lên Đám mây thất bại!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.loadUserProfile()
    }

    LaunchedEffect(saveSuccessMsg, errorMessage) {
        saveSuccessMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            userViewModel.saveSuccessMsg.value = null
        }
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            userViewModel.errorMessage.value = null
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Thông Tin Cá Nhân",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PurpleMain)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                // PHẦN 1: Hiển thị Avatar (Đọc trực tiếp link text trong cơ sở dữ liệu)
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    if (avatarUrlForm.isNotBlank()) {
                        AsyncImage(
                            model = avatarUrlForm,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(PurpleLightBg),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(PurpleLightBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Default",
                                tint = PurpleMain,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }

                    // 🟢 NÚT BẤM CÂY BÚT: Đã gỡ bỏ Dialog cũ, ấn vào tự bật Gallery chọn tệp luôn
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(PurpleMain)
                            .clickable {
                                if (!isUploadingImage) {
                                    imagePickerLauncher.launch("image/*") // Kích hoạt nạp bộ chọn ảnh Android
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploadingImage) {
                            // Quay vòng tròn nhỏ báo hiệu đang tải ảnh lên Cloud
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Photo",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // PHẦN 2: Danh sách các trường thông tin cá nhân
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BgGrayLight.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Trường Họ tên
                        Text(
                            "Họ và Tên",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PurpleMain
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleMain,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // 2. Trường Email (Khóa cố định)
                        Text(
                            "Địa chỉ Email",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = PurpleMain
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleMain,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF1F5F9),
                                disabledBorderColor = Color.LightGray.copy(alpha = 0.3f)
                            )
                        )

                        // 3. Trường Số điện thoại
                        Text(
                            "Số Điện Thoại",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = PurpleMain
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleMain,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // 4. Trường Địa chỉ mặc định
                        Text(
                            "Địa Chỉ Mặc Định",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    tint = PurpleMain
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleMain,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }

                // PHẦN 3: Nút bấm Lưu thay đổi chính dưới chân trang
                if (isSaving) {
                    CircularProgressIndicator(
                        color = PurpleMain,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            userViewModel.updateUserProfile(
                                onSuccess = {
                                    Toast.makeText(context, "Đã lưu thay đổi thành công!", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.popBackStack()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleMain)
                    ) {
                        Text("LƯU THAY ĐỔI", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
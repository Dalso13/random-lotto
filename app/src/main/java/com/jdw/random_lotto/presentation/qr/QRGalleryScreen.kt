package com.jdw.random_lotto.presentation.qr

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jdw.random_lotto.common.util.qr.QRCodeDecoder.decodeQrFromBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 갤러리에서 QR 코드 이미지 선택 후 분석 화면
 * @param onResult - QR 코드 분석 결과 콜백
 * @param onClose - 화면 닫기 콜백
 */
@Composable
fun QRGalleryScreen(
    onResult: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 갤러리 런처
    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            isProcessing = true
            errorMessage = null

            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(input)
                    }
                }

                if (bitmap == null) {
                    isProcessing = false
                    errorMessage = "이미지를 불러오지 못했어요."
                    return@launch
                }

                val text = withContext(Dispatchers.Default) {
                    decodeQrFromBitmap(bitmap)
                }

                isProcessing = false

                if (!text.isNullOrBlank()) {
                    onResult(text)
                    onClose()
                } else {
                    errorMessage = "QR 코드를 인식하지 못했어요."
                }
            }
        }

    // 화면 진입 시 바로 갤러리 열기
    LaunchedEffect(Unit) {
        galleryLauncher.launch("image/*")
    }

    // 배경 UI (사실상 로딩 + 에러 표시용)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 상단 바: 닫기 / 재선택
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }

            Text(
                text = "이미지에서 QR 스캔",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            IconButton(
                onClick = { galleryLauncher.launch("image/*") }
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "다시 선택",
                    tint = Color.White
                )
            }
        }

        // 가운데 안내/에러
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isProcessing) {
                Text(
                    text = "갤러리에서 QR 이미지 선택 후\n코드를 분석합니다.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                errorMessage?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = it,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


    if (isProcessing) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF222222))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "이미지에서 QR 코드를 분석 중이에요...",
                        color = Color.White
                    )
                }
            }
        }
    }
}

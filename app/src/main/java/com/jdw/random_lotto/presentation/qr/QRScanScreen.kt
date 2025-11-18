package com.jdw.random_lotto.presentation.qr

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.zxing.MultiFormatReader
import com.jdw.random_lotto.common.util.qr.QRCodeDecoder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * QR 스캔 화면
 * @param onResult - 스캔 성공 콜백
 * @param onClose - 닫기 버튼 콜백
 * @param guideRatio - 가이드 박스 비율 (기본: 0.8)
 */
@Composable
fun QRScanScreen(
    onResult: (String) -> Unit,
    onClose: () -> Unit,
    guideRatio: Float = 0.8f
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            // 성능/호환성 상황에 맞게 선택
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    // ZXing 리더 & 스레드
    val reader = remember { MultiFormatReader() }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    // PreviewView 크기 (오버레이/크롭 계산용)
    var previewWidth by remember { mutableStateOf(0) }
    var previewHeight by remember { mutableStateOf(0) }

    // 중복 호출 방지
    var analyzing by remember { mutableStateOf(false) }

    // 중복 스캔 방지
    var scanned by remember { mutableStateOf(false) }

    // 카메라 바인딩
    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val analyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(analysisExecutor) { proxy ->
                    if (!analyzing) {
                        analyzing = true
                        processImageForQr(
                            previewView = previewView,
                            imageProxy = proxy,
                            reader = reader,
                            guideRatio = guideRatio,
                            onSuccess = { text ->
                                if (!scanned) {
                                    scanned = true
                                    onResult(text)

                                    try {
                                        provider.unbindAll()
                                    } catch (_: Exception) {}
                                }
                            },
                            onFinally = {
                                analyzing = false
                            }
                        )
                    } else {
                        proxy.close()
                    }
                }
            }

        try {
            provider.unbindAll()
            val camera = provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analyzer
            )

            // 포커스/노출 중앙 맞추기 (PreviewView 측정 후)
            previewView.post {
                val width = previewView.width.toFloat()
                val height = previewView.height.toFloat()
                if (width > 0 && height > 0) {
                    val guideSize = width * guideRatio
                    val guideLeft = (width - guideSize) / 2
                    val guideTop = (height - guideSize) / 2
                    val guideCenterX = guideLeft + guideSize / 2
                    val guideCenterY = guideTop + guideSize / 2

                    val factory = SurfaceOrientedMeteringPointFactory(width, height)
                    val point = factory.createPoint(guideCenterX, guideCenterY)
                    val action = FocusMeteringAction.Builder(
                        point,
                        FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
                    )
                        .setAutoCancelDuration(3, TimeUnit.SECONDS)
                        .build()
                    camera.cameraControl.startFocusAndMetering(action)
                }
            }
        } catch (t: Throwable) {
            // 필요 시 로그/에러 처리
        }
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        // 미리보기
        AndroidView(
            factory = {
                previewView
            },
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    previewWidth = it.width
                    previewHeight = it.height
                }
        )

        // 가이드/마스크 오버레이 (Compose로 대체)
        QRMaskOverlay(
            modifier = Modifier.fillMaxSize(),
            guideRatio = guideRatio
        )

        // 상단 닫기 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }

    // 리소스 정리
    DisposableEffect(Unit) {
        onDispose {
            try {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Throwable) {}
            reader.reset()
            analysisExecutor.shutdownNow()
        }
    }
}

/** Compose 오버레이: 화면을 어둡게 마스킹하고 가운데 정사각형 가이드를 낸다. */
@Composable
private fun QRMaskOverlay(
    modifier: Modifier = Modifier,
    guideRatio: Float = 0.8f,
    cornerRadius: Dp = 12.dp,
    strokeWidth: Dp = 3.dp,
    maskColor: Color = Color.Black.copy(alpha = 0.55f),
    guideColor: Color = Color.White
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val guideSize = w * guideRatio
            val left = (w - guideSize) / 2f
            val top = (h - guideSize) / 2f

            // 마스크(구멍 뚫린) 그리기
            val outer = Path().apply { addRect(Rect(0f, 0f, w, h)) }
            val hole = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(left, top, left + guideSize, top + guideSize),
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )
                )
            }
            val mask = Path().apply {
                // EvenOdd로 구멍
                this.fillType = PathFillType.EvenOdd
                addPath(outer)
                addPath(hole)
            }
            drawPath(mask, color = maskColor)

            // 가이드 라인
            drawRoundRect(
                color = guideColor,
                topLeft = Offset(left, top),
                size = Size(guideSize, guideSize),
                cornerRadius = CornerRadius(cornerRadius.toPx()),
                style = Stroke(width = strokeWidth.toPx())
            )
        }
    }
}

/** CameraX ImageProxy + ZXing으로 QR 처리 */
@OptIn(ExperimentalGetImage::class)
private fun processImageForQr(
    previewView: PreviewView,
    imageProxy: ImageProxy,
    reader: MultiFormatReader,
    guideRatio: Float,
    onSuccess: (String) -> Unit,
    onFinally: () -> Unit
) {
    val mediaImage = imageProxy.image ?: run {
        imageProxy.close(); onFinally(); return
    }

    val imageWidth = mediaImage.width
    val imageHeight = mediaImage.height

    // PreviewView 크기 (화면 기준 컨테이너)
    val previewWidth = previewView.width.toFloat().coerceAtLeast(1f)
    val previewHeight = previewView.height.toFloat().coerceAtLeast(1f)

    // 1) 화면 기준 중앙 정사각형 crop 계산
    val guideCrop = QRCodeDecoder.calcCenterSquareCrop(
        containerWidth = previewWidth.toInt(),
        containerHeight = previewHeight.toInt(),
        guideRatio = guideRatio
    )

    // 2) PreviewView -> 실제 YUV 이미지 좌표로 스케일링
    val scaleX = imageWidth.toFloat() / previewWidth
    val scaleY = imageHeight.toFloat() / previewHeight

    val imageCrop = QRCodeDecoder.scaleCropRect(
        cropRect = guideCrop,
        scaleX = scaleX,
        scaleY = scaleY,
        maxWidth = imageWidth,
        maxHeight = imageHeight
    )

    // 3) Y 버퍼 추출
    val yBuffer = mediaImage.planes[0].buffer
    val ySize = yBuffer.remaining()
    val yData = ByteArray(ySize)
    yBuffer.get(yData)

    try {
        // 4) YUV LuminanceSource 생성
        val source = QRCodeDecoder.buildYuvLuminanceSource(
            yData = yData,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            crop = imageCrop
        )

        // 5) 공통 디코더로 QR 텍스트 추출
        val text = QRCodeDecoder.decodeQrFromSource(
            reader = reader,
            source = source,
        )

        if (text != null) {
            previewView.post {
                onSuccess(text)
            }
        }
    } catch (_: Throwable) {
        // 필요시 로그
    } finally {
        imageProxy.close()
        onFinally()
    }
}

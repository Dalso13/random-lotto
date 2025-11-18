package com.jdw.random_lotto.common.util.qr

import android.R.attr.bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.EnumMap

/**
 * QR 코드 디코더 유틸
 */
object QRCodeDecoder {

    /**
     * 이미지 크롭 영역 정보
     */
    data class CropRect(
        val left: Int,
        val top: Int,
        val width: Int,
        val height: Int
    )

    /**
     * containerWidth / containerHeight (예: PreviewView, Bitmap 전체 크기)를 기준으로
     * guideRatio 비율의 중앙 정사각형 crop 영역을 계산.
     *
     * guideRatio = 1.0f -> 가로 기준 전체 정사각형
     * guideRatio = 0.8f -> 가운데 80% 정사각형
     */
    fun calcCenterSquareCrop(
        containerWidth: Int,
        containerHeight: Int,
        guideRatio: Float
    ): CropRect {
        val w = containerWidth.toFloat().coerceAtLeast(1f)
        val h = containerHeight.toFloat().coerceAtLeast(1f)

        val size = (w * guideRatio).toInt().coerceAtLeast(1)
        val left = ((w - size) / 2f).toInt().coerceAtLeast(0)
        val top = ((h - size) / 2f).toInt().coerceAtLeast(0)

        val maxWidth = containerWidth - left
        val maxHeight = containerHeight - top

        return CropRect(
            left = left,
            top = top,
            width = size.coerceAtMost(maxWidth).coerceAtLeast(1),
            height = size.coerceAtMost(maxHeight).coerceAtLeast(1)
        )
    }

    /**
     * 화면 기준 cropRect 를 실제 이미지 해상도 기준으로 스케일링.
     *
     * - CameraX: 컨테이너 = PreviewView, 실제 이미지 = ImageProxy (Y plane)
     * - 갤러리 Bitmap: 보통 스케일링 필요 없이 그대로 사용 가능하지만, 필요시 재사용 가능
     */
    fun scaleCropRect(
        cropRect: CropRect,
        scaleX: Float,
        scaleY: Float,
        maxWidth: Int,
        maxHeight: Int
    ): CropRect {
        val left = (cropRect.left * scaleX).toInt().coerceIn(0, maxWidth - 1)
        val top = (cropRect.top * scaleY).toInt().coerceIn(0, maxHeight - 1)
        val width = (cropRect.width * scaleX).toInt().coerceIn(1, maxWidth - left)
        val height = (cropRect.height * scaleY).toInt().coerceIn(1, maxHeight - top)

        return CropRect(left, top, width, height)
    }

    /**
     * CameraX ImageProxy 의 Y 버퍼로부터 PlanarYUVLuminanceSource 생성
     *
     * @param yData   Y plane byte 배열
     * @param imageWidth  전체 이미지 가로
     * @param imageHeight 전체 이미지 세로
     * @param crop    이미지 기준 crop 영역
     */
    fun buildYuvLuminanceSource(
        yData: ByteArray,
        imageWidth: Int,
        imageHeight: Int,
        crop: CropRect
    ): PlanarYUVLuminanceSource {
        return PlanarYUVLuminanceSource(
            yData,
            imageWidth,
            imageHeight,
            crop.left,
            crop.top,
            crop.width,
            crop.height,
            /* reverseHorizontal = */ false
        )
    }

    /**
     * Bitmap QR 코드 디코딩 시도
     * 이미지 안에 작게 들어있는 경우를 먼저 시도하고 QR만 딱 있는 이미지도 시도
     * @param bitmap - 디코딩할 비트맵
     * @return 디코딩된 문자열 (없으면 null)
     */
    fun decodeQrFromSource(
        reader: MultiFormatReader,
        source: LuminanceSource,
    ): String? {

        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        // 공통 힌트
        val baseHints = EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
            put(DecodeHintType.TRY_HARDER, true)
            put(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            put(DecodeHintType.ALSO_INVERTED, true)
        }

        // 이미지 안에 작게 들어있는 경우
        try {
            reader.reset()
            val result = reader.decode(binaryBitmap, baseHints)
            return result.text
        } catch (_: Exception) {
        }

        // QR만 딱 있는 이미지용
        try {
            val pureHints = EnumMap(baseHints)
            pureHints[DecodeHintType.PURE_BARCODE] = true

            reader.reset()
            val result = reader.decode(binaryBitmap, pureHints)
            return result.text
        } catch (_: Exception) {
        } finally {
            reader.reset()
        }

        return null
    }
}

package com.jdw.random_lotto.common.util.qr

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

/**
 * QR 코드 디코더 유틸
 */
object QRCodeDecoder {

    // Bitmap에서 QR 코드 디코딩
    fun decodeQrFromBitmap(bitmap: Bitmap): String? {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()

        return try {
            reader.decode(binaryBitmap).text
        } catch (_: Throwable) {
            null
        } finally {
            reader.reset()
        }
    }
}
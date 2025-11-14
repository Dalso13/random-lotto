package com.jdw.random_lotto.presentation.qr.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.graphics.toColorInt

class QRMaskOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val maskPaint = Paint().apply {
        color = "#80000000".toColorInt()
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    val guideRect: RectF by lazy {
        val guideSize = width * 0.7f
        val left = (width - guideSize) / 2f
        val top = (height - guideSize) / 2f
        RectF(left, top, left + guideSize, top + guideSize)
    }

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskPaint)

        // 가이드 박스 클리어
        val radius = guideRect.width() / 10f
        canvas.drawRoundRect(guideRect, radius, radius, clearPaint)

        val borderPaint = Paint().apply {
            color = "#2881FF".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        canvas.drawRoundRect(guideRect, radius, radius, borderPaint)

        super.dispatchDraw(canvas)
        canvas.restoreToCount(sc)
    }
}
package com.github.lzaytseva.clockcustomview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import com.github.lzaytseva.clockcustomview.R
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


internal class ClockCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.clockViewStyle,
    @StyleRes defStyleRes: Int = R.style.DefaultClockViewStyle,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    @ColorInt
    var dialColor = 0

    @ColorInt
    var frameColor = 0

    @ColorInt
    var dotsColor = 0

    @ColorInt
    var minuteHandColor = 0

    @ColorInt
    var secondHandColor = 0

    @ColorInt
    var hourHandColor = 0

    @ColorInt
    var textColor = 0

    private var textStyle = 0


    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    private val point = PointF(0f, 0f)

    private val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textScaleX = 0.9f
        letterSpacing = -0.18f
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ClockCustomView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                dialColor = getColor(R.styleable.ClockCustomView_dialColor, 0)
                frameColor = getColor(R.styleable.ClockCustomView_frameColor, 0)
                dotsColor = getColor(R.styleable.ClockCustomView_dotsColor, 0)
                minuteHandColor = getColor(R.styleable.ClockCustomView_minuteHandColor, 0)
                secondHandColor = getColor(R.styleable.ClockCustomView_secondHandColor, 0)
                hourHandColor = getColor(R.styleable.ClockCustomView_hourHandColor, 0)
                textColor = getColor(R.styleable.ClockCustomView_textColor, 0)
                textStyle = getInt(R.styleable.ClockCustomView_textStyle, 0)
            } finally {
                recycle()
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = min(measuredHeight, measuredWidth)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        centerX = measuredWidth / 2f
        centerY = measuredHeight / 2f
        radius = measuredHeight / 2f

        drawBackground(canvas)
        drawFrame(canvas)
        drawDots(canvas)
        drawHourLabels(canvas)
        drawHands(canvas)

        postInvalidateDelayed(REFRESH_DELAY_MILLIS)
    }

    private fun drawBackground(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.FILL
            color = dialColor
        }
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    private fun drawFrame(canvas: Canvas) {
        val strokeWidth = radius / 12

        paint.apply {
            this.strokeWidth = strokeWidth
            style = Paint.Style.STROKE
            color = frameColor
        }

        val radiusFromCenterToFrame = radius - strokeWidth / 2

        canvas.drawCircle(centerX, centerY, radiusFromCenterToFrame, paint)
    }

    private fun drawDots(canvas: Canvas) {
        paint.apply {
            style = Paint.Style.FILL
            color = dotsColor
        }

        val radiusFromCenterToDot = 5 * radius / 6

        for (i in 1..60) {
            point.getXYForDots(radiusFromCenterToDot, i)
            val dotRadius = if (i % 5 == 0) radius / 96 else radius / 128
            canvas.drawCircle(point.x, point.y, dotRadius, paint)
        }
    }

    private fun drawHourLabels(canvas: Canvas) {
        paint.apply {
            textSize = radius * 2 / 7
            color = textColor
            typeface = when (textStyle) {
                1 -> Typeface.DEFAULT_BOLD
                2 -> Typeface.MONOSPACE
                3 -> Typeface.SERIF
                4 -> Typeface.SANS_SERIF
                else -> Typeface.DEFAULT
            }
        }

        val labelsDrawLineRadius = radius * 11 / 17

        for (i in 1..12) {
            point.getXYForHourLabels(i, labelsDrawLineRadius)
            val label = i.toString()
            canvas.drawText(label, point.x, point.y, paint)
        }
    }

    private fun drawHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        val hours = calendar[Calendar.HOUR_OF_DAY]
        val minutes = calendar[Calendar.MINUTE]
        val seconds = calendar[Calendar.SECOND]

        drawHourHand(canvas, hours, minutes)
        drawMinuteHand(canvas, minutes)
        drawSecondHand(canvas, seconds)
    }

    private fun drawHourHand(canvas: Canvas, hours: Int, minutes: Int) {
        val angle = (PI * (hours + minutes / 60) / 6 + START_ANGLE).toFloat()

        paint.apply {
            this.strokeWidth = radius / 15
            color = hourHandColor
        }

        canvas.drawLine(
            centerX - cos(angle) * radius * 3 / 14,
            centerY - sin(angle) * radius * 3 / 14,
            centerX + cos(angle) * radius * 7 / 14,
            centerY + sin(angle) * radius * 7 / 14,
            paint
        )
    }

    private fun drawMinuteHand(canvas: Canvas, minutes: Int) {
        paint.apply {
            strokeWidth = radius / 40
            color = minuteHandColor
        }

        val angle = (PI * minutes / 30 + START_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * radius * 3 / 14,
            centerY - sin(angle) * radius * 3 / 14,
            centerX + cos(angle) * radius * 9 / 14,
            centerY + sin(angle) * radius * 9 / 14,
            paint
        )
    }

    private fun drawSecondHand(canvas: Canvas, seconds: Int) {
        // широкая часть
        paint.apply {
            color = secondHandColor
            strokeWidth = radius / 50
        }

        var angle = (PI * seconds / 30 + START_ANGLE).toFloat()

        canvas.drawLine(
            centerX - cos(angle) * radius * 3 / 14,
            centerY - sin(angle) * radius * 3 / 14,
            centerX - cos(angle) * radius * 1 / 14,
            centerY - sin(angle) * radius * 1 / 14,
            paint
        )

        // узкая часть
        paint.apply {
            strokeWidth = radius / 80
        }

        angle = (PI * seconds / 30 + START_ANGLE).toFloat()

        canvas.drawLine(
            centerX - cos(angle) * radius * 1 / 14,
            centerY - sin(angle) * radius * 1 / 14,
            centerX + cos(angle) * radius * 5 / 7,
            centerY + sin(angle) * radius * 5 / 7,
            paint
        )

        canvas.drawCircle(centerX, centerY, radius / 40, paint)
    }


    private fun PointF.getXYForDots(radiusFromCenterToDot: Float, dotPosition: Int) {
        x = (cos(dotPosition * PI / 30) * radiusFromCenterToDot + radius).toFloat()
        y = (sin(dotPosition * PI / 30) * radiusFromCenterToDot + radius).toFloat()
    }

    private fun PointF.getXYForHourLabels(hour: Int, radius: Float) {
        val angle = (START_ANGLE + hour * (Math.PI / 6)).toFloat()
        x = radius * cos(angle) + centerX
        val textBaselineToCenter = (paint.descent() + paint.ascent()) / 2
        y = radius * sin(angle) + centerY - textBaselineToCenter
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            dialColor = state.getInt("baseColor")
            textColor = state.getInt("textColor")
            frameColor = state.getInt("frameColor")
            dotsColor = state.getInt("dotsColor")
            hourHandColor = state.getInt("hourHandColor")
            minuteHandColor = state.getInt("minuteHandColor")
            secondHandColor = state.getInt("secondHandColor")
            superState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                state.getParcelable("superState", Parcelable::class.java)
            } else {
                @Suppress("DEPRECATION") state.getParcelable("superState")
            }
        }
        super.onRestoreInstanceState(superState)
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putInt("baseColor", dialColor)
            putInt("textColor", textColor)
            putInt("frameColor", frameColor)
            putInt("dotsColor", dotsColor)
            putInt("hourHandColor", hourHandColor)
            putInt("minuteHandColor", minuteHandColor)
            putInt("secondHandColor", secondHandColor)
            putParcelable("superState", super.onSaveInstanceState())
        }
    }

    companion object {
        private const val START_ANGLE = -PI / 2
        private const val REFRESH_DELAY_MILLIS = 300L
    }

}
package nz.co.martinpaulo.wordjambalaya

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/**
 * Created by martin paulo on 29/07/2014.
 */
class WordDrawingView(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {
    private var isActive = false
    private var backgroundPaint: Paint? = null
    private var foregroundPaint: Paint? = null
    private var circlePaint: Paint? = null
    private var character = 0.toChar()
    private var wordPaint: Paint? = null


    constructor(context: Context?) : this(context, null) {
        backgroundPaint = Paint()
        backgroundPaint!!.color = LIGHT_CREAM
        foregroundPaint = Paint()
        foregroundPaint!!.color = LIGHT_RED
        circlePaint = Paint()
        circlePaint!!.color = LIGHT_RED
        wordPaint = Paint()
        wordPaint!!.color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint!!)
        val width = width - 1
        val height = height - 1
        canvas.drawRect(1f, 1f, width.toFloat(), height.toFloat(), foregroundPaint!!)
        if (isActive) {
            canvas.drawCircle(
                width.toFloat() / 2, height.toFloat() / 2, width.toFloat() / 2,
                circlePaint!!
            )
        }
        if (character.code != 0) {
            wordPaint!!.textSize = (width - 2).toFloat()
            wordPaint!!.textAlign = Paint.Align.CENTER
            canvas.drawText(
                character.toString(), width.toFloat() / 2, height - height.toFloat() / 5,
                wordPaint!!
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 100
        val desiredHeight = 100
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width = getDesiredDimension(desiredWidth, widthMode, widthSize)
        val height = getDesiredDimension(desiredHeight, heightMode, heightSize)
        setMeasuredDimension(width, height)
    }

    private fun getDesiredDimension(desired: Int, mode: Int, actual: Int): Int {
        if (mode == MeasureSpec.EXACTLY) {
            return actual //Must be this size
        } else if (mode == MeasureSpec.AT_MOST) {
            return min(desired.toDouble(), actual.toDouble()).toInt() //Can't be bigger than...
        }
        //Be whatever you want
        return desired
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        isActive = !isActive
        if (isActive) {
            Dictionary.getInstance().addAnswerChar(character)
        } else {
            Dictionary.getInstance().removeAnswerChar(character)
        }
        invalidate()
        return true
    }

    fun setCharacter(character: Char) {
        if (this.character.code > 0 && isActive) {
            Dictionary.getInstance().removeAnswerChar(this.character)
        }
        this.character = character
        if (this.character.code > 0 && isActive) {
            Dictionary.getInstance().addAnswerChar(this.character)
        }
    }

    fun cleanUp() {
        if (isActive) {
            Dictionary.getInstance().removeAnswerChar(character)
            isActive = false
        }
    }

    companion object {
        const val LIGHT_RED: Int = 0x22ff0000
        const val LIGHT_CREAM: Int = -0x71020
    }
}

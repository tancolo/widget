package com.tancolo.customview.countdownview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.tancolo.customview.R
import com.tancolo.customview.appendTimeUnit
import com.tancolo.customview.dip2px
import com.tancolo.customview.empty
import java.lang.ref.WeakReference


/**
 * Created by john.tan on 2024/04/16.
 *
 * The view animation for staying a few seconds on Splash Screen.
 * It is popular to show a short time(3 ~ 5 seconds) Ads in the Splash screen in many Applications in China.
 * That means it should be skipped by the users. Regards this tiny requirement, different App has different action.
 * Such as:
 * Elliptical graphics containing text "Skip"
 * The circle contains the text "Skip" or "x S"(S: seconds) + "Skip", and as the time counts down,
 * the circle arc animation effect appears or disappears.
 *
 * So it is necessary to define a custom view to handle these requirements.
 */
class CountdownCircleView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    companion object {
        const val TAG = "CountdownCircleView"
        const val DEBUG = false

        const val DEFAULT_CIRCLE_RADIUS = 25f // 25dp
        const val DEFAULT_ARC_WIDTH = 3f // 3dp
        const val DEFAULT_TEXT_SIZE = 14f // 14sp
        const val DEFAULT_LOADING_TIME = 3 // 3 seconds
        const val DEFAULT_CIRCLE_BACKGROUND_COLOR_GREY = "#FF888888"
        const val DEFAULT_ARC_COLOR_RED = "#FFFF0000"
        const val DEFAULT_TEXT_COLOR = Color.BLACK

        const val VALUE_START_ANGLE_NEGATIVE_180 = -180f
        const val VALUE_START_ANGLE_NEGATIVE_90 = -90f
        const val VALUE_START_ANGLE_ZERO = 0f
        const val VALUE_START_ANGLE_90 = 90f

        const val VALUE_START_SWEEP_ANGLE_ZERO = 0f
        const val VALUE_START_SWEEP_ANGLE_360 = 360f
        const val VALUE_START_SWEEP_ANGLE_NEGATIVE_360 = -360f
        const val VALUE_END_SWEEP_ANGLE_ZERO = 0f
        const val VALUE_END_SWEEP_ANGLE_360 = 360f
        const val VALUE_END_SWEEP_ANGLE_NEGATIVE_360 = -360f

        const val VALUE_TIMEUNIT_1000 = 1000
    }

    // the paint for circle view
    private lateinit var mPaintBackground: Paint

    // the paint for painting arc we want
    private lateinit var mPaintArc: Paint

    // the paint for the text
    private lateinit var mPaintText: Paint

    /**
     * The object of RectF for drawing the arc with Canvas.drawArc(), See the detail of Canvas.drawArc()
     * The bounds of oval used to define the shape and size of the arc.
     **/
    private lateinit var mRectF: RectF

    /**
     * The drawing types: in attrs.xml
     * clockwise_forward, value is 1
     * clockwise_backward, value is 2
     * counterclockwise_forward, value is 3
     * counterclockwise_backward, value is 4
     */
    private var mDrawType: Int

    // the width of outer side arc
    private val mPaintArcWidth: Float

    // the radius of the circle
    private val mCircleRadius: Int

    // the default color of arc
    private var mPaintArcColor = Color.parseColor(DEFAULT_ARC_COLOR_RED)

    // the default color for the background of circle
    private var mPaintBackgroundColor = Color.parseColor(DEFAULT_CIRCLE_BACKGROUND_COLOR_GREY)

    // the time for showing this custom view
    private var mLoadingTime: Int

    // the unit of loading time
    private var mLoadingTimeUnit: String = String.empty()

    // default color of the text
    private var mTextColor = DEFAULT_TEXT_COLOR

    // the size of the text
    private val mTextSize: Int

    /**
     * 4 starting points to start draw the arc: left, right, top, bottom. Defined in attrs.xml
     * left: the far left of circle, -180 degree ==> position value is 1
     * top: the far top of circle, -90 degree ==> position value is 2
     * right: the far right of circle, 0 degree ==> position value is 3
     * bottom: the far bottom of circle, 90 degree ==> position value is 4
     */
    private val startDrawPosition: Int

    // the starting angle for drawing, used in function Canvas.drawArc()
    private var startAngle = 0f


    // the starting position for sweeping angle, the value are: 0f, 360f, -360f etc.
    private var mmSweepAngleStart = 0f

    // the ending position for sweeping angle, the value are: 360f, 0f, 0f etc.
    private var mmSweepAngleEnd = 0f

    // the sweep angle
    private var mSweepAngle = 0f

    // the text will be painted
    private var mText = String.empty()

    // the width of the circle view, px
    private var mWidth = 0

    // the height of the circle view, px
    private var mHeight = 0

    private var mCallback: WeakReference<Callback>? = null

    init {
        val array = mContext.obtainStyledAttributes(attrs, R.styleable.CountdownCircleView)
        mDrawType = array.getInt(
            R.styleable.CountdownCircleView_countdown_arch_draw_types,
            DrawType.DRAW_CLOCKWISE_FORWARD.value
        )
        startDrawPosition = array.getInt(
            R.styleable.CountdownCircleView_countdown_start_draw_positions,
            DrawPositionType.POSITION_TOP_TWO.value
        )

        mCircleRadius = array.getDimension(
            R.styleable.CountdownCircleView_countdown_circle_radius,
            dip2px(mContext, DEFAULT_CIRCLE_RADIUS).toFloat()
        ).toInt()

        mPaintArcWidth = array.getDimension(
            R.styleable.CountdownCircleView_countdown_arc_width,
            dip2px(mContext, DEFAULT_ARC_WIDTH).toFloat()
        )

        mPaintArcColor =
            array.getColor(R.styleable.CountdownCircleView_countdown_arc_color, mPaintArcColor)
        mTextSize = array.getDimension(
            R.styleable.CountdownCircleView_countdown_text_size,
            dip2px(mContext, DEFAULT_TEXT_SIZE).toFloat()
        ).toInt()

        mTextColor =
            array.getColor(R.styleable.CountdownCircleView_countdown_text_color, mTextColor)
        mPaintBackgroundColor =
            array.getColor(
                R.styleable.CountdownCircleView_countdown_background_color,
                mPaintBackgroundColor
            )

        mLoadingTime =
            array.getInteger(
                R.styleable.CountdownCircleView_countdown_showing_time,
                DEFAULT_LOADING_TIME
            )
        if (mLoadingTime <= 0) {
            mLoadingTime = DEFAULT_LOADING_TIME
        }
        mLoadingTimeUnit =
            array.getString(R.styleable.CountdownCircleView_countdown_showing_time_unit)
                ?: String.empty()

        if (DEBUG) {
            Log.d(
                TAG, "startedDrawPosition = $startDrawPosition, "
                        + " mDrawType = $mDrawType, "
                        + "mLoadingTime = $mLoadingTime, "
                        + "mLoadingTimeUnit = $mLoadingTimeUnit"
            )
        }

        array.recycle()
        init()
    }

    private fun init() {
        // Set the transparent to the view
        this.background = ContextCompat.getDrawable(mContext, android.R.color.transparent)

        mPaintBackground = Paint().let { paint ->
            paint.style = Paint.Style.FILL
            paint.isAntiAlias = true
            paint.color = mPaintBackgroundColor
            paint
        }

        mPaintArc = Paint().let { paint ->
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            paint.color = mPaintArcColor
            paint.strokeWidth = mPaintArcWidth
            paint
        }

        mPaintText = Paint().let { paint ->
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            paint.color = mTextColor
            paint.textSize = mTextSize.toFloat()
            paint
        }

        when (startDrawPosition) {
            // left, -180 degree
            DrawPositionType.POSITION_LEFT_ONE.value -> {
                startAngle = VALUE_START_ANGLE_NEGATIVE_180
            }

            // top, -90 degree
            DrawPositionType.POSITION_TOP_TWO.value -> {
                startAngle = VALUE_START_ANGLE_NEGATIVE_90
            }

            // right, 0 degree
            DrawPositionType.POSITION_RIGHT_THREE.value -> {
                startAngle = VALUE_START_ANGLE_ZERO
            }

            // bottom, 90 degree
            DrawPositionType.POSITION_BOTTOM_FOUR.value -> {
                startAngle = VALUE_START_ANGLE_90
            }
        }

        // To get the draw type from attrs.xml and then to choose the start / end sweep angle
        when (mDrawType) {
            DrawType.DRAW_CLOCKWISE_FORWARD.value -> {
                mmSweepAngleStart = VALUE_START_SWEEP_ANGLE_ZERO // 0f
                mmSweepAngleEnd = VALUE_END_SWEEP_ANGLE_360 // 360f
            }

            DrawType.DRAW_CLOCKWISE_BACKWARD.value -> {
                mmSweepAngleStart = VALUE_START_SWEEP_ANGLE_NEGATIVE_360 // -360f
                mmSweepAngleEnd = VALUE_END_SWEEP_ANGLE_ZERO // 0f
            }

            DrawType.DRAW_COUNTERCLOCKWISE_FORWARD.value -> {
                mmSweepAngleStart = VALUE_START_SWEEP_ANGLE_ZERO // 0f
                mmSweepAngleEnd = VALUE_END_SWEEP_ANGLE_NEGATIVE_360 // -360f
            }

            DrawType.DRAW_COUNTERCLOCKWISE_BACKWARD.value -> {
                mmSweepAngleStart = VALUE_START_SWEEP_ANGLE_360 // 360F
                mmSweepAngleEnd = VALUE_END_SWEEP_ANGLE_ZERO // 0f
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Get the real width and height of the circle view
        mWidth = w
        mHeight = h

        // Create the object of RectF for Canvas.drawArc() to draw the arc.
        mRectF = RectF(
            0 + mPaintArcWidth / 2,
            0 + mPaintArcWidth / 2,
            mWidth - mPaintArcWidth / 2,
            mHeight - mPaintArcWidth / 2
        )
        if (DEBUG) {
            Log.d(TAG, "onSizeChanged, mWidth = $mWidth, mHeight = $mHeight")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (DEBUG) {
            Log.d(TAG, "onMeasure")
        }

        // Make sure the view is circle by specific radius
        setMeasuredDimension(mCircleRadius * 2, mCircleRadius * 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background, it is a circle
        canvas.drawCircle(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            mWidth / 2 - mPaintArcWidth,
            mPaintBackground
        )
        canvas.drawArc(mRectF, startAngle, mSweepAngle, false, mPaintArc)

        // Draw the text
        val mTextWidth = mPaintText.measureText(mText, 0, mText.length)
        val dx = mWidth / 2 - mTextWidth / 2
        val fontMetricsInt = mPaintText.fontMetricsInt
        val dy =
            ((fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom).toFloat()
        val baseLine = mHeight / 2 + dy
        canvas.drawText(mText, dx, baseLine, mPaintText)
    }

    /**
     * Use the value animator to control the value of sweep angle.
     * Two animator to show the circle arc and the text, for example, 3s, 2s, 1s
     */
    fun start() {
        if (DEBUG) {
            Log.d(TAG, "start the animation")
        }

        val animatorSweepAngle = ValueAnimator.ofFloat(mmSweepAngleStart, mmSweepAngleEnd)
        animatorSweepAngle.interpolator = LinearInterpolator()
        animatorSweepAngle.addUpdateListener { valueAnimator ->
            mSweepAngle = valueAnimator.animatedValue as Float

            // Invalidate, it will call fun onDraw
            invalidate()
        }

        /**
         * Using the animator to change the loading time.
         * From loading time countdown to 0
         */
        val animatorLoadingTime = ValueAnimator.ofInt(mLoadingTime, 0)
        animatorLoadingTime.interpolator = LinearInterpolator()
        animatorLoadingTime.addUpdateListener { valueAnimator ->
            val time = valueAnimator.animatedValue as Int
            mText = time.toString().appendTimeUnit(mLoadingTimeUnit)
        }

        AnimatorSet().let { set ->
            set.playTogether(animatorSweepAngle, animatorLoadingTime)
            set.setDuration((mLoadingTime * VALUE_TIMEUNIT_1000).toLong())
            set.interpolator = LinearInterpolator()
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    clearAnimation()

                    if (DEBUG) {
                        Log.d(TAG, "onAnimationEnd, mCallback = $mCallback, get() = ${mCallback?.get()}")
                    }
                    mCallback?.get()?.complete()
                }
            })
            set.start()
        }
    }

    fun setCallback(cb: Callback?) {
        if (DEBUG) {
            Log.d(TAG, "setCallback, cb = $cb")
        }
        mCallback = cb?.let { WeakReference<Callback>(cb) }
    }
}

interface Callback {
    fun complete()
}

enum class DrawPositionType(val value: Int) {
    POSITION_LEFT_ONE(1),
    POSITION_TOP_TWO(2),
    POSITION_RIGHT_THREE(3),
    POSITION_BOTTOM_FOUR(4);

    companion object Create {
        fun from(sourceValue: Int): DrawPositionType =
            values().find { it.value == sourceValue } ?: values().first()
    }
}

enum class DrawType(val value: Int) {
    DRAW_CLOCKWISE_FORWARD(1),
    DRAW_CLOCKWISE_BACKWARD(2),
    DRAW_COUNTERCLOCKWISE_FORWARD(3),
    DRAW_COUNTERCLOCKWISE_BACKWARD(4);

    companion object Create {
        fun from(sourceValue: Int): DrawType =
            values().find { it.value == sourceValue } ?: values().first()
    }
}

package com.ppjun.android.vivonex

import android.content.Context
import android.graphics.*

import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.graphics.BitmapFactory
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build


class PPTimePicker(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    //可选区域画笔
    lateinit var mSelectPaint: Paint
    //不可选区域画笔
    lateinit var mUnselectPaint: Paint
    //可选区域和不可选区域交集画笔
    lateinit var mSelectErrorPaint: Paint
    //背景刻度尺画笔
    lateinit var mScalePaint: Paint
    //文字画笔
    lateinit var mWordPaint: Paint
    //球半径，以后会用图片代替
    var mHandleRadius = 0

    //可选区域颜色
    var mSelectColor: Int = 0
    //不可选区域颜色
    var mUnselectColor: Int = 0
    //可选区域和不可选区域交集颜色
    var mSelectErrorColor: Int = 0
    //背景刻度尺颜色
    var mScaleColor: Int = 0
    //文字颜色
    var mWordColor: Int = 0

    //选中区域颜色
    val SELECT_COLOR = Color.parseColor("#FEE354")
    //不可选区域颜色
    val UN_SELECT_COLOR = Color.parseColor("#cdcdcd")
    //选择错误区域颜色
    val SELECT_ERROR_COLOR = R.color.colorPrimary
    //刻度尺颜色
    val SCALE_COLOR = Color.parseColor("#c0c1c0")
    //时间文本颜色
    val WORD_COLOR = Color.parseColor("#515151")
    //圆球半径
    val DEFAULT_RADIUS = 10
    //获取滑动x y方向速度
    var mVelocityTracker: VelocityTracker? = null
    //不可选区域矩形
    var unAvailableRect: Rect = Rect()
    //一个屏幕8个小矩形
    val A_SEREEN_SIZE = 8
    //屏幕总宽度
    var mScreenWidth = 0
    //每个格子宽度
    var mRectWidth = 0
    //时间轴总宽度
    var mSumWidth = 0
    //时间轴总的时间点，半小时为一个单位
    var mSize = 0//刻度尺长度
    //选中区域矩形
    var selectRect = Rect()
    var mCacel = true

    lateinit var mUnavailbleBitmap: Bitmap


    init {
        //从typearray获取自定义颜色,和半径
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.PPTimePicker)
        mSelectColor = typeArray.getColor(R.styleable.PPTimePicker_selectColor, SELECT_COLOR)
        mUnselectColor = typeArray.getColor(R.styleable.PPTimePicker_unSelectColor, UN_SELECT_COLOR)
        mSelectErrorColor = typeArray.getColor(R.styleable.PPTimePicker_SelectErrorColor, SELECT_ERROR_COLOR)
        mScaleColor = typeArray.getColor(R.styleable.PPTimePicker_scaleColor, SCALE_COLOR)
        mWordColor = typeArray.getColor(R.styleable.PPTimePicker_wordColor, WORD_COLOR)
        mHandleRadius = typeArray.getInteger(R.styleable.PPTimePicker_handleRadius, DEFAULT_RADIUS)
        typeArray.recycle()
        initPaint()
    }

    private fun initPaint() {
        mSelectPaint = Paint()
        mUnselectPaint = Paint()
        mSelectErrorPaint = Paint()
        mScalePaint = Paint()
        mWordPaint = Paint()
        mSelectPaint.isAntiAlias = true
        mUnselectPaint.isAntiAlias = true

        mUnselectPaint.color = mUnselectColor
        mSelectErrorPaint.isAntiAlias = true
        mScalePaint.color = mScaleColor
        mWordPaint.isAntiAlias = true
        mWordPaint.color = mWordColor
        //mWordPaint.isFakeBoldText=true
        mWordPaint.textSize = 55f
        mUnavailbleBitmap = getBitmap(context, R.mipmap.ic_launcher)
    }

    val mList: ArrayList<PPTimePickerVo> = ArrayList()

    fun setTimePickerVos(array: ArrayList<PPTimePickerVo>) {
        mList.clear()
        mList.addAll(array)
        mSize = array.size
        Log.d("debug=", mSize.toString())
        val windowManager = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)

        mScreenWidth = windowManager.defaultDisplay.width
        Log.d("debug", mScreenWidth.toString() + "init")
        //屏幕总宽度/8个格子，得出每个格子宽度
        mRectWidth = mScreenWidth / A_SEREEN_SIZE
        //每个格子宽度乘以列表的长度，显示多少个格子，半小时为1格
        mSumWidth = mRectWidth * mSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //画背景区域（线和文本）
        drawScale(canvas)
        //画不可选区域
        drawUnAvailable(canvas)
        //画点击选中区域
        drawSelect(canvas)
    }

    var af = 0
    var circleRect = Rect()
    private fun drawSelect(canvas: Canvas) {
        //加上线的宽度，讲究
        canvas.drawRect(selectRect.left.toFloat(), selectRect.top.toFloat(), selectRect.right.toFloat() + mLineWidth, selectRect.bottom.toFloat(), mSelectPaint)
        //滑动中的view
        canvas.drawRect(af.toFloat(), selectRect.top.toFloat(), (af + mLineWidth).toFloat(), selectRect.bottom.toFloat(), mSelectPaint)
        // 画bitmap，代替画圆
        val rectTemp = Rect()
        rectTemp.left = 0
        rectTemp.top = 0
        rectTemp.right = mUnavailbleBitmap.width
        rectTemp.bottom = mUnavailbleBitmap.height
        circleRect.left = selectRect.right
        circleRect.top = selectRect.height() / 2
        circleRect.right = selectRect.right + 30
        circleRect.bottom = selectRect.height() / 3

        canvas.drawBitmap(mUnavailbleBitmap, rectTemp, circleRect, Paint())

    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(event)
    }

    private fun drawUnAvailable(canvas: Canvas) {
        for (i in 0 until mSize) {
            if (mList[i].isNoAvailable) {
                unAvailableRect.left = mRectWidth * i + 50
                unAvailableRect.right = mRectWidth * i + mRectWidth + 50
                var fontMetrics = mWordPaint.fontMetrics
                var i2 = fontMetrics.top
                unAvailableRect.top = height / 4 + 100
                unAvailableRect.bottom = height
                val rectTemp = Rect()
                rectTemp.left = 0
                rectTemp.top = 0
                rectTemp.right = mUnavailbleBitmap.width
                rectTemp.bottom = mUnavailbleBitmap.height
                mUnselectPaint.alpha = 178
                //第一个bitmap是图片的裁剪bitmap，第二个bitmap是屏幕的位置
                canvas.drawRect(unAvailableRect, mUnselectPaint)

            }
        }
    }

    private fun getBitmap(context: Context, vectorDrawableId: Int): Bitmap {
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val vectorDrawable = context.getDrawable(vectorDrawableId)
            bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,
                    vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
            vectorDrawable.draw(canvas)
        } else {
            bitmap = BitmapFactory.decodeResource(context.resources, vectorDrawableId)
        }
        return bitmap
    }


    var rect = Rect()
    var mLineWidth = 3
    var zero = 0
    private fun drawScale(canvas: Canvas) {

        // 获取屏幕宽度 /8 每一个格子宽度，再for循环
        if (mList.size > 0) {
            for (i in 0 until mSize) {
                rect.left = mRectWidth * i + 50
                rect.right = (mRectWidth * i + 50) + mLineWidth
                rect.bottom = height - zero
                if ((mList[i].time).endsWith(".5")) {
                    rect.top = height / 2 + 50
                    // canvas.drawText(mList[i].time + "时", (rect.right - 50).toFloat(), 100f, mWordPaint)
                } else {
                    rect.top = height / 4 + 50
                    canvas.drawText(mList[i].time + "时", (rect.right - 50).toFloat(), 100f, mWordPaint)
                }
                canvas.drawRect(rect, mScalePaint)
            }
            Log.d("debug=", mSumWidth.toString() + "mSumWidth")
            rect.top = height - mLineWidth
            rect.left = 0
            rect.right = mSumWidth
            rect.bottom = height
            canvas.drawRect(rect, mScalePaint)
        }
    }

    var viewX = 0f
    var viewY = 0f
    var tempViewX = 0f
    var tempViewY = 0f

    var ac = false
    var ad = false
    //x是控件内的x坐标，rawx是屏幕内x坐标，如此类推y
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //初始化 mVelocityTracker
        initVelocityTracker(event)
        var y = 0f
        when (event.action) {
        //0
            MotionEvent.ACTION_DOWN -> {
                viewX = event.x
                viewY = event.y
                tempViewX = viewX
                tempViewY = viewY

            }
        //2
            MotionEvent.ACTION_MOVE -> {

            }
        //1
            MotionEvent.ACTION_UP -> {

            }
        //3
            MotionEvent.ACTION_CANCEL -> {
                if (mCacel) {
                    mCacel = true
                    return true
                }

            }
            else -> {
                return true
            }
        }


        return true

    }

    private fun initVelocityTracker(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker?.addMovement(event)
        }
    }

}
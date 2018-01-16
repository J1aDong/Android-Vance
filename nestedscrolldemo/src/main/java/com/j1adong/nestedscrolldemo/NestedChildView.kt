package com.j1adong.nestedscrolldemo

import android.content.Context
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller

import java.util.Arrays

/**
 * Created by J1aDong on 2017/12/29.
 */

class NestedChildView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), NestedScrollingChild {
    private var mLastMontionY: Float = 0.toFloat()
    private val consumed = IntArray(2)
    private val offsetInWindow = IntArray(2)
    private val childHelper = NestedScrollingChildHelper(this)
    private var mScroller: Scroller
    private var mVelocityTracker: VelocityTracker? = null
    private var mMaxFlingVelocity: Int = 0
    private var mMinimumVelocity: Int = 0
    private var mMaximumVelocity: Int = 0

    private var mPointerId: Int = 0

    init {
        isNestedScrollingEnabled = true

        mScroller = Scroller(getContext())
    }

    private fun obtainVelocityTracker(event: MotionEvent) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
            val configuration = ViewConfiguration
                    .get(context)
            mMinimumVelocity = configuration.scaledMinimumFlingVelocity
            mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        }
        mVelocityTracker?.addMovement(event)

    }

    private fun releaseVelocityTracker() {
        mVelocityTracker?.recycle()
        mVelocityTracker = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        //添加触摸对象MotionEvent ， 用于计算触摸速率
        obtainVelocityTracker(event)

        val action = event.action
        // 取第一根手指的id
        mPointerId = event.getPointerId(0)
        when (action) {
            MotionEvent.ACTION_DOWN -> {

                if (!mScroller.isFinished) {
                    // 禁止滑动
                    mScroller.abortAnimation()
                }
                // 取得当前的y,并赋值给lastY变量
                mLastMontionY = getPointerY(event, mPointerId)

                // 找不到手指，放弃掉这个触摸流
                if (mLastMontionY == -1f) {
                    return false
                }

                // 通知父view,开始滑动
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }
            MotionEvent.ACTION_MOVE -> {
                // 获得当前手指的Y
                val pointerY = getPointerY(event, mPointerId)
                // 找不到手指，放弃掉这个触摸事件流
                if (pointerY == -1f) {
                    return false
                }

                // 计算出滑动的偏移量
                var deltaY = pointerY - mLastMontionY

                Log.d(TAG, String.format("downY = %f", deltaY))
                Log.d(TAG, String.format("before dispatchNestedPreScroll, deltaY = %f", deltaY))
                // 通知父View, 子View想滑动 deltaY 个偏移量，父View要不要先滑一下，然后把父View滑了多少，告诉子View一下
                // 下面这个方法的前两个参数为在x，y方向上想要滑动的偏移量
                // 第三个参数为一个长度为2的整型数组，父View将消费掉的距离放置在这个数组里面
                // 第四个参数为一个长度为2的整型数组，父View在屏幕里面的偏移量放置在这个数组里面
                // 返回值为 true，代表父View有消费任何的滑动.
                if (dispatchNestedPreScroll(0, deltaY.toInt(), consumed, offsetInWindow)) {

                    // 偏移量需要减掉被父View消费掉的
                    deltaY -= consumed[1].toFloat()
                    Log.d(TAG, String.format("after dispatchNestedPreScroll , deltaY = %f", deltaY))

                }

                // 这里移动子View，下面的min,max是为了控制边界，避免子View越界
                y = Math.min(Math.max(y + deltaY, 0f), ((parent as View).height - height).toFloat())
            }
            MotionEvent.ACTION_UP -> {
                // 计算当前速度
                mVelocityTracker?.computeCurrentVelocity(1000)
                // 获取y上的速度
                val vY = mVelocityTracker?.yVelocity
//                Log.w(TAG, "vY-->" + vY)
                if (Math.abs(vY!!) > mMinimumVelocity) {
                    fling(-vY)
                }
                releaseVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL -> {
                releaseVelocityTracker()
            }
        }
        return true
    }

    private fun fling(vy: Float) {
        mScroller.fling(scrollX, scrollY, 0, vy.toInt(), 0, 0, -10000, 10000)
        //这里必须调用postInvalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
        postInvalidate()
    }

    override fun computeScroll() {
        Log.w(TAG,"computeScroll")
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            var scrollX: Int
            var scrollY: Int
            val x = mScroller.currX
            val y = mScroller.currY
            scrollX = x
            scrollY = y
            scrollY += 10
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(scrollX, scrollY)
            postInvalidate()  //这里必须调用postInvalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
        }
    }

    /**
     * 这个方法通过pointerId获取pointerIndex,然后获取Y
     */
    private fun getPointerY(event: MotionEvent, pointerId: Int): Float {
        val pointerIndex = event.findPointerIndex(pointerId)
        return if (pointerIndex < 0) {
            -1f
        } else event.getY(pointerIndex)
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        Log.d(TAG, String.format("setNestedScrollingEnabled , enabled = %b", enabled))
        childHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        Log.d(TAG, "isNestedScrollingEnabled")
        return childHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        Log.d(TAG, String.format("startNestedScroll , axes = %d", axes))
        return childHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        Log.d(TAG, "stopNestedScroll")
        childHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        Log.d(TAG, "hasNestedScrollingParent")
        return childHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        val b = childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
        Log.d(TAG, String.format("dispatchNestedScroll , dxConsumed = %d, dyConsumed = %d, dxUnconsumed = %d, dyUnconsumed = %d, offsetInWindow = %s", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, Arrays.toString(offsetInWindow)))
        return b
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        val b = childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
        Log.d(TAG, String.format("dispatchNestedPreScroll , dx = %d, dy = %d, consumed = %s, offsetInWindow = %s", dx, dy, Arrays.toString(consumed), Arrays.toString(offsetInWindow)))
        return b
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.d(TAG, String.format("dispatchNestedFling , velocityX = %f, velocityY = %f, consumed = %b", velocityX, velocityY, consumed))
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, String.format("dispatchNestedPreFling , velocityX = %f, velocityY = %f", velocityX, velocityY))
        return childHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    companion object {
        private val TAG = "NestedChildView"
    }
}

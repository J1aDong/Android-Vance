package com.j1adong.nestedscrolldemo

import android.content.Context
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.NestedScrollingParentHelper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout

import java.util.Arrays

/**
 * Created by J1aDong on 2018/1/8.
 */

class NestedParentView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent {

    private var parentHelper: NestedScrollingParentHelper? = null

    init {
        parentHelper = NestedScrollingParentHelper(this)

    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        Log.d(TAG, String.format("onStartNestedScroll, child = %s, target = %s, nestedScrollAxes = %d", child, target, nestedScrollAxes))
        return true
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        Log.d(TAG, String.format("onNestedScrollAccepted, child = %s, target = %s, nestedScrollAxes = %d", child, target, nestedScrollAxes))
        parentHelper!!.onNestedScrollAccepted(child, target, nestedScrollAxes)
    }

    override fun onStopNestedScroll(target: View) {
        Log.d(TAG, "onStopNestedScroll")
        parentHelper!!.onStopNestedScroll(target)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        Log.d(TAG, String.format("onNestedScroll, dxConsumed = %d, dyConsumed = %d, dxUnconsumed = %d, dyUnconsumed = %d", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed))
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // 应该移动的Y距离
        val shouldMoveY = y + dy

        // 获取到父View的容器的引用，这里假定父View容器是View
        val parent = parent as View

        val consumedY: Int
        // 如果超过了父View的上边界，只消费子View到父View上边的距离
        if (shouldMoveY <= 0) {
            consumedY = -y.toInt()
        } else if (shouldMoveY >= parent.height - height) {
            // 如果超过了父View的下边界，只消费子View到父View
            consumedY = (parent.height.toFloat() - height.toFloat() - y).toInt()
        } else {
            // 其他情况下全部消费
            consumedY = dy
        }

        // 对父View进行移动
        y += consumedY

        // 将父View消费掉的放入consumed数组中
        consumed[1] = consumedY

        Log.d(TAG, String.format("onNestedPreScroll, dx = %d, dy = %d, consumed = %s", dx, dy, Arrays.toString(consumed)))
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.d(TAG, String.format("onNestedFling, velocityX = %f, velocityY = %f, consumed = %b", velocityX, velocityY, consumed))
        return true
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, String.format("onNestedPreFling, velocityX = %f, velocityY = %f", velocityX, velocityY))
        return true
    }

    override fun getNestedScrollAxes(): Int {
        Log.d(TAG, "getNestedScrollAxes")
        return parentHelper!!.nestedScrollAxes
    }

    companion object {

        val TAG = NestedParentView::class.java.simpleName
    }
}

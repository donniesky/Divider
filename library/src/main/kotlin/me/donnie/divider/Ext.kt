package me.donnie.divider

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

/**
 * @author donnieSky
 * @created_at 2017/5/11.
 * @description
 * @version
 */
fun RecyclerView.getOrientation(): Int {
    var orientation: Int? = null

    val layoutManager = this.layoutManager
    if (layoutManager is LinearLayoutManager) {
        orientation = layoutManager.orientation
    } else if (layoutManager is StaggeredGridLayoutManager) {
        orientation = layoutManager.orientation
    } else {
        orientation = RecyclerView.VERTICAL
    }
    return orientation
}

fun RecyclerView.getSpanCount(): Int {
    var spanCount: Int? = null
    val layoutManager = this.layoutManager
    if (layoutManager is GridLayoutManager) {
        spanCount = layoutManager.spanCount
    } else if (layoutManager is StaggeredGridLayoutManager) {
        spanCount = layoutManager.spanCount
    } else {
        spanCount = 1
    }
    return spanCount
}

fun RecyclerView.getSpanSize(position: Int): Int {
    var spanSize: Int? = null
    val layoutManager = this.layoutManager
    if (layoutManager is GridLayoutManager) {
        spanSize = layoutManager.spanSizeLookup.getSpanSize(position)
    } else {
        spanSize = 1
    }
    return spanSize
}

fun RecyclerView.getGroupIndex(position: Int): Int {
    var index: Int = position
    val layoutManager = this.layoutManager
    if (layoutManager is GridLayoutManager) {
        index = layoutManager.spanSizeLookup.getSpanGroupIndex(position, layoutManager.spanCount)
    }
    return index
}

fun RecyclerView.getGroupCount(itemCount: Int): Int {
    var count: Int = itemCount
    val layoutManager = this.layoutManager
    if (layoutManager is GridLayoutManager) {
        val sizeLookup = layoutManager.spanSizeLookup
        val spanCount = layoutManager.spanCount
        val groupCount = (0..itemCount-1).count { sizeLookup.getSpanIndex(it, spanCount) == 0 }
        return groupCount
    }
    return count
}

fun RecyclerView.getAccumulatedSpanInLine(spanSize: Int, itemPosition: Int, groupIndex: Int): Int {
    var lineAccumulatedSpan = spanSize
    var tempPos: Int = itemPosition - 1
    while (tempPos >= 0) {
        val tempGroupIndex = this.getGroupIndex(tempPos)
        if (tempGroupIndex == groupIndex) {
            val tempSpanSize = this.getSpanSize(itemPosition)
            lineAccumulatedSpan += tempSpanSize
        } else {
            // if the group index change, it means that line is changed
            break
        }
        tempPos--
    }
    return lineAccumulatedSpan
}

fun Int.colorToDrawable(): Drawable {
    return ColorDrawable(this)
}
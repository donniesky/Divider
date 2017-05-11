package me.donnie.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.IntDef
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import me.donnie.divider.factory.*

/**
 * @author donnieSky
 * @created_at 2017/5/11.
 * @description
 * @version
 */
class Divider constructor(@param:Type val type: Long,
                          var visibleFactory: VisibleFactory?,
                          var drawableFactory: DrawableFactory?,
                          var tintFactory: TintFactory?,
                          var sizeFactory: SizeFactory?,
                          var marginFactory: MarginFactory?) : RecyclerView.ItemDecoration() {

    companion object {
        private val TAG = Divider::class.simpleName
        const val TYPE_SPACE = -1L
        const val TYPE_COLOR = 0L
        const val TYPE_DRAWABLE = 1L

        @IntDef(TYPE_SPACE, TYPE_COLOR, TYPE_DRAWABLE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Type

        fun with(context: Context): Builder {
            return Builder(context)
        }
    }

    class Builder {

        companion object {
            private const val INT_DEF = -1
        }

        private var context: Context? = null
        private var color: Int? = null
        private var drawable: Drawable? = null
        private var tint: Int? = null
        private var size: Int? = null
        private var marginSize: Int? = null
        private var hideLastDivider: Boolean = false

        private var visibleFactory: VisibleFactory? = null
        private var drawableFactory: DrawableFactory? = null
        private var tintFactory: TintFactory? = null
        private var sizeFactory: SizeFactory? = null
        private var marginFactory: MarginFactory? = null

        private @Type var type: Long? = null

        constructor(context: Context) {
            this.context = context
            this.size = INT_DEF
            this.marginSize = INT_DEF
            this.type = TYPE_COLOR
        }

        fun space(): Builder {
            this.type = TYPE_SPACE
            return this
        }

        fun color(@ColorInt color: Int): Builder {
            this.color = color
            this.type = TYPE_COLOR
            return this
        }

        fun drawable(drawable: Drawable): Builder {
            this.drawable = drawable
            this.type = TYPE_DRAWABLE
            return this
        }

        fun tint(@ColorInt color: Int): Builder {
            this.tint = color
            return this
        }

        fun size(size: Int): Builder {
            this.size = size
            return this
        }

        fun marginSize(marginSize: Int): Builder {
            this.marginSize = marginSize
            return this
        }

        fun hideLastDivider(): Builder {
            this.hideLastDivider = true
            return this
        }

        fun visibleFactory(visibleFactory: VisibleFactory?): Builder {
            this.visibleFactory = visibleFactory
            return this
        }

        fun drawableFactory(drawableFactory: DrawableFactory?): Builder {
            this.drawableFactory = drawableFactory
            return this
        }

        fun tintFactory(tintFactory: TintFactory?): Builder {
            this.tintFactory = tintFactory
            return this
        }

        fun sizeFactory(sizeFactory: SizeFactory?): Builder {
            this.sizeFactory = sizeFactory
            return this
        }

        fun marginFactory(marginFactory: MarginFactory?): Builder {
            this.marginFactory = marginFactory
            return this
        }

        fun build(): Divider {
            if (visibleFactory == null) {
                if (hideLastDivider) {
                    visibleFactory = VisibleFactory.getLastItemInvisibleFactory()
                } else {
                    visibleFactory = VisibleFactory.getDefault()
                }
            }

            if (sizeFactory == null) {
                if (this.size == INT_DEF) {
                    sizeFactory = SizeFactory.getDefault(this.context!!)
                } else {
                    sizeFactory = SizeFactory.getGeneralFactory(this.size!!)
                }
            }

            if (drawableFactory == null) {
                var currentDrawable: Drawable? = null
                when(this.type) {
                    TYPE_COLOR -> {
                        if (this.color != null) {
                            currentDrawable = color!!.colorToDrawable()
                        }
                    }
                    TYPE_DRAWABLE -> {
                        if (this.drawable != null) {
                            currentDrawable = drawable
                        }
                    }
                }
                if (currentDrawable == null) {
                    drawableFactory = DrawableFactory.getDefault(this.context!!)
                } else {
                    drawableFactory = DrawableFactory.getGeneralFactory(currentDrawable)
                }
            }

            if (tintFactory == null) {
                if (this.tint != null) {
                    this.tintFactory = TintFactory.getGeneralFactory(tint!!)
                }
            }

            if (marginFactory == null) {
                if (marginSize == INT_DEF) {
                    marginFactory = MarginFactory.getDefault(this.context!!)
                } else {
                    marginFactory = MarginFactory.getGeneralFactory(marginSize!!)
                }
            }

            return Divider(type!!, visibleFactory, drawableFactory, tintFactory, sizeFactory, marginFactory)
        }
    }

    fun addTo(recyclerView: RecyclerView) {
        removeDivider(recyclerView)
        recyclerView.addItemDecoration(this)
    }

    fun removeDivider(recyclerView: RecyclerView) {
        recyclerView.removeItemDecoration(this)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter
        var listSize: Int = adapter.itemCount

        if (type == TYPE_SPACE || adapter == null || listSize == 0) {
            return
        }

        var left: Int? = null
        var top: Int? = null
        var right: Int? = null
        var bottom: Int? = null

        val orientation = parent.getOrientation()
        val spanCount = parent.getSpanCount()
        val childCount = parent.childCount
        for (i in 0..childCount-1) {
            val child = parent.getChildAt(i)
            val itemPosition = parent.getChildAdapterPosition(child)
            val groupIndex = parent.getGroupIndex(itemPosition)
            val groupCount = parent.getGroupCount(listSize)

            var divider = drawableFactory!!.drawableForItem(groupCount, groupIndex)
            val showDivider = visibleFactory!!.displayDividerForItem(groupCount, groupIndex)

            if (divider == null || showDivider == VisibleFactory.SHOW_NONE) continue

            val spanSize = parent.getSpanSize(itemPosition)
            val lineAccumulatedSpan = parent.getAccumulatedSpanInLine(spanSize, itemPosition, groupIndex)

            var margin = marginFactory!!.marginSizeForItem(groupCount, groupIndex)
            var size = sizeFactory!!.sizeForItem(divider, orientation, groupCount, groupIndex)

            if (tintFactory != null) {
                val tint = tintFactory!!.tintForItem(groupCount, groupIndex)
                val wrappedDrawable = DrawableCompat.wrap(divider)
                DrawableCompat.setTint(wrappedDrawable, tint)
                divider = wrappedDrawable
            }

            val params = child.layoutParams as RecyclerView.LayoutParams

            var halfSize = if (size < 2) size else size / 2

            size = if (showDivider == VisibleFactory.SHOW_ITEMS_ONLY) 0 else size
            halfSize = if (showDivider == VisibleFactory.SHOW_GROUP_ONLY) 0 else halfSize

            val childBottom = child.bottom
            val childTop = child.top
            val childRight = child.right
            val childLeft = child.left

            val lastElementInSpanSize = if (itemPosition == listSize - 1) halfSize * 2 else halfSize

            val useCellMargin = margin == 0

            var marginToAddBefore = 0
            var marginToAddAfter = 0

            if (orientation == RecyclerView.VERTICAL) {
                if (spanCount > 1 && spanSize < spanCount) {
                    top = childTop + margin
                    bottom = childBottom - margin

                    if (useCellMargin) {
                        if (groupIndex > 0) {
                            top -= params.topMargin
                        }
                        if (groupIndex < groupCount - 1 || size > 0) {
                            bottom += params.bottomMargin
                        }
                        bottom += size
                    }

                    if (lineAccumulatedSpan == spanSize) {
                        left = childRight + margin + params.rightMargin
                        right = left + lastElementInSpanSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)
                        if (useCellMargin) {
                            marginToAddAfter = params.rightMargin
                        }
                    } else if (lineAccumulatedSpan == spanCount) {
                        right = childLeft - margin - params.leftMargin
                        left = right - halfSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)
                        if (useCellMargin) {
                            marginToAddBefore = params.leftMargin
                        }
                    } else {
                        right = childLeft - margin - params.leftMargin
                        left = right - halfSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)

                        left = childRight + margin + params.rightMargin
                        right = left + lastElementInSpanSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)
                        if (useCellMargin) {
                            marginToAddAfter = params.rightMargin
                            marginToAddBefore = params.leftMargin
                        }
                    }
                }

                top = childBottom + params.bottomMargin
                bottom = top + size
                left = childLeft + margin - marginToAddBefore
                right = childRight - margin + marginToAddAfter

                setBoundsAndDraw(divider, c, left, top, right, bottom)
            } else {
                if (spanCount > 1 && spanSize < spanCount) {
                    left = childLeft + margin
                    right = childRight - margin
                    if (useCellMargin) {
                        if (groupIndex > 0) {
                            left -= params.leftMargin
                        }
                        if (groupIndex < groupCount - 1 || size > 0) {
                            right += params.rightMargin
                        }
                        right += size
                    }

                    if (lineAccumulatedSpan == spanSize) {
                        top = childBottom + margin + params.bottomMargin
                        bottom = top + lastElementInSpanSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)
                        if (useCellMargin) {
                            marginToAddAfter = params.bottomMargin
                        }
                    } else if (lineAccumulatedSpan == spanCount) {
                        bottom = childTop - margin - params.topMargin
                        top = bottom - halfSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)
                        if (useCellMargin) {
                            marginToAddBefore = params.topMargin
                        }
                    } else {
                        bottom = childTop - margin - params.topMargin
                        top = bottom - halfSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)

                        top = childBottom + margin + params.bottomMargin
                        bottom = top + lastElementInSpanSize

                        setBoundsAndDraw(divider, c, left, top, right, bottom)
                        if (useCellMargin) {
                            marginToAddAfter = params.bottomMargin
                            marginToAddBefore = params.topMargin
                        }
                    }
                }

                bottom = childBottom - margin + marginToAddAfter
                top = childTop + margin - marginToAddBefore
                left = childRight + params.rightMargin
                right = left + size

                setBoundsAndDraw(divider, c, left, top, right, bottom)
            }
        }
    }

    private fun setBoundsAndDraw(drawable: Drawable, canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        drawable.setBounds(left, top, right, bottom)
        drawable.draw(canvas)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val listSize = parent.adapter.itemCount
        if (listSize <= 0) {
            return
        }
        val itemPosition = parent.getChildAdapterPosition(view)
        val groupIndex = parent.getGroupIndex(itemPosition)
        val groupCount = parent.getGroupCount(listSize)

        val showDivider = visibleFactory!!.displayDividerForItem(groupCount, groupIndex)
        if (showDivider == VisibleFactory.SHOW_NONE) {
            return
        }

        val orientation = parent.getOrientation()
        val spanCount = parent.getSpanCount()
        val spanSize = parent.getSpanSize(itemPosition)
        val lineAccumulatedSpan = parent.getAccumulatedSpanInLine(spanSize, itemPosition, groupIndex)

        val divider = drawableFactory!!.drawableForItem(groupCount, groupIndex)
        var size = sizeFactory!!.sizeForItem(divider, orientation, groupCount, groupIndex)
        val marginSize = marginFactory!!.marginSizeForItem(groupCount, groupIndex)

        var halfSize = size / 2 + marginSize

        size = if (showDivider == VisibleFactory.SHOW_ITEMS_ONLY) 0 else size
        Log.d("size", size.toString())
        halfSize = if (showDivider == VisibleFactory.SHOW_GROUP_ONLY) 0 else halfSize

        if (orientation == RecyclerView.VERTICAL) {
            if (spanCount == 1 || spanSize == spanCount) {
                outRect.set(0, 0, 0, size)
            } else if (lineAccumulatedSpan == spanSize) {
                outRect.set(0, 0, halfSize, size)
            } else if (lineAccumulatedSpan == spanCount) {
                outRect.set(halfSize, 0, 0, size)
            } else {
                outRect.set(halfSize, 0, halfSize, size)
            }
        } else {
            if (spanCount == 1 || spanSize == spanCount) {
                outRect.set(0, 0, size, 0)
            } else if (lineAccumulatedSpan == spanSize) {
                outRect.set(0, 0, size, halfSize)
            } else if (lineAccumulatedSpan == spanCount) {
                outRect.set(0, halfSize, size, 0)
            } else {
                outRect.set(0, halfSize, size, halfSize)
            }
        }
    }
}
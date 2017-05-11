package me.donnie.divider.factory

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import me.donnie.library.R

/**
 * @author donnieSky
 * @created_at 2017/5/11.
 * @description
 * @version
 */
abstract class SizeFactory {

    companion object {
        private var defaultFactory: SizeFactory? = null
        fun getDefault(context: Context): SizeFactory {
            if (defaultFactory == null) {
                defaultFactory = Default(context)
            }
            return defaultFactory!!
        }

        fun getGeneralFactory(size: Int): SizeFactory {
            return General(size)
        }

        class Default : SizeFactory {

            private var defaultSize: Int? = null

            constructor(context: Context) {
                defaultSize = context.resources.getDimensionPixelSize(R.dimen.divider_size)
            }

            override fun sizeForItem(drawable: Drawable?, orientation: Int, groupCount: Int, groupIndex: Int): Int {
                var size: Int? = null
                if (drawable != null) {
                    size = if (orientation == RecyclerView.VERTICAL) drawable.intrinsicHeight else drawable.intrinsicWidth
                } else {
                    size = -1
                }

                if (size == -1) {
                    size = defaultSize
                }
                return size!!
            }
        }

        class General : SizeFactory {

            private var size: Int? = null

            constructor(size: Int) {
                this.size = size
            }

            override fun sizeForItem(drawable: Drawable?, orientation: Int, groupCount: Int, groupIndex: Int): Int {
                return size!!
            }
        }
    }

    abstract fun sizeForItem(drawable: Drawable?, orientation: Int, groupCount: Int, groupIndex: Int): Int
}
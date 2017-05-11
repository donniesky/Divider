package me.donnie.divider.factory

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import me.donnie.divider.colorToDrawable
import me.donnie.library.R

/**
 * @author donnieSky
 * @created_at 2017/5/11.
 * @description
 * @version
 */
abstract class DrawableFactory {

    companion object {
        private var defaultFactory: DrawableFactory? = null
        fun getDefault(context: Context): DrawableFactory {
            if (defaultFactory == null) {
                defaultFactory = Default(context)
            }
            return defaultFactory!!
        }

        fun getGeneralFactory(drawable: Drawable): DrawableFactory {
            return General(drawable)
        }

        class Default : DrawableFactory {

            private var defaultDrawable: Drawable? = null

            constructor(context: Context) {
                defaultDrawable = ContextCompat.getColor(context, R.color.divider_color).colorToDrawable()
            }

            override fun drawableForItem(groupCount: Int, groupIndex: Int): Drawable {
                return defaultDrawable!!
            }
        }

        class General : DrawableFactory {

            private var drawable: Drawable? = null

            constructor(drawable: Drawable) {
                this.drawable = drawable
            }

            override fun drawableForItem(groupCount: Int, groupIndex: Int): Drawable {
                return drawable!!
            }
        }
    }

    abstract fun drawableForItem(groupCount: Int, groupIndex: Int): Drawable
}
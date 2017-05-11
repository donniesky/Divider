package me.donnie.divider.factory

import android.support.annotation.ColorInt

/**
 * @author donnieSky
 * @created_at 2017/5/11.
 * @description
 * @version
 */
abstract class TintFactory {

    companion object {

        fun getGeneralFactory(@ColorInt tint: Int): TintFactory {
            return General(tint)
        }

        class General : TintFactory {

            private var tint: Int? = null

            constructor(@ColorInt tint: Int) {
                this.tint = tint
            }

            override fun tintForItem(groupCount: Int, groupIndex: Int): Int {
                return tint!!
            }

        }
    }

    abstract fun tintForItem(groupCount: Int, groupIndex: Int): Int
}
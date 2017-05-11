package me.donnie.divider.factory

import android.content.Context
import me.donnie.library.R

/**
 * @author donnieSky
 * @created_at 2017/5/11.
 * @description
 * @version
 */
abstract class MarginFactory {

    companion object {
        private var defaultFactory: MarginFactory? = null
        fun getDefault(context: Context): MarginFactory {
            if (defaultFactory == null) {
                defaultFactory = Default(context)
            }
            return defaultFactory!!
        }

        fun getGeneralFactory(marginSize: Int): MarginFactory {
            return General(marginSize)
        }

        class Default : MarginFactory {

            private var defaultMarginSize: Int? = null

            constructor(context: Context) {
                defaultMarginSize = context.resources.getDimensionPixelSize(R.dimen.margin_size)
            }

            override fun marginSizeForItem(groupCount: Int, groupIndex: Int): Int {
                return defaultMarginSize!!
            }
        }

        class General : MarginFactory {

            private var marginSize: Int? = null

            constructor(marginSize: Int) {
                this.marginSize = marginSize
            }

            override fun marginSizeForItem(groupCount: Int, groupIndex: Int): Int {
                return marginSize!!
            }
        }
    }

    abstract fun marginSizeForItem(groupCount: Int, groupIndex: Int): Int

}
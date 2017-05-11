package me.donnie.divider.factory

import android.support.annotation.IntDef

/**
 * @author donnieSky
 * @created_at 2017/5/11.
 * @description
 * @version
 */
abstract class VisibleFactory {

    companion object {
        private var defaultFactory: VisibleFactory? = null

        fun getDefault(): VisibleFactory {
            if (defaultFactory == null) {
                defaultFactory = Default()
            }
            return defaultFactory!!
        }

        fun getLastItemInvisibleFactory(): VisibleFactory {
            return LastItemInvisible()
        }

        const val SHOW_NONE = 0L
        const val SHOW_ITEMS_ONLY = 1L
        const val SHOW_GROUP_ONLY = 2L
        const val SHOW_ALL = 3L

        @IntDef(SHOW_NONE, SHOW_ITEMS_ONLY, SHOW_GROUP_ONLY, SHOW_ALL)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Show

        class Default : VisibleFactory {

            constructor()

            override fun displayDividerForItem(groupCount: Int, groupIndex: Int): Long {
                return SHOW_ALL
            }
        }

        class LastItemInvisible : VisibleFactory() {

            override fun displayDividerForItem(groupCount: Int, groupIndex: Int): Long {
                return if (groupIndex == groupCount - 1) SHOW_ITEMS_ONLY else SHOW_ALL
            }
        }
    }

    abstract fun displayDividerForItem(groupCount: Int, groupIndex: Int): Long
}
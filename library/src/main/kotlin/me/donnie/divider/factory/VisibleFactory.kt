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

        fun getTopItemVisibleFactory(): VisibleFactory {
            return TopItemVisible()
        }

        const val SHOW_NONE = 0L
        const val SHOW_ITEMS_ONLY = 1L
        const val SHOW_GROUP_ONLY = 2L
        const val SHOW_ALL = 3L
        const val SHOW_TOP = 4L

        @IntDef(SHOW_NONE, SHOW_ITEMS_ONLY, SHOW_GROUP_ONLY, SHOW_ALL, SHOW_TOP)
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

        class TopItemVisible : VisibleFactory() {
            override fun displayDividerForItem(groupCount: Int, groupIndex: Int): Long {
                return if (groupIndex == 0) SHOW_TOP else SHOW_ALL
            }
        }
    }

    abstract fun displayDividerForItem(groupCount: Int, groupIndex: Int): Long
}
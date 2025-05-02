package com.ssafy.sotory.common.presentation.ui

import androidx.annotation.StringRes
import com.ssafy.sotory.R
import com.ssafy.sotory.common.BottomRoute

sealed class BottomNavItem(
    @StringRes val title: Int,
    val icon: Int,
    val screenRoute: BottomRoute,
) {
    abstract val name: String

    data object Diary :
        BottomNavItem(
            R.string.bottom_item_diary,
            R.drawable.collection,
            BottomRoute.DiaryRoute
        ) {
        override val name: String = "기록"
    }

    data object Consume :
        BottomNavItem(
            R.string.bottom_item_consume,
            R.drawable.story,
            BottomRoute.ConsumeRoute
        ) {
        override val name: String = "작성"
    }

    data object Profile :
        BottomNavItem(
            R.string.bottom_item_profile,
            R.drawable.profile,
            BottomRoute.MyRoomRoute
        ) {
        override val name: String = "프로필"
    }

}
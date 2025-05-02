package com.ssafy.sotory.presentation.diary.ui

import CardSizeUtil
import android.util.Log
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aghajari.compose.lazyswipecards.LazySwipeCards
import com.ssafy.sotory.util.ScreenSizeUtil

@Composable
fun CardStack(modifier: Modifier = Modifier, imageUrls: MutableList<String>) {


    val screenSize = ScreenSizeUtil.getScreenSizeDp()
    val cardSize = CardSizeUtil.getDetailCardSize(screenSize.first.value)

    Log.d("CardStack", "imageUrls: $imageUrls")
    val list = mutableListOf(10)

    LazySwipeCards(
        cardModifier = Modifier,
        cardShape = RoundedCornerShape(16.dp),
        cardShadowElevation = 4.dp,
        visibleItemCount = 4,
        rotateDegree = 15f,
        translateSize = 24.dp,
        animationSpec = SpringSpec(),
        swipeThreshold = 1f,
        scaleFactor = ScaleFactor(
            scaleX = 0.1f, scaleY = 0.1f
        ),
        contentPadding = PaddingValues(
            vertical = 24.dp * 4, // visibleItemCount
            horizontal = 24.dp
        ),

        ) {
        onSwiped { item, direction ->
//            println("OnSwiped: $item to ${direction.name}")
            item?.let {
                list.add(it as Int)
            }
        }
        onSwiping { dx, ratio, direction ->
//            println("$dx : $ratio : ${direction.name}")
        }

        items(list) { it ->
            OtherCard(

                it.toString(),
                modifier
                    .height(
                        cardSize.height
                    )
                    .width(cardSize.width)
            )
        }
    }
}

@Composable
fun OtherCard(imgSrc: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imgSrc, contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier
    )
//
//    Column {
//        Text("$imgSrc", style = Heading_L_Bold.copy(color = Black100))
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Box(modifier = modifier) {
//
//        }
//
//    }

}
package com.ssafy.sotory.common.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.BottomNavigationDefaults.windowInsets
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ssafy.sotory.common.currentBottomRoute
import com.ssafy.sotory.ui.theme.ActivateColorFrom
import com.ssafy.sotory.ui.theme.BottomBarBackgroundColor
import com.ssafy.sotory.ui.theme.Caption_M_Medium
import com.ssafy.sotory.ui.theme.DeactivateColor
import timber.log.Timber


@Composable
fun SotoryBottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val items = listOf(
        BottomNavItem.Diary,
        BottomNavItem.Consume,
        BottomNavItem.Profile,
    )

    val currentRoute = currentBottomRoute(navController)

//    Log.d("route", "$currentRoute")
    AnimatedVisibility(
        visible = currentRoute != null
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
            .background(
                BottomBarBackgroundColor
            )
            .border(0.dp, Color.Transparent)
            .selectableGroup(),
//            horizontalArrangement = Arrangement.spacedBy(NavigationBarItemHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.hasRoute(screen.screenRoute::class)
                    } == true
                    val iconColor: Color = if (isSelected) ActivateColorFrom else DeactivateColor

                    NavigationBarItem(
                        selected = isSelected,// currentRoute == item.screenRoute,
                        colors = NavigationBarItemColors(
                            selectedIconColor = ActivateColorFrom,
                            selectedTextColor = ActivateColorFrom,
                            selectedIndicatorColor = BottomBarBackgroundColor,
                            unselectedIconColor = DeactivateColor,
                            unselectedTextColor = DeactivateColor,
                            disabledIconColor = DeactivateColor,
                            disabledTextColor = DeactivateColor,
                        ),
                        label = {
                            Text(
                                text = stringResource(id = screen.title), style = Caption_M_Medium
                            )
                        },
                        icon = {
                            Image(
                                modifier = Modifier.size(32.dp),
                                painter = painterResource(screen.icon),
                                contentDescription = stringResource(screen.title),
                                colorFilter = ColorFilter.tint(color = iconColor)
                            )
                        },
                        onClick = {
                            navController.navigate(screen.screenRoute) {
                                navBackStackEntry?.destination?.route?.let {
                                    Timber.d("entry = $it")
                                    popUpTo(it) {
                                        inclusive = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            })

    }
}
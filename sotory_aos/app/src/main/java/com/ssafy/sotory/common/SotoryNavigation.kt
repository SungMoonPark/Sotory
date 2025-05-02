package com.ssafy.sotory.common

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.composableLambda
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.ssafy.sotory.AuthViewModel
import com.ssafy.sotory.common.presentation.PermissionScreen
import com.ssafy.sotory.common.presentation.SplashScreen
import com.ssafy.sotory.domain.diary.DiaryModel
import com.ssafy.sotory.domain.diary.DiaryType
import com.ssafy.sotory.domain.payment.PaymentModel
import com.ssafy.sotory.domain.payment.PaymentType
import com.ssafy.sotory.presentation.auth.AccountVerificationScreen
import com.ssafy.sotory.presentation.auth.LoginScreen
import com.ssafy.sotory.presentation.auth.TermsScreen
import com.ssafy.sotory.presentation.auth.viewmodel.AccountVerificationViewModel
import com.ssafy.sotory.presentation.auth.viewmodel.TermsNav
import com.ssafy.sotory.presentation.diary.DiaryCheckScreen
import com.ssafy.sotory.presentation.diary.DiaryDateScreen
import com.ssafy.sotory.presentation.diary.DiaryEditScreen
import com.ssafy.sotory.presentation.diary.DiaryOtherCardScreen
import com.ssafy.sotory.presentation.diary.DiaryResultScreen
import com.ssafy.sotory.presentation.diary.DiaryWaitingScreen
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryGenerationViewModel
import com.ssafy.sotory.presentation.diary.viewmodel.DiaryHistoryFormViewModel
import com.ssafy.sotory.presentation.payment.PaymentFormScreen
import com.ssafy.sotory.presentation.payment.PaymentHistoryScreen
import com.ssafy.sotory.presentation.payment.PaymentListScreen
import com.ssafy.sotory.presentation.payment.viewmodel.NavigationAction
import com.ssafy.sotory.presentation.report.ReportScreen
import com.ssafy.sotory.presentation.report.viewmodel.ReportNav
import com.ssafy.sotory.presentation.report.viewmodel.ReportViewModel
import com.ssafy.sotory.presentation.settings.SettingsAddingCardScreen
import com.ssafy.sotory.presentation.settings.SettingsMainScreen
import com.ssafy.sotory.presentation.settings.SettingsMyCardListScreen
import com.ssafy.sotory.presentation.settings.SettingsProfileScreen
import com.ssafy.sotory.presentation.settings.SettingsTermsScreen
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsCardListNav
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsMainNav
import com.ssafy.sotory.presentation.settings.viewmodel.SettingsProfileNav
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.typeOf

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    toastViewModel: SharedToastViewModel,
    navController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val diaryGenerationViewModel: DiaryGenerationViewModel = hiltViewModel()

    NavHost(
        navController = navController, startDestination = Route.SplashRoute
    ) {
        composable<Route.SplashRoute> {
            SplashScreen(navigateToLogin = {
                navController.navigate(Route.AuthBaseRoute.LoginRoute) {
                    popUpTo(Route.SplashRoute) { inclusive = true }
                }
            }, navigateToHome = {
                navController.navigate(BottomRoute.DiaryRoute) {
                    popUpTo(Route.SplashRoute) { inclusive = true }
                }
            }, navigateToPermission = {
                navController.navigate(Route.PermissionRoute) {
                    popUpTo(Route.SplashRoute) { inclusive = true }
                }
            }, isLoggedIn = isLoggedIn
            )
        }
        composable<Route.PermissionRoute> {
            PermissionScreen(onNavigateUp = {
                navController.navigate(BottomRoute.DiaryRoute) {

                }
            })
        }
        composable<BottomRoute.DiaryRoute> {
            DiaryDateScreen(modifier = modifier,
                drawerState = drawerState,
                onNavigatePayment = { date ->
                    navController.navigate(Route.PaymentBaseRoute.PaymentListRoute(date))
                },
                onNavigateForm = {
                    val now = LocalDate.now()
                    val date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                    navController.navigate(
                        Route.DiaryBaseRoute.DiaryCheckRoute(
                            date = date
                        )
                    )
                })
        }
        composable<BottomRoute.ConsumeRoute> {
            PaymentHistoryScreen(toastViewModel = toastViewModel,
                drawerState = drawerState,
                onNavigate = { action ->
                    when (action) {
                        is NavigationAction.ToPaymentAdd -> {
                            navController.navigate(Route.PaymentBaseRoute.PaymentFormRoute(payment = null))
                        }

                        is NavigationAction.ToPaymentEdit -> {
                            Log.d("Route", "AppNavigation: ToDiaryEdit ${action.payment}")
                            navController.navigate(Route.PaymentBaseRoute.PaymentFormRoute(payment = action.payment))
                        }

                        is NavigationAction.ToDiaryEdit -> {
                            Log.d("Route", "AppNavigation: ToPaymentEdit ${action.payment}")
                            navController.navigate(Route.DiaryBaseRoute.DiaryEditRoute(payment = action.payment))
                        }
                    }
                },

                onNavigateCheck = { date ->
                    navController.navigate(Route.DiaryBaseRoute.DiaryCheckRoute(date))
                })
        }
        composable<BottomRoute.MyRoomRoute> {
            val viewModel: ReportViewModel = hiltViewModel()

            ReportScreen(viewModel = viewModel, drawerState = drawerState, onNavigate = { action ->
                when (action) {
                    is ReportNav.ToBack -> {
                        navController.popBackStack()
                    }

                    is ReportNav.ToSetting -> {
                        navController.navigate(Route.MyRoomBaseRoute.SettingMainRoute)
                    }
                }
            })
        }
        authGraph(navController)
        paymentGraph(navController)
        diaryGraph(navController, diaryGenerationViewModel = diaryGenerationViewModel)
        myRoomGraph(navController, drawerState)
    }
}

@Composable
fun currentBottomRoute(navController: NavHostController): BottomRoute? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route // string
    return route?.let {
        return when (route.split(".").last()) {
            BottomRoute.DiaryRoute::class.simpleName -> BottomRoute.DiaryRoute
            BottomRoute.ConsumeRoute::class.simpleName -> BottomRoute.ConsumeRoute
            BottomRoute.MyRoomRoute::class.simpleName -> BottomRoute.MyRoomRoute
            else -> null
        }
    }
}


sealed interface Route {
    @Serializable
    data object SplashRoute : Route

    @Serializable
    data object PermissionRoute : Route

    @Serializable
    data object AuthBaseRoute : Route {
        @Serializable
        data object LoginRoute : Route

        @Serializable
        data object SignUpRoute

        @Serializable
        data object TermsRoute : Route

        @Serializable
        data object AccountVerificationRoute : Route
    }

    @Serializable
    data object PaymentBaseRoute : Route {

        @Serializable
        data class PaymentFormRoute(val payment: PaymentModel? = null) : Route

        @Serializable
        data class PaymentListRoute(val date: String) : Route
    }


    @Serializable
    data object DiaryBaseRoute : Route {
        const val ROUTE = "diary_base"

        @Serializable
        data class DiaryEditRoute(val payment: PaymentModel) : Route {
            companion object {
                const val ROUTE = "diary_edit_route"
            }
        }

        @Serializable
        data class DiaryCheckRoute(val date: String) : Route {
            companion object {
                const val ROUTE = "diary_check_route"
            }
        }

        @Serializable
        data object DiaryWaitingRoute : Route {
            const val ROUTE = "diary_waiting_route"
        }

        @Serializable
        data class DiaryResultRoute(val diary: DiaryModel) : Route {
            companion object {
                const val ROUTE = "diary_result_route"
            }
        }

        @Serializable
        data object DiaryOtherCardRoute : Route {
            const val ROUTE = "diary_other_card_route"
        }
    }

    @Serializable
    data object MyRoomBaseRoute : Route {

        @Serializable
        data object ReportRoute : Route

        @Serializable
        data object SettingMainRoute : Route

        @Serializable
        data object ProfileEditRoute : Route

        @Serializable
        data object MyCardRoute : Route

        @Serializable
        data object AddingCardRoute : Route

        @Serializable
        data object ConnectingCardRoute : Route

        @Serializable
        data object SettingAlarmRoute : Route

        @Serializable
        data object SettingTermsRoute : Route

    }

}

sealed interface BottomRoute {
    @Serializable
    data object MyRoomRoute : BottomRoute

    @Serializable
    data object DiaryRoute : BottomRoute

    @Serializable
    data object ConsumeRoute : BottomRoute
}

@SuppressLint("NewApi")
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation<Route.AuthBaseRoute>(startDestination = Route.AuthBaseRoute.LoginRoute) {
        composable<Route.AuthBaseRoute.LoginRoute> {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Route.AuthBaseRoute.TermsRoute)
                }, navController = navController
            )
        }

        composable<Route.AuthBaseRoute.TermsRoute> {
            TermsScreen(onNavigate = { action ->
                when (action) {
                    is TermsNav.ToBack -> {
                        navController.popBackStack()
                    }

                    is TermsNav.ToNext -> {
                        navController.navigate(Route.AuthBaseRoute.AccountVerificationRoute)
                    }
                }
            })
//            AccountVerificationScreen(
//                navController = naveController,
//                viewModel = AccountVerificationViewModel()
//            )
        }

        composable<Route.AuthBaseRoute.AccountVerificationRoute> {
            AccountVerificationScreen(
                navController = navController, viewModel = AccountVerificationViewModel()
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.paymentGraph(navController: NavHostController) {
    navigation<Route.PaymentBaseRoute>(
        startDestination = Route.PaymentBaseRoute.PaymentFormRoute(payment = null)
    ) {
        composable<Route.PaymentBaseRoute.PaymentFormRoute>(
            typeMap = mapOf(
                typeOf<PaymentModel?>() to PaymentType,
            )
        ) {

            // 이전 백스택 엔트리 가져오기
            val previousBackStackEntry = navController.previousBackStackEntry

            // 이전 백스택에 있는 ToastViewModel 참조하기
            val toastViewModel = if (previousBackStackEntry != null) {
                // 이전 화면의 ViewModelStoreOwner에서 ViewModel 가져오기
                hiltViewModel<SharedToastViewModel>(viewModelStoreOwner = previousBackStackEntry)
            } else {
                // 이전 화면이 없는 경우, 현재 화면에서 새로운 ViewModel 생성
                hiltViewModel<SharedToastViewModel>()
            }


            val args = it.toRoute<Route.PaymentBaseRoute.PaymentFormRoute>()
            val paymentModel = args.payment
            Log.d("Route", "paymentGraph: $paymentModel")
            PaymentFormScreen(
                viewModel = hiltViewModel(),
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                paymentModel = paymentModel,
                toastViewModel = toastViewModel,
            )
        }
        composable<Route.PaymentBaseRoute.PaymentListRoute> {
            PaymentListScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                date = it.toRoute<Route.PaymentBaseRoute.PaymentListRoute>().date
            )
        }

    }
}

fun NavGraphBuilder.diaryGraph(
    navController: NavHostController,
    diaryGenerationViewModel: DiaryGenerationViewModel,
) {
    // 고정된 문자열로 startDestination 설정
    navigation<Route.DiaryBaseRoute>(
        startDestination = Route.DiaryBaseRoute.DiaryEditRoute(
            payment = PaymentModel(
                paymentDiaryId = "",
                categoryName = "",
                diary = "",
                merchantName = "",
                paymentId = "",
                transactionBalance = "",
                transactionTime = "",
                isUserAdded = false
            )
        )
    ) {

        // DiaryEditRoute 경로 설정
        composable<Route.DiaryBaseRoute.DiaryEditRoute>(
//            route = "diary_edit_route", // 고정 경로 추가
            typeMap = mapOf(
                typeOf<PaymentModel>() to PaymentType,
            )
        ) {
            val args = it.toRoute<Route.DiaryBaseRoute.DiaryEditRoute>()
            val diaryFormViewModel =
                hiltViewModel<DiaryHistoryFormViewModel, DiaryHistoryFormViewModel.DiaryHistoryViewModelFactory>(
                    key = args.payment.paymentDiaryId
                ) { factory ->
                    factory.create(args.payment.paymentDiaryId!!)
                }
            DiaryEditScreen(args.payment,
                viewModel = diaryFormViewModel,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() })
        }

        composable<Route.DiaryBaseRoute.DiaryCheckRoute>(
//            route = "diary_check_route/{date}"
        ) {
            DiaryCheckScreen(
                diaryGenerationViewModel = diaryGenerationViewModel,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                date = it.toRoute<Route.DiaryBaseRoute.DiaryCheckRoute>().date,
                onNavigateWrite = {},
                onNavigateWaiting = {
                    navController.navigate(Route.DiaryBaseRoute.DiaryWaitingRoute)
//                    navController.navigate(Route.DiaryBaseRoute.DiaryOtherCardRoute)
                },
            )
        }

        composable<Route.DiaryBaseRoute.DiaryWaitingRoute>(
//            route = "diary_waiting_route"
        ) {
            DiaryWaitingScreen(canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                viewModel = diaryGenerationViewModel,
                onNavigateComplete = { diary ->
                    Log.d("Route", "AppNavigation: DiaryWaitingRoute $diary")
                    // 직렬화된 문자열로 변환
                    val diaryJson = Json.encodeToString(DiaryModel.serializer(), diary)
                    // 안전하게 URI 인코딩
                    val encodedDiary = java.net.URLEncoder.encode(diaryJson, "UTF-8")
                    // 고정 경로로 이동하면서 인코딩된 파라미터 전달
                    navController.navigate("diary_result_route/$encodedDiary") {
                        popUpTo(Route.DiaryBaseRoute.DiaryCheckRoute.ROUTE) {}
                    }
                },
                onNavigateOtherCard = {
                    navController.navigate(Route.DiaryBaseRoute.DiaryOtherCardRoute)
                })
        }

        composable<Route.DiaryBaseRoute.DiaryOtherCardRoute> {
            DiaryOtherCardScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                viewModel = diaryGenerationViewModel,
                onNavigateComplete = { diary ->
                    Log.d("Route", "AppNavigation: DiaryWaitingRoute $diary")
                    // 직렬화된 문자열로 변환
                    val diaryJson = Json.encodeToString(DiaryModel.serializer(), diary)
                    // 안전하게 URI 인코딩
                    val encodedDiary = java.net.URLEncoder.encode(diaryJson, "UTF-8")
                    // 고정 경로로 이동하면서 인코딩된 파라미터 전달
                    navController.navigate("diary_result_route/$encodedDiary") {
                        popUpTo(Route.DiaryBaseRoute.DiaryCheckRoute.ROUTE) {}
                    }
                },
            )
        }

        composable(route = "diary_result_route/{diary}", arguments = listOf(navArgument("diary") {
            type = NavType.StringType
        })) { backStackEntry ->
            // URL 디코딩 후 객체로 변환
            val encodedDiary = backStackEntry.arguments?.getString("diary") ?: ""
            val decodedDiary = java.net.URLDecoder.decode(encodedDiary, "UTF-8")
            val diary = Json.decodeFromString<DiaryModel>(decodedDiary)

            DiaryResultScreen(canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                diary = diary,
                onNavigateHome = {
                    navController.navigate(BottomRoute.DiaryRoute) {
                        popUpTo(BottomRoute.DiaryRoute) {
                            inclusive = true
                        }
                    }
                })
        }


    }
}


fun NavGraphBuilder.myRoomGraph(
    navController: NavHostController,
    drawerState: DrawerState,
) {
    navigation<Route.MyRoomBaseRoute>(
        startDestination = Route.MyRoomBaseRoute.ReportRoute
    ) {
        composable<Route.MyRoomBaseRoute.ReportRoute> {
            ReportScreen(viewModel = hiltViewModel(),
                drawerState = drawerState,
                onNavigate = { action ->
                    when (action) {
                        is ReportNav.ToBack -> {
                            navController.popBackStack()
                        }

                        is ReportNav.ToSetting -> {
                            navController.navigate(Route.MyRoomBaseRoute.SettingMainRoute)
                        }
                    }
                })
        }

        composable<Route.MyRoomBaseRoute.SettingMainRoute>{
            SettingsMainScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
//                drawerState = drawerState,
                navigateUp = { navController.popBackStack() },
                onNavigate = { action ->
                    when (action) {
                        is SettingsMainNav.ToBack -> {
                            navController.popBackStack()
                        }

                        is SettingsMainNav.ToEditProfile -> {
                            navController.navigate(Route.MyRoomBaseRoute.ProfileEditRoute)
                        }

                        is SettingsMainNav.ToListCard -> {
                            navController.navigate(Route.MyRoomBaseRoute.MyCardRoute)
                        }

                        is SettingsMainNav.ToSetAlarm -> {
                            navController.navigate(Route.MyRoomBaseRoute.SettingAlarmRoute)
                        }

                        is SettingsMainNav.ToTerms -> {
                            navController.navigate(Route.MyRoomBaseRoute.SettingTermsRoute)
                        }

                        is SettingsMainNav.ToLogOut -> {
                            navController.navigate(Route.AuthBaseRoute.LoginRoute) {
                                popUpTo(Route.SplashRoute) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                })
        }

        composable<Route.MyRoomBaseRoute.ProfileEditRoute> {
            SettingsProfileScreen(canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                viewModel = hiltViewModel(),
                onNavigate = { action ->
                    when (action) {
                        is SettingsProfileNav.ToBack -> {
                            navController.popBackStack()
                        }
                    }
                })
        }

        composable<Route.MyRoomBaseRoute.MyCardRoute> {
            SettingsMyCardListScreen(canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                viewModel = hiltViewModel(),
                onNavigate = { action ->
                    when (action) {
                        is SettingsCardListNav.ToAddCardToConnect -> {
                            navController.navigate(Route.MyRoomBaseRoute.ConnectingCardRoute)
                        }
                        is SettingsCardListNav.ReturnToMyCard -> {
                            navController.navigate(Route.MyRoomBaseRoute.MyCardRoute)
                        }
                    }
                })
        }

        composable<Route.MyRoomBaseRoute.ConnectingCardRoute> {
            SettingsAddingCardScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
                viewModel = hiltViewModel(),
                onNavigate = { action ->
                    when (action) {
                        is SettingsCardListNav.ToAddCardToConnect -> {
                            navController.navigate(Route.MyRoomBaseRoute.ConnectingCardRoute)
                        }
                        is SettingsCardListNav.ReturnToMyCard -> {
                            navController.popBackStack(Route.MyRoomBaseRoute.MyCardRoute, inclusive = false)
                        }
                    }
                }
            )
        }

//        composable<Route.MyRoomBaseRoute.SettingAlarmRoute>{
//            SettingsAlarmScreen(
//                canNavigateBack = navController.previousBackStackEntry != null,
//                navigateUp = { navController.popBackStack() },
//                viewModel = hiltViewModel()
//            )
//        }

        composable<Route.MyRoomBaseRoute.SettingTermsRoute> {
            SettingsTermsScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.popBackStack() },
            )
        }
    }
}

//// 별도의 함수로 menuGraph 범위에 한정된 keyWeViewModel을 생성하는 예시입니다.
//@SuppressLint("UnrememberedGetBackStackEntry")
//@Composable
//fun getScopedKeyWeViewModel(navController: NavHostController): KeyWeViewModel {
//    // "menuGraph"라는 route로 지정한 네비게이션 그래프의 BackStackEntry를 가져옵니다.
//    val parentEntry = remember { navController.getBackStackEntry(Route.MenuBaseRoute) }
//    // 이 BackStackEntry를 스코프로 하여 ViewModel을 생성하면, menuGraph 내에서만 생존하게 됩니다.
//    return hiltViewModel(parentEntry)
//}


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun SharedViewModel(navController: NavHostController): DiaryGenerationViewModel {
    val graphEntry = navController.getBackStackEntry(Route.DiaryBaseRoute)
    return hiltViewModel(graphEntry)
}
package com.ssafy.sotory

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kakao.sdk.common.KakaoSdk
import com.ssafy.sotory.common.AppNavigation
import com.ssafy.sotory.common.BottomRoute
import com.ssafy.sotory.common.Route
import com.ssafy.sotory.common.SharedToastViewModel
import com.ssafy.sotory.common.presentation.BaseDrawerScaffold
import com.ssafy.sotory.data.TokenManager
import com.ssafy.sotory.ui.theme.BackgroundColor
import com.ssafy.sotory.ui.theme.SotoryTheme
import com.ssafy.sotory.util.NotificationUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            // 권한 승인됨, 알림 사용 가능
//        } else {
//            // 권한 거부됨, 사용자에게 설정 화면으로 이동 안내
//            showPermissionRequiredDialog()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 스플래시 화면 설치
        val splashScreen = installSplashScreen()
//        enableEdgeToEdge()

        // 알림 채널 생성
        NotificationUtil.createNotificationChannel(this)

        // 알림 권한 확인 및 요청
//        checkAndRequestNotificationPermission()

        KakaoSdk.init(this, "1cb45917db975185084444ec9d73e2d6")
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val drawerState = scaffoldState.drawerState
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val toastViewModel = hiltViewModel<SharedToastViewModel>()

            SotoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = BackgroundColor
                ) {
                    // AppNavigation 컴포넌트 실행
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val context = LocalContext.current

                    // 앱 전체에 하나의 토스트 리스너만 설정
                    LaunchedEffect(Unit) {
                        toastViewModel.toastEvent.collect { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    BaseDrawerScaffold(
                        onNavigateToRecord = navigateRoot(
                            currentDestination, navController, BottomRoute.DiaryRoute
                        ),
                        onNavigateToWrite = navigateRoot(
                            currentDestination, navController, BottomRoute.ConsumeRoute
                        ),
                        onNavigateToMyRoom = navigateRoot(
                            currentDestination, navController, BottomRoute.MyRoomRoute
                        ),
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Route.AuthBaseRoute.LoginRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        },
                        scaffoldState = scaffoldState,
                        drawerState = drawerState,
                    ) { innerPadding, modifier ->
                        AppNavigation(
                            authViewModel = authViewModel,
                            toastViewModel = toastViewModel,
                            navController = navController,
                            modifier = modifier,
                            drawerState = drawerState,
                        )
                    }
                }
            }
        }
    }

//    private fun checkAndRequestNotificationPermission() {
//        if (!NotificationUtil.hasNotificationPermission(this)) {
//            NotificationUtil.requestNotificationPermission(this, requestPermissionLauncher)
//        }
//    }

    private fun showPermissionRequiredDialog() {
        AlertDialog.Builder(this).setTitle("알림 권한 필요")
            .setMessage("일기 생성 결과를 받으려면 알림 권한이 필요합니다. 설정 화면에서 권한을 허용해주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                NotificationUtil.openNotificationSettings(this)
            }.setNegativeButton("나중에") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    @Composable
    private fun navigateRoot(
        currentDestination: NavDestination?,
        navController: NavHostController,
        rootRoute: BottomRoute,
    ): () -> Unit = {
        val isCurrentNavigation = currentDestination?.hierarchy?.any {
            it.hasRoute(rootRoute::class)
        } ?: false
        if (!isCurrentNavigation) {
            navController.navigate(rootRoute) {
                // 네비게이션 그래프의 ID를 사용하여 모든 백스택을 비움
                popUpTo(navController.graph.id) {
                    // inclusive = true로 설정하여 그래프 자체도 포함해 제거
                    inclusive = true
                }
                // 중복된 경로 생성 방지
                launchSingleTop = true
            }

        }
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null) // null은 로딩 상태를 의미
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            val tokenValid = tokenManager.hasValidToken()
            if (tokenValid) {
                _isLoggedIn.value = true
            } else {
                checkLoginStatus() // 또는 바로 false 처리도 가능
            }
        }
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                val loginStatus = authRepository.isUserLoggedIn()
                _isLoggedIn.value = loginStatus
            } catch (e: Exception) {
                // 에러 처리
                _isLoggedIn.value = false
            }
        }
    }

    fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val success = authRepository.login(username, password)
                if (success) {
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    onError("로그인 실패")
                }
            } catch (e: Exception) {
                onError(e.message ?: "알 수 없는 오류")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
        }
    }
}

@Singleton
class AuthRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USERNAME = stringPreferencesKey("username")
    }

    suspend fun isUserLoggedIn(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }.first()
    }

    suspend fun login(username: String, password: String): Boolean {
        // 실제 구현에서는 API 호출 등의 인증 로직이 들어갑니다
        // 여기서는 간단한 예시로 항상 성공하도록 구현합니다
        dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_USERNAME] = username
        }
        return true
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = false
            preferences.remove(KEY_USERNAME)
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}


//////////////////////////////////////////////

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "keyhash : ${Utility.getKeyHash(this)}")
//        KakaoSdk.init(this, "e42479fed977a84b5de6542c53cc1682")
//        enableEdgeToEdge()
//        setContent {
//            SotoryTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding),
//                        context = this
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier, context: Context) {
//
//
//    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//        if (error != null) {
//            Log.e(TAG, "카카오계정으로 로그인 실패", error)
//        } else if (token != null) {
//            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
//        }
//    }
//
//    Column {
//        Text(
//            text = "Hello $name!",
//            modifier = modifier
//        )
//        Spacer(Modifier.height(100.dp))
//        TextButton(onClick = {
//            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
//            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
//                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
//                    if (error != null) {
//                        Log.e(TAG, "카카오톡으로 로그인 실패", error)
//
//                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
//                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
//                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
//                            return@loginWithKakaoTalk
//                        }
//
//                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
//                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
//                    } else if (token != null) {
//                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
//                    }
//                }
//            } else {
//                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
//            }
//        }) {
//            Text(text = "카카오 로그인")
//        }
//    }
//
//}
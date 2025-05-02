package com.ssafy.sotory


import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ssafy.sotory.ui.theme.SotoryTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import javax.inject.Inject

@HiltAndroidTest
class NavigationTest {

    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Grant [android.Manifest.permission.POST_NOTIFICATIONS] permission.
     */
//    @get:Rule(order = 1)
//    val postNotificationsPermission = GrantPostNotificationsPermissionRule()

    /**
     * Use the primary activity to initialize the app normally.
     */
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() = hiltRule.inject()


    @Test
    fun firstScreen_isForYou() {
        composeTestRule.apply {
            // VERIFY for you is selected
//            onNodeWithText(forYou).assertIsSelected()
        }
    }
}
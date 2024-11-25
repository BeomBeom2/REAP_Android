package com.reap.presentation
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.reap.presentation.ui.main.BottomNavigationBar
import com.reap.presentation.ui.main.MainScreen
import com.reap.presentation.ui.main.ScreenNavigationConfigurations
import com.reap.presentation.ui.main.MainViewModel
import com.reap.presentation.ui.main.RecordBottomSheet
import com.reap.presentation.ui.main.UploadStatus
import com.reap.presentation.ui.main.isValidAudioFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = mockk(relaxed = true)
    }

    @Test
    fun `uploadAudioFile should update uploadStatus correctly`() {
        val mockUri = mockk<Uri>()
        val uploadStatusFlow = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
        every { mainViewModel.uploadStatus } returns uploadStatusFlow

        mainViewModel.uploadAudioFile(mockUri, "일상")

        verify { mainViewModel.uploadAudioFile(mockUri, "일상") }
        assertEquals(UploadStatus.Uploading, uploadStatusFlow.value)
    }

    @Test
    fun `isValidAudioFile should return correct validation for audio files`() {
        val mockContext = mockk<Context>(relaxed = true)
        val validUri = mockk<Uri>()
        val invalidUri = mockk<Uri>()

        every { mockContext.contentResolver.getType(validUri) } returns "audio/mpeg"
        every { mockContext.contentResolver.openFileDescriptor(validUri, "r")?.statSize } returns 10 * 1024 * 1024L
        assertEquals(true, isValidAudioFile(mockContext, validUri))

        every { mockContext.contentResolver.getType(invalidUri) } returns "image/png"
        assertEquals(false, isValidAudioFile(mockContext, invalidUri))
    }

    @Test
    fun `navigation routes should be correctly configured`() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            ScreenNavigationConfigurations(
                navController = navController,
                paddingValues = PaddingValues(),
                bottomBarState = mutableStateOf(true),
                mainViewModel = mainViewModel
            )
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chat").assertIsDisplayed()
    }

    @Test
    fun `uploadStatus should update UI state correctly in RecordBottomSheet`() {
        val uploadStatusFlow = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
        every { mainViewModel.uploadStatus } returns uploadStatusFlow

        composeTestRule.setContent {
            RecordBottomSheet(
                navController = rememberNavController(),
                onDismiss = {},
                mainViewModel = mainViewModel
            )
        }

        uploadStatusFlow.value = UploadStatus.Uploading
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Uploading").assertExists()

        uploadStatusFlow.value = UploadStatus.Success("fileId")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("파일 업로드에 성공하였습니다").assertIsDisplayed()
    }

    @Test
    fun `onRecordClick should update showBottomSheet state correctly`() {
        val showBottomSheet = mutableStateOf(false)

        composeTestRule.setContent {
            val navController = rememberNavController()

            BottomNavigationBar(
                modifier = Modifier,
                navController = navController,
                onRecordClick = { showBottomSheet.value = true }
            )
        }

        composeTestRule.onNodeWithText("Record").performClick()
        assertEquals(true, showBottomSheet.value)
    }

    @Test
    fun `MainScreen should show BottomNavigationBar and RecordBottomSheet based on conditions`() {
        composeTestRule.setContent {
            MainScreen()
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()

        composeTestRule.onNodeWithText("Record").performClick()
        composeTestRule.onNodeWithText("새로 만들기").assertIsDisplayed()
    }
}

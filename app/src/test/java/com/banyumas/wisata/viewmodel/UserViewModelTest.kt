package com.banyumas.wisata.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.banyumas.wisata.DummyUser
import com.banyumas.wisata.R
import com.banyumas.wisata.model.repository.UserRepository
import com.banyumas.wisata.utils.MainDispatcherRule
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userViewModel: UserViewModel
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userRepository = Mockito.mock(UserRepository::class.java)
        userViewModel = UserViewModel(userRepository)
    }

    @get:Rule
    val mainCustomRule = MainDispatcherRule()

    private val emptyEmail = ""
    private val emptyPassword = ""
    private val emptyUsername = ""
    private val invalidEmail = "ramada.com"
    private val invalidPassword = "123"
    private val validUsername = "ramada"
    private val validEmail = "ramada@gmail.com"
    private val validPassword = "ramada123"

    //Register
    @Test
    fun userViewModel_RegisterUserWithEmptyFields_ReturnFieldsRequiredError() {
        userViewModel.registerUser(emptyEmail, emptyPassword, emptyUsername)

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val message = (state as UiState.Error).message
        assertTrue(message is UiText.StringResource)

        assertEquals(R.string.error_fields_required, (message as UiText.StringResource).resId)
    }

    @Test
    fun userViewModel_RegisterUserWithInvalidEmail_ReturnsInvalidEmailError() {
        userViewModel.registerUser(invalidEmail, validPassword, validUsername)

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val message = (state as UiState.Error).message
        assertTrue(message is UiText.StringResource)

        assertEquals(R.string.error_invalid_email, (message as UiText.StringResource).resId)

    }

    @Test
    fun userViewModel_RegisterUserWithInvalidPassword_ReturnsInvalidPasswordError() {
        userViewModel.registerUser(validEmail, invalidPassword, validUsername)

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val message = (state as UiState.Error).message
        assertTrue(message is UiText.StringResource)

        assertEquals(R.string.error_invalid_password, (message as UiText.StringResource).resId)
    }

    @Test
    fun userViewModel_RegisterUserWithValidData_ReturnsEmptyStateAfterSuccess() = runTest {

        Mockito.`when`(userRepository.registerUser(validEmail, validPassword, validUsername))
            .thenReturn(UiState.Success(Unit))


        userViewModel.registerUser(validEmail, validPassword, validUsername)
        advanceUntilIdle()

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Empty)
    }

    @Test
    fun userViewModel_RegisterUserRepositoryError_ReturnsErrorState() = runTest {
        val errorText = UiText.StringResource(R.string.error_register)
        val errorState = UiState.Error(errorText)

        Mockito.`when`(userRepository.registerUser(validEmail, validPassword, validUsername))
            .thenReturn(errorState)

        userViewModel.registerUser(validEmail, validPassword, validUsername)
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Error) {
            assertEquals(UiText.StringResource(R.string.error_register), state.message)
        } else {
            fail("Expected UiState.Error but got $state")
        }
    }

    //Login
    @Test
    fun userViewModel_LoginUserWithInvalidEmail_ReturnsInvalidEmailError() {
        userViewModel.loginUser(invalidEmail, validPassword)


        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val errorMessage = (state as UiState.Error).message
        assertTrue(errorMessage is UiText.StringResource)

        assertEquals(R.string.error_invalid_email, (errorMessage as UiText.StringResource).resId)

    }

    @Test
    fun userViewModel_LoginUserWithEmptyFields_ReturnFieldsRequiredError() {
        userViewModel.loginUser(emptyEmail, emptyPassword)

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val errorMessage = (state as UiState.Error).message
        assertTrue(errorMessage is UiText.StringResource)

        assertEquals(UiText.StringResource(R.string.error_fields_required), errorMessage)
    }

    @Test
    fun userViewModel_LoginUserWithValidCredentials_ReturnSuccessState() = runTest {
        val user = DummyUser.generateUser()

        Mockito.`when`(userRepository.loginUser(user.email, user.hashedPassword))
            .thenReturn(UiState.Success(user))

        userViewModel.loginUser(user.email, user.hashedPassword)
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Success) {
            assertEquals(user, state.data)
        } else {
            fail("Expected UiState.Success but got $state")
        }
    }

    @Test
    fun userViewModel_LoginUserRepositoryError_ReturnsErrorState() = runTest {
        val errorText = UiText.StringResource(R.string.error_login)
        val errorState = UiState.Error(errorText)

        Mockito.`when`(userRepository.loginUser(validEmail, validPassword))
            .thenReturn(errorState)

        userViewModel.loginUser(validEmail, validPassword)
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Error) {
            assertEquals(UiText.StringResource(R.string.error_login), state.message)
        } else {
            fail("Expected UiState.Error but got $state")
        }
    }


    //Password

    @Test
    fun userViewModel_ResetPasswordWithInvalidEmail_ReturnsInvalidEmailError() {
        userViewModel.resetPassword(invalidEmail)

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val errorMessage = (state as UiState.Error).message
        assertTrue(errorMessage is UiText.StringResource)

        assertEquals(R.string.error_invalid_email, (errorMessage as UiText.StringResource).resId)

    }

    @Test
    fun userViewModel_ResetPasswordWithEmptyEmail_ReturnsFieldsRequiredError() {
        userViewModel.resetPassword(emptyEmail)

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val errorMessage = (state as UiState.Error).message
        assertTrue(errorMessage is UiText.StringResource)

        assertEquals(UiText.StringResource(R.string.error_email_empty), errorMessage)

    }

    @Test
    fun userViewModel_ResetPasswordWithValidEmail_ReturnsEmptyStateAfterSuccess() = runTest {
        Mockito.`when`(userRepository.resetPassword(validEmail))
            .thenReturn(UiState.Success(Unit))

        userViewModel.resetPassword(validEmail)

        advanceUntilIdle()
        val state = userViewModel.authState.value
        assertTrue(state is UiState.Empty)
    }

    @Test
    fun userViewModel_ResetPasswordRepositoryError_ReturnsErrorState() = runTest {
        val errorText = UiText.StringResource(R.string.error_reset_password)
        val errorState = UiState.Error(errorText)

        Mockito.`when`(userRepository.resetPassword(validEmail))
            .thenReturn(errorState)

        userViewModel.resetPassword(validEmail)
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Error) {
            assertEquals(UiText.StringResource(R.string.error_reset_password), state.message)
        } else {
            fail("Expected UiState.Error but got $state")
        }
    }

    //Check Login Status
    @Test
    fun userViewModel_CheckLoginStatusWithExistingUser_ReturnsSuccessState() = runTest {
        val user = DummyUser.generateUser()

        Mockito.`when`(userRepository.getCurrentUserId())
            .thenReturn(UiState.Success(user))

        userViewModel.checkLoginStatus()
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Success) {
            assertEquals(user, state.data)
        } else {
            fail("Expected UiState.Success but got $state")
        }

    }

    @Test
    fun userViewModel_CheckLoginStatusWithNullUser_ReturnsUserNotFoundError() = runTest {
        Mockito.`when`(userRepository.getCurrentUserId())
            .thenReturn(UiState.Success(null))

        userViewModel.checkLoginStatus()
        advanceUntilIdle()

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Error)

        val errorMessage = (state as UiState.Error).message
        assertTrue(errorMessage is UiText.StringResource)

        assertEquals(
            UiText.StringResource(R.string.error_user_not_found),
            errorMessage
        )
    }

    @Test
    fun userViewModel_CheckLoginStatusRepositoryError_ReturnsErrorState() = runTest {
        val errorText = UiText.StringResource(R.string.error_get_user_id)
        val errorState = UiState.Error(errorText)

        Mockito.`when`(userRepository.getCurrentUserId())
            .thenReturn(errorState)

        userViewModel.checkLoginStatus()
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Error) {
            assertEquals(UiText.StringResource(R.string.error_get_user_id), state.message)
        } else {
            fail("Expected UiState.Error but got $state")
        }

    }

    //Logout
    @Test
    fun userViewModel_LogoutUser_ReturnsEmptyStateAfterSuccess() = runTest {
        Mockito.`when`(userRepository.logoutUser())
            .thenReturn(UiState.Success(Unit))

        userViewModel.logout()
        advanceUntilIdle()

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Empty)
    }

    @Test
    fun userViewModel_LogoutUserRepositoryError_ReturnsErrorState() = runTest {
        val errorText = UiText.StringResource(R.string.error_logout)
        val errorState = UiState.Error(errorText)

        Mockito.`when`(userRepository.logoutUser())
            .thenReturn(errorState)

        userViewModel.logout()
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Error) {
            assertEquals(UiText.StringResource(R.string.error_logout), state.message)
        } else {
            fail("Expected UiState.Error but got $state")
        }
    }

    //Delete
    @Test
    fun userViewModel_DeleteAccount_ReturnsEmptyStateAfterSuccess() = runTest {
        Mockito.`when`(userRepository.deleteAccount())
            .thenReturn(UiState.Success(Unit))

        userViewModel.deleteAccount()
        advanceUntilIdle()

        val state = userViewModel.authState.value
        assertTrue(state is UiState.Empty)

    }

    @Test
    fun userViewModel_DeleteAccountRepositoryError_ReturnsErrorState() = runTest {
        val errorText = UiText.StringResource(R.string.error_delete_account)
        val errorState = UiState.Error(errorText)

        Mockito.`when`(userRepository.deleteAccount())
            .thenReturn(errorState)

        userViewModel.deleteAccount()
        advanceUntilIdle()

        val state = userViewModel.authState.value
        if (state is UiState.Error) {
            assertEquals(UiText.StringResource(R.string.error_delete_account), state.message)
        } else {
            fail("Expected UiState.Error but got $state")
        }

    }
}
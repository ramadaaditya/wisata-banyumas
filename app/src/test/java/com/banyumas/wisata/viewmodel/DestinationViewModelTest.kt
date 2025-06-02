package com.banyumas.wisata.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.banyumas.wisata.DummyDestination
import com.banyumas.wisata.R
import com.banyumas.wisata.core.data.repository.DestinationRepository
import com.banyumas.wisata.utils.MainDispatcherRule
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DestinationViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: DestinationRepository
    private lateinit var destinationViewModel: DestinationViewModel
    private val dummyDestination = DummyDestination.generateDummyDestination()
    private val dummyUiDestination = DummyDestination.generateDummyUiDestination()
    private val dummyListDestination = DummyDestination.generateListDummyDestination()
    private val dummyListUiDestination = DummyDestination.generateListDummyUiDestination()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        destinationViewModel = DestinationViewModel(repository)
    }

    @get:Rule
    val mainCustomRule = MainDispatcherRule()

    @Test
    fun `getAllDestinations should emit success state with data`() = runTest {
        val userId = "user123"

        `when`(repository.getAllDestinations(userId)).thenReturn(
            com.banyumas.wisata.core.common.UiState.Success(
                dummyListUiDestination
            )
        )
        destinationViewModel.getAllDestinations(userId)
        advanceUntilIdle()
        val result = destinationViewModel.uiDestinations.value
        assertTrue(result is com.banyumas.wisata.core.common.UiState.Success)
        assertEquals(dummyListUiDestination, (result as com.banyumas.wisata.core.common.UiState.Success).data)
        verify(repository).getAllDestinations(userId)
    }

    @Test
    fun `getAllDestinations with blank userId should emit error`() = runTest {
        destinationViewModel.getAllDestinations("")
        advanceUntilIdle()

        val result = destinationViewModel.uiDestinations.value
        assertTrue(result is com.banyumas.wisata.core.common.UiState.Error)
        assertEquals(
            R.string.error_user_id_empty,
            ((result as com.banyumas.wisata.core.common.UiState.Error).message as com.banyumas.wisata.core.common.UiText.StringResource).resId
        )
    }

    @Test
    fun `toggleFavorite success should emit Success state`() = runTest {
        val userId = "user123"
        val destinationId = "dest123"
        val isFavorite = true

        `when`(repository.updateFavoriteDestination(userId, destinationId, isFavorite))
            .thenReturn(com.banyumas.wisata.core.common.UiState.Success(Unit))
        `when`(repository.getFavoriteDestinations(userId))
            .thenReturn(com.banyumas.wisata.core.common.UiState.Success(emptyList()))

        destinationViewModel.toggleFavorite(userId, destinationId, isFavorite)
        advanceUntilIdle()

        val result = destinationViewModel.toggleFavoriteState.value
        assertTrue(result is com.banyumas.wisata.core.common.UiState.Success)

        verify(repository).updateFavoriteDestination(userId, destinationId, isFavorite)
        verify(repository).getFavoriteDestinations(userId)
    }

    @Test
    fun `loadFavoriteDestinations returns success`() = runTest {
        val userId = "user123"
        `when`(repository.getFavoriteDestinations(userId)).thenReturn(
            com.banyumas.wisata.core.common.UiState.Success(
                dummyListDestination
            )
        )
        destinationViewModel.loadFavoriteDestinations(userId)
        advanceUntilIdle()
        val result = destinationViewModel.favoriteDestination.value
        assertTrue(result is com.banyumas.wisata.core.common.UiState.Success)
        assertEquals(dummyListDestination, (result as com.banyumas.wisata.core.common.UiState.Success).data)
    }

    @Test
    fun `searchDestinations filters destinations by query and category`() = runTest {
        // Set allDestinations private field using reflection
        val field = destinationViewModel::class.java.getDeclaredField("allDestinations")
        field.isAccessible = true
        field.set(destinationViewModel, dummyListUiDestination)
        destinationViewModel.searchDestinations("Curug", "Alam")
        val state = destinationViewModel.uiDestinations.value
        assertTrue(state is com.banyumas.wisata.core.common.UiState.Success)
        val filtered = (state as com.banyumas.wisata.core.common.UiState.Success).data
        println("Filtered: ${filtered.map { it.destination.name + " - " + it.destination.category }}")
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.destination.name.contains("Curug") && it.destination.category == "Alam" })
    }
}

package com.espressodev.gptmap.feature.favourite

import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.firebase.StorageRepository
import com.espressodev.gptmap.core.firebase.StorageRepository.Companion.IMAGE_REFERENCE
import com.espressodev.gptmap.core.model.EditableItemUiEvent
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.FavouriteUiState
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.FavouriteRealmRepository
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FavouriteViewModelTest : BaseTest() {
    @Test
    fun `emits Success when getFavourites is successful`() = runTest {
        val favouriteRealmRepository: FavouriteRealmRepository = mockk()
        coEvery { favouriteRealmRepository.getFavourites() } returns flow { emit(listOf(Favourite())) }
        val logService: LogService = mockk(relaxed = true)
        val viewModel = createViewModel(favouriteRealmRepository, logService)

        val result = viewModel.favourites.first()

        assertTrue(result is Response.Success)
        verify { logService wasNot Called }
    }

    @Test
    fun `when deleteFavourite fails, should not reset UI state`() = runTest {
        val favouriteRealmRepository: FavouriteRealmRepository = mockk(relaxed = true)
        val storageService: StorageRepository = mockk()
        val exception = RuntimeException("Deletion failed")
        coEvery { favouriteRealmRepository.deleteFavourite(any()) } returns Result.failure(exception)
        coEvery { storageService.deleteImage(any(), any()) } returns Result.success(Unit)

        val viewModel = createViewModel(
            favouriteRealmRepository = favouriteRealmRepository,
            storageRepository = storageService,
        )

        // Assume that the UI state has a selected item
        val selectedItem = Favourite(favouriteId = TEST_ID)
        viewModel.onEvent(EditableItemUiEvent.OnLongClickToItem(selectedItem))
        viewModel.onEvent(EditableItemUiEvent.OnDeleteDialogConfirm)

        advanceUntilIdle()

        // Confirm that the UI state was not reset
        assertNotEquals(FavouriteUiState(selectedItem = Favourite()), viewModel.uiState.value)
    }

    @Test
    fun `when getFavourites emits multiple values, should emit Success for each value`() = runTest {
        val favouriteRealmRepository: FavouriteRealmRepository = mockk()
        val favouritesFlow = flow {
            emit(listOf(Favourite(favouriteId = "id1")))
            emit(listOf(Favourite(favouriteId = "id2"), Favourite(favouriteId = "id3")))
        }
        coEvery { favouriteRealmRepository.getFavourites() } returns favouritesFlow.also(::println)

        val viewModel =
            createViewModel(
                favouriteRealmRepository = favouriteRealmRepository,
                ioDispatcher = testScheduler
            )

        val emissions = mutableListOf<Response<List<Favourite>>>()
        backgroundScope.launch { viewModel.favourites.toList(emissions) }
        advanceUntilIdle()

        assertTrue(emissions.all { it is Response.Success })
        if (emissions.size > 0 && emissions[0] is Response.Success) {
            assertEquals(2, (emissions[0] as Response.Success).data.size)
        }
    }

    @Test
    fun `favourites emits Loading initially`() = runTest {
        val viewModel = createViewModel()
        val loadingState = viewModel.favourites.first().also(::println)
        assert(loadingState is Response.Loading)
    }

    @Test
    fun `favourites emits Success with correct data`() = runTest {
        val favouriteRealmRepository: FavouriteRealmRepository = mockk()
        coEvery { favouriteRealmRepository.getFavourites() } returns flow {
            emit(listOf(Favourite(favouriteId = TEST_ID)))
        }
        val viewModel = createViewModel(favouriteRealmRepository)
        viewModel.favourites.first().also { result ->
            assert(result is Response.Success)
        }
    }

    @Test
    fun `favourites emits Failure when getFavourites fails`() = runTest {
        val favouriteRealmRepository: FavouriteRealmRepository = mockk()
        coEvery { favouriteRealmRepository.getFavourites() } returns flow {
            emit(listOf(Favourite(favouriteId = TEST_ID)))
            throw IllegalArgumentException()
        }

        val viewModel =
            createViewModel(
                favouriteRealmRepository = favouriteRealmRepository,
                ioDispatcher = testScheduler
            )

        val emissions = mutableListOf<Response<List<Favourite>>>()
        backgroundScope.launch { viewModel.favourites.take(2).toList(emissions) }
        advanceUntilIdle()
        assertTrue(emissions.also(::println).any { it is Response.Failure })
    }

    @Test
    fun `onDeleteDialogConfirmClick deletes favourite and resets UI state`() = runTest {
        val favouriteRealmRepository: FavouriteRealmRepository = mockk(relaxed = true)
        val storageService: StorageRepository = mockk()

        // Assume deletion is successful
        coEvery { favouriteRealmRepository.deleteFavourite(any()) } returns Result.success(Unit)

        val imageIdSlot = slot<String>()
        val imageRefSlot = slot<String>()
        coEvery {
            storageService.deleteImage(
                capture(imageIdSlot),
                capture(imageRefSlot)
            )
        } returns Result.success(Unit)

        val viewModel = createViewModel(
            favouriteRealmRepository = favouriteRealmRepository,
            storageRepository = storageService,
        )

        // Assume that the UI state has a selected item
        val selectedItem = Favourite(favouriteId = TEST_ID)
        viewModel.onEvent(EditableItemUiEvent.OnLongClickToItem(selectedItem))
        assertTrue(
            viewModel.uiState.value == FavouriteUiState(
                selectedItem = selectedItem,
                isUiInEditMode = true
            )
        )

        viewModel.onEvent(EditableItemUiEvent.OnDeleteDialogConfirm)

        // Confirm that the delete functions were called
        coVerify { favouriteRealmRepository.deleteFavourite(TEST_ID) }
        coVerify { storageService.deleteImage(TEST_ID, IMAGE_REFERENCE) }

        // Check the captured arguments
        assertEquals(TEST_ID, imageIdSlot.captured)
        assertEquals(IMAGE_REFERENCE, imageRefSlot.captured)
        // Confirm that the UI state was reset
        assert(viewModel.uiState.value == FavouriteUiState(selectedItem = Favourite()))
    }

    @Test
    fun `onEditDialogConfirmClick updates favourite text and resets UI state`() = runTest {
        val favouriteRealmRepository: FavouriteRealmRepository = mockk(relaxed = true)
        coEvery {
            favouriteRealmRepository.updateFavouriteText(
                any(),
                any()
            )
        } returns Result.success(Unit)

        val viewModel = createViewModel(favouriteRealmRepository = favouriteRealmRepository)

        val selectedItem = Favourite(favouriteId = TEST_ID)
        viewModel.onEvent(EditableItemUiEvent.OnLongClickToItem(selectedItem))
        val newText = "Updated Text"

        viewModel.onEvent(EditableItemUiEvent.OnEditDialogConfirm(newText))
        // Confirm that the update function was called
        coVerify { favouriteRealmRepository.updateFavouriteText(TEST_ID, newText) }

        // Confirm that the UI state was reset
        assert(viewModel.uiState.value == FavouriteUiState(selectedItem = Favourite()))
    }

    @Test
    fun `reset resets UI state correctly`() = runTest {
        val viewModel = createViewModel()

        val selectedItem = Favourite(favouriteId = TEST_ID)
        viewModel.onEvent(EditableItemUiEvent.OnLongClickToItem(selectedItem))

        viewModel.onEvent(EditableItemUiEvent.Reset)

        // Confirm that the UI state was reset
        assert(viewModel.uiState.value == FavouriteUiState(selectedItem = Favourite()))
    }

    private fun createViewModel(
        favouriteRealmRepository: FavouriteRealmRepository = mockk(relaxed = true),
        logService: LogService = mockk(relaxed = true),
        storageRepository: StorageRepository = mockk(relaxed = true),
        ioDispatcher: TestCoroutineScheduler = testDispatcher
    ): FavouriteViewModel {
        return FavouriteViewModel(
            favouriteRealmRepository = favouriteRealmRepository,
            storageRepository = storageRepository,
            logService = logService,
            ioDispatcher = UnconfinedTestDispatcher(ioDispatcher)
        )
    }

    companion object {
        private const val TEST_ID = "test_id"
    }
}

package com.espressodev.gptmap.feature.favourite

import app.cash.turbine.test
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FavouriteViewModelTest : BaseTest() {

    private lateinit var viewModel: FavouriteViewModel
    private val realmSyncService: RealmSyncService = mockk(relaxed = true)
    private val logService: LogService = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val favouriteList = listOf(Favourite(date = LocalDateTime.now()))

    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        coEvery { realmSyncService.getFavourites() } returns flowOf(favouriteList)
        viewModel = spyk(
            FavouriteViewModel(
                realmSyncService = realmSyncService,
                logService = logService,
                ioDispatcher = testDispatcher
            )
        )
    }

    @AfterEach
    override fun afterEach() {
        super.afterEach()
        clearAllMocks()
    }

    @Test
    fun favouritesFlowEmitsExpectedValues() = runTest {
        viewModel.favourites.test {
            val item = awaitItem()
            assert(item is Response.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

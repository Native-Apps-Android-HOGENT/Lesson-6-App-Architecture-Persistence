package com.example.android.trackmysleepquality.sleepquality

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.trackmysleepquality.util.CoroutinesTestRule
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SleepQualityViewModelTest {

    private lateinit var viewModel: SleepQualityViewModel
    private val sleepDao: SleepDatabaseDao = mockk()
    private val sleepNightKey = 1L

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantTestExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = SleepQualityViewModel(sleepNightKey, sleepDao)
    }

    @Test
    fun sleepQualityVM_onSetSleepQuality_updatesNightInDatabase() = coroutinesTestRule.testDispatcher.runBlockingTest {
        // Arrange
        val night = SleepNight()
        every { sleepDao.get(sleepNightKey) } returns night
        val updatedNight = slot<SleepNight>()
        every { sleepDao.update(capture(updatedNight)) } returns Unit
        val sleepQuality = 3

        // Act
        viewModel.onSetSleepQuality(sleepQuality)

        // Assert
        verify { sleepDao.update(night) }
        assertEquals(sleepQuality, updatedNight.captured.sleepQuality)
    }

}

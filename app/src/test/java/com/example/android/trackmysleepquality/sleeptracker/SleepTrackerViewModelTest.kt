package com.example.android.trackmysleepquality.sleeptracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.CoroutinesTestRule
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.observeForTesting
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SleepTrackerViewModelTest {

    private lateinit var viewModel: SleepTrackerViewModel
    private val sleepDao: SleepDatabaseDao = mockk()

    private val tonight = SleepNight()
    private val nights = MutableLiveData<List<SleepNight>>()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantTestExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        every { sleepDao.getAllNights() } returns MutableLiveData<List<SleepNight>>()
    }

    @Test
    fun sleepTrackerVM_constructor_initialisesNight() = coroutinesTestRule.testDispatcher.runBlockingTest {
        // Arrange
        // When starting the app for the very first time, there's no Night in the db yet.
        // As such, getTonight returns null
        every { sleepDao.getTonight() } returns null

        // Act
        viewModel = SleepTrackerViewModel(
                application = mockk(),
                sleepDao = sleepDao)

        // Assert
        verify { sleepDao.getTonight() }
        viewModel.tonight.observeForTesting {
            assertNull(viewModel.tonight.value)
        }
    }


    @Test
    fun sleepTrackerVM_startTracking_initialisesNight() = coroutinesTestRule.testDispatcher.runBlockingTest {
        // Arrange
        every { sleepDao.insert(any()) } returns Unit
        // Simulate the night being inserted
        every { sleepDao.getTonight() } returns tonight

        viewModel = SleepTrackerViewModel(
                application = mockk(),
                sleepDao = sleepDao)

        // Act
        viewModel.onStartTacking()

        // Assert
        viewModel.tonight.observeForTesting {
            assertNotNull(viewModel.tonight.value)
        }
    }

    @Test
    fun sleepTrackerVM_onStopTracking_updatesDatabase() = coroutinesTestRule.testDispatcher.runBlockingTest {
        // Arrange
        // Simulate a sleep already being started
        every { sleepDao.getTonight() } returns tonight
        every { sleepDao.update(any()) } returns Unit
        viewModel = SleepTrackerViewModel(
                application = mockk(),
                sleepDao = sleepDao)

        // Act
        viewModel.onStopTracking()

        //Assert
        verify { sleepDao.update(any()) }
    }

    @Test
    fun sleepTrackerVM_onClear_clearDatabaseAndNight() = coroutinesTestRule.testDispatcher.runBlockingTest {
        // Arrange
        // Simulate a sleep already being started
        every { sleepDao.getTonight() } returns tonight
        every { sleepDao.clear() } returns Unit
        viewModel = SleepTrackerViewModel(
                application = mockk(),
                sleepDao = sleepDao)

        // Act
        viewModel.onClear()

        //Assert
        verify { sleepDao.clear() }
        viewModel.tonight.observeForTesting {
            assertNull(viewModel.tonight.value)
        }
    }
}

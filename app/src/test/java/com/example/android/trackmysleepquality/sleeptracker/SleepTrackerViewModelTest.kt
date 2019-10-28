package com.example.android.trackmysleepquality.sleeptracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.util.CoroutinesTestRule
import com.example.android.trackmysleepquality.util.getValueForTest
import com.example.android.trackmysleepquality.util.observeForTesting
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SleepTrackerViewModelTest {

    private lateinit var viewModel: SleepTrackerViewModel
    private lateinit var sleepDao: SleepDatabaseDao

    private lateinit var tonight: SleepNight

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    var instantTestExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        tonight = SleepNight()
        sleepDao = mockk()
        every { sleepDao.getAllNights() } returns MutableLiveData<List<SleepNight>>()
    }

    @Test
    fun sleepTrackerVM_constructor_initialisesCorrectly() {
        // Arrange
        // When starting the app for the very first time, there's no Night in the db yet.
        // As such, getTonight returns null
        every { sleepDao.getTonight() } returns null

        runBlockingTest {
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
    }

    @Test
    fun sleepTrackerVM_constructor_correctButtonState() {
        // Arrange
        // When starting the app for the very first time, there's no Night in the db yet.
        // As such, getTonight returns null
        every { sleepDao.getTonight() } returns null
        val nights = MutableLiveData<List<SleepNight>>()
        nights.value = emptyList()
        every { sleepDao.getAllNights() } returns nights

        // Act
        runBlockingTest {
            viewModel = SleepTrackerViewModel(application = mockk(), sleepDao = sleepDao)
            // Assert
            assertTrue(viewModel.startButtonVisible.getValueForTest()!!)
            assertFalse(viewModel.stopButtonVisible.getValueForTest()!!)
            assertFalse(viewModel.clearButtonVisible.getValueForTest()!!)
        }
    }


    @Test
    fun sleepTrackerVM_startTracking_initialisesNight() {
        // Arrange
        every { sleepDao.insert(any()) } returns Unit
        // Simulate the night being inserted
        every { sleepDao.getTonight() } returns tonight

        runBlockingTest {
            viewModel = SleepTrackerViewModel(application = mockk(), sleepDao = sleepDao)

            // Act
            viewModel.onStartTacking()

            // Assert
            viewModel.tonight.observeForTesting {
                assertNotNull(viewModel.tonight.value)
            }
        }
    }

    @Test
    fun sleepTrackerVM_onStopTracking_updatesDatabase() {
        // Arrange
        // Simulate a sleep already being started
        every { sleepDao.getTonight() } returns tonight
        every { sleepDao.update(any()) } returns Unit

        runBlockingTest {
            viewModel = SleepTrackerViewModel(application = mockk(), sleepDao = sleepDao)

            // Act
            viewModel.onStopTracking()

            //Assert
            verify { sleepDao.update(any()) }
        }
    }

    @Test
    fun sleepTrackerVM_onStopTracking_requestsNavigation() {
        // Arrange
        // Simulate a sleep already being started
        every { sleepDao.getTonight() } returns tonight
        every { sleepDao.update(any()) } returns Unit

        runBlockingTest {
            viewModel = SleepTrackerViewModel(application = mockk(), sleepDao = sleepDao)

            // Act
            viewModel.onStopTracking()

            //Assert
            viewModel.navigateToSleepQuality.observeForTesting {
                assertNotNull(viewModel.navigateToSleepQuality.value)
            }
        }
    }

    @Test
    fun sleepTrackerVM_onClear_clearDatabaseAndNight() {
        // Arrange
        // Simulate a sleep already being started
        every { sleepDao.getTonight() } returns tonight
        every { sleepDao.clear() } returns Unit

        runBlockingTest {
            viewModel = SleepTrackerViewModel(application = mockk(), sleepDao = sleepDao)

            // Act
            viewModel.onClear()

            //Assert
            verify { sleepDao.clear() }
            viewModel.tonight.observeForTesting {
                assertNull(viewModel.tonight.value)
            }
        }

    }


    @Test
    fun sleepTrackerVM_onClear_requestsSnackbar() {
        // Arrange
        // Simulate a sleep already being started
        every { sleepDao.getTonight() } returns tonight
        every { sleepDao.clear() } returns Unit

        runBlockingTest {
            viewModel = SleepTrackerViewModel(
                    application = mockk(),
                    sleepDao = sleepDao)

            // Act
            viewModel.onClear()

            //Assert
            viewModel.showSnackBarEvent.observeForTesting {
                assertTrue(viewModel.showSnackBarEvent.value!!)
            }
        }
    }
}

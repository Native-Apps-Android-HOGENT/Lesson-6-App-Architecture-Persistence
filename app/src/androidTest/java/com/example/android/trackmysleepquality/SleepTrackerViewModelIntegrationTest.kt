package com.example.android.trackmysleepquality

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.sleeptracker.SleepTrackerViewModel
import com.example.android.trackmysleepquality.util.CoroutinesTestRule
import com.example.android.trackmysleepquality.util.getValueForTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SleepTrackerViewModelIntegrationTest {

    private lateinit var viewModel: SleepTrackerViewModel

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        createDb()
        viewModel = SleepTrackerViewModel(
                sleepDao,
                InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application)
    }

    private fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory sleepDao because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    fun tearDown() {
        clearDb()
    }

    private fun clearDb() {
        db.clearAllTables()
    }

    @Test
    fun sleepTrackerVM_constructor_initialisesCorrectly() {
        assertNull(viewModel.tonight.getValueForTest())
        assertEquals(0, sleepDao.getAllNights().getValueForTest()!!.size)
    }

    @Test
    fun sleepTrackerVM_startTracking_newNightInDb() {
        runBlocking {
            viewModel.onStartTacking()
        }
        assertNotNull(sleepDao.getTonight())
    }

    @Test
    fun sleepTrackerVM_recordOneNight_savedInDatabase() {
        runBlocking {
            viewModel.onStartTacking()
            delay(1_000)
            viewModel.onStopTracking()
        }
        assertEquals(1, sleepDao.getAllNights().getValueForTest()!!.size)
    }

    @Test
    fun sleepTrackerVM_onClear_emptiesDatabase() {
        runBlocking {
            repeat(3) {
                viewModel.onStartTacking()
                delay(1_000)
                viewModel.onStopTracking()
            }
            viewModel.onClear()
        }
        assertEquals(0, sleepDao.getAllNights().getValueForTest()!!.size)
    }


}

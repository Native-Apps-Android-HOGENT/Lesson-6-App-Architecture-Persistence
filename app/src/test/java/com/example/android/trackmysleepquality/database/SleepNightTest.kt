package com.example.android.trackmysleepquality.database

import org.junit.Assert.assertEquals
import org.junit.Test

class SleepNightTest {


    @Test
    fun sleepNight_constructor_endTimeSameAsStartTime() {
        // Act
        val sleepNight = SleepNight()

        // Assert
        assertEquals(sleepNight.startTimeMilli, sleepNight.endTimeMilli)
    }
}
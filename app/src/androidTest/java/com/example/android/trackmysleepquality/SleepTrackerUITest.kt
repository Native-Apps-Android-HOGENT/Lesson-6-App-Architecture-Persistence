package com.example.android.trackmysleepquality

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SleepTrackerUITest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun onStartUp_showCorrectFragment() {
        onView(withId(R.id.constraint_sleepTracker)).check(matches(isDisplayed()))
    }

    @Test
    fun onStopTracking_goesToSleepQualityFragment() {
        onView(withId(R.id.start_button)).perform(click())
        onView(withId(R.id.stop_button)).perform(click())

        onView(withId(R.id.constraint_sleepQuality)).check(matches(isDisplayed()))
    }

    @Test
    fun onQualityPicked_goesBackToTrackerFragment() {
        onView(withId(R.id.start_button)).perform(click())
        onView(withId(R.id.stop_button)).perform(click())
        onView(withId(R.id.quality_two_image)).perform(click())

        onView(withId(R.id.constraint_sleepTracker)).check(matches(isDisplayed()))
    }
}
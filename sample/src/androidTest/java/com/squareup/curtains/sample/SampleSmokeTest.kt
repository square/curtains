package com.squareup.curtains.sample

import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class SampleSmokeTest {

  @get:Rule
  val activityRule = ActivityScenarioRule(MainActivity::class.java)

  @Test fun createATest() {
    TODO("Write tests")
  }
}
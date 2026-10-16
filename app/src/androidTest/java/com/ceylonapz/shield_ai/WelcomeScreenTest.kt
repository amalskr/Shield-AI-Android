package com.ceylonapz.shield_ai

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomeScreen_showsUi() {
        //set ui
        composeTestRule.setContent {
            WelcomeScreen(name = "Test")
        }

        //assert : is display welcome text
        composeTestRule.onNodeWithText("Welcome Test")
            .assertIsDisplayed()

        //assert : is display login button
        composeTestRule.onNodeWithText("Login")
            .assertIsDisplayed()

    }
}
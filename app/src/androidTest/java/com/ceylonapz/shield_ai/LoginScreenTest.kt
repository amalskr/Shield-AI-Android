package com.ceylonapz.shield_ai

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginButton_whenEmailIsEmpty_showsError() {
        //set ui
        composeTestRule.setContent {
            LoginScreen()
        }

        //Act
        composeTestRule.onNodeWithTag("loginBtn")
            .performClick()

        //Assert
        composeTestRule.onNodeWithText("Email cannot be empty")
            .assertIsDisplayed()

    }

    @Test
    fun loginButton_whenEmailIsNotEmpty_showsWelcomeMessage() {
        //set ui
        composeTestRule.setContent {
            LoginScreen()
        }

        //Act input
        composeTestRule.onNodeWithTag("inputEml").performTextInput("amal@gml.cm")

        //Act button
        composeTestRule.onNodeWithTag("loginBtn").performClick()

        //Assert
        composeTestRule.onNodeWithText("Welcome amal@gml.cm").assertIsDisplayed()
    }

}
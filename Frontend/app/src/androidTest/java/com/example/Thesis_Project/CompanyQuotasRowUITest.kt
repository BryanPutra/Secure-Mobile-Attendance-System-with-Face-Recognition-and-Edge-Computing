package com.example.Thesis_Project

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertTextEquals
import com.example.Thesis_Project.ui.components.CompanyQuotasRow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class CompanyQuotasRowUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun companyQuotasRow_DisplaysCorrectText() {
        composeTestRule.setContent {
            CompanyQuotasRow(name = "Name", value = "Value")
        }

        composeTestRule.onNodeWithText("Name").assertTextEquals("Name")
        composeTestRule.onNodeWithText("Value").assertTextEquals("Value")
    }
}

package tec.spn.rieti

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tec.spn.rieti.view.MainActivity

@RunWith(AndroidJUnit4::class)
class MainFlowIntegrationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainScreen_DisplaysStatBlocksProperly() {
        val expectedStats = listOf(
            "24/7" to "Disponibilidad",
            "14" to "Municipios cubiertos",
            "100%" to "Confidencial",
            "3" to "Canales de comunicación"
        )

        expectedStats.forEach { (value, label) ->
            composeRule.onNodeWithText(value).performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText(label).performScrollTo().assertIsDisplayed()
        }
    }

    @Test
    fun tappingHelp_NavigatesToHelpScreenAndBack() {
        composeRule.onNodeWithText("Sistema RIETI").assertIsDisplayed()
        composeRule.onNodeWithText("Ayuda").performClick()
        composeRule.onNodeWithText("¿Cómo funciona?").assertIsDisplayed()
        composeRule.onNodeWithText("1. Complete el formulario").assertIsDisplayed()
        Espresso.pressBack()
        composeRule.onNodeWithText("Sistema RIETI").assertIsDisplayed()
    }
}

package com.klipy.demoapp.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klipy.demoapp.presentation.features.conversation.ui.ConversationScreen
import com.klipy.demoapp.presentation.features.conversationlist.ConversationListScreen
import com.klipy.demoapp.presentation.theme.KlipyDemoAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            KlipyDemoAppTheme(darkTheme = true) {
                MainContent()
            }
        }
    }

    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = CONVERSATION_LIST,
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            composable(CONVERSATION_LIST) {
                ConversationListScreen(onConversationClick = {
                    navController.navigate("$CONVERSATION/$it")
                })
            }
            composable("$CONVERSATION/{conversationId}") { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId")
                ConversationScreen(
                    conversationId = conversationId,
                    onBackClicked = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }

    private companion object {
        const val CONVERSATION_LIST = "conversationList"
        const val CONVERSATION = "conversation"
    }
}
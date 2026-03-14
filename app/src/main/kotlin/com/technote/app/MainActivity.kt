package com.technote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.technote.app.ui.auth.AuthDestination
import com.technote.app.ui.auth.LoginScreen
import com.technote.app.ui.auth.SignUpScreen
import com.technote.app.ui.theme.TechnoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechnoteTheme {
                TechnoteApp()
            }
        }
    }
}

@Composable
fun TechnoteApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthDestination.Login.route
    ) {
        composable(AuthDestination.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(AuthDestination.SignUp.route)
                },
                onLoginSuccess = {
                    // Navigate to dashboard (to be implemented)
                }
            )
        }
        composable(AuthDestination.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    // Navigate to dashboard (to be implemented)
                }
            )
        }
    }
}

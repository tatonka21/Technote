package com.technote.app.ui.auth

sealed class AuthDestination(val route: String) {
    data object Login : AuthDestination("login")
    data object SignUp : AuthDestination("signup")
    data object GitHubDeviceFlow : AuthDestination("github_device_flow")
}

package com.technote.app.ui.github

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.technote.app.ui.theme.BackgroundDark
import com.technote.app.ui.theme.CardDark
import com.technote.app.ui.theme.SurfaceVariantDark
import com.technote.app.ui.theme.TechnoteAccent
import com.technote.app.ui.theme.TechnoteBlue
import com.technote.app.ui.theme.TechnoteTheme
import com.technote.app.ui.theme.TextHint
import com.technote.app.ui.theme.TextSecondary

/**
 * GitHub Device-Flow OAuth screen.
 *
 * Standard browser-based OAuth does not work in headless or terminal-only
 * environments such as Termux on Android.  This screen implements the
 * GitHub Device Authorization Grant (RFC 8628) which requires only:
 *   1. Displaying a short user-code (e.g. "ABCD-1234")
 *   2. Asking the user to visit github.com/login/device
 *   3. Polling the token endpoint until the user approves or the code expires
 *
 * This is the same flow triggered by `gh auth login --git-protocol https` in
 * Termux, which resolves the common error:
 *   "error connecting to browser, using one-time code instead"
 */
@Composable
fun GitHubAuthScreen(
    onAuthSuccess: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var uiState by remember { mutableStateOf<GitHubAuthState>(GitHubAuthState.Connect) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        Color(0xFF0D1520),
                        BackgroundDark
                    )
                )
            )
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TechnoteAccent.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(72.dp))

                // GitHub mark
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF24292E)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐙", fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Connect GitHub",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        brush = Brush.linearGradient(
                            colors = listOf(TechnoteBlue, TechnoteAccent)
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Link your GitHub account to sync repos,\npush notes, and run AI-assisted workflows",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "github-auth-content"
                ) { state ->
                    when (state) {
                        GitHubAuthState.Connect -> ConnectCard(
                            onStartDeviceFlow = {
                                uiState = GitHubAuthState.DeviceCode(
                                    userCode = "ABCD-1234",
                                    verificationUri = "github.com/login/device",
                                    expiresInSeconds = 900
                                )
                            },
                            onDismiss = onDismiss
                        )

                        is GitHubAuthState.DeviceCode -> DeviceCodeCard(
                            state = state,
                            onPolling = { uiState = GitHubAuthState.Polling },
                            onCancel = { uiState = GitHubAuthState.Connect }
                        )

                        GitHubAuthState.Polling -> PollingCard(
                            onSuccess = { uiState = GitHubAuthState.Success },
                            onCancel = { uiState = GitHubAuthState.Connect }
                        )

                        GitHubAuthState.Success -> SuccessCard(onContinue = onAuthSuccess)

                        is GitHubAuthState.Error -> ErrorCard(
                            message = state.message,
                            onRetry = { uiState = GitHubAuthState.Connect }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// State
// ---------------------------------------------------------------------------

sealed interface GitHubAuthState {
    data object Connect : GitHubAuthState
    data class DeviceCode(
        val userCode: String,
        val verificationUri: String,
        val expiresInSeconds: Int
    ) : GitHubAuthState
    data object Polling : GitHubAuthState
    data object Success : GitHubAuthState
    data class Error(val message: String) : GitHubAuthState
}

// ---------------------------------------------------------------------------
// Cards
// ---------------------------------------------------------------------------

@Composable
private fun ConnectCard(
    onStartDeviceFlow: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardDark)
            .padding(24.dp)
    ) {
        Text(
            text = "Choose how to connect",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Works in all environments, including Termux and headless terminals",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Device flow option (recommended for Termux)
        AuthOptionRow(
            emoji = "📱",
            title = "Device code (recommended)",
            subtitle = "No browser needed · Works in Termux & CI",
            onClick = onStartDeviceFlow
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Browser OAuth option
        AuthOptionRow(
            emoji = "🌐",
            title = "Browser OAuth",
            subtitle = "Opens github.com in your default browser",
            onClick = { /* browser OAuth intent */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PAT option
        AuthOptionRow(
            emoji = "🔑",
            title = "Personal access token",
            subtitle = "Paste a token from github.com/settings/tokens",
            onClick = { /* PAT flow */ }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Cancel", color = TextHint)
        }
    }
}

@Composable
private fun AuthOptionRow(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariantDark)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Text(text = "›", fontSize = 20.sp, color = TextHint)
    }
}

@Composable
private fun DeviceCodeCard(
    state: GitHubAuthState.DeviceCode,
    onPolling: () -> Unit,
    onCancel: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Activate on GitHub",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Open  ${state.verificationUri}  in any browser\n(or on another device) and enter the code below",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // User code display
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(TechnoteBlue, TechnoteAccent)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(SurfaceVariantDark)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = state.userCode,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.linearGradient(
                        colors = listOf(TechnoteBlue, TechnoteAccent)
                    )
                )
            )
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(state.userCode))
                    copied = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy code",
                    tint = if (copied) TechnoteAccent else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (copied) "Copied!" else "Tap to copy",
            style = MaterialTheme.typography.bodySmall,
            color = if (copied) TechnoteAccent else TextHint
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Steps
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeviceFlowStep(number = "1", text = "Visit  github.com/login/device")
            DeviceFlowStep(number = "2", text = "Enter the code shown above")
            DeviceFlowStep(number = "3", text = "Tap 'I\u2019ve entered the code' below")
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onPolling,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TechnoteBlue)
        ) {
            Text(
                text = "I've entered the code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onCancel) {
            Text(text = "Back", color = TextHint)
        }
    }
}

@Composable
private fun DeviceFlowStep(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(TechnoteBlue.copy(alpha = 0.20f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall,
                color = TechnoteBlue,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun PollingCard(
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    // In a real implementation this composable would call a coroutine that polls
    // the GitHub token endpoint (POST https://github.com/login/oauth/access_token)
    // every 5 seconds until it receives an access_token or an error.
    // Here we expose the callbacks so the ViewModel can drive the state.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardDark)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = TechnoteBlue,
            modifier = Modifier.size(56.dp),
            strokeWidth = 3.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Waiting for authorization…",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Complete the steps on github.com/login/device,\nthen return here",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Simulate success for preview / demo purposes
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel", color = TextHint)
            }
            TextButton(onClick = onSuccess) {
                Text(text = "Simulate success", color = TechnoteAccent)
            }
        }
    }
}

@Composable
private fun SuccessCard(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardDark)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Success",
            tint = TechnoteAccent,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "GitHub connected!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your repositories, commits, and workflows\nare now available in Technote",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TechnoteBlue)
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            tint = Color(0xFFE53935),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Authentication failed",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TechnoteBlue)
        ) {
            Text(
                text = "Try again",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun GitHubAuthScreenConnectPreview() {
    TechnoteTheme {
        GitHubAuthScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun GitHubAuthScreenDeviceCodePreview() {
    TechnoteTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(28.dp)
        ) {
            DeviceCodeCard(
                state = GitHubAuthState.DeviceCode(
                    userCode = "ABCD-1234",
                    verificationUri = "github.com/login/device",
                    expiresInSeconds = 900
                ),
                onPolling = {},
                onCancel = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GitHubAuthScreenSuccessPreview() {
    TechnoteTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(28.dp)
        ) {
            SuccessCard(onContinue = {})
        }
    }
}

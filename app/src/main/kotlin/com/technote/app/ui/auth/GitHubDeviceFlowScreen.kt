package com.technote.app.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import kotlinx.coroutines.delay

/** How long to wait between polling attempts (matches GitHub's default interval). */
private const val POLLING_INTERVAL_MS = 5_000L

/** How long to show the "Copied to clipboard!" feedback label. */
private const val CLIPBOARD_FEEDBACK_DURATION_MS = 2_000L

/**
 * GitHub Device Flow authentication screen.
 *
 * Works without a browser on the current device — ideal for Termux / headless
 * environments.  The user visits https://github.com/login/device on *any*
 * browser-capable device (phone, laptop, …) and enters the one-time code
 * displayed here.  This screen polls GitHub until the authorisation is granted.
 *
 * OAuth Device Flow spec: https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps#device-flow
 */
@Composable
fun GitHubDeviceFlowScreen(
    /**
     * The one-time code returned by GitHub's device/code endpoint.
     * In production this is fetched from the API; for the preview / mockup a
     * placeholder value is shown.
     */
    userCode: String = "ABCD-1234",
    verificationUri: String = "https://github.com/login/device",
    onAuthSuccess: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    var isPolling by remember { mutableStateOf(true) }
    var isSuccess by remember { mutableStateOf(false) }
    var codeCopied by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) { visible = true }

    // Simulate polling — replace with real GitHub API calls in production.
    LaunchedEffect(isPolling) {
        if (isPolling) {
            delay(POLLING_INTERVAL_MS)
            // Keep polling until success; real impl checks token endpoint.
        }
    }

    // Reset "Copied!" label after the feedback duration.
    LaunchedEffect(codeCopied) {
        if (codeCopied) {
            delay(CLIPBOARD_FEEDBACK_DURATION_MS)
            codeCopied = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color(0xFF0D1520), BackgroundDark)
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
                Spacer(modifier = Modifier.height(56.dp))

                // GitHub Octocat icon (simplified placeholder)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF24292F)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐙", fontSize = 36.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Sign in with GitHub",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        brush = Brush.linearGradient(colors = listOf(TechnoteBlue, TechnoteAccent))
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No browser needed on this device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Main card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(CardDark)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isSuccess) {
                        // ── Success state ──────────────────────────────────
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Authorised",
                            tint = TechnoteAccent,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Authorised!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your GitHub account is now linked to Technote.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onAuthSuccess,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TechnoteAccent)
                        ) {
                            Text(
                                text = "Continue",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    } else {
                        // ── Pending state ──────────────────────────────────
                        StepRow(number = "1", text = "Open a browser on any device")
                        Spacer(modifier = Modifier.height(12.dp))

                        // Verification URL pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceVariantDark)
                                .border(
                                    width = 1.dp,
                                    color = TechnoteBlue.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = verificationUri,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = TechnoteBlue
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        StepRow(number = "2", text = "Enter this one-time code")
                        Spacer(modifier = Modifier.height(12.dp))

                        // User code box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1A1D27))
                                .border(
                                    width = 2.dp,
                                    color = TechnoteAccent.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = userCode,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 4.sp
                                ),
                                color = Color.White
                            )
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(userCode))
                                codeCopied = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = if (codeCopied) TechnoteAccent else TextHint
                                )
                            }
                        }

                        if (codeCopied) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Copied to clipboard!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TechnoteAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        StepRow(number = "3", text = "Waiting for you to authorise…")
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = TechnoteBlue,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Polling for authorisation…",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Simulate success (demo only)
                        Button(
                            onClick = { isSuccess = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292F))
                        ) {
                            Text(
                                text = "I've entered the code ✓",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onBack) {
                    Text(
                        text = "← Back to sign in",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun StepRow(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(TechnoteBlue, TechnoteAccent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GitHubDeviceFlowScreenPreview() {
    TechnoteTheme {
        GitHubDeviceFlowScreen()
    }
}

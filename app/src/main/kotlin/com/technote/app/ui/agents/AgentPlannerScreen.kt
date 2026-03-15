package com.technote.app.ui.agents

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.technote.app.ui.theme.BackgroundDark
import com.technote.app.ui.theme.CardDark
import com.technote.app.ui.theme.SurfaceVariantDark
import com.technote.app.ui.theme.TechnoteAccent
import com.technote.app.ui.theme.TechnoteBlue
import com.technote.app.ui.theme.TechnoteTheme
import com.technote.app.ui.theme.TextHint
import com.technote.app.ui.theme.TextPrimary
import com.technote.app.ui.theme.TextSecondary

@Composable
fun AgentPlannerScreen() {
    val scrollState = rememberScrollState()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Multi-agent instance is live",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Each page has a dedicated agent creating UI + logic. Approve page-by-page to ship safely.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            PlannerHero()

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Agent per page", action = "View full roster")
            PageStatusCards()

            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "Approval queue", action = "Auto-approve rules")
            ApprovalQueueCards()

            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "Live activity", action = "View logs")
            LiveActivityList()

            Spacer(modifier = Modifier.height(80.dp))
            BottomNav()
        }
    }
}

@Composable
private fun PlannerHero() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        TechnoteBlue.copy(alpha = 0.25f),
                        TechnoteAccent.copy(alpha = 0.15f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = TechnoteBlue.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Multi-agent instance",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Agent-per-page builders are running with approvals to keep changes safe.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillChip(
                text = "4 agents online",
                background = TechnoteBlue.copy(alpha = 0.15f),
                border = TechnoteBlue.copy(alpha = 0.35f),
                content = Color(0xFF90C2FF)
            )
            PillChip(
                text = "2 pages ready",
                background = Color(0xFF1E2A24),
                border = Color(0xFF3B5247),
                content = Color(0xFF7AEBD5)
            )
            PillChip(
                text = "1 approval pending",
                background = Color(0xFF2A2418),
                border = Color(0xFF4E3F1F),
                content = Color(0xFFFFD166)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(title = "Builds running", value = "3", detail = "Realtime")
            MetricCard(title = "Queue", value = "2", detail = "Next pages")
            MetricCard(title = "Reviews", value = "1", detail = "Needs approval")
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, detail: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, SurfaceVariantDark, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = TextPrimary
        )
        Text(text = detail, style = MaterialTheme.typography.labelSmall, color = TextHint)
    }
}

@Composable
private fun SectionHeader(title: String, action: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = action,
            style = MaterialTheme.typography.labelLarge,
            color = TechnoteBlue
        )
    }
}

@Composable
private fun PageStatusCards() {
    StatusCard(
        title = "Page 1 — Authentication",
        subtitle = "Auth agent (Ava) finalized flows",
        status = StatusPill("Done", Color(0xFF0F2C24), Color(0xFF4DD7A9)),
        chips = listOf(StatusChip("✅ Ready", Color(0xFF1E2A24), Color(0xFF7AEBD5))),
        actions = {
            Button(
                onClick = {},
                modifier = Modifier.height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechnoteBlue)
            ) {
                Text("Approve")
            }
        }
    )

    StatusCard(
        title = "Page 2 — Workspace Dashboard",
        subtitle = "UI agent (Nova) proposing quick actions",
        status = StatusPill("Review", Color(0xFF2A2418), Color(0xFFFFD166)),
        chips = listOf(StatusChip("⏳ Awaiting feedback", Color(0xFF2A2418), Color(0xFFFFD166))),
        actions = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.height(44.dp),
                    border = BorderStroke(1.dp, SurfaceVariantDark),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Request changes")
                }
                Button(
                    onClick = {},
                    modifier = Modifier.height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TechnoteBlue)
                ) {
                    Text("Approve")
                }
            }
        }
    )

    StatusCard(
        title = "Page 3 — Notes + Agents",
        subtitle = "Systems agent (Iris) stitching note detail + agent panel",
        status = StatusPill("Building", TechnoteBlue.copy(alpha = 0.15f), Color(0xFF90C2FF)),
        chips = listOf(
            StatusChip("🚧 In progress", TechnoteBlue.copy(alpha = 0.15f), Color(0xFF90C2FF)),
            StatusChip("ETA 12m", TechnoteBlue.copy(alpha = 0.15f), Color(0xFF90C2FF))
        )
    )

    StatusCard(
        title = "Page 4 — Knowledge base",
        subtitle = "Research agent (Kite) prepping taxonomy + search",
        status = StatusPill("Queued", Color(0xFF2F251A), Color(0xFFFFB47D)),
        chips = listOf(StatusChip("🔁 Up next", Color(0xFF2F251A), Color(0xFFFFB47D))),
        actions = {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.height(44.dp),
                border = BorderStroke(1.dp, SurfaceVariantDark),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary
                )
            ) {
                Text("Assign reviewer")
            }
        }
    )
}

@Composable
private fun ApprovalQueueCards() {
    StatusCard(
        title = "Workspace dashboard",
        subtitle = "Snapshot ready · synced to branch ui/page-02",
        status = null,
        chips = emptyList(),
        actions = {
            Button(
                onClick = {},
                modifier = Modifier.height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechnoteBlue)
            ) {
                Text("Approve & merge")
            }
        }
    )

    StatusCard(
        title = "Authentication",
        subtitle = "Approved · ready to release with other pages",
        status = null,
        chips = listOf(StatusChip("✅ Ready", Color(0xFF1E2A24), Color(0xFF7AEBD5)))
    )
}

@Composable
private fun LiveActivityList() {
    ActivityItem(
        title = "Nova updated hero CTA on Page 2",
        subtitle = "2m ago · diff preview attached"
    )
    ActivityItem(
        title = "Iris requested schema for Note detail",
        subtitle = "8m ago · waiting on spec"
    )
    ActivityItem(
        title = "Kite queued data sources for Knowledge base",
        subtitle = "12m ago · will start after Page 3"
    )
}

@Composable
private fun StatusCard(
    title: String,
    subtitle: String,
    status: StatusPill?,
    chips: List<StatusChip>,
    actions: (@Composable () -> Unit)? = null
 ) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, SurfaceVariantDark, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            status?.let {
                PillChip(text = it.label, background = it.background, border = it.border, content = it.content)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (chips.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                chips.forEach { chip ->
                    PillChip(text = chip.label, background = chip.background, border = chip.border, content = chip.content)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        actions?.invoke()
    }
}

@Composable
private fun ActivityItem(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, SurfaceVariantDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextHint)
    }
}

@Composable
private fun BottomNav() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, SurfaceVariantDark, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NavPill(icon = Icons.Default.Dashboard, label = "Home")
        NavPill(icon = Icons.Default.Inventory2, label = "Notes")
        NavPill(icon = Icons.Default.Star, label = "Agents", active = true)
        NavPill(icon = Icons.Default.Update, label = "Profile")
    }
}

@Composable
private fun NavPill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, active: Boolean = false) {
    val background = if (active) Brush.linearGradient(listOf(TechnoteBlue, TechnoteAccent)) else null
    val contentColor = if (active) Color.White else TextSecondary
    Box(
        modifier = Modifier
            .width(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (background != null) Modifier.background(background) else Modifier
                    .border(1.dp, SurfaceVariantDark, RoundedCornerShape(12.dp))
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = contentColor)
        }
    }
}

@Composable
private fun PillChip(text: String, background: Color, border: Color, content: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = content
        )
    }
}

private data class StatusPill(
    val label: String,
    val background: Color,
    val content: Color,
    val border: Color = content.copy(alpha = 0.35f)
)

private data class StatusChip(
    val label: String,
    val background: Color,
    val content: Color,
    val border: Color = content.copy(alpha = 0.35f)
)

@Preview(showBackground = true)
@Composable
private fun AgentPlannerScreenPreview() {
    TechnoteTheme {
        AgentPlannerScreen()
    }
}

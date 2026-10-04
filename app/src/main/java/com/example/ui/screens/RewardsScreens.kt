package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.LoveDoctorViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.AvatarWithBadge
import com.example.ui.components.GradientButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    viewModel: LoveDoctorViewModel
) {
    val context = LocalContext.current
    val currentUser by viewModel.repository.currentUser.collectAsStateWithLifecycle()
    val availableBalance by viewModel.repository.availableBalance.collectAsStateWithLifecycle()
    val totalEarnings by viewModel.repository.totalEarnings.collectAsStateWithLifecycle()
    val pendingRewards by viewModel.repository.pendingRewards.collectAsStateWithLifecycle()
    val completedRewards by viewModel.repository.completedRewards.collectAsStateWithLifecycle()
    val referrals by viewModel.repository.referrals.collectAsStateWithLifecycle()
    val withdrawals by viewModel.repository.withdrawals.collectAsStateWithLifecycle()
    val adminSettings by viewModel.repository.adminSettings.collectAsStateWithLifecycle()

    val isWithdrawOpen by viewModel.isWithdrawDialogOpen.collectAsStateWithLifecycle()
    val isSimulateReferralOpen by viewModel.isSimulateReferralOpen.collectAsStateWithLifecycle()

    var selectedDashboardTab by remember { mutableIntStateOf(0) } // 0: Overview & Refer, 1: Referrals Status, 2: Withdrawals

    val referralLink = "https://lovedoctor.app/ref/${currentUser.referralCode}"

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareExternal(platform: String) {
        val shareMessage = "Join Love Doctor! Connect, find romance & earn rewards. Use my referral code ${currentUser.referralCode} to claim a ₹500 bonus: $referralLink"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        try {
            when (platform.lowercase()) {
                "whatsapp" -> intent.setPackage("com.whatsapp")
                "telegram" -> intent.setPackage("org.telegram.messenger")
                "facebook" -> intent.setPackage("com.facebook.katana")
                "instagram" -> intent.setPackage("com.instagram.android")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to standard chooser
            context.startActivity(Intent.createChooser(intent, "Share via $platform"))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("rewards_main_screen")
    ) {
        // Sub-tabs: Program Overview, Referral Status, Withdrawals
        TabRow(
            selectedTabIndex = selectedDashboardTab,
            containerColor = NavySurface,
            contentColor = PinkLight,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedDashboardTab == 0,
                onClick = { selectedDashboardTab = 0 },
                text = { Text("Program", fontWeight = FontWeight.Bold, color = if (selectedDashboardTab == 0) PinkLight else Color.LightGray) }
            )
            Tab(
                selected = selectedDashboardTab == 1,
                onClick = { selectedDashboardTab = 1 },
                text = { Text("Referrals (${referrals.size})", fontWeight = FontWeight.Bold, color = if (selectedDashboardTab == 1) PinkLight else Color.LightGray) }
            )
            Tab(
                selected = selectedDashboardTab == 2,
                onClick = { selectedDashboardTab = 2 },
                text = { Text("Withdraw (${withdrawals.size})", fontWeight = FontWeight.Bold, color = if (selectedDashboardTab == 2) PinkLight else Color.LightGray) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedDashboardTab) {
                0 -> {
                    // TAB 0: Referral Program Overview & Sharing
                    item {
                        // Section 6: Header Card "Share Love Earn Rewards"
                        ShareLoveEarnHeaderCard(
                            referralCode = currentUser.referralCode,
                            referralLink = referralLink,
                            rewardAmount = adminSettings.rewardAmount,
                            onCopyCode = { copyToClipboard(currentUser.referralCode, "Referral Code") },
                            onCopyLink = { copyToClipboard(referralLink, "Referral Link") },
                            onSharePlatform = { shareExternal(it) },
                            onOpenDedicatedShareScreen = { viewModel.navigateTo(ScreenDestination.ShareReferralView) }
                        )
                    }

                    item {
                        // Earnings Quick Summary Bar
                        EarningsMetricsRow(
                            balance = availableBalance,
                            pending = pendingRewards,
                            completed = completedRewards,
                            onRequestWithdraw = { viewModel.isWithdrawDialogOpen.value = true }
                        )
                    }

                    item {
                        // How It Works 3-Step Timeline
                        HowItWorksCard(
                            rewardAmount = adminSettings.rewardAmount,
                            trialDays = adminSettings.trialDaysRequirement,
                            minPayment = adminSettings.minQualifyingPayment
                        )
                    }

                    item {
                        // Action buttons row: Terms, History, Admin Panel & Simulation
                        ReferralActionButtons(
                            onViewTerms = { viewModel.navigateTo(ScreenDestination.TermsAndConditionsView) },
                            onViewHistory = { viewModel.navigateTo(ScreenDestination.ReferralHistoryView) },
                            onSimulateFriend = { viewModel.isSimulateReferralOpen.value = true },
                            onOpenAdmin = { viewModel.navigateTo(ScreenDestination.AdminDashboardView) }
                        )
                    }
                }

                1 -> {
                    // TAB 1: Section 9: Referral Status with Filters
                    item {
                        ReferralStatusHeader(
                            total = referrals.size,
                            qualified = referrals.count { it.status == ReferralStatus.QUALIFIED || it.status == ReferralStatus.REWARDED },
                            pending = referrals.count { it.status == ReferralStatus.PENDING },
                            filter = viewModel.referralFilter.collectAsStateWithLifecycle().value,
                            onFilterChange = { viewModel.referralFilter.value = it }
                        )
                    }

                    val currentFilter = viewModel.referralFilter.value
                    val filteredReferrals = if (currentFilter == ReferralStatus.ALL) referrals else referrals.filter { it.status == currentFilter }

                    items(filteredReferrals, key = { it.id }) { item ->
                        ReferralStatusCard(
                            item = item,
                            onQualifyManually = { viewModel.repository.qualifyReferral(item.id) },
                            onReleaseReward = { viewModel.repository.releaseReward(item.id) },
                            onReverseReward = { viewModel.repository.reverseReward(item.id, "Violation of promotional policy") }
                        )
                    }
                }

                2 -> {
                    // TAB 2: Section 7 & 14: Withdrawal Dashboard
                    item {
                        WithdrawalDashboardHeader(
                            availableBalance = availableBalance,
                            onRequestWithdraw = { viewModel.isWithdrawDialogOpen.value = true }
                        )
                    }

                    items(withdrawals, key = { it.id }) { wdr ->
                        WithdrawalItemCard(
                            withdrawal = wdr,
                            onApprove = { viewModel.repository.approveWithdrawal(wdr.id) },
                            onReject = { viewModel.repository.rejectWithdrawal(wdr.id, "Destination details invalid") }
                        )
                    }
                }
            }
        }
    }

    // Withdrawal Request Modal Dialog
    if (isWithdrawOpen) {
        RequestWithdrawalDialog(
            maxBalance = availableBalance,
            onDismiss = { viewModel.isWithdrawDialogOpen.value = false },
            onSubmit = { amount, method, details ->
                val success = viewModel.repository.requestWithdrawal(amount, method, details)
                if (success) {
                    Toast.makeText(context, "Withdrawal request submitted! ⏳", Toast.LENGTH_SHORT).show()
                    viewModel.isWithdrawDialogOpen.value = false
                } else {
                    Toast.makeText(context, "Insufficient balance or invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Simulate New Friend Registration Dialog (for instant testing)
    if (isSimulateReferralOpen) {
        SimulateFriendReferralDialog(
            onDismiss = { viewModel.isSimulateReferralOpen.value = false },
            onSubmit = { name, username ->
                viewModel.repository.simulateNewFriendRegistration(name, username)
                Toast.makeText(context, "Friend registered with code ${currentUser.referralCode}! Status: Pending", Toast.LENGTH_LONG).show()
                viewModel.isSimulateReferralOpen.value = false
            }
        )
    }
}

@Composable
fun ShareLoveEarnHeaderCard(
    referralCode: String,
    referralLink: String,
    rewardAmount: Double,
    onCopyCode: () -> Unit,
    onCopyLink: () -> Unit,
    onSharePlatform: (String) -> Unit,
    onOpenDedicatedShareScreen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .testTag("referral_header_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF280B4A),
                            Color(0xFF6A1B7A),
                            Color(0xFFFF2D87)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Heart & Tagline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = PinkLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LOVE DOCTOR REFERRAL PROGRAM",
                        color = Color(0xFFFFD54F),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Share Love, Earn Rewards",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Invite friends to find their soulmate & earn ₹${rewardAmount.toInt()} per qualified referral!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Unique Referral Code Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "YOUR REFERRAL CODE",
                                color = Color(0xFFFFD54F),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = referralCode,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp
                            )
                        }

                        Button(
                            onClick = onCopyCode,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(38.dp).testTag("copy_referral_code_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Social Share Buttons Row (WhatsApp, Facebook, Instagram, Telegram, More)
                Text(
                    text = "Share via:",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialShareCircleButton(
                        name = "WhatsApp",
                        icon = Icons.Default.Chat,
                        color = Color(0xFF25D366),
                        onClick = { onSharePlatform("WhatsApp") }
                    )
                    SocialShareCircleButton(
                        name = "Facebook",
                        icon = Icons.Default.ThumbUp,
                        color = Color(0xFF1877F2),
                        onClick = { onSharePlatform("Facebook") }
                    )
                    SocialShareCircleButton(
                        name = "Instagram",
                        icon = Icons.Default.CameraAlt,
                        color = Color(0xFFE4405F),
                        onClick = { onSharePlatform("Instagram") }
                    )
                    SocialShareCircleButton(
                        name = "Telegram",
                        icon = Icons.Default.Send,
                        color = Color(0xFF0088CC),
                        onClick = { onSharePlatform("Telegram") }
                    )
                    SocialShareCircleButton(
                        name = "Copy Link",
                        icon = Icons.Default.Link,
                        color = Color(0xFFFF9800),
                        onClick = onCopyLink
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dedicated Promotional Screen CTA button
                Button(
                    onClick = onOpenDedicatedShareScreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Filled.QrCode2, contentDescription = null, tint = PurpleDeep, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Branded Share Card",
                        color = PurpleDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SocialShareCircleButton(
    name: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color)
                .shadow(2.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EarningsMetricsRow(
    balance: Double,
    pending: Double,
    completed: Double,
    onRequestWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AVAILABLE TO WITHDRAW",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${balance.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenSuccess
                    )
                }

                Button(
                    onClick = onRequestWithdraw,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    modifier = Modifier.height(40.dp).testTag("request_withdrawal_button")
                ) {
                    Text("Withdraw", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Pending Rewards", fontSize = 11.sp, color = TextMuted)
                    Text("₹${pending.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OrangeWarning)
                }
                Column {
                    Text("Total Earned", fontSize = 11.sp, color = TextMuted)
                    Text("₹${completed.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PurpleViolet)
                }
                Column {
                    Text("Payout Speed", fontSize = 11.sp, color = TextMuted)
                    Text("Instant UPI", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }
    }
}

@Composable
fun HowItWorksCard(
    rewardAmount: Double,
    trialDays: Int,
    minPayment: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "How It Works?",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(14.dp))

            StepItem(
                step = 1,
                title = "Share your referral link",
                desc = "Send your link or code via WhatsApp, Facebook, or Telegram to single friends looking for romance."
            )
            StepItem(
                step = 2,
                title = "Friend registers and qualifies",
                desc = "Your friend registers and completes the ${trialDays}-day trial period and eligible ₹${minPayment.toInt()} membership/booking."
            )
            StepItem(
                step = 3,
                title = "Reward is released after verification",
                desc = "Server verifies payment integrity. ₹${rewardAmount.toInt()} is credited directly to your wallet for instant withdrawal!"
            )
        }
    }
}

@Composable
fun StepItem(step: Int, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(PinkPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextDark
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ReferralActionButtons(
    onViewTerms: () -> Unit,
    onViewHistory: () -> Unit,
    onSimulateFriend: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onViewTerms,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp)
            ) {
                Icon(Icons.Default.Gavel, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Terms & Rules", fontSize = 12.5.sp, color = TextDark)
            }

            OutlinedButton(
                onClick = onViewHistory,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Audit History", fontSize = 12.5.sp, color = TextDark)
            }
        }

        // Test action: Simulate friend signup + Admin Dashboard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onSimulateFriend,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleViolet)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simulate Friend", fontSize = 12.sp, color = Color.White)
            }

            Button(
                onClick = onOpenAdmin,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyCardElevated)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PinkLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Admin Console", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun ReferralStatusHeader(
    total: Int,
    qualified: Int,
    pending: Int,
    filter: ReferralStatus,
    onFilterChange: (ReferralStatus) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Referral Status Tracker",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark
            )
            Text(
                text = "$qualified qualified / $total total",
                fontSize = 12.sp,
                color = PinkPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips: All, Pending, Qualified, Rewarded, Cancelled
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(ReferralStatus.entries) { st ->
                val isSelected = filter == st
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChange(st) },
                    label = { Text(st.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PinkPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun ReferralStatusCard(
    item: ReferralItem,
    onQualifyManually: () -> Unit,
    onReleaseReward: () -> Unit,
    onReverseReward: () -> Unit
) {
    val statusColor = when (item.status) {
        ReferralStatus.PENDING -> OrangeWarning
        ReferralStatus.QUALIFIED -> BlueInfo
        ReferralStatus.REWARDED -> GreenSuccess
        ReferralStatus.CANCELLED -> RedDanger
        ReferralStatus.ALL -> TextMuted
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.5.dp, RoundedCornerShape(18.dp))
            .testTag("referral_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarWithBadge(
                        avatarUrl = item.referredUser.avatarUrl,
                        size = 44.dp,
                        isOnline = item.referredUser.isOnline
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.referredUser.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = TextDark
                        )
                        Text(
                            text = "Registered: ${item.registeredDate}",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = item.status.name,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Verification & Qualification Detail
            Surface(
                color = LavenderCard,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Reward Amount:", fontSize = 12.sp, color = TextMuted)
                        Text("₹${item.rewardAmount.toInt()}", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Trial Progress:", fontSize = 12.sp, color = TextMuted)
                        Text("${item.trialDaysCompleted}/${item.trialRequiredDays} Days", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: ${item.paymentVerificationStatus}",
                        fontSize = 11.5.sp,
                        color = if (item.isFraudulent) RedDanger else PurpleViolet,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick actions based on status
            if (item.status == ReferralStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onQualifyManually,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueInfo)
                ) {
                    Text("Verify ₹1000 Payment & Qualify", fontSize = 12.sp)
                }
            } else if (item.status == ReferralStatus.QUALIFIED) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onReleaseReward,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                ) {
                    Text("Release ₹${item.rewardAmount.toInt()} Reward to Wallet", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun WithdrawalDashboardHeader(
    availableBalance: Double,
    onRequestWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Withdrawal Center", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            Text(
                "Withdraw your referral earnings directly to your preferred payment account.",
                fontSize = 12.5.sp,
                color = TextMuted,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ready Balance", fontSize = 11.sp, color = TextMuted)
                    Text("₹${availableBalance.toInt()}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = GreenSuccess)
                }

                Button(
                    onClick = onRequestWithdraw,
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Request Payout")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Supported Payment Methods: UPI, Bank Transfer, Paytm, eSewa",
                fontSize = 11.sp,
                color = PurpleViolet,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun WithdrawalItemCard(
    withdrawal: WithdrawalRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val statusColor = when (withdrawal.status) {
        WithdrawalStatus.PENDING -> OrangeWarning
        WithdrawalStatus.PROCESSING -> BlueInfo
        WithdrawalStatus.COMPLETED -> GreenSuccess
        WithdrawalStatus.REJECTED, WithdrawalStatus.CANCELLED -> RedDanger
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "₹${withdrawal.amount.toInt()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "${withdrawal.method.displayName} • ${withdrawal.destinationDetails}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = withdrawal.status.name,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Requested on ${withdrawal.date}", fontSize = 11.sp, color = TextMuted)

            if (withdrawal.adminNote != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: ${withdrawal.adminNote}",
                    fontSize = 11.5.sp,
                    color = PurpleViolet,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RequestWithdrawalDialog(
    maxBalance: Double,
    onDismiss: () -> Unit,
    onSubmit: (Double, WithdrawalMethod, String) -> Unit
) {
    var amountInput by remember { mutableStateOf(if (maxBalance >= 500) "500" else maxBalance.toInt().toString()) }
    var selectedMethod by remember { mutableStateOf(WithdrawalMethod.UPI) }
    var destinationDetails by remember { mutableStateOf("user@okhdfcbank") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Withdrawal", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Available Balance: ₹${maxBalance.toInt()}",
                    color = GreenSuccess,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Select Method:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    WithdrawalMethod.entries.forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = {
                                selectedMethod = method
                                destinationDetails = when (method) {
                                    WithdrawalMethod.UPI -> "alex@okicici"
                                    WithdrawalMethod.BANK_TRANSFER -> "A/C: 9876543210 IFSC: HDFC0001234"
                                    WithdrawalMethod.PAYTM -> "+91 9876543210"
                                    WithdrawalMethod.ESEWA -> "9801234567"
                                }
                            },
                            label = { Text(method.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinkPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = destinationDetails,
                    onValueChange = { destinationDetails = it },
                    label = { Text("${selectedMethod.displayName} Details / ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountInput.toDoubleOrNull() ?: 0.0
                    onSubmit(amt, selectedMethod, destinationDetails)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Text("Submit Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun SimulateFriendReferralDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var friendName by remember { mutableStateOf("Karan Singhania") }
    var friendUsername by remember { mutableStateOf("karan_s") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Simulate New Referral", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "This simulates a new friend registering via your link to test the pending -> qualified -> rewarded pipeline.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = friendName,
                    onValueChange = { friendName = it },
                    label = { Text("Friend's Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = friendUsername,
                    onValueChange = { friendUsername = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(friendName, friendUsername) },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleViolet)
            ) {
                Text("Register Friend")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

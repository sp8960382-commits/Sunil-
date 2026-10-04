package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.LoveDoctorViewModel
import com.example.ui.components.AvatarWithBadge
import com.example.ui.components.GradientButton
import com.example.ui.theme.*

// SECTION 8: SHARE REFERRAL LINK SCREEN
@Composable
fun ShareReferralScreen(
    viewModel: LoveDoctorViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val currentUser by viewModel.repository.currentUser.collectAsStateWithLifecycle()
    val referralLink = "https://lovedoctor.app/ref/${currentUser.referralCode}"

    fun copyLink() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("LoveDoctorReferralLink", referralLink)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Referral link copied!", Toast.LENGTH_SHORT).show()
    }

    fun shareApp(platform: String) {
        val shareMessage = "Hey! Join me on Love Doctor - Chat • Call • Meet • Be Yourself! Use my invite code ${currentUser.referralCode} to claim ₹500 free bonus: $referralLink"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMessage)
        }
        context.startActivity(Intent.createChooser(intent, "Share via $platform"))
    }

    Scaffold(
        topBar = {
            Surface(color = NavySurface, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Share & Earn",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LavenderBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Branded Promotional Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                        .testTag("share_referral_promo_card"),
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
                                        Color(0xFF8E24AA),
                                        Color(0xFFFF2D87)
                                    )
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Join Love Doctor",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = "“Chat • Call • Meet • Be Yourself”",
                                color = Color(0xFFFFD54F),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Text(
                                text = "Refer friends to India's most genuine relationship network. Get ₹500 when they complete their trial & VIP membership!",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.5.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Referral Link Copy Field
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "YOUR UNIQUE INVITE LINK",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(LavenderCard)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = referralLink,
                                fontSize = 13.sp,
                                color = TextDark,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { copyLink() },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Copy", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                // Social Apps Row
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Direct Social Sharing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SocialShareCircleButton("WhatsApp", Icons.Default.Chat, Color(0xFF25D366)) { shareApp("WhatsApp") }
                            SocialShareCircleButton("Facebook", Icons.Default.ThumbUp, Color(0xFF1877F2)) { shareApp("Facebook") }
                            SocialShareCircleButton("Instagram", Icons.Default.CameraAlt, Color(0xFFE4405F)) { shareApp("Instagram") }
                            SocialShareCircleButton("Telegram", Icons.Default.Send, Color(0xFF0088CC)) { shareApp("Telegram") }
                        }
                    }
                }
            }

            item {
                GradientButton(
                    text = "Share Invite Link Everywhere",
                    onClick = { shareApp("all") },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Share
                )
            }
        }
    }
}

// SECTION 10: REFERRAL HISTORY SCREEN
@Composable
fun ReferralHistoryScreen(
    viewModel: LoveDoctorViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val referrals by viewModel.repository.referrals.collectAsStateWithLifecycle()
    val withdrawals by viewModel.repository.withdrawals.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Surface(color = NavySurface, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Referral & Wallet History",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LavenderBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Transaction History & Audit Records",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextDark
                )
            }

            // Reward Credits
            items(referrals.filter { it.status == ReferralStatus.REWARDED }) { ref ->
                HistoryItemRow(
                    title = "+ ₹${ref.rewardAmount.toInt()} Rewarded",
                    subtitle = "Referral: ${ref.referredUser.name} completed VIP qualification",
                    date = ref.registeredDate,
                    isCredit = true,
                    statusText = "Rewarded"
                )
            }

            items(referrals.filter { it.status == ReferralStatus.QUALIFIED }) { ref ->
                HistoryItemRow(
                    title = "+ ₹${ref.rewardAmount.toInt()} Qualified",
                    subtitle = "Referral: ${ref.referredUser.name} passed trial & payment check",
                    date = ref.registeredDate,
                    isCredit = true,
                    statusText = "Ready to Credit"
                )
            }

            // Withdrawals
            items(withdrawals) { wdr ->
                HistoryItemRow(
                    title = "- ₹${wdr.amount.toInt()} Withdrawal Request",
                    subtitle = "Method: ${wdr.method.displayName} (${wdr.destinationDetails})",
                    date = wdr.date,
                    isCredit = false,
                    statusText = wdr.status.name
                )
            }
        }
    }
}

@Composable
fun HistoryItemRow(
    title: String,
    subtitle: String,
    date: String,
    isCredit: Boolean,
    statusText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isCredit) GreenSuccess.copy(alpha = 0.15f) else OrangeWarning.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (isCredit) GreenSuccess else OrangeWarning,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Text(text = subtitle, fontSize = 11.5.sp, color = TextMuted)
                Text(text = date, fontSize = 10.5.sp, color = TextMuted)
            }
            Surface(
                color = if (isCredit) GreenSuccess.copy(alpha = 0.12f) else LavenderCard,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCredit) GreenSuccess else TextDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// SECTION 11: TERMS & CONDITIONS SCREEN
@Composable
fun TermsAndConditionsScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var isAgreed by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            Surface(color = NavySurface, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Referral Terms & Conditions",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LavenderBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("1. Eligibility", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                        Text(
                            "Every registered and verified Love Doctor user in India and supported regions is eligible to participate in the Share & Earn program with an active account in good standing.",
                            fontSize = 12.5.sp,
                            color = TextDark,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text("2. Referral Qualification Criteria", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                        Text(
                            "A referral is deemed qualified only when the referred user:\n" +
                                    "• Registers using the unique referral link or code.\n" +
                                    "• Successfully completes the mandatory 7-day trial period.\n" +
                                    "• Makes an eligible paid Gold Membership/booking of ₹1,000 or greater, verified via backend banking webhook.",
                            fontSize = 12.5.sp,
                            color = TextDark,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text("3. Fraud Prevention & Restrictions", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                        Text(
                            "• Self-referrals using duplicate device identifiers or identical IP addresses are strictly prohibited and automatically cancelled.\n" +
                                    "• Creation of multiple accounts, bot traffic, or emulator spoofing constitutes immediate suspension.\n" +
                                    "• One qualifying transaction generates only one reward.",
                            fontSize = 12.5.sp,
                            color = TextDark,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text("4. Admin Rights & Reward Reversal", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                        Text(
                            "Love Doctor administration reserves the right to review, withhold, or reverse rewards if fraudulent activity or chargebacks occur. Complete audit logs are maintained for every qualification and payout.",
                            fontSize = 12.5.sp,
                            color = TextDark,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Text("5. Payout Methods & Timelines", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                        Text(
                            "Withdrawals can be requested via UPI, Bank Transfer, Paytm, or eSewa. Standard processing time is within 24 hours of admin compliance review.",
                            fontSize = 12.5.sp,
                            color = TextDark,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAgreed,
                        onCheckedChange = { isAgreed = it },
                        colors = CheckboxDefaults.colors(checkedColor = PinkPrimary)
                    )
                    Text(
                        text = "I have read and agree to Love Doctor Referral Program Terms & Policies.",
                        fontSize = 12.5.sp,
                        color = TextDark
                    )
                }
            }

            item {
                GradientButton(
                    text = "Accept & Return",
                    onClick = {
                        Toast.makeText(context, "Terms accepted", Toast.LENGTH_SHORT).show()
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isAgreed
                )
            }
        }
    }
}

// SECTION 12: ADMIN DASHBOARD SCREEN
@Composable
fun AdminDashboardScreen(
    viewModel: LoveDoctorViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val adminSettings by viewModel.repository.adminSettings.collectAsStateWithLifecycle()
    val referrals by viewModel.repository.referrals.collectAsStateWithLifecycle()
    val withdrawals by viewModel.repository.withdrawals.collectAsStateWithLifecycle()
    val auditLogs by viewModel.repository.auditLogs.collectAsStateWithLifecycle()

    var rewardInput by remember { mutableStateOf(adminSettings.rewardAmount.toInt().toString()) }
    var minPaymentInput by remember { mutableStateOf(adminSettings.minQualifyingPayment.toInt().toString()) }
    var trialDaysInput by remember { mutableStateOf(adminSettings.trialDaysRequirement.toString()) }

    Scaffold(
        topBar = {
            Surface(color = NavySurface, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column {
                            Text("Love Doctor Admin", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Referral & Financial Control Panel", color = PinkLight, fontSize = 11.sp)
                        }
                    }

                    // Program Toggle
                    Switch(
                        checked = adminSettings.isReferralProgramActive,
                        onCheckedChange = { viewModel.repository.toggleReferralProgramActive() },
                        colors = SwitchDefaults.colors(checkedThumbColor = PinkPrimary)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LavenderBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // KPI Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("SYSTEM METRICS & KPIS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminKpiCard("Total Referrals", referrals.size.toString(), PinkPrimary, Modifier.weight(1f))
                        AdminKpiCard("Qualified", referrals.count { it.status == ReferralStatus.QUALIFIED || it.status == ReferralStatus.REWARDED }.toString(), BlueInfo, Modifier.weight(1f))
                        AdminKpiCard("Fraud Blocked", referrals.count { it.isFraudulent }.toString(), RedDanger, Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminKpiCard("Pending Withdrawals", withdrawals.count { it.status == WithdrawalStatus.PENDING }.toString(), OrangeWarning, Modifier.weight(1f))
                        AdminKpiCard("Completed Payouts", "₹${withdrawals.filter { it.status == WithdrawalStatus.COMPLETED }.sumOf { it.amount }.toInt()}", GreenSuccess, Modifier.weight(1f))
                    }
                }
            }

            // Configurable Settings Editor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Configure Program Rules", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = rewardInput,
                                onValueChange = { rewardInput = it },
                                label = { Text("Reward (₹)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = minPaymentInput,
                                onValueChange = { minPaymentInput = it },
                                label = { Text("Min Pay (₹)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = trialDaysInput,
                                onValueChange = { trialDaysInput = it },
                                label = { Text("Trial (Days)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val r = rewardInput.toDoubleOrNull() ?: 500.0
                                val m = minPaymentInput.toDoubleOrNull() ?: 1000.0
                                val t = trialDaysInput.toIntOrNull() ?: 7
                                viewModel.repository.updateAdminSettings(
                                    adminSettings.copy(
                                        rewardAmount = r,
                                        minQualifyingPayment = m,
                                        trialDaysRequirement = t
                                    )
                                )
                                Toast.makeText(context, "Admin settings updated!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Program Rules")
                        }
                    }
                }
            }

            // Pending Withdrawals Review
            val pendingWdrs = withdrawals.filter { it.status == WithdrawalStatus.PENDING || it.status == WithdrawalStatus.PROCESSING }
            if (pendingWdrs.isNotEmpty()) {
                item {
                    Text("Pending Withdrawal Approvals (${pendingWdrs.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                }

                items(pendingWdrs) { wdr ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("₹${wdr.amount.toInt()} • ${wdr.method.displayName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(wdr.status.name, color = OrangeWarning, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Text(wdr.destinationDetails, fontSize = 12.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.repository.approveWithdrawal(wdr.id)
                                        Toast.makeText(context, "Withdrawal Approved! Payout sent.", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Text("Approve Payout", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.repository.rejectWithdrawal(wdr.id, "Destination account mismatch")
                                        Toast.makeText(context, "Withdrawal Rejected & Refunded.", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Text("Reject", fontSize = 12.sp, color = RedDanger)
                                }
                            }
                        }
                    }
                }
            }

            // Audit Logs Viewer
            item {
                Text("Audit Logs (Server Verification Trail)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            }

            items(auditLogs.take(6)) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCard)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (log.severity == "ALERT") RedDanger else PurpleViolet)
                            Text(log.timestamp, fontSize = 10.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(log.details, fontSize = 11.5.sp, color = TextDark)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminKpiCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 10.5.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

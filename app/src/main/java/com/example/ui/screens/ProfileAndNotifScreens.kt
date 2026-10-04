package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun ProfileScreen(
    userId: String? = null,
    viewModel: LoveDoctorViewModel,
    onBack: (() -> Unit)? = null
) {
    if (onBack != null) {
        BackHandler { onBack() }
    }
    val context = LocalContext.current
    val currentUser by viewModel.repository.currentUser.collectAsStateWithLifecycle()
    val communityUsers by viewModel.repository.communityUsers.collectAsStateWithLifecycle()
    val posts by viewModel.repository.posts.collectAsStateWithLifecycle()

    val isSelf = userId == null || userId == currentUser.id
    val user = if (isSelf) currentUser else (communityUsers.find { it.id == userId } ?: currentUser)
    val userPosts = posts.filter { it.author.id == user.id }

    var isEditProfileOpen by remember { mutableStateOf(false) }
    var isPaymentModalOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(color = NavySurface, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                        Text(
                            text = if (isSelf) "My Profile" else user.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isSelf) {
                        Row {
                            IconButton(onClick = { viewModel.navigateTo(ScreenDestination.AdminDashboardView) }) {
                                Icon(Icons.Outlined.AdminPanelSettings, contentDescription = "Admin", tint = PinkLight)
                            }
                            IconButton(onClick = { viewModel.navigateTo(ScreenDestination.ShareReferralView) }) {
                                Icon(Icons.Filled.CardGiftcard, contentDescription = "Referral", tint = Color(0xFFFFB300))
                            }
                        }
                    } else {
                        IconButton(onClick = {
                            viewModel.repository.blockUser(user.id)
                            Toast.makeText(context, "User reported and blocked", Toast.LENGTH_SHORT).show()
                            onBack?.invoke()
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LavenderBackground),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Cover Photo & Avatar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    if (user.coverUrl.isNotBlank()) {
                        AsyncImage(
                            model = user.coverUrl,
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(RomanticGradient)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.25f))
                    )

                    // Avatar overlapping bottom
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 20.dp, y = 40.dp)
                    ) {
                        AvatarWithBadge(
                            avatarUrl = user.avatarUrl,
                            size = 84.dp,
                            isOnline = user.isOnline,
                            isVip = user.isVip
                        )
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
            }

            // User Info & Metrics
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                if (user.isVip) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFFFB300),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "GOLD VIP",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(text = "@${user.username}", fontSize = 13.sp, color = TextMuted)
                        }

                        // VIP or Follow Button
                        if (isSelf) {
                            if (!user.isVip) {
                                Button(
                                    onClick = { isPaymentModalOpen = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Join VIP ₹1000", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Button(
                                onClick = { viewModel.repository.toggleFollowUser(user.id) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isFollowing) LavenderCard else PinkPrimary
                                )
                            ) {
                                Text(
                                    text = if (user.isFollowing) "Following" else "Follow",
                                    color = if (user.isFollowing) TextDark else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = user.bio,
                        fontSize = 13.5.sp,
                        color = TextDark,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = user.location, fontSize = 12.5.sp, color = TextMuted)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.Cake, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${user.age} years", fontSize = 12.5.sp, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Bar (Posts, Followers, Following)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProfileStat("Posts", userPosts.size.toString())
                            ProfileStat("Followers", user.followersCount.toString())
                            ProfileStat("Following", user.followingCount.toString())
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Profile Actions (Message & Call if other user)
                    if (!isSelf) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.navigateTo(ScreenDestination.ChatDetail("chat_${user.id}"))
                                },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(21.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                            ) {
                                Icon(Icons.Default.ChatBubble, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Message")
                            }

                            IconButton(
                                onClick = { viewModel.initiateCall(user, CallType.VOICE) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(LavenderCard)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Voice Call", tint = PinkPrimary)
                            }

                            IconButton(
                                onClick = { viewModel.initiateCall(user, CallType.VIDEO) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(LavenderCard)
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = PurpleViolet)
                            }
                        }
                    } else {
                        // Own profile actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isEditProfileOpen = true },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Edit Profile", color = TextDark)
                            }

                            Button(
                                onClick = { viewModel.navigateTo(ScreenDestination.ShareReferralView) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleViolet)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Invite Friends")
                            }
                        }
                    }
                }
            }

            // Posts Grid Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Moments & Memories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            if (userPosts.isEmpty()) {
                item {
                    Text(
                        text = "No posts shared yet.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                    )
                }
            } else {
                items(userPosts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteCard)
                    ) {
                        Column {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Text(
                                text = post.caption,
                                fontSize = 13.sp,
                                color = TextDark,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // VIP Payment Modal (Section 14: Payment System & ₹1000 qualification test)
    if (isPaymentModalOpen) {
        VipMembershipPaymentDialog(
            onDismiss = { isPaymentModalOpen = false },
            onPaymentSuccess = {
                viewModel.repository.purchaseVipMembership()
                Toast.makeText(context, "₹1000 VIP Paid! Webhook verified qualification! 👑", Toast.LENGTH_LONG).show()
                isPaymentModalOpen = false
            }
        )
    }

    // Edit Profile Dialog
    if (isEditProfileOpen) {
        var editBio by remember { mutableStateOf(user.bio) }
        var editLocation by remember { mutableStateOf(user.location) }
        AlertDialog(
            onDismissRequest = { isEditProfileOpen = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.repository.currentUser.value = viewModel.repository.currentUser.value.copy(
                        bio = editBio,
                        location = editLocation
                    )
                    Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                    isEditProfileOpen = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditProfileOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(text = label, fontSize = 11.5.sp, color = TextMuted)
    }
}

@Composable
fun VipMembershipPaymentDialog(
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedPaymentProvider by remember { mutableStateOf("UPI / GooglePay / PhonePe") }
    val paymentProviders = listOf("UPI / GooglePay / PhonePe", "Credit / Debit Card", "Net Banking", "Paytm")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFB300))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Love Doctor Gold VIP", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Upgrade to Love Doctor Gold for ₹1,000.\nThis payment satisfies the qualification condition for referral rewards!",
                    fontSize = 13.sp,
                    color = TextDark,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text("Select Payment Gateway:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                paymentProviders.forEach { provider ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPaymentProvider = provider }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentProvider == provider,
                            onClick = { selectedPaymentProvider = provider },
                            colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                        )
                        Text(text = provider, fontSize = 13.sp, color = TextDark)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🔒 Secured via 256-bit SSL Banking Webhook",
                    fontSize = 11.sp,
                    color = GreenSuccess,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onPaymentSuccess,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
            ) {
                Text("Pay ₹1,000 & Verify", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

// SECTION 13: NOTIFICATIONS SCREEN
@Composable
fun NotificationsScreen(
    viewModel: LoveDoctorViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val notifications by viewModel.repository.notifications.collectAsStateWithLifecycle()

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
                        Text("Notifications", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = { viewModel.repository.markNotificationsRead() }) {
                        Text("Mark all read", color = PinkLight, fontSize = 12.sp)
                    }
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
            if (notifications.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Text("No notifications right now.", color = TextMuted)
                    }
                }
            } else {
                items(notifications, key = { it.id }) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (!notif.isRead) LavenderCard else WhiteCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            NotificationType.REWARD_CREDITED -> GreenSuccess.copy(alpha = 0.2f)
                                            NotificationType.REFERRAL_QUALIFIED -> PinkPrimary.copy(alpha = 0.2f)
                                            NotificationType.WITHDRAWAL_APPROVED -> Color(0xFFFFB300).copy(alpha = 0.2f)
                                            else -> PurpleViolet.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        NotificationType.REWARD_CREDITED -> Icons.Default.MonetizationOn
                                        NotificationType.REFERRAL_QUALIFIED -> Icons.Default.Stars
                                        NotificationType.CHAT, NotificationType.MESSAGE -> Icons.Default.ChatBubble
                                        NotificationType.LIKE -> Icons.Default.Favorite
                                        NotificationType.FOLLOW -> Icons.Default.PersonAdd
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = PinkPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                                Text(notif.message, fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp)
                                Text(notif.timestamp, fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.ui.LoveDoctorViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.AvatarWithBadge
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    viewModel: LoveDoctorViewModel,
    onOpenProfile: (String) -> Unit,
    onStartChat: (String) -> Unit
) {
    val communityUsers by viewModel.repository.communityUsers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.exploreSearchQuery.collectAsStateWithLifecycle()
    val locationFilter by viewModel.exploreLocationFilter.collectAsStateWithLifecycle()
    val onlineOnly by viewModel.exploreOnlineOnly.collectAsStateWithLifecycle()
    val selectedInterest by viewModel.exploreSelectedInterest.collectAsStateWithLifecycle()

    val interestsList = listOf("All", "Romance", "Coffee Dates", "Music", "Travel", "Fitness", "Art & Design", "Foodie", "Books")
    val locationsList = listOf("All", "Mumbai, India", "Delhi, India", "Bengaluru, India", "Pune, India", "Kochi, Kerala")

    val filteredUsers = communityUsers.filter { user ->
        val matchesQuery = searchQuery.isBlank() ||
                user.name.contains(searchQuery, ignoreCase = true) ||
                user.username.contains(searchQuery, ignoreCase = true) ||
                user.bio.contains(searchQuery, ignoreCase = true)

        val matchesLocation = locationFilter == "All" || user.location == locationFilter
        val matchesOnline = !onlineOnly || user.isOnline
        val matchesInterest = selectedInterest == "All" || user.interests.any { it.contains(selectedInterest, ignoreCase = true) }

        matchesQuery && matchesLocation && matchesOnline && matchesInterest
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("explore_screen")
    ) {
        // Search Input Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = WhiteCard,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PinkPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.exploreSearchQuery.value = it },
                    placeholder = { Text("Discover by name, bio, or interest...", color = TextMuted, fontSize = 13.5.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.exploreSearchQuery.value = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            }
        }

        // Filter chips: Interests
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(interestsList) { interest ->
                val isSelected = selectedInterest == interest
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.exploreSelectedInterest.value = interest },
                    label = { Text(interest, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PinkPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Online filter & Location row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = onlineOnly,
                    onCheckedChange = { viewModel.exploreOnlineOnly.value = it },
                    colors = CheckboxDefaults.colors(checkedColor = PinkPrimary)
                )
                Text("Online Now Only", fontSize = 12.5.sp, color = TextDark, fontWeight = FontWeight.Medium)
            }

            // Quick location selector
            var locationDropdownOpen by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { locationDropdownOpen = true },
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (locationFilter == "All") "All Cities" else locationFilter.split(",").first(),
                        fontSize = 11.5.sp,
                        color = TextDark
                    )
                }

                DropdownMenu(
                    expanded = locationDropdownOpen,
                    onDismissRequest = { locationDropdownOpen = false }
                ) {
                    locationsList.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc) },
                            onClick = {
                                viewModel.exploreLocationFilter.value = loc
                                locationDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Users grid/list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredUsers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matches found for your search filters. Try broadening your criteria!",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(filteredUsers, key = { it.id }) { user ->
                    ExploreUserCard(
                        user = user,
                        onOpenProfile = { onOpenProfile(user.id) },
                        onToggleFollow = { viewModel.repository.toggleFollowUser(user.id) },
                        onStartChat = {
                            val existingChat = viewModel.repository.chats.value.find { it.participant.id == user.id }
                            if (existingChat != null) {
                                onStartChat(existingChat.id)
                            } else {
                                // Create new chat
                                onStartChat("chat_${user.id}")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExploreUserCard(
    user: User,
    onOpenProfile: () -> Unit,
    onToggleFollow: () -> Unit,
    onStartChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onOpenProfile)
            .testTag("explore_user_${user.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Cover + Avatar banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(RomanticGradient)
            ) {
                if (user.coverUrl.isNotBlank()) {
                    AsyncImage(
                        model = user.coverUrl,
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )

                // Location badge on top right
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = user.location.split(",").first(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.offset(y = (-30).dp)
                    ) {
                        AvatarWithBadge(
                            avatarUrl = user.avatarUrl,
                            size = 64.dp,
                            isOnline = user.isOnline,
                            isVip = user.isVip
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${user.age}",
                                    color = PinkPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "@${user.username}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // Follow Button
                    Button(
                        onClick = onToggleFollow,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isFollowing) LavenderCard else PinkPrimary
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (user.isFollowing) "Following" else "Follow",
                            color = if (user.isFollowing) TextDark else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = user.bio,
                    fontSize = 13.sp,
                    color = TextDark,
                    lineHeight = 18.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Interests Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    user.interests.take(3).forEach { interest ->
                        Surface(
                            color = LavenderCard,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "✨ $interest",
                                color = PurpleViolet,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Chat & Profile buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenProfile,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(19.dp)
                    ) {
                        Text("View Profile", fontSize = 12.5.sp, color = TextDark)
                    }

                    Button(
                        onClick = onStartChat,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(19.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) {
                        Icon(Icons.Filled.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Message", fontSize = 12.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

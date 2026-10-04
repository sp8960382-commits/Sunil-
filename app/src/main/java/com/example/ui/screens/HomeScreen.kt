package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.ui.LoveDoctorViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.AvatarWithBadge
import com.example.ui.components.GradientButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LoveDoctorViewModel,
    onNavigateToReferral: () -> Unit
) {
    val context = LocalContext.current
    val posts by viewModel.repository.posts.collectAsStateWithLifecycle()
    val stories by viewModel.repository.stories.collectAsStateWithLifecycle()
    val currentUser by viewModel.repository.currentUser.collectAsStateWithLifecycle()
    val commentsMap by viewModel.repository.comments.collectAsStateWithLifecycle()

    val isCreatePostOpen by viewModel.isCreatePostOpen.collectAsStateWithLifecycle()
    val activeCommentsPostId by viewModel.activeCommentsPostId.collectAsStateWithLifecycle()

    var showShareSnackbar by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(LavenderBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_feed_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Stories Carousel
            item {
                StoriesSection(
                    stories = stories,
                    onStoryClick = { /* View story */ },
                    onAddStoryClick = { viewModel.isCreatePostOpen.value = true }
                )
            }

            // 2. Share & Earn Referral Hero Banner (Instagram / Tinder VIP style banner)
            item {
                ReferralHeroBanner(
                    referralCode = currentUser.referralCode,
                    onInviteClick = onNavigateToReferral,
                    onShareLinkClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Find your romantic match on Love Doctor! Join using my referral code ${currentUser.referralCode} and get ₹500 bonus: https://lovedoctor.app/ref/${currentUser.referralCode}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                    }
                )
            }

            // 3. Create Post Bar
            item {
                CreatePostTriggerCard(
                    userAvatar = currentUser.avatarUrl,
                    onClick = { viewModel.isCreatePostOpen.value = true }
                )
            }

            // 4. Feed Posts
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    onLikeClick = { viewModel.repository.toggleLikePost(post.id) },
                    onCommentClick = { viewModel.activeCommentsPostId.value = post.id },
                    onSaveClick = { viewModel.repository.toggleSavePost(post.id) },
                    onShareClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out this romantic post on Love Doctor by ${post.author.name}: ${post.caption}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Post"))
                    },
                    onAuthorClick = {
                        if (post.author.id != currentUser.id) {
                            viewModel.navigateTo(ScreenDestination.UserProfileView(post.author.id))
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Create Post Dialog
        if (isCreatePostOpen) {
            CreatePostDialog(
                onDismiss = { viewModel.isCreatePostOpen.value = false },
                onPostCreated = { caption, imageUrl ->
                    viewModel.repository.createPost(caption, imageUrl)
                    viewModel.isCreatePostOpen.value = false
                }
            )
        }

        // Comments Bottom Sheet
        if (activeCommentsPostId != null) {
            val postId = activeCommentsPostId!!
            val comments = commentsMap[postId] ?: emptyList()
            CommentsBottomSheet(
                comments = comments,
                onDismiss = { viewModel.activeCommentsPostId.value = null },
                onAddComment = { text ->
                    viewModel.repository.addComment(postId, text)
                }
            )
        }
    }
}

@Composable
fun StoriesSection(
    stories: List<Story>,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add Your Story
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(onClick = onAddStoryClick)
                ) {
                    Box(
                        modifier = Modifier.size(68.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(LavenderCard)
                                .border(1.5.dp, PinkPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Story",
                                tint = PinkPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your Story",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                }
            }

            // User stories
            items(stories) { story ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onStoryClick(story) }
                ) {
                    AvatarWithBadge(
                        avatarUrl = story.user.avatarUrl,
                        size = 64.dp,
                        hasStory = true,
                        isStoryViewed = story.isViewed,
                        isOnline = story.user.isOnline
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = story.user.name.split(" ").firstOrNull() ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ReferralHeroBanner(
    referralCode: String,
    onInviteClick: () -> Unit,
    onShareLinkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .testTag("home_referral_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2E0854),
                            Color(0xFF67147A),
                            Color(0xFFFF2D87)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CardGiftcard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SHARE & EARN REWARDS",
                                color = Color(0xFFFFE082),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Earn ₹500 for every friend!",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Code: $referralCode",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "When your friend signs up & completes their 7-day trial and ₹1000 VIP booking, you receive ₹500 directly in your wallet!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onInviteClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "View Program",
                            color = PinkPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = onShareLinkClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2D87))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Link",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePostTriggerCard(
    userAvatar: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("create_post_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarWithBadge(avatarUrl = userAvatar, size = 42.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LavenderCard)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Share your romantic thoughts or photos...",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Upload photo",
                tint = PinkPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onAuthorClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .testTag("post_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Post Author Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onAuthorClick)
                ) {
                    AvatarWithBadge(
                        avatarUrl = post.author.avatarUrl,
                        size = 44.dp,
                        isOnline = post.author.isOnline
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.author.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = TextDark
                            )
                            if (post.isSponsored) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = PinkPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "SPONSORED",
                                        color = PinkPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (post.isSponsored) post.sponsoredBrand else "@${post.author.username} • ${post.timestamp}",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = { /* Menu */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextMuted
                    )
                }
            }

            // Post Media Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = post.mediaUrl,
                    contentDescription = "Post image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Action Icons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Like button with animation
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) PinkPrimary else TextDark,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Comment button
                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier.testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = TextDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.testTag("share_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = "Share",
                            tint = TextDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Bookmark / Save
                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) PurpleViolet else TextDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Likes and Caption
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${post.likesCount} likes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = post.author.name + " ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextDark
                    )
                    Text(
                        text = post.caption,
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )
                }

                if (post.commentsCount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "View all ${post.commentsCount} comments",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable(onClick = onCommentClick)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onPostCreated: (caption: String, imageUrl: String) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var selectedPresetImage by remember {
        mutableStateOf("https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=1000")
    }

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=1000",
        "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=1000",
        "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=1000",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Love Post", fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("What romantic thought is on your mind?") },
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Select Photo:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(presetImages) { url ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedPresetImage == url) 2.dp else 0.dp,
                                    color = if (selectedPresetImage == url) PinkPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPresetImage = url }
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPostCreated(caption, selectedPresetImage) },
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                enabled = caption.isNotBlank()
            ) {
                Text("Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    comments: List<Comment>,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WhiteCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Comments (${comments.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Text(
                            text = "No comments yet. Be the first to spread some love! 💕",
                            color = TextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                } else {
                    items(comments) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            AvatarWithBadge(avatarUrl = comment.author.avatarUrl, size = 36.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = comment.author.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = comment.timestamp,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = comment.text,
                                    fontSize = 13.sp,
                                    color = TextDark,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Add a sweet comment...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            onAddComment(newCommentText)
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PinkPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Post Comment",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

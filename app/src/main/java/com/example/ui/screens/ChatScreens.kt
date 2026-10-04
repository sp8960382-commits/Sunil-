package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextOverflow
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
fun ChatListScreen(
    viewModel: LoveDoctorViewModel,
    onOpenChat: (String) -> Unit
) {
    val chats by viewModel.repository.chats.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Messages, 1: Requests
    var searchQuery by remember { mutableStateOf("") }

    val activeChats = chats.filter { !it.isRequest }
    val requestChats = chats.filter { it.isRequest }

    val filteredList = (if (selectedTab == 0) activeChats else requestChats).filter {
        it.participant.name.contains(searchQuery, ignoreCase = true) ||
                it.participant.username.contains(searchQuery, ignoreCase = true) ||
                it.lastMessage.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .testTag("chat_list_screen")
    ) {
        // Search bar
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
                    tint = TextMuted
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search matches & chats...", color = TextMuted, fontSize = 14.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            }
        }

        // Tabs: Active Chats vs Chat Requests
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = WhiteCard,
            contentColor = PinkPrimary,
            modifier = Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Messages (${activeChats.size})",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Requests",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                        if (requestChats.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(PinkPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = requestChats.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chats list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = null,
                                tint = PinkPrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (selectedTab == 0) "No conversations yet" else "No pending chat requests",
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Explore profiles to connect and share love!",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { chat ->
                    ChatListItemCard(
                        chat = chat,
                        isRequest = selectedTab == 1,
                        onClick = { onOpenChat(chat.id) },
                        onVoiceCall = { viewModel.initiateCall(chat.participant, CallType.VOICE) },
                        onVideoCall = { viewModel.initiateCall(chat.participant, CallType.VIDEO) },
                        onAccept = { viewModel.repository.acceptChatRequest(chat.id) },
                        onReject = { viewModel.repository.rejectChatRequest(chat.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItemCard(
    chat: Chat,
    isRequest: Boolean,
    onClick: () -> Unit,
    onVoiceCall: () -> Unit,
    onVideoCall: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag("chat_item_${chat.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarWithBadge(
                    avatarUrl = chat.participant.avatarUrl,
                    size = 52.dp,
                    isOnline = chat.isOnline
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = chat.participant.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                        Text(
                            text = chat.lastMessageTime,
                            fontSize = 11.5.sp,
                            color = if (chat.unreadCount > 0) PinkPrimary else TextMuted,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = chat.lastMessage,
                            fontSize = 13.sp,
                            color = if (chat.unreadCount > 0) TextDark else TextMuted,
                            fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (chat.unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(PinkPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chat.unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (isRequest) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(17.dp)
                    ) {
                        Text("Decline", fontSize = 12.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) {
                        Text("Accept", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    viewModel: LoveDoctorViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val chats by viewModel.repository.chats.collectAsStateWithLifecycle()
    val chat = chats.find { it.id == chatId } ?: return
    val messagesMap by viewModel.repository.messages.collectAsStateWithLifecycle()
    val messages = (messagesMap[chatId] ?: emptyList()).filter { !it.isDeletedForMe }
    val listState = rememberLazyListState()

    var messageInput by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var selectedMessageForMenu by remember { mutableStateOf<Message?>(null) }
    var showMenuDropdown by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var chatSearchQuery by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = NavySurface,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Avatar + Name + Online
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.navigateTo(ScreenDestination.UserProfileView(chat.participant.id))
                            }
                    ) {
                        AvatarWithBadge(
                            avatarUrl = chat.participant.avatarUrl,
                            size = 40.dp,
                            isOnline = chat.isOnline
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = chat.participant.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (chat.isOnline) GreenSuccess else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (chat.isOnline) "Online" else "Offline",
                                    color = if (chat.isOnline) PinkLight else Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Voice Call Button
                    IconButton(
                        onClick = { viewModel.initiateCall(chat.participant, CallType.VOICE) },
                        modifier = Modifier.testTag("chat_voice_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Voice Call",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Video Call Button
                    IconButton(
                        onClick = { viewModel.initiateCall(chat.participant, CallType.VIDEO) },
                        modifier = Modifier.testTag("chat_video_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Videocam,
                            contentDescription = "Video Call",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 3-dot menu
                    Box {
                        IconButton(onClick = { showMenuDropdown = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenuDropdown,
                            onDismissRequest = { showMenuDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("View Profile") },
                                onClick = {
                                    showMenuDropdown = false
                                    viewModel.navigateTo(ScreenDestination.UserProfileView(chat.participant.id))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (chat.isMuted) "Unmute Notifications" else "Mute Notifications") },
                                onClick = {
                                    showMenuDropdown = false
                                    viewModel.repository.toggleMuteChat(chat.id)
                                    Toast.makeText(context, if (chat.isMuted) "Unmuted" else "Muted notifications", Toast.LENGTH_SHORT).show()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Search Conversation") },
                                onClick = {
                                    showMenuDropdown = false
                                    isSearchActive = !isSearchActive
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Block / Report User", color = RedDanger) },
                                onClick = {
                                    showMenuDropdown = false
                                    viewModel.repository.blockUser(chat.participant.id)
                                    Toast.makeText(context, "User blocked and reported", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LavenderBackground)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // In-chat Search Bar
                if (isSearchActive) {
                    Surface(
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = chatSearchQuery,
                                onValueChange = { chatSearchQuery = it },
                                placeholder = { Text("Search in chat...", fontSize = 13.sp) },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                            IconButton(onClick = { isSearchActive = false; chatSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    }
                }

                // Messages list
                val displayedMessages = if (chatSearchQuery.isBlank()) messages else messages.filter {
                    it.text.contains(chatSearchQuery, ignoreCase = true)
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedMessages, key = { it.id }) { msg ->
                        val isMe = msg.senderId == "user_me"
                        MessageBubbleItem(
                            message = msg,
                            isMe = isMe,
                            onLongPress = { selectedMessageForMenu = msg },
                            onReact = { emoji ->
                                viewModel.repository.addReaction(chat.id, msg.id, emoji)
                            }
                        )
                    }
                }

                // Reply indicator
                if (replyingToMessage != null) {
                    Surface(
                        color = LavenderCard,
                        modifier = Modifier.fillMaxWidth().border(1.dp, GrayBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(32.dp)
                                    .background(PinkPrimary, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Replying to message", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PinkPrimary)
                                Text(replyingToMessage!!.text, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextDark)
                            }
                            IconButton(onClick = { replyingToMessage = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel reply", tint = TextMuted)
                            }
                        }
                    }
                }

                // Emoji Picker Row
                if (showEmojiPicker) {
                    val emojis = listOf("❤️", "😍", "😘", "💖", "✨", "🌹", "☕", "🥰", "🎉", "🔥", "💋", "👍")
                    Surface(
                        color = WhiteCard,
                        modifier = Modifier.fillMaxWidth().border(1.dp, GrayBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            emojis.forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .clickable {
                                            messageInput += emoji
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }
                }

                // Input Bar (matches Reference #2)
                Surface(
                    color = WhiteCard,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji Toggle Button
                        IconButton(onClick = { showEmojiPicker = !showEmojiPicker }) {
                            Icon(
                                imageVector = if (showEmojiPicker) Icons.Filled.Keyboard else Icons.Outlined.SentimentSatisfied,
                                contentDescription = "Emojis",
                                tint = PinkPrimary
                            )
                        }

                        // Attachments (Photos / Voice / Files)
                        IconButton(onClick = {
                            // Send quick preset romantic photo
                            viewModel.repository.sendMessage(
                                chatId = chat.id,
                                text = "Sharing a romantic snapshot! 📸",
                                type = MessageType.IMAGE,
                                mediaUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=800"
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.AttachFile,
                                contentDescription = "Attach",
                                tint = TextMuted
                            )
                        }

                        // Text Field
                        TextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Type a romantic message...", fontSize = 14.sp, color = TextMuted) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = LavenderCard,
                                unfocusedContainerColor = LavenderCard,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp, max = 100.dp)
                                .testTag("chat_input_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Large Pink Gradient Send / Voice record Button
                        if (messageInput.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    viewModel.repository.sendMessage(
                                        chatId = chat.id,
                                        text = messageInput.trim(),
                                        replyTo = replyingToMessage?.text
                                    )
                                    messageInput = ""
                                    replyingToMessage = null
                                    showEmojiPicker = false
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(RomanticGradient)
                                    .testTag("chat_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            // Voice note recording simulation
                            IconButton(
                                onClick = {
                                    viewModel.repository.sendMessage(
                                        chatId = chat.id,
                                        text = "Voice message",
                                        type = MessageType.VOICE,
                                        voiceDurationSec = 14
                                    )
                                    Toast.makeText(context, "Voice note sent 🎤", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(PinkPrimary)
                                    .testTag("chat_voice_record_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "Record Voice Note",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Message action context dialog
        if (selectedMessageForMenu != null) {
            val msg = selectedMessageForMenu!!
            AlertDialog(
                onDismissRequest = { selectedMessageForMenu = null },
                title = { Text("Message Options", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Quick Reactions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("❤️", "🔥", "😍", "👏", "😂", "😢").forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.repository.addReaction(chat.id, msg.id, emoji)
                                            selectedMessageForMenu = null
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        ListItem(
                            headlineContent = { Text("Reply") },
                            leadingContent = { Icon(Icons.Default.Reply, contentDescription = null) },
                            modifier = Modifier.clickable {
                                replyingToMessage = msg
                                selectedMessageForMenu = null
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Copy Text") },
                            leadingContent = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                            modifier = Modifier.clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("LoveDoctorMessage", msg.text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                selectedMessageForMenu = null
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Delete For Me") },
                            leadingContent = { Icon(Icons.Default.Delete, contentDescription = null) },
                            modifier = Modifier.clickable {
                                viewModel.repository.deleteMessageForMe(chat.id, msg.id)
                                selectedMessageForMenu = null
                            }
                        )
                        if (msg.senderId == "user_me") {
                            ListItem(
                                headlineContent = { Text("Delete For Everyone", color = RedDanger) },
                                leadingContent = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = RedDanger) },
                                modifier = Modifier.clickable {
                                    viewModel.repository.deleteMessageForEveryone(chat.id, msg.id)
                                    selectedMessageForMenu = null
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedMessageForMenu = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun MessageBubbleItem(
    message: Message,
    isMe: Boolean,
    onLongPress: () -> Unit,
    onReact: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(
                    elevation = if (isMe) 2.dp else 1.dp,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isMe) 18.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 18.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isMe) 18.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 18.dp
                    )
                )
                .background(
                    if (isMe) RomanticGradient
                    else Brush.linearGradient(listOf(Color.White, Color.White))
                )
                .clickable(onClick = onLongPress)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                // Reply quote if present
                if (message.replyToText != null) {
                    Surface(
                        color = (if (isMe) Color.Black else Color.Gray).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "↩ ${message.replyToText}",
                            color = if (isMe) Color.White.copy(alpha = 0.9f) else TextDark,
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Image Media
                if (message.type == MessageType.IMAGE && message.mediaUrl != null) {
                    AsyncImage(
                        model = message.mediaUrl,
                        contentDescription = "Shared image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Voice Note Waveform Player
                if (message.type == MessageType.VOICE) {
                    var isPlaying by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isMe) Color.White.copy(alpha = 0.3f) else PinkPrimary)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play voice note",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                                listOf(12, 24, 16, 32, 20, 28, 14, 22, 30, 18, 26, 12).forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(h.dp)
                                            .background(
                                                if (isMe) Color.White else PinkPrimary,
                                                RoundedCornerShape(1.5.dp)
                                            )
                                    )
                                }
                            }
                            Text(
                                text = "0:${String.format("%02d", message.voiceDurationSec ?: 14)}",
                                fontSize = 10.sp,
                                color = if (isMe) Color.White.copy(alpha = 0.8f) else TextMuted
                            )
                        }
                    }
                } else {
                    // Regular text
                    Text(
                        text = message.text,
                        color = if (isMe) Color.White else TextDark,
                        fontSize = 14.5.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Time and Status checks
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        color = if (isMe) Color.White.copy(alpha = 0.75f) else TextMuted,
                        fontSize = 10.5.sp
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = "Seen",
                            tint = if (message.isRead) Color(0xFFFFE082) else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Reaction pill
        if (message.reaction != null) {
            Surface(
                color = WhiteCard,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .padding(horizontal = 6.dp)
            ) {
                Text(
                    text = message.reaction,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun CallScreen(
    viewModel: LoveDoctorViewModel
) {
    val activeCall by viewModel.repository.activeCall.collectAsStateWithLifecycle()
    val participant = activeCall.participant ?: return

    val durationMin = activeCall.durationSeconds / 60
    val durationSec = activeCall.durationSeconds % 60
    val durationStr = String.format("%02d:%02d", durationMin, durationSec)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkRomanticGradient)
            .testTag("active_call_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (activeCall.type == CallType.VIDEO) Icons.Filled.Videocam else Icons.Filled.Call,
                            contentDescription = null,
                            tint = PinkLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (activeCall.type == CallType.VIDEO) "Love Doctor Video Call" else "Love Doctor Voice Call",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = participant.name,
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (!activeCall.isConnected) "Calling..." else durationStr,
                    color = if (!activeCall.isConnected) PinkLight else GreenSuccess,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Big Avatar or Video Frame
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(RomanticGradient)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = participant.avatarUrl,
                    contentDescription = participant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            // Call Controls Row
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    IconButton(
                        onClick = { viewModel.repository.toggleCallMute() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (activeCall.isMuted) Color.White else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (activeCall.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                            contentDescription = "Mute",
                            tint = if (activeCall.isMuted) Color.Black else Color.White
                        )
                    }

                    // Speaker Button
                    IconButton(
                        onClick = { viewModel.repository.toggleCallSpeaker() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (activeCall.isSpeakerOn) Color.White else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (activeCall.isSpeakerOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeDown,
                            contentDescription = "Speaker",
                            tint = if (activeCall.isSpeakerOn) Color.Black else Color.White
                        )
                    }

                    if (activeCall.type == CallType.VIDEO) {
                        // Video camera toggle
                        IconButton(
                            onClick = { viewModel.repository.toggleVideoCamera() },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (activeCall.isVideoCameraOff) Color.White else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (activeCall.isVideoCameraOff) Icons.Filled.VideocamOff else Icons.Filled.Videocam,
                                contentDescription = "Video",
                                tint = if (activeCall.isVideoCameraOff) Color.Black else Color.White
                            )
                        }
                    }

                    // Large Red End Call Button
                    IconButton(
                        onClick = { viewModel.endCurrentCall() },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(RedDanger)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class LoveDoctorRepository {

    // Current Logged-in User
    val currentUser = MutableStateFlow(
        User(
            id = "user_me",
            name = "Alex Johnson",
            username = "alex_lovelover",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            coverUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=1000",
            bio = "Doctor of Love ❤️ Relationship advice, deep talks & genuine connections. Living life one romantic sunset at a time ✨",
            location = "Mumbai, Maharashtra",
            age = 25,
            interests = listOf("Romance", "Coffee Dates", "Psychology", "Live Music", "Travel"),
            followersCount = 1420,
            followingCount = 312,
            isFollowing = false,
            isOnline = true,
            referralCode = "LDK12345",
            isVip = true
        )
    )

    // Community Users
    private val _communityUsers = MutableStateFlow<List<User>>(listOf(
        User(
            id = "user_priya",
            name = "Priya Sharma",
            username = "priya_smiles",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
            coverUrl = "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=1000",
            bio = "Hopeless romantic searching for genuine connection 🌸 Believer in coffee dates and long late-night talks.",
            location = "Delhi, India",
            age = 23,
            interests = listOf("Poetry", "Romance", "Sunset Walks", "Books"),
            followersCount = 3850,
            followingCount = 420,
            isFollowing = true,
            isOnline = true,
            referralCode = "PRIYA88"
        ),
        User(
            id = "user_rohan",
            name = "Rohan Mehta",
            username = "rohan_vibes",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            coverUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1000",
            bio = "Fitness enthusiast, foodie & weekend traveler ✈️ Looking for someone with a great sense of humor!",
            location = "Bengaluru, India",
            age = 26,
            interests = listOf("Fitness", "Travel", "Cooking", "Live Music"),
            followersCount = 2100,
            followingCount = 390,
            isFollowing = false,
            isOnline = false,
            referralCode = "ROHAN99"
        ),
        User(
            id = "user_ananya",
            name = "Ananya Roy",
            username = "ananya_sparkle",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
            coverUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000",
            bio = "Art lover & architect 🎨 Seeking deep conversations that make you forget your phone exists.",
            location = "Pune, India",
            age = 24,
            interests = listOf("Art & Design", "Coffee Dates", "Psychology", "Cinema"),
            followersCount = 5400,
            followingCount = 280,
            isFollowing = true,
            isOnline = true,
            referralCode = "ANANYA21"
        ),
        User(
            id = "user_arjun",
            name = "Arjun Kapoor",
            username = "arjun_lens",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400",
            coverUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=1000",
            bio = "Photographer capturing candid love stories 📸 Looking for my muse.",
            location = "Mumbai, India",
            age = 27,
            interests = listOf("Photography", "Road Trips", "Acoustic Music", "Indie Films"),
            followersCount = 6800,
            followingCount = 510,
            isFollowing = false,
            isOnline = true,
            referralCode = "ARJUN55"
        ),
        User(
            id = "user_kavya",
            name = "Kavya Nair",
            username = "kavya_nair",
            avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400",
            coverUrl = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=1000",
            bio = "Dancer and food blogger 🍰 Let's share dessert and meaningful stories!",
            location = "Kochi, Kerala",
            age = 22,
            interests = listOf("Dance", "Foodie", "Beaches", "Pet Lover"),
            followersCount = 4120,
            followingCount = 330,
            isFollowing = false,
            isOnline = true,
            referralCode = "KAVYA77"
        )
    ))
    val communityUsers: StateFlow<List<User>> = _communityUsers.asStateFlow()

    // Stories
    private val _stories = MutableStateFlow<List<Story>>(listOf(
        Story(
            id = "story_me",
            user = currentUser.value,
            mediaUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=600",
            isViewed = false,
            timestamp = "Just now"
        ),
        Story(
            id = "story_1",
            user = _communityUsers.value[0], // Priya
            mediaUrl = "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=600",
            isViewed = false,
            timestamp = "35m ago"
        ),
        Story(
            id = "story_2",
            user = _communityUsers.value[1], // Rohan
            mediaUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600",
            isViewed = false,
            timestamp = "1h ago"
        ),
        Story(
            id = "story_3",
            user = _communityUsers.value[2], // Ananya
            mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600",
            isViewed = true,
            timestamp = "3h ago"
        ),
        Story(
            id = "story_4",
            user = _communityUsers.value[3], // Arjun
            mediaUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=600",
            isViewed = true,
            timestamp = "5h ago"
        )
    ))
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // Posts Feed
    private val _posts = MutableStateFlow<List<Post>>(listOf(
        Post(
            id = "post_sponsor_1",
            author = User(
                id = "admin_love_doctor",
                name = "Love Doctor Official",
                username = "lovedoctor_hq",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                isOnline = true
            ),
            mediaUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=1000",
            caption = "🎉 Share Love & Earn Big! Refer your single friends to Love Doctor. When they complete their 7-day trial and join Gold Membership, you earn ₹500 instantly in your wallet! 💖 #LoveDoctor #ShareAndEarn #Dating",
            timestamp = "Promoted",
            likesCount = 890,
            commentsCount = 142,
            isLiked = true,
            isSaved = true,
            isSponsored = true,
            sponsoredBrand = "Official Referral Reward Program"
        ),
        Post(
            id = "post_1",
            author = _communityUsers.value[0], // Priya
            mediaUrl = "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?w=1000",
            caption = "Found the coziest candle-lit rooftop cafe in South Delhi! Who else believes a first date should always have good coffee and laughter? ☕✨",
            timestamp = "2 hours ago",
            likesCount = 342,
            commentsCount = 28,
            isLiked = false,
            isSaved = false
        ),
        Post(
            id = "post_2",
            author = _communityUsers.value[2], // Ananya
            mediaUrl = "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=1000",
            caption = "Sunset walks along Marine Drive. Reminds me that every love story begins with a simple 'Hello' and a warm smile 🌅✨",
            timestamp = "4 hours ago",
            likesCount = 612,
            commentsCount = 47,
            isLiked = true,
            isSaved = false
        ),
        Post(
            id = "post_3",
            author = _communityUsers.value[3], // Arjun
            mediaUrl = "https://images.unsplash.com/photo-1519741497674-611481863552?w=1000",
            caption = "Golden hour vibes on a weekend road trip. When you meet someone on Love Doctor who loves acoustic jam sessions as much as you do! 🎸🎵",
            timestamp = "Yesterday",
            likesCount = 480,
            commentsCount = 35,
            isLiked = false,
            isSaved = false
        )
    ))
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Comments map: PostId -> List<Comment>
    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(mapOf(
        "post_1" to listOf(
            Comment("c1", "post_1", _communityUsers.value[1], "Totally agree! Coffee dates allow real conversations without high pressure 👌", "1h ago", 12),
            Comment("c2", "post_1", currentUser.value, "That cafe looks stunning Priya! Will have to check it out ✨", "45m ago", 8),
            Comment("c3", "post_1", _communityUsers.value[2], "Such aesthetic lighting! You look wonderful 💕", "20m ago", 5)
        ),
        "post_sponsor_1" to listOf(
            Comment("cs1", "post_sponsor_1", _communityUsers.value[0], "Just referred 2 of my college friends and received ₹1000 in my UPI! Best feature ever 💸❤️", "3h ago", 45),
            Comment("cs2", "post_sponsor_1", _communityUsers.value[1], "Smooth payout directly to my Paytm wallet! Loved it.", "2h ago", 19)
        )
    ))
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    // Chats
    private val _chats = MutableStateFlow<List<Chat>>(listOf(
        Chat(
            id = "chat_priya",
            participant = _communityUsers.value[0],
            lastMessage = "I really enjoyed talking to you yesterday! When are we getting coffee? ☕",
            lastMessageTime = "10:42 AM",
            unreadCount = 2,
            isPinned = true,
            isMuted = false,
            isRequest = false,
            isOnline = true,
            isTyping = false
        ),
        Chat(
            id = "chat_ananya",
            participant = _communityUsers.value[2],
            lastMessage = "Here is that playlist of romantic acoustic tracks I promised 🎵",
            lastMessageTime = "Yesterday",
            unreadCount = 0,
            isPinned = false,
            isMuted = false,
            isRequest = false,
            isOnline = true,
            isTyping = false
        ),
        Chat(
            id = "chat_rohan",
            participant = _communityUsers.value[1],
            lastMessage = "Hey Alex, thanks for sharing your Love Doctor referral code! Just registered.",
            lastMessageTime = "Yesterday",
            unreadCount = 0,
            isPinned = false,
            isMuted = false,
            isRequest = false,
            isOnline = false,
            isTyping = false
        ),
        Chat(
            id = "chat_kavya",
            participant = _communityUsers.value[4],
            lastMessage = "Hey! I saw you love travel too! Have you ever been to Munnar?",
            lastMessageTime = "2 days ago",
            unreadCount = 1,
            isPinned = false,
            isMuted = false,
            isRequest = true, // Chat request
            isOnline = true,
            isTyping = false
        )
    ))
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    // Messages per chatId
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(mapOf(
        "chat_priya" to listOf(
            Message(
                id = "m1",
                chatId = "chat_priya",
                senderId = "user_priya",
                text = "Hey Alex! 👋 I saw on your profile that you're interested in psychology and relationships.",
                timestamp = "10:30 AM",
                isRead = true
            ),
            Message(
                id = "m2",
                chatId = "chat_priya",
                senderId = "user_me",
                text = "Hey Priya! Yes, human connection and love dynamics are so fascinating to me! What about you?",
                timestamp = "10:32 AM",
                isRead = true,
                reaction = "❤️"
            ),
            Message(
                id = "m3",
                chatId = "chat_priya",
                senderId = "user_priya",
                text = "Same here! And I loved that photo of the sunset you posted.",
                timestamp = "10:38 AM",
                isRead = true
            ),
            Message(
                id = "m4",
                chatId = "chat_priya",
                senderId = "user_me",
                text = "Thank you! Mumbai sunsets have their own romantic magic. 🌅",
                timestamp = "10:40 AM",
                isRead = true
            ),
            Message(
                id = "m5",
                chatId = "chat_priya",
                senderId = "user_priya",
                text = "I really enjoyed talking to you yesterday! When are we getting coffee? ☕",
                timestamp = "10:42 AM",
                isRead = false,
                reaction = "✨"
            )
        )
    ))
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    // Call Records History
    private val _callRecords = MutableStateFlow<List<CallRecord>>(listOf(
        CallRecord(
            id = "call_1",
            participant = _communityUsers.value[0],
            type = CallType.VIDEO,
            isIncoming = true,
            timestamp = "Yesterday, 8:45 PM",
            duration = "14:22 mins",
            status = CallStatus.COMPLETED
        ),
        CallRecord(
            id = "call_2",
            participant = _communityUsers.value[2],
            type = CallType.VOICE,
            isIncoming = false,
            timestamp = "2 days ago",
            duration = "8:10 mins",
            status = CallStatus.COMPLETED
        ),
        CallRecord(
            id = "call_3",
            participant = _communityUsers.value[3],
            type = CallType.VIDEO,
            isIncoming = true,
            timestamp = "3 days ago",
            duration = "0:00",
            status = CallStatus.MISSED
        )
    ))
    val callRecords: StateFlow<List<CallRecord>> = _callRecords.asStateFlow()

    // Active Simulated Call State
    data class ActiveCallState(
        val isCallActive: Boolean = false,
        val participant: User? = null,
        val type: CallType = CallType.VOICE,
        val isConnected: Boolean = false,
        val durationSeconds: Int = 0,
        val isMuted: Boolean = false,
        val isSpeakerOn: Boolean = false,
        val isVideoCameraOff: Boolean = false
    )
    val activeCall = MutableStateFlow(ActiveCallState())

    // Admin & Qualification Settings
    val adminSettings = MutableStateFlow(AdminSettings())

    // Referral Program State
    private val _referrals = MutableStateFlow<List<ReferralItem>>(listOf(
        ReferralItem(
            id = "ref_1",
            referredUser = User(
                id = "user_ref_siddharth",
                name = "Siddharth Rao",
                username = "sid_rao",
                avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400",
                location = "Bangalore, India",
                isOnline = true
            ),
            registeredDate = "28 Sep 2026",
            status = ReferralStatus.REWARDED,
            rewardAmount = 500.0,
            eligiblePaidAmount = 1000.0,
            trialDaysCompleted = 7,
            trialRequiredDays = 7,
            paymentVerificationStatus = "Paid ₹1000 Gold Membership (Verified)",
            isFraudulent = false
        ),
        ReferralItem(
            id = "ref_2",
            referredUser = User(
                id = "user_ref_meera",
                name = "Meera Kapoor",
                username = "meera_k",
                avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400",
                location = "Delhi, India",
                isOnline = true
            ),
            registeredDate = "29 Sep 2026",
            status = ReferralStatus.QUALIFIED,
            rewardAmount = 500.0,
            eligiblePaidAmount = 1000.0,
            trialDaysCompleted = 7,
            trialRequiredDays = 7,
            paymentVerificationStatus = "Server-verified ₹1000 Membership • Ready to Reward",
            isFraudulent = false
        ),
        ReferralItem(
            id = "ref_3",
            referredUser = User(
                id = "user_ref_tarun",
                name = "Tarun Verma",
                username = "tarun_v",
                avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=400",
                location = "Mumbai, India",
                isOnline = false
            ),
            registeredDate = "01 Oct 2026",
            status = ReferralStatus.PENDING,
            rewardAmount = 500.0,
            eligiblePaidAmount = 0.0,
            trialDaysCompleted = 3,
            trialRequiredDays = 7,
            paymentVerificationStatus = "Trial day 3/7 • Pending ₹1000 membership payment",
            isFraudulent = false
        ),
        ReferralItem(
            id = "ref_4",
            referredUser = User(
                id = "user_ref_isha",
                name = "Isha Patel",
                username = "isha_p",
                avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400",
                location = "Ahmedabad, India",
                isOnline = true
            ),
            registeredDate = "02 Oct 2026",
            status = ReferralStatus.PENDING,
            rewardAmount = 500.0,
            eligiblePaidAmount = 0.0,
            trialDaysCompleted = 1,
            trialRequiredDays = 7,
            paymentVerificationStatus = "Trial day 1/7 • Pending qualification",
            isFraudulent = false
        ),
        ReferralItem(
            id = "ref_5",
            referredUser = User(
                id = "user_ref_fake",
                name = "Test Bot 99",
                username = "bot_99",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
                location = "VPN Node",
                isOnline = false
            ),
            registeredDate = "25 Sep 2026",
            status = ReferralStatus.CANCELLED,
            rewardAmount = 0.0,
            eligiblePaidAmount = 0.0,
            trialDaysCompleted = 0,
            trialRequiredDays = 7,
            paymentVerificationStatus = "Flagged: Same IP & Device ID (Self-referral rejected)",
            isFraudulent = true
        )
    ))
    val referrals: StateFlow<List<ReferralItem>> = _referrals.asStateFlow()

    // Withdrawal Requests
    private val _withdrawals = MutableStateFlow<List<WithdrawalRequest>>(listOf(
        WithdrawalRequest(
            id = "wdr_101",
            amount = 2000.0,
            method = WithdrawalMethod.UPI,
            destinationDetails = "alex.lovelover@okhdfcbank",
            date = "25 Sep 2026, 04:30 PM",
            status = WithdrawalStatus.COMPLETED,
            adminNote = "Processed via UPI Instant Settlement UTR#994821039"
        ),
        WithdrawalRequest(
            id = "wdr_102",
            amount = 1500.0,
            method = WithdrawalMethod.PAYTM,
            destinationDetails = "+91 9876543210",
            date = "30 Sep 2026, 11:15 AM",
            status = WithdrawalStatus.PROCESSING,
            adminNote = "Verification in queue with Paytm Gateway"
        )
    ))
    val withdrawals: StateFlow<List<WithdrawalRequest>> = _withdrawals.asStateFlow()

    // Audit Logs for Admin
    private val _auditLogs = MutableStateFlow<List<AuditLog>>(listOf(
        AuditLog(
            id = "log_1",
            timestamp = "Today, 09:12 AM",
            action = "REFERRAL_QUALIFIED",
            details = "User Meera Kapoor (user_ref_meera) completed ₹1000 payment and 7-day trial. Status -> QUALIFIED.",
            severity = "SUCCESS"
        ),
        AuditLog(
            id = "log_2",
            timestamp = "Yesterday, 06:40 PM",
            action = "FRAUD_DETECTION",
            details = "Prevented duplicate referral claim for IP 192.168.1.1 (bot_99). Account marked CANCELLED.",
            severity = "ALERT"
        ),
        AuditLog(
            id = "log_3",
            timestamp = "30 Sep 2026, 11:15 AM",
            action = "WITHDRAWAL_REQUESTED",
            details = "Withdrawal request of ₹1,500 via Paytm (+91 9876543210) queued for admin approval.",
            severity = "INFO"
        ),
        AuditLog(
            id = "log_4",
            timestamp = "25 Sep 2026, 04:35 PM",
            action = "WITHDRAWAL_COMPLETED",
            details = "Admin approved UPI payout of ₹2,000 to alex.lovelover@okhdfcbank.",
            severity = "SUCCESS"
        )
    ))
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(listOf(
        NotificationItem(
            id = "n_1",
            type = NotificationType.REWARD_CREDITED,
            title = "₹500 Reward Credited! 🎉",
            message = "Congratulations! Your referral Siddharth Rao completed his Gold membership. ₹500 added to your wallet!",
            timestamp = "1h ago",
            isRead = false
        ),
        NotificationItem(
            id = "n_2",
            type = NotificationType.REFERRAL_QUALIFIED,
            title = "Referral Qualified ✨",
            message = "Meera Kapoor has completed 7-day trial and verified payment. Ready for payout.",
            timestamp = "4h ago",
            isRead = false
        ),
        NotificationItem(
            id = "n_3",
            type = NotificationType.CHAT,
            title = "Priya Sharma sent a message",
            message = "When are we getting coffee? ☕",
            timestamp = "10:42 AM",
            isRead = false
        ),
        NotificationItem(
            id = "n_4",
            type = NotificationType.LIKE,
            title = "New Like on your post",
            message = "Ananya Roy liked your romantic sunset photo.",
            timestamp = "Yesterday",
            isRead = true
        ),
        NotificationItem(
            id = "n_5",
            type = NotificationType.WITHDRAWAL_APPROVED,
            title = "Withdrawal of ₹2,000 Processed",
            message = "Your UPI withdrawal has been successfully credited to your bank account.",
            timestamp = "25 Sep 2026",
            isRead = true
        )
    ))
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // User Balance and Earnings summary computed state
    val totalEarnings: StateFlow<Double> = MutableStateFlow(4500.0)
    val availableBalance = MutableStateFlow(2500.0)
    val pendingRewards = MutableStateFlow(1000.0)
    val completedRewards = MutableStateFlow(3500.0)

    // ACTIONS: Social Feed
    fun toggleLikePost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newLikesCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                post.copy(isLiked = newLiked, likesCount = newLikesCount)
            } else post
        }
    }

    fun toggleSavePost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        val currentList = _comments.value[postId] ?: emptyList()
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            postId = postId,
            author = currentUser.value,
            text = text.trim(),
            timestamp = "Just now",
            likesCount = 0
        )
        _comments.value = _comments.value + (postId to (listOf(newComment) + currentList))
        _posts.value = _posts.value.map {
            if (it.id == postId) it.copy(commentsCount = it.commentsCount + 1) else it
        }
    }

    fun createPost(caption: String, mediaUrl: String) {
        val newPost = Post(
            id = "post_${UUID.randomUUID()}",
            author = currentUser.value,
            mediaType = MediaType.IMAGE,
            mediaUrl = mediaUrl.ifBlank { "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=1000" },
            caption = caption,
            timestamp = "Just now",
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
            isSaved = false
        )
        _posts.value = listOf(newPost) + _posts.value
        currentUser.value = currentUser.value.copy(followersCount = currentUser.value.followersCount + 1)
    }

    fun toggleFollowUser(userId: String) {
        _communityUsers.value = _communityUsers.value.map { u ->
            if (u.id == userId) {
                val newFollow = !u.isFollowing
                val newFollowers = if (newFollow) u.followersCount + 1 else u.followersCount - 1
                u.copy(isFollowing = newFollow, followersCount = newFollowers)
            } else u
        }
    }

    // ACTIONS: Chat & Messaging
    fun sendMessage(
        chatId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        voiceDurationSec: Int? = null,
        replyTo: String? = null
    ) {
        if (text.isBlank() && mediaUrl == null && type == MessageType.TEXT) return
        val msgList = _messages.value[chatId] ?: emptyList()
        val timeNow = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val newMsg = Message(
            id = "msg_${UUID.randomUUID()}",
            chatId = chatId,
            senderId = currentUser.value.id,
            text = text,
            type = type,
            timestamp = timeNow,
            isRead = true,
            replyToText = replyTo,
            mediaUrl = mediaUrl,
            voiceDurationSec = voiceDurationSec
        )
        _messages.value = _messages.value + (chatId to (msgList + newMsg))

        // Update chat list
        _chats.value = _chats.value.map {
            if (it.id == chatId) {
                it.copy(
                    lastMessage = if (type == MessageType.VOICE) "🎤 Voice message (${voiceDurationSec ?: 5}s)" else if (type == MessageType.IMAGE) "📷 Photo" else text,
                    lastMessageTime = timeNow
                )
            } else it
        }
    }

    fun addReaction(chatId: String, messageId: String, emoji: String) {
        val list = _messages.value[chatId] ?: return
        _messages.value = _messages.value + (chatId to list.map {
            if (it.id == messageId) it.copy(reaction = if (it.reaction == emoji) null else emoji) else it
        })
    }

    fun deleteMessageForMe(chatId: String, messageId: String) {
        val list = _messages.value[chatId] ?: return
        _messages.value = _messages.value + (chatId to list.map {
            if (it.id == messageId) it.copy(isDeletedForMe = true) else it
        })
    }

    fun deleteMessageForEveryone(chatId: String, messageId: String) {
        val list = _messages.value[chatId] ?: return
        _messages.value = _messages.value + (chatId to list.map {
            if (it.id == messageId) it.copy(
                isDeletedForEveryone = true,
                text = "🚫 This message was deleted"
            ) else it
        })
    }

    fun toggleMuteChat(chatId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(isMuted = !it.isMuted) else it
        }
    }

    fun acceptChatRequest(chatId: String) {
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(isRequest = false) else it
        }
    }

    fun rejectChatRequest(chatId: String) {
        _chats.value = _chats.value.filter { it.id != chatId }
    }

    fun blockUser(userId: String) {
        _communityUsers.value = _communityUsers.value.filter { it.id != userId }
        _chats.value = _chats.value.filter { it.participant.id != userId }
    }

    // ACTIONS: Call System
    fun startCall(participant: User, type: CallType) {
        activeCall.value = ActiveCallState(
            isCallActive = true,
            participant = participant,
            type = type,
            isConnected = false,
            durationSeconds = 0
        )
    }

    fun connectCall() {
        activeCall.value = activeCall.value.copy(isConnected = true)
    }

    fun toggleCallMute() {
        activeCall.value = activeCall.value.copy(isMuted = !activeCall.value.isMuted)
    }

    fun toggleCallSpeaker() {
        activeCall.value = activeCall.value.copy(isSpeakerOn = !activeCall.value.isSpeakerOn)
    }

    fun toggleVideoCamera() {
        activeCall.value = activeCall.value.copy(isVideoCameraOff = !activeCall.value.isVideoCameraOff)
    }

    fun endCall() {
        val call = activeCall.value
        if (call.participant != null) {
            val record = CallRecord(
                id = "call_${System.currentTimeMillis()}",
                participant = call.participant,
                type = call.type,
                isIncoming = false,
                timestamp = "Just now",
                duration = if (call.isConnected) "${call.durationSeconds / 60}:${String.format("%02d", call.durationSeconds % 60)} mins" else "Missed",
                status = if (call.isConnected) CallStatus.COMPLETED else CallStatus.MISSED
            )
            _callRecords.value = listOf(record) + _callRecords.value
        }
        activeCall.value = ActiveCallState(isCallActive = false)
    }

    // ACTIONS: Referral & Rewards System
    fun simulateNewFriendRegistration(friendName: String, friendUsername: String) {
        val newFriend = User(
            id = "user_ref_${UUID.randomUUID().toString().take(6)}",
            name = friendName,
            username = friendUsername,
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            location = "Delhi, India"
        )
        val newItem = ReferralItem(
            id = "ref_${System.currentTimeMillis()}",
            referredUser = newFriend,
            registeredDate = "Today",
            status = ReferralStatus.PENDING,
            rewardAmount = adminSettings.value.rewardAmount,
            eligiblePaidAmount = 0.0,
            trialDaysCompleted = 1,
            trialRequiredDays = adminSettings.value.trialDaysRequirement,
            paymentVerificationStatus = "Trial day 1/${adminSettings.value.trialDaysRequirement} • Pending ₹${adminSettings.value.minQualifyingPayment.toInt()} payment"
        )
        _referrals.value = listOf(newItem) + _referrals.value
        pendingRewards.value += adminSettings.value.rewardAmount

        // Add audit log
        addAuditLog(
            action = "REFERRAL_REGISTERED",
            details = "Friend ${friendName} (@${friendUsername}) registered via referral code ${currentUser.value.referralCode}. Marked PENDING.",
            severity = "INFO"
        )

        // Add notification
        addNotification(
            type = NotificationType.REFERRAL_REGISTERED,
            title = "New Referral Registered! 🌟",
            message = "${friendName} just signed up using your link! Reward will unlock after trial & qualification."
        )
    }

    fun qualifyReferral(referralId: String) {
        _referrals.value = _referrals.value.map { item ->
            if (item.id == referralId && item.status == ReferralStatus.PENDING) {
                item.copy(
                    status = ReferralStatus.QUALIFIED,
                    trialDaysCompleted = item.trialRequiredDays,
                    eligiblePaidAmount = adminSettings.value.minQualifyingPayment,
                    paymentVerificationStatus = "Server-verified ₹${adminSettings.value.minQualifyingPayment.toInt()} Payment • Qualification Met"
                )
            } else item
        }

        addAuditLog(
            action = "REFERRAL_QUALIFIED",
            details = "Referral ID $referralId passed 7-day trial and verified ₹${adminSettings.value.minQualifyingPayment.toInt()} booking/membership.",
            severity = "SUCCESS"
        )

        addNotification(
            type = NotificationType.REFERRAL_QUALIFIED,
            title = "Referral Conditions Met! 🎉",
            message = "Your friend completed the 7-day trial & payment. Reward of ₹${adminSettings.value.rewardAmount.toInt()} is ready to be credited!"
        )
    }

    fun releaseReward(referralId: String) {
        val target = _referrals.value.find { it.id == referralId } ?: return
        if (target.status == ReferralStatus.REWARDED) return

        _referrals.value = _referrals.value.map {
            if (it.id == referralId) it.copy(status = ReferralStatus.REWARDED) else it
        }

        val reward = target.rewardAmount
        availableBalance.value += reward
        completedRewards.value += reward
        if (pendingRewards.value >= reward) {
            pendingRewards.value -= reward
        }

        addAuditLog(
            action = "REWARD_RELEASED",
            details = "Credited ₹${reward.toInt()} to user balance for qualified referral ${target.referredUser.name}.",
            severity = "SUCCESS"
        )

        addNotification(
            type = NotificationType.REWARD_CREDITED,
            title = "₹${reward.toInt()} Credited to Wallet! 💰",
            message = "Reward for ${target.referredUser.name} has been credited. You can withdraw anytime!"
        )
    }

    fun reverseReward(referralId: String, reason: String) {
        val target = _referrals.value.find { it.id == referralId } ?: return
        _referrals.value = _referrals.value.map {
            if (it.id == referralId) it.copy(
                status = ReferralStatus.CANCELLED,
                isFraudulent = true,
                paymentVerificationStatus = "Cancelled by Admin: $reason"
            ) else it
        }

        if (target.status == ReferralStatus.REWARDED) {
            availableBalance.value = (availableBalance.value - target.rewardAmount).coerceAtLeast(0.0)
            completedRewards.value = (completedRewards.value - target.rewardAmount).coerceAtLeast(0.0)
        } else if (target.status == ReferralStatus.PENDING || target.status == ReferralStatus.QUALIFIED) {
            pendingRewards.value = (pendingRewards.value - target.rewardAmount).coerceAtLeast(0.0)
        }

        addAuditLog(
            action = "REWARD_REVERSED",
            details = "Admin reversed reward for ${target.referredUser.name}. Reason: $reason",
            severity = "ALERT"
        )
    }

    // ACTIONS: Withdrawal
    fun requestWithdrawal(amount: Double, method: WithdrawalMethod, details: String): Boolean {
        if (amount <= 0 || amount > availableBalance.value) return false
        availableBalance.value -= amount
        val timeNow = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val req = WithdrawalRequest(
            id = "wdr_${System.currentTimeMillis()}",
            amount = amount,
            method = method,
            destinationDetails = details,
            date = timeNow,
            status = WithdrawalStatus.PENDING,
            adminNote = "Submitted by user. Awaiting administrator approval."
        )
        _withdrawals.value = listOf(req) + _withdrawals.value

        addAuditLog(
            action = "WITHDRAWAL_REQUEST",
            details = "User requested withdrawal of ₹${amount.toInt()} via ${method.displayName} ($details).",
            severity = "INFO"
        )

        addNotification(
            type = NotificationType.ANNOUNCEMENT,
            title = "Withdrawal Submitted ⏳",
            message = "Your withdrawal request for ₹${amount.toInt()} via ${method.displayName} is being verified."
        )
        return true
    }

    fun approveWithdrawal(requestId: String) {
        _withdrawals.value = _withdrawals.value.map {
            if (it.id == requestId) {
                it.copy(
                    status = WithdrawalStatus.COMPLETED,
                    adminNote = "Approved & processed via banking payout partner."
                )
            } else it
        }

        val item = _withdrawals.value.find { it.id == requestId }
        addAuditLog(
            action = "WITHDRAWAL_APPROVED",
            details = "Admin approved ₹${item?.amount?.toInt() ?: 0} withdrawal (${item?.method?.displayName}).",
            severity = "SUCCESS"
        )

        addNotification(
            type = NotificationType.WITHDRAWAL_APPROVED,
            title = "Withdrawal Successful! 💸",
            message = "₹${item?.amount?.toInt() ?: 0} has been sent to your ${item?.method?.displayName}."
        )
    }

    fun rejectWithdrawal(requestId: String, reason: String) {
        val item = _withdrawals.value.find { it.id == requestId }
        if (item != null && item.status == WithdrawalStatus.PENDING) {
            availableBalance.value += item.amount
        }
        _withdrawals.value = _withdrawals.value.map {
            if (it.id == requestId) {
                it.copy(
                    status = WithdrawalStatus.REJECTED,
                    adminNote = "Rejected: $reason. Funds refunded to wallet."
                )
            } else it
        }

        addAuditLog(
            action = "WITHDRAWAL_REJECTED",
            details = "Admin rejected withdrawal $requestId. Reason: $reason. Refunded balance.",
            severity = "ALERT"
        )

        addNotification(
            type = NotificationType.WITHDRAWAL_REJECTED,
            title = "Withdrawal Rejected",
            message = "Your request was declined ($reason). The amount has been refunded to your wallet."
        )
    }

    // ACTIONS: Admin Settings
    fun updateAdminSettings(newSettings: AdminSettings) {
        adminSettings.value = newSettings
        addAuditLog(
            action = "SETTINGS_UPDATED",
            details = "Admin updated reward to ₹${newSettings.rewardAmount.toInt()}, min pay ₹${newSettings.minQualifyingPayment.toInt()}, trial ${newSettings.trialDaysRequirement} days.",
            severity = "INFO"
        )
    }

    fun toggleReferralProgramActive() {
        val cur = adminSettings.value
        adminSettings.value = cur.copy(isReferralProgramActive = !cur.isReferralProgramActive)
        addAuditLog(
            action = "PROGRAM_TOGGLED",
            details = "Referral program active status changed to ${adminSettings.value.isReferralProgramActive}",
            severity = "WARNING"
        )
    }

    // VIP Membership Simulation
    fun purchaseVipMembership(): Boolean {
        currentUser.value = currentUser.value.copy(isVip = true)
        addAuditLog(
            action = "VIP_MEMBERSHIP_PAID",
            details = "User completed eligible ₹1,000 Gold Membership payment via UPI Gateway. Triggered backend webhook.",
            severity = "SUCCESS"
        )
        addNotification(
            type = NotificationType.ANNOUNCEMENT,
            title = "Welcome to Love Doctor Gold! 👑",
            message = "Unlimited chat, voice/video calls, and verified match badge are now unlocked!"
        )
        return true
    }

    fun markNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    private fun addAuditLog(action: String, details: String, severity: String) {
        val timeNow = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
        val log = AuditLog(
            id = "log_${System.currentTimeMillis()}",
            timestamp = timeNow,
            action = action,
            details = details,
            severity = severity
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }

    private fun addNotification(type: NotificationType, title: String, message: String) {
        val n = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            type = type,
            title = title,
            message = message,
            timestamp = "Just now",
            isRead = false
        )
        _notifications.value = listOf(n) + _notifications.value
    }
}

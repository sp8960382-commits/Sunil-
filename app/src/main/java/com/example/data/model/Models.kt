package com.example.data.model

data class User(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val coverUrl: String = "",
    val bio: String = "",
    val location: String = "Mumbai, India",
    val age: Int = 24,
    val interests: List<String> = listOf("Romance", "Coffee Dates", "Music", "Travel"),
    val followersCount: Int = 1240,
    val followingCount: Int = 380,
    val isFollowing: Boolean = false,
    val isOnline: Boolean = true,
    val referralCode: String = "LDK12345",
    val isVip: Boolean = false
)

enum class MediaType {
    IMAGE, VIDEO
}

data class Post(
    val id: String,
    val author: User,
    val mediaType: MediaType = MediaType.IMAGE,
    val mediaUrl: String,
    val caption: String,
    val timestamp: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isSponsored: Boolean = false,
    val sponsoredBrand: String = "Love Doctor VIP"
)

data class Story(
    val id: String,
    val user: User,
    val mediaUrl: String,
    val isViewed: Boolean = false,
    val timestamp: String = "2h ago"
)

data class Comment(
    val id: String,
    val postId: String,
    val author: User,
    val text: String,
    val timestamp: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

enum class MessageType {
    TEXT, IMAGE, VOICE, VIDEO, FILE
}

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val timestamp: String,
    val isRead: Boolean = true,
    val reaction: String? = null,
    val replyToText: String? = null,
    val mediaUrl: String? = null,
    val voiceDurationSec: Int? = null,
    val isDeletedForMe: Boolean = false,
    val isDeletedForEveryone: Boolean = false
)

data class Chat(
    val id: String,
    val participant: User,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isRequest: Boolean = false,
    val isOnline: Boolean = true,
    val isTyping: Boolean = false
)

enum class CallType {
    VOICE, VIDEO
}

enum class CallStatus {
    MISSED, COMPLETED, REJECTED
}

data class CallRecord(
    val id: String,
    val participant: User,
    val type: CallType,
    val isIncoming: Boolean,
    val timestamp: String,
    val duration: String,
    val status: CallStatus
)

enum class ReferralStatus {
    ALL, PENDING, QUALIFIED, REWARDED, CANCELLED
}

data class ReferralItem(
    val id: String,
    val referredUser: User,
    val registeredDate: String,
    val status: ReferralStatus,
    val rewardAmount: Double = 500.0,
    val eligiblePaidAmount: Double = 1000.0,
    val trialDaysCompleted: Int = 7,
    val trialRequiredDays: Int = 7,
    val paymentVerificationStatus: String = "Verified by Backend Webhook",
    val isFraudulent: Boolean = false
)

enum class WithdrawalMethod(val displayName: String) {
    UPI("UPI"),
    BANK_TRANSFER("Bank Transfer"),
    PAYTM("Paytm"),
    ESEWA("eSewa")
}

enum class WithdrawalStatus {
    PENDING, PROCESSING, COMPLETED, REJECTED, CANCELLED
}

data class WithdrawalRequest(
    val id: String,
    val amount: Double,
    val method: WithdrawalMethod,
    val destinationDetails: String,
    val date: String,
    val status: WithdrawalStatus = WithdrawalStatus.PENDING,
    val adminNote: String? = null
)

data class AuditLog(
    val id: String,
    val timestamp: String,
    val action: String,
    val details: String,
    val severity: String = "INFO" // INFO, WARNING, SUCCESS, ALERT
)

data class AdminSettings(
    val rewardAmount: Double = 500.0,
    val minQualifyingPayment: Double = 1000.0,
    val trialDaysRequirement: Int = 7,
    val isReferralProgramActive: Boolean = true,
    val autoApproveVerified: Boolean = false
)

enum class NotificationType {
    CHAT, MESSAGE, FOLLOW, LIKE, COMMENT, REFERRAL_REGISTERED,
    REFERRAL_QUALIFIED, REWARD_CREDITED, WITHDRAWAL_APPROVED,
    WITHDRAWAL_REJECTED, ANNOUNCEMENT
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false
)

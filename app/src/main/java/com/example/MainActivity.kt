package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LoveDoctorViewModel
import com.example.ui.MainTab
import com.example.ui.ScreenDestination
import com.example.ui.components.LoveDoctorBottomNav
import com.example.ui.components.LoveDoctorTopBar
import com.example.ui.screens.*
import com.example.ui.theme.LoveDoctorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoveDoctorTheme {
                LoveDoctorApp()
            }
        }
    }
}

@Composable
fun LoveDoctorApp(viewModel: LoveDoctorViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val chats by viewModel.repository.chats.collectAsStateWithLifecycle()
    val notifications by viewModel.repository.notifications.collectAsStateWithLifecycle()

    val unreadChatCount = chats.sumOf { it.unreadCount }
    val unreadNotifCount = notifications.count { !it.isRead }

    BackHandler(enabled = currentScreen !is ScreenDestination.Main) {
        viewModel.goBack()
    }

    // Full screen call view overrides scaffold
    if (currentScreen is ScreenDestination.CallView) {
        CallScreen(viewModel = viewModel)
        return
    }

    val isMainScreen = currentScreen is ScreenDestination.Main

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isMainScreen) {
                LoveDoctorTopBar(
                    title = "Love Doctor",
                    tagline = "Connect • Share • Earn",
                    showBack = false,
                    unreadNotifCount = unreadNotifCount,
                    onNotifClick = { viewModel.navigateTo(ScreenDestination.NotificationsView) },
                    onReferralClick = { viewModel.navigateToTab(MainTab.REWARDS) },
                    onAdminClick = { viewModel.navigateTo(ScreenDestination.AdminDashboardView) }
                )
            }
        },
        bottomBar = {
            if (isMainScreen) {
                LoveDoctorBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { tab -> viewModel.navigateToTab(tab) },
                    unreadChatCount = unreadChatCount
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val dest = currentScreen) {
                is ScreenDestination.Main -> {
                    when (currentTab) {
                        MainTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToReferral = { viewModel.navigateToTab(MainTab.REWARDS) }
                        )
                        MainTab.CHAT -> ChatListScreen(
                            viewModel = viewModel,
                            onOpenChat = { chatId -> viewModel.navigateTo(ScreenDestination.ChatDetail(chatId)) }
                        )
                        MainTab.EXPLORE -> ExploreScreen(
                            viewModel = viewModel,
                            onOpenProfile = { userId -> viewModel.navigateTo(ScreenDestination.UserProfileView(userId)) },
                            onStartChat = { chatId -> viewModel.navigateTo(ScreenDestination.ChatDetail(chatId)) }
                        )
                        MainTab.REWARDS -> RewardsScreen(
                            viewModel = viewModel
                        )
                        MainTab.PROFILE -> ProfileScreen(
                            userId = null,
                            viewModel = viewModel
                        )
                    }
                }
                is ScreenDestination.ChatDetail -> {
                    ChatDetailScreen(
                        chatId = dest.chatId,
                        viewModel = viewModel,
                        onBack = { viewModel.goBack() }
                    )
                }
                is ScreenDestination.ShareReferralView -> {
                    ShareReferralScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.goBack() }
                    )
                }
                is ScreenDestination.ReferralHistoryView -> {
                    ReferralHistoryScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.goBack() }
                    )
                }
                is ScreenDestination.TermsAndConditionsView -> {
                    TermsAndConditionsScreen(
                        onBack = { viewModel.goBack() }
                    )
                }
                is ScreenDestination.AdminDashboardView -> {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.goBack() }
                    )
                }
                is ScreenDestination.NotificationsView -> {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.goBack() }
                    )
                }
                is ScreenDestination.UserProfileView -> {
                    ProfileScreen(
                        userId = dest.userId,
                        viewModel = viewModel,
                        onBack = { viewModel.goBack() }
                    )
                }
                else -> {}
            }
        }
    }
}

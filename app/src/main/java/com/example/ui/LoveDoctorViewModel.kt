package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.LoveDoctorRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, CHAT, EXPLORE, REWARDS, PROFILE
}

sealed class ScreenDestination {
    data object Main : ScreenDestination()
    data class ChatDetail(val chatId: String) : ScreenDestination()
    data object CallView : ScreenDestination()
    data object ShareReferralView : ScreenDestination()
    data object ReferralStatusView : ScreenDestination()
    data object ReferralHistoryView : ScreenDestination()
    data object TermsAndConditionsView : ScreenDestination()
    data object AdminDashboardView : ScreenDestination()
    data object NotificationsView : ScreenDestination()
    data class UserProfileView(val userId: String) : ScreenDestination()
}

class LoveDoctorViewModel : ViewModel() {
    val repository = LoveDoctorRepository()

    // Navigation Backstack
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Main)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // UI Dialog & Sheet states
    val isCreatePostOpen = MutableStateFlow(false)
    val activeCommentsPostId = MutableStateFlow<String?>(null)
    val isWithdrawDialogOpen = MutableStateFlow(false)
    val isVipPaymentModalOpen = MutableStateFlow(false)
    val isSimulateReferralOpen = MutableStateFlow(false)

    // Explore Filters
    val exploreSearchQuery = MutableStateFlow("")
    val exploreLocationFilter = MutableStateFlow("All")
    val exploreOnlineOnly = MutableStateFlow(false)
    val exploreSelectedInterest = MutableStateFlow("All")

    // Referral Status Filter
    val referralFilter = MutableStateFlow(ReferralStatus.ALL)

    // Call timer ticker
    init {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val call = repository.activeCall.value
                if (call.isCallActive && call.isConnected) {
                    repository.activeCall.value = call.copy(durationSeconds = call.durationSeconds + 1)
                }
            }
        }
    }

    // Navigation methods
    fun navigateToTab(tab: MainTab) {
        _currentTab.value = tab
        _currentScreen.value = ScreenDestination.Main
    }

    fun navigateTo(dest: ScreenDestination) {
        _currentScreen.value = dest
    }

    fun goBack(): Boolean {
        return if (_currentScreen.value !is ScreenDestination.Main) {
            _currentScreen.value = ScreenDestination.Main
            true
        } else {
            false
        }
    }

    // Voice & Video Call triggers
    fun initiateCall(user: User, type: CallType) {
        repository.startCall(user, type)
        _currentScreen.value = ScreenDestination.CallView

        // Auto connect after 2.5 seconds to simulate answer
        viewModelScope.launch {
            delay(2500)
            if (repository.activeCall.value.isCallActive) {
                repository.connectCall()
            }
        }
    }

    fun endCurrentCall() {
        repository.endCall()
        _currentScreen.value = ScreenDestination.Main
    }
}

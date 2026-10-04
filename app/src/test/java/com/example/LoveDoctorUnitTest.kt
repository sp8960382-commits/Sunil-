package com.example

import com.example.data.model.ReferralStatus
import com.example.data.model.WithdrawalMethod
import com.example.data.model.WithdrawalStatus
import com.example.data.repository.LoveDoctorRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoveDoctorUnitTest {

    private lateinit var repository: LoveDoctorRepository

    @Before
    fun setUp() {
        repository = LoveDoctorRepository()
    }

    @Test
    fun testReferralCodeAndLinkFormat() {
        val user = repository.currentUser.value
        assertEquals("LDK12345", user.referralCode)
        val link = "https://lovedoctor.app/ref/${user.referralCode}"
        assertEquals("https://lovedoctor.app/ref/LDK12345", link)
    }

    @Test
    fun testSimulateFriendRegistrationAndPendingStatus() {
        val initialPending = repository.pendingRewards.value
        val initialCount = repository.referrals.value.size

        repository.simulateNewFriendRegistration("Amit Verma", "amit_v")

        assertEquals(initialCount + 1, repository.referrals.value.size)
        val newReferral = repository.referrals.value.first()
        assertEquals("Amit Verma", newReferral.referredUser.name)
        assertEquals(ReferralStatus.PENDING, newReferral.status)
        assertEquals(initialPending + 500.0, repository.pendingRewards.value, 0.01)
    }

    @Test
    fun testReferralQualificationAndRewardRelease() {
        val initialBalance = repository.availableBalance.value
        val pendingItem = repository.referrals.value.first { it.status == ReferralStatus.PENDING }

        // Step 1: Server verifies qualification
        repository.qualifyReferral(pendingItem.id)
        val qualifiedItem = repository.referrals.value.first { it.id == pendingItem.id }
        assertEquals(ReferralStatus.QUALIFIED, qualifiedItem.status)

        // Step 2: Release reward to wallet
        repository.releaseReward(pendingItem.id)
        val rewardedItem = repository.referrals.value.first { it.id == pendingItem.id }
        assertEquals(ReferralStatus.REWARDED, rewardedItem.status)
        assertEquals(initialBalance + pendingItem.rewardAmount, repository.availableBalance.value, 0.01)
    }

    @Test
    fun testWithdrawalRequestAndApproval() {
        val startBalance = repository.availableBalance.value
        val withdrawAmount = 500.0

        val requested = repository.requestWithdrawal(
            amount = withdrawAmount,
            method = WithdrawalMethod.UPI,
            details = "user@upi"
        )
        assertTrue(requested)
        assertEquals(startBalance - withdrawAmount, repository.availableBalance.value, 0.01)

        val newWdr = repository.withdrawals.value.first()
        assertEquals(WithdrawalStatus.PENDING, newWdr.status)

        repository.approveWithdrawal(newWdr.id)
        val approvedWdr = repository.withdrawals.value.first { it.id == newWdr.id }
        assertEquals(WithdrawalStatus.COMPLETED, approvedWdr.status)
    }

    @Test
    fun testFraudReversalRefundsOrCancels() {
        val target = repository.referrals.value.first { it.status == ReferralStatus.QUALIFIED }
        repository.reverseReward(target.id, "Duplicate device detection")

        val reversedItem = repository.referrals.value.first { it.id == target.id }
        assertEquals(ReferralStatus.CANCELLED, reversedItem.status)
        assertTrue(reversedItem.isFraudulent)
    }
}

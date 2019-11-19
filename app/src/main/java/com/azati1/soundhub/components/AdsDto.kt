package com.azati1.soundhub.components

import com.google.gson.annotations.SerializedName

data class AdsDto(
    @SerializedName("rate_dialog_frequency")
    val rateDialogFrequency: Int,
    @SerializedName("admob_app_id")
    val admobAppId: String,
    @SerializedName("admob_baner_id")
    val admobBannerId: String,
    @SerializedName("admob_interstitial_id")
    val admobInterstitialId: String,
    @SerializedName("admob_reward_id")
    val admobRewardId: String,
    @SerializedName("return_ads_delay")
    val returnAdsDelay: Int,
    @SerializedName("enable_return_ads")
    val enableReturnAds: Boolean,
    @SerializedName("interstitial_frequency")
    val interstitialFrequency: Int,
    @SerializedName("enable_exit_ads")
    val enableExitAds: Boolean,
    @SerializedName("enable_splash_ads")
    val enableSplashAds: Boolean,
    @SerializedName("publisher_id")
    val publisherId: String,
    @SerializedName("feed_back_email")
    val feedbackEmail: String,
    @SerializedName("privacy_policy_url")
    val privacyPolicyUrl: String
)
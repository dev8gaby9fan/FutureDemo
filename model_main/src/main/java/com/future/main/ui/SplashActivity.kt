package com.future.main.ui

import android.os.Bundle
import android.util.Log
import com.fsh.common.base.BaseActivity
import com.future.main.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.InterstitialAd



class SplashActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.activity_splash

    override fun getStatusBarColorRes(): Int = android.R.color.transparent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.decorView.postDelayed({
//            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
//            finish()
//        },3000)
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713")/*{
            Log.d("SplashActivity","MobileAds initialize status $it ${it.adapterStatusMap}")
        }*/
        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClicked() {
                Log.d("SplashActivity","AD Clicked")
            }

            override fun onAdClosed() {
                Log.d("SplashActivity","AD Closed")
            }

            override fun onAdFailedToLoad(p0: Int) {
                Log.d("SplashActivity","AD FailedToLoad $p0")
            }

            override fun onAdImpression() {
                Log.d("SplashActivity","AD Impression")
            }

            override fun onAdLeftApplication() {
                Log.d("SplashActivity","AD LeftApplication")
            }

            override fun onAdLoaded() {
                Log.d("SplashActivity","AD Loaded")
                mInterstitialAd.show()
            }

            override fun onAdOpened() {
                Log.d("SplashActivity","AD Opened")
            }
        }
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }
}

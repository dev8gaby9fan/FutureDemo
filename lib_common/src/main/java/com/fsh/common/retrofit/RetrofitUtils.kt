package com.fsh.common.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by devFan
 *
 * author: devFan
 * email:  devfanshan@gmail.com
 * date: 2020/3/10
 * description: Retrofit封装
 *
 */

object RetrofitUtils {
    private val _retrofit: Retrofit by lazy {
        initRetrofit()
    }
    private val _okHttpClient: OkHttpClient by lazy {
        initOkHttpClient()
    }

    val okHttpClient = _okHttpClient

    private fun initOkHttpClient(): OkHttpClient {
        val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        var trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })
        val trustManager: X509TrustManager = trustAllCerts[0] as X509TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val factory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(60 * 1000L, TimeUnit.MILLISECONDS)
            .readTimeout(60 * 1000L, TimeUnit.MILLISECONDS)
            .pingInterval(3, TimeUnit.SECONDS)
            .sslSocketFactory(factory, trustManager)
            .build()
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(_okHttpClient)
            .baseUrl("")
            .build()
    }

    fun <T:BaseRetrofitApi> createApi(clazz: Class<T>): T =
        _retrofit.create(clazz)
}

interface BaseRetrofitApi
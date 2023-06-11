package com.fsh.common.retrofit

import com.fsh.common.R
import com.fsh.common.util.CommonUtil
import com.fsh.common.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.RuntimeException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
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
        val builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .pingInterval(3, TimeUnit.SECONDS)

//            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return getSSLOkHttpClient(builder)
//        return builder.build()
    }

    private fun getSSLOkHttpClient(builder: OkHttpClient.Builder): OkHttpClient {
        //证书类型 //KeyStore 是一个存储了证书的文件。文件包含证书的私钥，公钥和对应的数字证书的信息。
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null);

        val certificate = CommonUtil.openRawResource(R.raw.shinnytech).use {
            //设置证书类型，X.509是一种格式标准
            val certificateFactory = CertificateFactory.getInstance("X.509")
            certificateFactory.generateCertificate(it)
        }
        keyStore.setCertificateEntry("shinnytech", certificate)
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);//通过keyStore得到信任管理器

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, null);//通过keyStore得到密匙管理器

        val sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), SecureRandom())
        val sslSocketFactory = sslContext.getSocketFactory();//拿到SSLSocketFactory

        val trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw RuntimeException("trustManager error");
        }
        val trustManager = trustManagers[0] as X509TrustManager;
        builder.sslSocketFactory(sslSocketFactory, trustManager)
        builder.hostnameVerifier { _, _ -> true }
        return builder.build()
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
//            .addConverterFactory(JsonConvertFactor())
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(Constants.BASE_URL_SHINNYTECH)
            .client(_okHttpClient)
            .build()
    }

    fun <T : BaseRetrofitApi> createApi(clazz: Class<T>): T =
        _retrofit.create(clazz)
}

interface BaseRetrofitApi
package com.banyumas.wisata.di

import com.banyumas.wisata.BuildConfig
import com.banyumas.wisata.model.api.BackendService
import com.banyumas.wisata.model.repository.UserRepository
import com.banyumas.wisata.utils.MyDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Singleton
    @Provides
    fun provideAuthInterceptor(dataStore: MyDataStore): AuthInterceptor {
        return AuthInterceptor(dataStore)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        authService: BackendService
    ): UserRepository {
        return UserRepository(firebaseAuth, firestore, authService)
    }

    @Singleton
    @Provides
    @Named("auth")
    fun provideBackendOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideBackendService(@Named("auth") client: OkHttpClient): BackendService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BackendUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(BackendService::class.java)
    }
}

class AuthInterceptor(
    private val dataStore: MyDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { dataStore.token.first() }
        val requestBuilder = chain.request().newBuilder()

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}

package com.banyumas.wisata.core.data.di

import com.banyumas.wisata.core.data.auth.repository.AuthDataRepositoryImpl
import com.banyumas.wisata.core.data.repository.AuthRepository
import com.banyumas.wisata.core.data.auth.repository.DestinationDataRepositoryImpl
import com.banyumas.wisata.core.data.repository.DestinationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDestinationRepository(
        destinationDataRepositoryImpl: DestinationDataRepositoryImpl
    ): DestinationRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authDataRepositoryImpl: AuthDataRepositoryImpl
    ): AuthRepository
}
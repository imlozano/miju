package com.julian.miju2.di

import com.julian.miju2.data.repository.AccountRepositoryImpl
import com.julian.miju2.data.repository.TransactionRepositoryImpl
import com.julian.miju2.data.repository.UserPreferencesRepositoryImpl
import com.julian.miju2.data.repository.UserRepositoryImpl
import com.julian.miju2.domain.repository.AccountRepository
import com.julian.miju2.domain.repository.TransactionRepository
import com.julian.miju2.domain.repository.UserPreferencesRepository
import com.julian.miju2.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    @Singleton
    fun provideAccountRepository(impl: AccountRepositoryImpl): AccountRepository = impl

    @Provides
    @Singleton
    fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository = impl

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository = impl
}

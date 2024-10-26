package com.reap.data.di

import com.reap.data.repository.HomeRepositoryImpl
import com.reap.data.repository.LoginRepositoryImpl
import com.reap.data.repository.MainRepositoryImpl
import com.reap.domain.repository.HomeRepository
import com.reap.domain.repository.LoginRepository
import com.reap.domain.repository.MainRepository
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
    abstract fun bindMainRepository(
        mainRepositoryImpl : MainRepositoryImpl
    ): MainRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl : HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl : LoginRepositoryImpl
    ): LoginRepository
}
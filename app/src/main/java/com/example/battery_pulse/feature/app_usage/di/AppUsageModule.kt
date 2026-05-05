package com.example.battery_pulse.feature.app_usage.di

import com.example.battery_pulse.feature.app_usage.data.repositoryImpl.AppUsageRepositoryImpl
import com.example.battery_pulse.feature.app_usage.domain.repository.AppUsageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppUsageModule {

    @Binds
    abstract fun bindAppUsageRepository(
        impl: AppUsageRepositoryImpl
    ): AppUsageRepository
}
package com.example.battery_pulse.feature.battery.di

import android.content.Context
import com.example.battery_pulse.feature.battery.data.datasource.BatteryDataSource
import com.example.battery_pulse.feature.battery.data.repositoryImpl.BatteryRepositoryImpl
import com.example.battery_pulse.feature.battery.domain.repository.BatteryRepository
import com.example.battery_pulse.feature.battery.domain.usecase.GetBatteryInfoUseCase
import com.example.battery_pulse.feature.battery.presentaion.BatteryViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BatteryModule {

    @Provides
    @Singleton
    fun provideDataSource(
        @ApplicationContext context: Context
    ): BatteryDataSource {
        return BatteryDataSource(context)
    }

    @Provides
    @Singleton
    fun provideRepository(
        dataSource: BatteryDataSource
    ): BatteryRepository {
        return BatteryRepositoryImpl(dataSource)
    }

    @Provides
    @Singleton
    fun provideUseCase(
        repository: BatteryRepository
    ): GetBatteryInfoUseCase {
        return GetBatteryInfoUseCase(repository)
    }

    @Provides
    fun provideViewModel(
        useCase: GetBatteryInfoUseCase,
        repository: BatteryRepository
    ): BatteryViewModel {
        return BatteryViewModel(useCase, repository)
    }
}

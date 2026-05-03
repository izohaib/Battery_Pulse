package com.example.battery_pulse.feature.on_display.di

import android.content.Context
import com.example.battery_pulse.feature.battery.domain.repository.BatteryRepository
import com.example.battery_pulse.feature.battery.domain.usecase.GetBatteryInfoUseCase
import com.example.battery_pulse.feature.on_display.data.datasrouce.OnDisplaySettingsDataSource
import com.example.battery_pulse.feature.on_display.data.repositoryImpl.OnDisplayRepositoryImpl
import com.example.battery_pulse.feature.on_display.domain.repository.OnDisplayRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnDisplayModule {

    @Provides
    @Singleton
    fun provideOnDisplayDataSource(
        @ApplicationContext context: Context
    ): OnDisplaySettingsDataSource  {
        return OnDisplaySettingsDataSource(context)
    }

    @Provides
    @Singleton
    fun provideOnDisplayRepository(
        dataSource: OnDisplaySettingsDataSource
    ): OnDisplayRepository {
        return OnDisplayRepositoryImpl(dataSource)
    }

}
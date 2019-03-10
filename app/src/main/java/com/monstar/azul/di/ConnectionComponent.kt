package com.monstar.azul.di

import com.monstar.azul.GameActivity
import com.monstar.azul.domain.BluetoothConnector
import com.monstar.azul.presentation.game.GamePresenter
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ConnectionScope

@ConnectionScope
@Component(modules = [ConnectionModule::class])
interface ConnectionComponent {
    fun inject(gameActivity: GameActivity)
    fun inject(gamePresenterImpl: GamePresenter)
}

@Module
class ConnectionModule(private val bluetoothConnector: BluetoothConnector) {

    @Provides
    @ConnectionScope
    fun provideBluetoothConnector(): BluetoothConnector = bluetoothConnector
}
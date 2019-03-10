package com.monstar.azul

import android.app.Application
import com.monstar.azul.di.ConnectionComponent
import com.monstar.azul.di.ConnectionModule
import com.monstar.azul.di.DaggerConnectionComponent
import com.monstar.azul.domain.BluetoothConnector

class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {

        lateinit var connectionComponent: ConnectionComponent

        fun buildConnectionComponent(connector: BluetoothConnector) {
            connectionComponent = DaggerConnectionComponent
                .builder()
                .connectionModule(ConnectionModule(connector))
                .build()
        }
    }
}
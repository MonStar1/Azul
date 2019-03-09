package com.monstar.azul.domain

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.widget.Toast
import io.reactivex.Observable


class BluetoothConnector(context: Context) {

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    init {
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Пожалуйста, включите блюзут", Toast.LENGTH_LONG).show()
        }
    }

    fun startScan(): Observable<BluetoothDevice> {
        return Observable.create<BluetoothDevice> {
            bluetoothAdapter
        }
    }

}
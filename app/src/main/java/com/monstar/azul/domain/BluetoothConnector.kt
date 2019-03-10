package com.monstar.azul.domain

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.google.gson.Gson
import com.monstar.azul.data.entities.Game
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.min


private val UUID_AZUL = java.util.UUID.fromString("b2e348a4-1467-4812-a60f-b194d3f99a27")
private val PART_SIZE = 128

class BluetoothConnector(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var socket: BluetoothSocket? = null

    var isServer: Boolean? = null
        private set

    fun startScan(): Observable<BluetoothDevice> {
        lateinit var broadcastReceiver: BroadcastReceiver

        return Observable.create<BluetoothDevice> {
            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                        it.onNext(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
                    }
                }
            }

            context.registerReceiver(broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

            if (!bluetoothAdapter.startDiscovery()) {
                Log.d("logoff_exce", "Discovery don't started")
            } else {
                Log.d("logoff_exce", "Discovery started")
            }
        }
            .doOnDispose {
                context.unregisterReceiver(broadcastReceiver)
            }
    }

    fun stopScan() {
        bluetoothAdapter.cancelDiscovery()
    }

    fun startServer(): Single<BluetoothSocket> {
        val serverSocket =
            bluetoothAdapter.listenUsingRfcommWithServiceRecord("Azul", UUID_AZUL)

        return Single.create<BluetoothSocket> {
            var socket: BluetoothSocket? = null

            while (socket == null) {
                try {
                    socket = serverSocket.accept()
                } catch (ex: IOException) {
                    it.onError(ex)
                    break
                }
            }

            it.onSuccess(socket!!)
        }
            .doOnSuccess {
                this.socket = it
                isServer = true
                serverSocket.close()
            }
            .doOnDispose {
                serverSocket.close()
            }
    }

    fun connectToServer(device: BluetoothDevice): Single<BluetoothSocket> {
        val clientSocket = device.createRfcommSocketToServiceRecord(UUID_AZUL)

        return Single.create<BluetoothSocket> {
            try {
                clientSocket.connect()
            } catch (ex: Throwable) {
                clientSocket.close()
                it.onError(ex)
            }

            it.onSuccess(clientSocket)
        }
            .doOnSuccess {
                this.socket = it
                isServer = false
            }
            .doOnDispose {
                clientSocket.close()
            }
    }

    fun getBondedDevices(): MutableSet<BluetoothDevice>? {
        return bluetoothAdapter.bondedDevices
    }

    fun checkBluetoothIsEnabled(): Boolean {
        if (bluetoothAdapter.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(discoverableIntent)

            return false
        } else {
            return true
        }
    }

    fun sendGame(game: Game): Completable {
        return Observable.interval(1, 30, TimeUnit.SECONDS).flatMapCompletable {
            val gameString = Gson().toJson(game)

            sendDataTo(gameString)
        }
    }

    private fun sendDataTo(data: String): Completable {
        return Completable.create {
            val bytearray = data.toByteArray()

            socket?.outputStream?.write(bytearray.size.toString().toByteArray())

            var startIndex = 0

            while (bytearray.size > startIndex) {
                val startInd = startIndex
                val endInd = min(startIndex + PART_SIZE, bytearray.size - 1)

                val subArray = bytearray.sliceArray(startInd until endInd)

                socket?.outputStream?.write(subArray)

                startIndex += PART_SIZE
            }

            it.onComplete()
        }
    }

    fun getDataFrom(): Observable<String> {
        return Observable.create {
            while (!it.isDisposed) {
                try {
                    val arraySize = ByteArray(64)

                    while (arraySize[0].toInt() == 0) {
                        socket?.inputStream?.read(arraySize)
                    }

                    val size = String(trim(arraySize)).toInt()

                    val result = ByteArray(size)

                    var startIndex = 0

                    while (size > startIndex) {
                        val part = ByteArray(PART_SIZE)

                        socket?.inputStream?.read(part)

                        for (i in startIndex until  min(startIndex + PART_SIZE, size)) {
                            result[i] = part[i - startIndex]
                        }

                        startIndex += PART_SIZE
                    }

                    it.onNext(String(trim(result)))
                } catch (ex: Throwable) {
                    socket?.close()
                    it.onError(ex)
                }
            }
        }
    }

    fun trim(bytes: ByteArray): ByteArray {
        var i = bytes.size - 1

        while (i >= 0 && bytes[i].toInt() == 0) {
            --i
        }

        return bytes.copyOf(i + 1)
    }

}
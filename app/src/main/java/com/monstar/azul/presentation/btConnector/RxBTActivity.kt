package com.monstar.azul.presentation.btConnector

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.ivbaranov.rxbluetooth.BluetoothConnection
import com.github.ivbaranov.rxbluetooth.RxBluetooth
import com.monstar.azul.App
import com.monstar.azul.GameActivity
import com.monstar.azul.R
import com.monstar.azul.domain.BluetoothConnector
import com.monstar.azul.domain.UUID_AZUL
import com.monstar.azul.util.thread
import com.monstar.azul.util.toast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_bluetooth_connect.*
import pub.devrel.easypermissions.EasyPermissions
import kotlin.concurrent.thread

class RxBTActivity : AppCompatActivity() {

    private var isServer: Boolean = false

    private val rxBluetooth = RxBluetooth(this)

    private lateinit var btConnection: BluetoothConnection

    private val disposables = CompositeDisposable()

    private var adapter: DeviceAdapter? = null

    private val bigText by lazy {
        val buffer = StringBuilder()
        repeat(199999) {
            buffer.append("It is test text number $it")
        }

        buffer.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_connect)

        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                this,
                "Прими разрешение или играть не сможем",
                0,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        createServerButton.setOnClickListener {
            isServer = true
            createServer()
            hideButtons()
            showProgress(true)
        }

        createClientButton.setOnClickListener {
            isServer = false
            initBT()
        }


        // check if bluetooth is supported on your hardware
        if (!rxBluetooth.isBluetoothAvailable) {
            // handle the lack of bluetooth support
        } else {
            // check if bluetooth is currently enabled and ready for use
            if (!rxBluetooth.isBluetoothEnabled) {
                // to enable bluetooth via startActivityForResult()
                rxBluetooth.enableBluetooth(this, 0);
            } else {
                if (!rxBluetooth.isDiscovering) {
                    rxBluetooth.enableDiscoverability(this, 1)
                }
            }
        }

        sendTest.setOnClickListener {
            thread {
                btConnection.send(bigText + "\n")
            }
        }
    }

    private fun connectClient(device: BluetoothDevice) {
        rxBluetooth.cancelDiscovery()

        disposables.add(
            rxBluetooth.connectAsClient(device, UUID_AZUL)
                .thread()
                .doAfterTerminate { showProgress(false) }
                .subscribe { success, error ->
                    initBtConnection(success)
                    toast(success.remoteDevice.name)
                }
        )
    }

    private fun initBT() {
        adapter = DeviceAdapter(rxBluetooth.bondedDevices.toMutableSet(),
            object : DeviceAdapter.OnDeviceClickListener {
                override fun onClick(device: BluetoothDevice) {
                    hideButtons()
                    showProgress(true)
                    connectClient(device)
                }
            })
        remoteDevices.adapter = adapter

        disposables.add(
            rxBluetooth.observeDevices()
                .thread()
                .subscribeBy {
                    adapter?.deviceList?.add(it)
                    adapter?.notifyDataSetChanged()
                }
        )

        rxBluetooth.startDiscovery()
    }

    private fun createServer() {
        rxBluetooth.cancelDiscovery()

        disposables.add(
            rxBluetooth.connectAsServer("azul", UUID_AZUL)
                .thread()
                .doAfterTerminate { showProgress(false) }
                .subscribe { success, error ->
                    initBtConnection(success)
                }
        )
    }

    private fun hideButtons() {
        createClientButton.visibility = View.GONE
        createServerButton.visibility = View.GONE
        remoteDevices.visibility = View.GONE
    }

    private fun showProgress(show: Boolean) {
        progressView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun initBtConnection(socket: BluetoothSocket) {
        btConnection = BluetoothConnection(socket)

        App.buildConnectionComponent(BluetoothConnector(btConnection, isServer))

        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
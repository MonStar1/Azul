package com.monstar.azul.presentation.btConnector

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.monstar.azul.App
import com.monstar.azul.GameActivity
import com.monstar.azul.R
import com.monstar.azul.domain.BluetoothConnector
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bluetooth_connect.*
import pub.devrel.easypermissions.EasyPermissions

class BluetoothConnectActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private val btConnector by lazy { BluetoothConnector(applicationContext) }

    private var adapter: DeviceAdapter? = null

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

        createClientButton.setOnClickListener {
            if (!btConnector.checkBluetoothIsEnabled()) {
                return@setOnClickListener
            }

            createClientButton.visibility = View.GONE
            createServerButton.visibility = View.GONE

            adapter = DeviceAdapter(btConnector.getBondedDevices()?.toMutableSet() ?: mutableSetOf(),
                object : DeviceAdapter.OnDeviceClickListener {
                    override fun onClick(device: BluetoothDevice) {
                        btConnector.stopScan()

                        progressView.visibility = View.VISIBLE

                        disposables.add(
                            btConnector.connectToServer(device)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { success, error ->
                                    if (error == null) {
                                        App.buildConnectionComponent(btConnector)

                                        Toast.makeText(
                                            this@BluetoothConnectActivity,
                                            "Connected to server: " + success.remoteDevice.name,
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        disposables.add(btConnector.getDataFrom()
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeBy {
                                                Toast.makeText(
                                                    this@BluetoothConnectActivity,
                                                    "BT data: $it",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            })

                                        startGame()
                                    }

                                    progressView.visibility = View.GONE


                                }
                        )
                    }

                })
            remoteDevices.adapter = adapter

            disposables.add(
                btConnector.startScan()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onError = {},
                        onNext = {
                            Log.d("logoff_exce", it.name)
                            adapter?.deviceList?.add(it)
                            adapter?.notifyDataSetChanged()
                        }
                    )
            )
        }

        createServerButton.setOnClickListener {
            if (!btConnector.checkBluetoothIsEnabled()) {
                return@setOnClickListener
            }

            createClientButton.visibility = View.GONE
            createServerButton.visibility = View.GONE

            progressView.visibility = View.VISIBLE

            disposables.add(btConnector.startServer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { success, error ->
                    if (error == null) {
                        App.buildConnectionComponent(btConnector)

                        Toast.makeText(
                            this@BluetoothConnectActivity,
                            "Connected to client: " + success.remoteDevice.name,
                            Toast.LENGTH_SHORT
                        ).show()

                        disposables.add(btConnector.getDataFrom()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy {
                                Toast.makeText(
                                    this@BluetoothConnectActivity,
                                    "BT data: $it",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })

                        startGame()
                    }

                    progressView.visibility = View.GONE
                }
            )
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Toast.makeText(this, keyCode.toString(), Toast.LENGTH_SHORT).show()

        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startGame() {
        disposables.dispose()
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }
}
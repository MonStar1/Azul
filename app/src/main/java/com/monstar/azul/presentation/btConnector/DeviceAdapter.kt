package com.monstar.azul.presentation.btConnector

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.monstar.azul.R
import kotlinx.android.synthetic.main.item_bluetooth_connect.view.*

class DeviceAdapter(var deviceList: MutableSet<BluetoothDevice>, val deviceClickListener: OnDeviceClickListener) :
    RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bluetooth_connect, parent, false))
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = deviceList.elementAt(position)

        holder.deviceId.text = item.address.toString()
        holder.deviceName.text = item.name
        holder.itemView.tag = item
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                deviceClickListener.onClick(it.tag as BluetoothDevice)
            }
        }

        val deviceName = itemView.deviceName
        val deviceId = itemView.deviceId
    }

    interface OnDeviceClickListener {
        fun onClick(device: BluetoothDevice)
    }
}
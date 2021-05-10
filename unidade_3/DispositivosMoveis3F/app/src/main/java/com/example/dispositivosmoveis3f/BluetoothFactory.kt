package com.example.dispositivosmoveis3f

import android.bluetooth.BluetoothAdapter

class BluetoothFactory {

    companion object {
        private var adapter : BluetoothAdapter? = null

        fun getBluetooth(): BluetoothAdapter? {
            if (adapter == null) {
                adapter = BluetoothAdapter.getDefaultAdapter()
            }
            return adapter
        }
    }

}

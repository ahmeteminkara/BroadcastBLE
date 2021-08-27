package com.ahmet.broadcastble.tools;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ahmet.broadcastble.BroadcastBLE;
import com.ahmet.broadcastble.enums.BleDeviceErrors;
import com.ahmet.broadcastble.listener.BroadcastBleErrorCallback;

public class DeviceControls {
    /**
     * cihazda bluetooth u kontrol eder
     */
    private static boolean isOpenBluetooth(Activity activity) {
        BluetoothManager bluetoothManager = (BluetoothManager)
                activity.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.isEnabled();
    }

    /**
     * cihazda ble desteğini kontrol eder
     */
    private static boolean isSupportBle(Activity activity) {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * cihazda ble yayını desteğini kontrol eder
     */
    private static boolean isSupportAdvertisement() {
        return BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported();
    }

    /**
     *  uygulama için cihazda gerekli ayarlamaları kontrol eder
     */
    public static boolean checkSetting(
            Activity activity,
            BroadcastBleErrorCallback errorCallback) {

        if (!DeviceControls.isOpenBluetooth(activity)) {
            errorCallback.onDeviceError(BleDeviceErrors.BLUETOOTH_NOT_ACTIVE);
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, BroadcastBLE.bluetoothRequestCode);
            return false;
        }
        if (!DeviceControls.isSupportBle(activity)) {
            errorCallback.onDeviceError(BleDeviceErrors.UNSUPPORT_BLUETOOTH_LE);
            return false;
        }

        /*
        if (!DeviceControls.isSupportAdvertisement()) {

            errorCallback.onDeviceError(BleDeviceErrors.UNSUPPORT_BLE_ADVERTISER);
            return false;
        }

         */

        return true;
    }
}

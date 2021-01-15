package com.ahmet.broadcastble.tools;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.ahmet.broadcastble.BroadcastBLE;
import com.ahmet.broadcastble.enums.BleBroadcastErrors;
import com.ahmet.broadcastble.listener.BroadcastBleCallback;
import com.ahmet.broadcastble.listener.BroadcastBleErrorCallback;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Executer {
    /**
     * Arayüz aktivitesi
     */
    private final Activity activity;

    private ParcelUuid deviceUUID;

    /**
     * bluetooth servis listesi
     */
    private final Map<BluetoothGattService, List<BluetoothGattCharacteristic>> services;

    /**
     * Bluetooth durumunu izlemek için gerekli fonksiyonlar
     */
    private final BroadcastBleCallback broadcastBleCallback;
    private final BroadcastBleErrorCallback broadcastBleErrorCallback;

    /**
     * Yayın yapmamızı sağlayan sınıf
     */
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer bluetoothGattServer;

    /**
     * @param activity             Arayüz aktivitesi
     * @param services             Servis listesi
     * @param broadcastBleCallback ble durum izleme methodu
     * @param errorCallback        ble hatalarını yakalama methodu
     */
    public Executer(
            Activity activity,
            Map<BluetoothGattService, List<BluetoothGattCharacteristic>> services,
            BroadcastBleCallback broadcastBleCallback,
            BroadcastBleErrorCallback errorCallback) {
        this.activity = activity;
        this.services = services;
        this.broadcastBleCallback = broadcastBleCallback;
        this.broadcastBleErrorCallback = errorCallback;
    }

    public void setDeviceUUID(String uuid) {
        this.deviceUUID = ParcelUuid.fromString(uuid);
    }

    /**
     * BLE yayınını başlatır
     */
    public void startGattServer() {

        if (deviceUUID==null){
            broadcastBleErrorCallback.onBroadcastError(BleBroadcastErrors.UNDEFINED_DEVICE_UUID);
            return;
        }

        BluetoothManager manager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        bluetoothLeAdvertiser = adapter.getBluetoothLeAdvertiser();

        bluetoothGattServer = manager.openGattServer(activity, bluetoothGattServerCallback);


        AdvertiseSettings.Builder advertiseSettings = new AdvertiseSettings.Builder();

        advertiseSettings.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        advertiseSettings.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        advertiseSettings.setConnectable(true);
        advertiseSettings.setTimeout(0);


        AdvertiseData.Builder advertiseData = new AdvertiseData.Builder();
        advertiseData.setIncludeDeviceName(true);
        advertiseData.setIncludeTxPowerLevel(true);
        advertiseData.addServiceUuid(deviceUUID);


        Log.d(BroadcastBLE.TAG, "startAdvertising çalıştı");

        try {


            bluetoothLeAdvertiser.startAdvertising(advertiseSettings.build(), advertiseData.build(), advertiseCallback);

            addServices();


        } catch (Exception e) {
            broadcastBleErrorCallback.onBroadcastError(BleBroadcastErrors.BROADCAST_NOT_STARTED);
            Log.e(BroadcastBLE.TAG, "startAdvertising: " + e);
        }

    }

    private void addServices() {
        for (Map.Entry<BluetoothGattService, List<BluetoothGattCharacteristic>> serviceListEntry : services.entrySet()) {

            BluetoothGattService service = serviceListEntry.getKey();
            for (BluetoothGattCharacteristic c : serviceListEntry.getValue()) {
                service.addCharacteristic(c);
            }
            bluetoothGattServer.addService(service);

            services.remove(service);
            break;

        }
    }

    /**
     * BLE yayınını bitirir
     */
    public void stopGattServer() {
        if (bluetoothGattServer != null)
            bluetoothGattServer.close();
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        broadcastBleCallback.onBroadcast(false);
    }


    private final AdvertiseCallback advertiseCallback = new AdvertiseCallback() {


        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            broadcastBleCallback.onBroadcast(true);

            super.onStartSuccess(settingsInEffect);

        }

        @Override
        public void onStartFailure(int errorCode) {
            broadcastBleCallback.onBroadcast(false);

            super.onStartFailure(errorCode);
        }
    };

    private final BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {

            Log.d(BroadcastBLE.TAG, "added service " + service.getUuid());

            if (!services.isEmpty()) addServices();

            super.onServiceAdded(status, service);
        }

        @Override
        public void onCharacteristicWriteRequest(
                BluetoothDevice device, int requestId,
                BluetoothGattCharacteristic characteristic, boolean preparedWrite,
                boolean responseNeeded, int offset, byte[] value) {

            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite,
                    responseNeeded, offset, value);

            try {
                broadcastBleCallback.onWriteRequest(true, characteristic.getUuid(), value);
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
            } catch (Exception e) {
                broadcastBleErrorCallback.onBroadcastError(BleBroadcastErrors.FAILED_WRITING);
            }

        }


        @Override
        public void onCharacteristicReadRequest(
                BluetoothDevice device, int requestId, int offset,
                BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            try {
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                        broadcastBleCallback.onReadRequest(characteristic.getUuid()));
            } catch (Exception e) {
                broadcastBleErrorCallback.onBroadcastError(BleBroadcastErrors.FAILED_READING);

            }

        }

        @Override
        public void onConnectionStateChange(
                BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED)
                broadcastBleCallback.onConnectedDevice(true, device);
            else
                broadcastBleCallback.onConnectedDevice(false, null);
        }


        @Override
        public void onNotificationSent(
                BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);

        }
    };
}

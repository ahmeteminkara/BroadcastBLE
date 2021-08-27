package com.ahmet.broadcastble;


import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.ahmet.broadcastble.listener.BroadcastBleCallback;
import com.ahmet.broadcastble.listener.BroadcastBleErrorCallback;
import com.ahmet.broadcastble.tools.DeviceControls;
import com.ahmet.broadcastble.tools.Executer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


final public class BroadcastBLE extends Executer {
    /**
     * Log ekraınıda filtreleme için "BroadcastBLE" ı aratın
     */
    public static final String TAG = "BroadcastBLE";

    public static final int bluetoothRequestCode = 57;

    /**
     * Tanımlanan activity
     */
    private final Activity activity;

    /**
     * Hata yakalamak için kullanılır
     */
    private final BroadcastBleErrorCallback errorCallback;
    private final BroadcastBleCallback bleCallback;

    /**
     * Yayınlanacak servis ve servise bağlı hizmetlerin listesi
     */
    public static final Map<BluetoothGattService, List<BluetoothGattCharacteristic>> services = new HashMap<>();

    /**
     * @param activity      [...Activity.this] şeklinde belirtilmesi lazım
     * @param errorCallback Hataları yakalamak için
     * @param bleCallback   BLE yayını durumunu izlemek için
     */
    public BroadcastBLE(
            Activity activity,
            BroadcastBleErrorCallback errorCallback,
            BroadcastBleCallback bleCallback) {

        super(activity, services, bleCallback, errorCallback);


        this.activity = activity;
        this.errorCallback = errorCallback;
        this.bleCallback = bleCallback;

    }

    /**
     * Yayın yapmaya başla
     */
    public void start() {
        if (bleCallback == null || errorCallback == null) return;
        if (!DeviceControls.checkSetting(this.activity, this.errorCallback)) return;

        super.startGattServer();

    }

    /**
     * Yayını sonlandır
     */
    public void stop() {
        Log.d(TAG, "BİTİR");
        super.stopGattServer();
    }

    /**
     * BLE servisi ekle
     */
    public void addService(BluetoothGattService service, List<BluetoothGattCharacteristic> characteristicList) {
        services.put(service, characteristicList);
    }

    /**
     * Ble servis listesinden servisi sil
     */
    public void removeService(BluetoothGattService service) {
        services.remove(service);
    }

    /**
     * Ble servis listesini temizle
     */
    public void removeAllService() {
        services.clear();
    }

}
package com.ahmet.broadcastble.listener;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Hizmet, servisin içindeki "characteristic"
 * (BluetoothGattCharacteristic)
 */

public abstract class BroadcastBleCallback {
    public abstract void onBroadcast(boolean isStarting);
    /**
     * @param isConnected cihaz ile bağlı olma durumu
     * @param device      bağlı cihaz bilgileri
     */
    public abstract void onConnectedDevice(boolean isConnected, BluetoothDevice device);

    /**
     * @param uuid yayıncıdan okuma yapan hizmet
     * @return yayıncıdan hizmete karşılık giden veri
     */
    public abstract byte[] onReadRequest(UUID uuid);


    /**
     * @param success yayıncıya data gönderme işleminin durumu
     * @param uuid    yayıncıya gelen veriyi taşıyan hizmet
     * @param data    yayıncıya gelen veri
     */
    public abstract void onWriteRequest(boolean success, UUID uuid, byte[] data);
}

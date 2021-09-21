package com.ahmet.broadcastble.listener;

import com.ahmet.broadcastble.enums.BleBroadcastErrors;
import com.ahmet.broadcastble.enums.BleDeviceErrors;

public interface BroadcastBleErrorCallback {
    /**
     * Cihaz ile ilgili bir hata olursa çalışır
     */
    void onDeviceError(BleDeviceErrors deviceError);

    /**
     * Yayın işlemi ile ilgili bir hata olursa çalışır
     */
    void onBroadcastError(BleBroadcastErrors broadcastError);
}

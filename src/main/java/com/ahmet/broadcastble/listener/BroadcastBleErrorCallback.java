package com.ahmet.broadcastble.listener;

import com.ahmet.broadcastble.enums.BleBroadcastErrors;
import com.ahmet.broadcastble.enums.BleDeviceErrors;

public abstract class BroadcastBleErrorCallback {
    /**
     * Cihaz ile ilgili bir hata olursa çalışır
     */
    public abstract void onDeviceError(BleDeviceErrors deviceError);

    /**
     * Yayın işlemi ile ilgili bir hata olursa çalışır
     */
    public abstract void onBroadcastError(BleBroadcastErrors broadcastError);
}

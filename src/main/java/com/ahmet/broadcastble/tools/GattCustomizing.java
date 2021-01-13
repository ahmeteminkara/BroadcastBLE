package com.ahmet.broadcastble.tools;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class GattCustomizing {

    /**
     * Yeni bir bluetooth servisi oluşturur
     * @param uuid Servis uuid si
     * @param serviceType Örneğin: BluetoothGattService.SERVICE_TYPE_PRIMARY
     */
    public static BluetoothGattService createService(UUID uuid, int serviceType) {
        return new BluetoothGattService(uuid, serviceType);
    }

    /**
     * Yeni bir okuma servis hizmeti oluşturur
     * @param uuid characteristic uuid
     */
    public static BluetoothGattCharacteristic createCharacteristicRead(UUID uuid) {
        return new BluetoothGattCharacteristic(uuid,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
    }

    /**
     * Yeni bir okuma servis hizmeti oluşturur
     * @param uuid characteristic uuid
     * @param properties hizmetin özelliği [PROPERTY_READ | PROPERTY_WRITE]
     * @param permissions hizmetin izni [PERMISSION_READ | PERMISSION_WRITE]
     */
    public static BluetoothGattCharacteristic createCharacteristicRead(
            UUID uuid, int properties, int permissions) {
        return new BluetoothGattCharacteristic(uuid, properties, permissions);
    }

    /**
     * Yeni bir yazma servis hizmeti oluşturur
     * @param uuid characteristic uuid
     */
    public static BluetoothGattCharacteristic createCharacteristicWrite(UUID uuid) {
        return new BluetoothGattCharacteristic(uuid,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
    }

    /**
     * Yeni bir yazma servis hizmeti oluşturur
     * @param uuid characteristic uuid
     * @param properties hizmetin özelliği [PROPERTY_READ | PROPERTY_WRITE]
     * @param permissions hizmetin izni [PERMISSION_READ | PERMISSION_WRITE]
     */
    public static BluetoothGattCharacteristic createCharacteristicWrite(
            UUID uuid, int properties, int permissions) {
        return new BluetoothGattCharacteristic(uuid, properties, permissions);
    }

}

# BroadcastBLE

> BluetoothLE Advertising for **Android** with **Java**

- minSdkVersion ***21***

![Android & BLE](https://raw.githubusercontent.com/ahmeteminkara/BroadcastBLE/main/src/main/res/drawable/icon.png)

## Getting Started

```yaml
implementation project(path: ':BroadcastBLE')
```

#### Variables
```java
    private BroadcastBLE broadcastBLE;

    String customerId = "575757";
    String apiUrl = "https://git.aek.com";
    //UUID
    UUID UUID_SERVICE = UUID.fromString("a6c33970-6c90-49f8-bf3b-47d149400b9c");
    UUID UUID_CUSTOMER_INFO = UUID.fromString("addc4be8-7a4c-4fa7-80b1-f6b69fecf81e");
    UUID UUID_API = UUID.fromString("86f774ee-31b7-40c2-adb0-bfbb0550626f");
    UUID UUID_USERRESPONSE = UUID.fromString("0c5ce913-5432-440d-8acd-4e301006682d");

    // for dynamic Hex value
    Map<UUID, String> uuidCharacteristicValueApiUrl = new HashMap<>();

```

#### Prepare Method
```java
    public void prepareBroadcastBle() {

        broadcastBLE = new BroadcastBLE(
                MainActivity.this,
                errorCallback,
                bleCallback);

        // CUSTOM SERVICE #1
        BluetoothGattService service = GattCustomizing.createService(UUID_SERVICE);
        List<BluetoothGattCharacteristic> characteristicList = Arrays.asList(
                GattCustomizing.createCharacteristicRead(UUID_CUSTOMER_INFO),
                GattCustomizing.createCharacteristicRead(UUID_API),
                GattCustomizing.createCharacteristicWrite(UUID_USERRESPONSE)
        );
        broadcastBLE.addService(service,characteristicList);

        // CUSTOM SERVICE #2
        // string -> hex -> split(fixed size 16) -> list
        BluetoothGattService serviceDynamic = GattCustomizing.createService(
            UUID.fromString("22e7ac41-88ab-4ff7-b4e9-5b9d7c2dd257"))
        List<String> apiUrlHexList = stringToHexArray(apiUrl);
        List<BluetoothGattCharacteristic> characteristicApiUrl = new ArrayList<>();
        for (int i = 0; i < apiUrlHexList.size(); i++) {
            UUID uuid = UUID.randomUUID();
            String characteristicValue = (i > 10 ? "" : "0") + i + apiUrlHexList.get(i);
            uuidCharacteristicValueApiUrl.put(uuid, characteristicValue);
            characteristicApiUrl.add(GattCustomizing.createCharacteristicRead(uuid));
        }
        broadcastBLE.addService(serviceDynamic, characteristicApiUrl);
        
        // for scan filter (required)
        broadcastBLE.setDeviceUUID("99999999-8888-7777-6666-555555555555");
        broadcastBLE.start();
    }
```


#### Broadcast Handler
``` java
    BroadcastBleCallback bleCallback = new BroadcastBleCallback() {
        @Override
        public void onBroadcast(boolean isStarting) {
            if (isStarting) {
                changeUI(UiPosition.SCAN);
                Toast.makeText(MainActivity.this, "Yayın Aktif", Toast.LENGTH_SHORT).show();
                Log.d(BroadcastBLE.TAG, "Yayın aktif");
            } else
                Log.d(BroadcastBLE.TAG, "Yayın devre dışı");

        }

        @Override
        public void onConnectedDevice(boolean isConnected, BluetoothDevice device) {
            if (isConnected) {
                changeUI(UiPosition.DETECT, device.getName());
                Log.d(BroadcastBLE.TAG, "Yayıncıya cihaz bağlandı");
            } else {
                //postDelayed -> changeUI(UiPosition.SCAN)
                Log.d(BroadcastBLE.TAG, "Yayıncı ile cihaz bağlantısı kesildi");
            }
        }

        @Override
        public byte[] onReadRequest(UUID uuid) {

            changeUI(UiPosition.READ);
            if (uuid.toString().equals(UUID_API.toString())) {
                return apiUrl.getBytes();
            } else if (uuid.toString().equals(UUID_CUSTOMER_INFO.toString())) {
                return customerId.getBytes();
            }else{
                // dynamic uuid read
                if (uuidCharacteristicValueApiUrl.containsKey(uuid)) {
                    Log.d(BroadcastBLE.TAG, "detect dynamic uuid");
                    String data = uuidCharacteristicValueApiUrl.get(uuid);
                    if (data == null)
                        Log.d(BroadcastBLE.TAG, "null data");
                    else
                        return data.getBytes();
                        
                }
            }
            return null;
        }

        @Override
        public void onWriteRequest(boolean success, UUID uuid, byte[] data) {
            changeUI(UiPosition.WRITE);
            Log.d(BroadcastBLE.TAG, "Yayıncıya yazma isteği geldi");
            Log.d(BroadcastBLE.TAG, "data: " + new String(data));
            runOnUiThread(() -> setTextData(new String(data)));
        }
    };
```

#### Broadcast Error Handler
```java
    BroadcastBleErrorCallback errorCallback = new BroadcastBleErrorCallback() {
        @Override
        public void onDeviceError(BleDeviceErrors bleError) {
            Log.e(BroadcastBLE.TAG, "onDeviceError: " + bleError);
            
            //BLUETOOTH_NOT_ACTIVE,
            //UNSUPPORT_BLUETOOTH_LE,
            //UNSUPPORT_BLE_ADVERTISER,
        }

        @Override
        public void onBroadcastError(BleBroadcastErrors broadcastError) {
            Log.e(BroadcastBLE.TAG, "broadcastError: " + broadcastError);

            //FAILED_READING,
            //FAILED_WRITING,
            //ADVERTISING_NOT_STARTED,
        }
    };

```

#### Bluetooth Handler (required)
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BroadcastBLE.bluetoothRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                new Handler().postDelayed(this::prepareBroadcastBle, 1000);
                Toast.makeText(this, "Bluetooth açıldı", Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Uygulamanın Bluetooth erişimine ihtiyacı var");
                builder.setMessage("Bu uygulamanın çevre birimlerini" +
                        " algılayabilmesi için lütfen Bluetooth'u açınız");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> prepareBroadcastBle());
                builder.show();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
```

#### Enum
```java
enum UiPosition {
    SCAN, 
    READ, 
    WRITE, 
    DETECT,
}
```

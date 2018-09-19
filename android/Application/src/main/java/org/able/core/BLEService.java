/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.able.core;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * This interface needs to be implemented by every activity you want to create,
 * this measure makes sure you handle every indispensable situation considering a bluetooth connection.
 * For a better understanding take a look at the BLEBroadcastReceiver.class.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */
public class BLEService extends Service {
    private final static String TAG = BLEService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public static BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static BluetoothGattCharacteristic mCharacteristicToPass;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private final boolean CONNECTION_SUCCEEDED = true;
    private final boolean CONNECTION_FAILED = false;
    private final boolean INITIALIZATION_SUCCEEDED = true;
    private final boolean INITIALIZATION_FAILED = false;


    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    /**
     * Implements callback method for GATT events that the app cares about. For example, connection change and services discovered.
     *
     * @param gatt not used
     * @param status not used
     * @param newState the new Bluetooth connection state
     * @return BluetoothGattCallback
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        /**
         * This method is called, if a GATT service is discovered.
         * @param gatt not used
         * @param status status of GATT connection
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        /**
         * This method is called, if a read command for a characteristic is sent.
         * @param gatt not used
         * @param characteristic chosen characteristic for the read process
         * @param status status of GATT connection
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        /**
         * If data of the observed characteristic is updated, this method is called.
         * @param gatt not used
         * @param characteristic chosen characteristic that is observed
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };


    /**
     * Broadcast a chosen action.
     * @param action that is braodcasted
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * This method broadcasts the new value of a characteristic.
     * @param action
     * @param characteristic characteristic of the value
     */
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
            {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        mCharacteristicToPass = characteristic;
        sendBroadcast(intent);
    }

    /**
     * Returns the BluetoothLeService.
     */
    public class LocalBinder extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }

    /**
     * Returns mBinder.
     * @param intent not used
     * @return IBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * After using a given device, you should make sure that BluetoothGatt.close() is called
     * such that resources are cleaned up properly.  In this particular example, close() is
     * invoked when the UI is disconnected from the Service.
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {

            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return INITIALIZATION_FAILED;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }else
            {
                mBluetoothAdapter.getDefaultAdapter();
            }
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return INITIALIZATION_FAILED;
        }
        return INITIALIZATION_SUCCEEDED;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");

            return CONNECTION_FAILED;
        }
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return CONNECTION_SUCCEEDED;
            } else {
                return CONNECTION_FAILED;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            Toast.makeText(this, "This device is no longer available. Try to refresh the list.", Toast.LENGTH_SHORT).show();
            return CONNECTION_FAILED;
        }
        // We want directly connect to the device, so we are setting the autoConnect
        // For better understanding to choose this parameter take a look at the following link:
        // https://stackoverflow.com/questions/40156699/which-correct-flag-of-autoconnect-in-connectgatt-of-ble
        boolean noReconnectIfDisconnected = false; //attribute represent the intened behaviour, false means no reconnect intended
        mBluetoothGatt = device.connectGatt(this, noReconnectIfDisconnected, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return CONNECTION_SUCCEEDED;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Toast.makeText(this, "Reading Characteristics did not work yet." +
                    "Try to reconnect and read again.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     * */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            //Toast.makeText(this, "Setting notifications did not work yet. Try to reconnect and set again.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }


    /**
     * @return true, if AbleDeviceScanActivity has a active BluetoothAdapter.
     */
    public static boolean existBluetoothAdapter(){
        if(AbleDeviceScanActivity.getmBluetoothLeService() != null){return true;}
        else return false;
    }

    /**
     * @return true, if a GATT server is active.
     */
    public static boolean existBluetoothGatt(){
        if(mBluetoothGatt != null){return true;}
        else return false;
    }

    /**
     * Read a characteristic of a GATT server.
     * @param someCharToRead the chosen characteristic.
     */
    public static void genericReadCharacteristic(BluetoothGattCharacteristic someCharToRead){
        mBluetoothGatt.readCharacteristic(someCharToRead);
        return;
    }

    /**
     * Rewrites a chosen characteristic.
     * @param someCharToWrite the characteristic which is overwritten.
     */
    public static void genericWriteCharacteristic(BluetoothGattCharacteristic someCharToWrite){
        mBluetoothGatt.writeCharacteristic(someCharToWrite);
        return;
    }

    /**
     * @return mCharacteristicToPass
     */
    public static BluetoothGattCharacteristic getmCharacteristicToPass() {
        return mCharacteristicToPass;
    }
}

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

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.able.core.CharacteristicSorterClass.settingUpServices;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */
public class DeviceControlActivity extends Activity implements BLEServiceListener {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;

    private BluetoothLeService mBluetoothLeService;

    CharacteristicSorterClass containsCollections = null;
    List<HashMap<String, String>> gattServiceData = new ArrayList<>();
    List<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();
    BLEBroadcastReceiver thisReceiver;

    private List<List<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    /**
     * If a given GATT characteristic is selected, check for supported features.  This sample
     * demonstrates 'Read' and 'Notify' features.  See
     * http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
     * list of supported characteristic features.
     */
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                    mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
    };


    /**
     * Clears all the shown characteristics on the GUI.
     */
    void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    /**
     * Starting method for the instantiation of the GUI elements.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = findViewById(R.id.connection_state);
        mDataField = findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Called after onCreate and sets GATT variables.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        thisReceiver= new BLEBroadcastReceiver(this);
        registerReceiver(thisReceiver ,
                thisReceiver.makeGattUpdateIntentFilter());
        mBluetoothLeService = DeviceScanActivity.getmBluetoothLeService();

        /*
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
        }
        */
    }

    /**
     * Called every time the app is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(thisReceiver);
    }

    /**
     * Called if the app is closed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService = null;
    }

    /**
     * Initialization of the GUI menu.
     * @param menu used by the method getMenuInflater()
     * @return true if menu added successfully
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    /**
     * Called if an item of the menu is selected.
     * @param item the selected item of the menu.
     * @return true, if everything was handled correctly.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                if(mBluetoothLeService == null)
                {
                    Toast.makeText(this, "this should not happen, as this object is static", Toast.LENGTH_SHORT).show();
                }
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                mBluetoothLeService.disconnect();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        DeviceScanActivity.getmBluetoothLeService().disconnect();
        super.onBackPressed();
    }

    /**
     * @return mBluetoothLeService
     */
    public BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    /**
     * Updates the TextView GUI object which represents the connection state.
     * @param resourceId a GUI object ID
     */
    void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    /**
     * Set the TextView element mDataField of the GUI to the String data.
     * @param data
     */
    void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    /**
     * Sorting gets done from settingsUpServices(..) and iterates through the supported GATT Services/Characteristics.
     * In this sample, we populate the data structure that is bound to the ExpandableListView
     * on the UI.
     * @param gattServices
     */
    void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        containsCollections = settingUpServices(gattServices, LIST_NAME, LIST_UUID, mGattCharacteristics, this);
        gattServiceData = containsCollections.getGattServiceData();
        gattCharacteristicData = containsCollections.getGattCharacteristicData();
        mGattCharacteristics = containsCollections.getCharacteristicList();

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }


    /**
     * Called when GATT connection starts.
     */
    @Override
    public void gattConnected() {
        mConnected = true;
        updateConnectionState(R.string.connected);
        invalidateOptionsMenu();
    }

    /**
     * Called when GATT connection ends.
     */
    @Override
    public void gattDisconnected() {
        mConnected = false;
        updateConnectionState(R.string.disconnected);
        invalidateOptionsMenu();
        clearUI();
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        displayGattServices(getmBluetoothLeService().getSupportedGattServices());
    }

    /**
     * Called if data is available.
     * @param intent
     */
    @Override
    public void dataAvailable(Intent intent) {
        displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
    }
}

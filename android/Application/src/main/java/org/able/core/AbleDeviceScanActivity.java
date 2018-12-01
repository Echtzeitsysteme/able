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

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import org.able.capled.CapLEDServiceRegistryUpdater;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

import static android.content.ContentValues.TAG;
import static org.able.core.AblePermissionUtils.isLocationEnabled;

//class to make permissionhandling more clear

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 * Furthermore this is the start screen of the app.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */
public class AbleDeviceScanActivity extends ListActivity implements BLEServiceListener {

    private BLEDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    boolean locationPermisstions = false;
    private Button scanButton;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST = 1;
    //TODO: SCANNING TIME CHANGED!!
    private static final long SCAN_PERIOD = 1000; // Stops scanning after 2 seconds.

    private static BLEService mBluetoothLeService;
    private BLEBroadcastReceiver deviceScanActivityReiceiver;
    private String mDeviceName;
    private String mDeviceAddress;
    BluetoothDevice device = null;
    //ListView ableBleScanList;

    private Switch bluetoothSwitch;
    private Switch locationSwitch;
    private final String scanButtonStart = "START SCAN";
    private final String scanButtonStop = "STOP SCAN";

    public AbleServiceRegistry serviceRegistry;

    private HashMap<BluetoothDevice, byte[]> deviceScanResponseMap = new HashMap<>();
    private List<UUID> uuidList = new ArrayList<>();

    /**
     * This Listener handles button clicks of the GUI.
     */
    private View.OnClickListener buttonListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scanButton:
                    if (mBluetoothAdapter.isEnabled() && locationPermisstions && !mScanning) {
                        scanButton.setText(scanButtonStop);
                        mLeDeviceListAdapter.clear();
                        scanLeDevice(true);
                        break;
                    } else {
                        scanButton.setActivated(false);
                        scanButton.setText(scanButtonStart);
                        mLeDeviceListAdapter.clear();
                        mLeDeviceListAdapter.notifyDataSetInvalidated();
                        scanLeDevice(false);
                    }
                    break;
            }

        }
    };


    private void askForLocationPermission() {
        ArrayList<String> arrPerm = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            arrPerm.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!arrPerm.isEmpty()) {
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.
                    requestPermissions(AbleDeviceScanActivity.this, permissions, MY_PERMISSIONS_REQUEST);
        }
    }

    /**
     * Initializes activity and GUI objects.
     *
     * @param savedInstanceState the instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();
        setContentView(R.layout.able_device_scan_activity);


        serviceRegistry = AbleServiceRegistry.getInstance();

        serviceRegistry.initializeServices(this);

        this.sendBroadcast(new Intent(AbleServiceRegistryUpdatingBroadcastReceiver.INTENT_ACTION_UPDATE_UUID_MAPPING));

        scanButton = new Button(this);
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(buttonListener2);

        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);
        bluetoothSwitch.setTag(false);
        locationSwitch = findViewById(R.id.locationSwitch);
        locationSwitch.setTag(false);

        /*
         * This is the handler of the switch button for location data.
         */
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBluetoothAdapter.enable();
                    if (isLocationEnabled(getApplicationContext())) {
                        scanButton.setActivated(true);
                        scanButton.setClickable(true);
                        scanButton.setBackgroundColor(Color.rgb(45, 45, 45));
                    } else {
                        scanButton.setActivated(false);
                        scanButton.setClickable(false);
                        scanButton.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                } else {
                    mBluetoothAdapter.disable();
                    scanButton.setActivated(false);
                    scanButton.setClickable(false);
                    scanButton.setBackgroundColor(Color.rgb(220, 220, 220));
                }

            }
        });

        /**
         * This is the handler of the switch button for Bluetooth.
         */
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (locationSwitch.getTag().equals(true)) {
                    if (isChecked) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        setLocationSwitch();

                        if (mBluetoothAdapter.isEnabled()) {
                            scanButton.setActivated(true);
                            scanButton.setClickable(true);
                            scanButton.setBackgroundColor(Color.rgb(45, 45, 45));
                        } else {
                            scanButton.setActivated(false);
                            scanButton.setClickable(false);
                            scanButton.setBackgroundColor(Color.rgb(220, 220, 220));
                        }
                    } else {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        ArrayList<String> arrPerm = new ArrayList<>();

                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            arrPerm.add(Manifest.permission.ACCESS_FINE_LOCATION);
                        }

                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            arrPerm.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                        }

                        if (!arrPerm.isEmpty()) {
                            String[] permissions = new String[arrPerm.size()];
                            permissions = arrPerm.toArray(permissions);
                            ActivityCompat.
                                    requestPermissions(AbleDeviceScanActivity.this, permissions, MY_PERMISSIONS_REQUEST);
                        }
                        setLocationSwitch();
                        scanButton.setActivated(false);
                        scanButton.setClickable(false);
                        scanButton.setBackgroundColor(Color.rgb(220, 220, 220));
                    }
                }
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            bluetoothSwitch.setChecked(false);
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        } else {
            setBluetoothSwitch();
        }
        setLocationSwitch();

        bluetoothSwitch.setTag(true);
        locationSwitch.setTag(true);

        setScanButton();

        mLeDeviceListAdapter = new BLEDeviceListAdapter(AbleDeviceScanActivity.this.getLayoutInflater());
        setListAdapter(mLeDeviceListAdapter);

        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        askForLocationPermission();
    }

    /**
     * Called if the back button of the smartphone is pressed.
     */
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Called on start to initialize the GUI menu.
     *
     * @param menu
     * @return true if menu is initialized.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_stop).setVisible(false);
        menu.findItem(R.id.menu_scan).setVisible(false);

        if (!mScanning) {
            menu.findItem(R.id.menu_refresh).setActionView(null);
            scanButton.setText(scanButtonStart);
        } else {
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.able_actionbar_progess_icon);
            scanButton.setText(scanButtonStop);
        }
        return true;
    }


    /**
     * Called after onCreate(..) and initializes BLE variables.
     */
    @Override
    protected void onResume() {
        super.onResume();

        setBluetoothSwitch();
        setLocationSwitch();

        deviceScanActivityReiceiver = new BLEBroadcastReceiver(this);
        registerReceiver(deviceScanActivityReiceiver,
                deviceScanActivityReiceiver.makeGattUpdateIntentFilter());
    }

    /**
     * Called if app is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);

        setLocationSwitch();
        unregisterReceiver(deviceScanActivityReiceiver);
    }

    /**
     * Called if app is closed temporary.
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * Called if app is closed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    /**
     * Callback function for startActivityForResult(..), which is called inside the location button switch handler.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        if (requestCode == resultCode) {
            Log.d("ActivityResult", "so expected");

            locationSwitch.setTag(false);
            setLocationSwitch();
            locationSwitch.setTag(true);
            setScanButton();

            onRestart();
            return;
        }
        if (requestCode != resultCode) {
            Log.d("ActivityResult", "expected does not match received");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called if an item of the menu is pressed.
     *
     * @param item pressed item of the menu.
     * @return true if everything handled well.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                if (!mBluetoothAdapter.isEnabled() || !locationPermisstions) {
                    Toast.makeText(this, "Bluetooth or Location is NOT enabled, use the buttons please.", Toast.LENGTH_SHORT).show();
                    break;
                }
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    /**
     * Called if an item of the ABLE scan list is clicked. Then this method will try to connect to the
     * chosen device.
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;

        /*
        if (mBluetoothLeService != null)
        {
            final boolean result = mBluetoothLeService.connect(device.getAddress());
            Log.d(TAG, "Connect request result=" + result);
            if(result)
            {
                Toast.makeText(this, "Connection in progress please wait.", Toast.LENGTH_SHORT).show();
            }

        }
        */

        Intent intent = new Intent(this, checkForKnownServices(device));

        intent.putExtra(AbleDeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(AbleDeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        startActivity(intent);
    }

    /**
     * Searches for BLE devices if enabled.
     *
     * @param enable if true scans for BLE devices.
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;

            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();
    }

    /**
     * Callback function for the bluetooth devices list while doing the BLE scan.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            deviceScanResponseMap.put(device, scanRecord);
                            Log.d(TAG, scanRecord.toString());
                            mLeDeviceListAdapter.addDevice(device);

                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    /**
     * Decodes a binary input into a hex string.
     *
     * @param a binary byte input
     * @return String of the hex number of the binary input
     */
    public String byte2hex(byte[] a) {

        String hexString = "";

        for (int i = 0; i < a.length; i++) {
            String thisByte = "".format("%x", a[i]);
            hexString += thisByte;
        }

        return hexString;

    }

    /**
     * Parses a byte stream of a BLE advertisement message and saves the service UUIDs.
     *
     * @param scanRecord byte stream input of the BLE advertisement message
     */
    public void parseAdvertisementPacket(final byte[] scanRecord) {

        byte[] advertisedData = Arrays.copyOf(scanRecord, scanRecord.length);
        uuidList.clear();
        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++] & 0xFF;
                        uuid16 |= (advertisedData[offset++] << 8);
                        len -= 2;
                        uuidList.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData,
                                    offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuidList.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            //Log.e("BlueToothDeviceFilter.parseUUID", e.toString());
                            //continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                case 0xFF:  // Manufacturer Specific Data
                    /*
                    Log.d(TAG, "Manufacturer Specific Data size:" + len + " bytes");
                    while (len > 1) {
                        if (i < 32) {
                            MfgData[i++] = advertisedData[offset++];
                        }
                        len -= 1;
                    }
                    Log.d(TAG, "Manufacturer Specific Data saved." + MfgData.toString());
                    break;
                    */
                default:
                    offset += (len - 1);
                    break;
            }
        }
    }

    /**
     * This method checks if the found Gatt service matches one of the saved ones inside the ServiceRegistry.
     *
     * @return A actitivity that will be called for the matching UUID. The Default activity is org.able.core.AbleDeviceControlActivity.
     */
    private Class<?> checkForKnownServices(BluetoothDevice bleDevice) {
        Class<?> choosenActivity = AbleDeviceControlActivity.class;
        byte[] bleAdvertisementData = deviceScanResponseMap.get(bleDevice);
        parseAdvertisementPacket(bleAdvertisementData);
        for (UUID uuid : uuidList) {
            if (serviceRegistry.getRegisteredServices().containsKey(uuid)) {
                choosenActivity = serviceRegistry.getServiceClass(uuid);
                break;
            }
        }
        return choosenActivity;
    }

    /**
     * Sets the View of the BluetoothSwitch.
     */
    private void setBluetoothSwitch() {
        if (mBluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(true);
        } else if (!mBluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(false);
        }
    }

    /**
     * Sets the View of the LocationSwitch.
     */
    private void setLocationSwitch() {
        locationPermisstions = isLocationEnabled(getApplicationContext());
        if (!locationPermisstions) {
            locationSwitch.setChecked(false);
        } else if (locationPermisstions) {
            locationSwitch.setChecked(true);
        }
    }

    /**
     * Sets the View of the ScanButton.
     */
    private void setScanButton() {
        if (mBluetoothAdapter.isEnabled() && isLocationEnabled(getApplicationContext())) {
            scanButton.setActivated(true);
            scanButton.setClickable(true);
            scanButton.setBackgroundColor(Color.rgb(45, 45, 45));
        } else {
            scanButton.setActivated(false);
            scanButton.setClickable(false);
            scanButton.setBackgroundColor(Color.rgb(220, 220, 220));
        }
    }

    /**
     * Called when GATT connection starts.
     */
    @Override
    public void gattConnected() {
    }

    /**
     * Called when GATT connection ends.
     */
    @Override
    public void gattDisconnected() {
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        /*
        if (!CySmartFix){
            Intent intent = new Intent(this, checkForKnownServices());

            intent.putExtra(AbleDeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(AbleDeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            //TODO: Is this the fix?
            //mBluetoothLeService.disconnect();
            startActivity(intent);
        }
        */
    }

    /**
     * This method is called if data is available, but is not used for this activity.
     *
     * @param intent
     */
    @Override
    public void dataAvailable(Intent intent) {

    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


    /**
     * This manages the service lifecycle.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "onServiceConnected: WE BOUND OUR SERVICE !");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Log.d(TAG, "onServiceConnected: WE UN....BOUND OUR SERVICE !");
        }
    };

    /**
     * @return mBluetoothLeService.
     */
    public static BLEService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

}
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

package de.tudarmstadt.es.able;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//class to make permissionhandling more clear

import static android.content.ContentValues.TAG;
import static de.tudarmstadt.es.able.PermissionUtils.isLocationEnabled;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 * Furthermore this is the start screen of the app.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */
public class DeviceScanActivity extends ListActivity implements BLEServiceListener{

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    boolean locationPermisstions = false;
    private Button scanButton;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST= 1;
    private static final long SCAN_PERIOD = 10000; // Stops scanning after 10 seconds.

    private static BluetoothLeService mBluetoothLeService;
    private BLEBroadcastReceiver deviceScanActivityReiceiver;
    private String mDeviceName;
    private String mDeviceAddress;
    BluetoothDevice device = null;

    private Switch bluetoothSwitch;
    private Switch locationSwitch;
    private final String scanButtonStart = "START SCAN";
    private final String scanButtonStop = "STOP SCAN";

    private ServiceRegistry serviceRegistry;

    /**
     * This Listener handles button clicks of the GUI.
     */
    private View.OnClickListener buttonListener2 = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.scanButton:
                    if (mBluetoothAdapter.isEnabled() && locationPermisstions && !mScanning) {
                        scanButton.setText(scanButtonStop);
                        mLeDeviceListAdapter.clear();
                        scanLeDevice(true);
                        break;
                    }
                    else {
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

    /**
     * @return mBluetoothLeService
     */
    public BluetoothLeService getmBluetoothLeServiceFromDeviceScanActivity() {
        return mBluetoothLeService;
    }

    /**
     * Initializes activity and GUI objects.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();
        setContentView(R.layout.permission_handling);

        this.sendBroadcast(new Intent(ServiceRegistryUpdatingBroadcastReceiver.INTENT_ACTION_UPDATE_UUID_MAPPING));

        serviceRegistry = ServiceRegistry.getInstance();

        scanButton = new Button(this);
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(buttonListener2);

        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);
        bluetoothSwitch.setTag(false);
        locationSwitch = findViewById(R.id.locationSwitch);
        locationSwitch.setTag(false);

        /**
         * This is the handler of the switch button for location data.
         */
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBluetoothAdapter.enable();
                    if(isLocationEnabled(getApplicationContext())){
                        scanButton.setActivated(true);
                        scanButton.setClickable(true);
                        scanButton.setBackgroundColor(Color.rgb(45,45,45));
                    }
                    else{
                        scanButton.setActivated(false);
                        scanButton.setClickable(false);
                        scanButton.setBackgroundColor(Color.rgb(220,220,220));
                    }
                }
                else {
                    mBluetoothAdapter.disable();
                    scanButton.setActivated(false);
                    scanButton.setClickable(false);
                    scanButton.setBackgroundColor(Color.rgb(220,220,220));
                }

            }
        });

        /**
         * This is the handler of the switch button for Bluetooth.
         */
        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(locationSwitch.getTag().equals(true)) {
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
                                    requestPermissions(DeviceScanActivity.this, permissions, MY_PERMISSIONS_REQUEST);
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

        if(mBluetoothAdapter == null){
            bluetoothSwitch.setChecked(false);
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
        }

        else {
            setBluetoothSwitch();
        }
        setLocationSwitch();

        bluetoothSwitch.setTag(true);
        locationSwitch.setTag(true);

        setScanButton();

        mLeDeviceListAdapter = new LeDeviceListAdapter(DeviceScanActivity.this.getLayoutInflater());
        setListAdapter(mLeDeviceListAdapter);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * Called if the back button of the smartphone is pressed.
     */
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Called on start to initialize the GUI menu.
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
                    R.layout.actionbar_indeterminate_progress);
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
     * @param item pressed item of the menu.
     * @return true if everything handled well.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                if(!mBluetoothAdapter.isEnabled() || !locationPermisstions)
                {
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
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
            device = mLeDeviceListAdapter.getDevice(position);
            if (device == null) return;
            if (mBluetoothLeService != null)
            {
                final boolean result = mBluetoothLeService.connect(device.getAddress());
                Log.d(TAG, "Connect request result=" + result);
                if(result)
                {
                    Toast.makeText(this, "Connection in progress please wait.", Toast.LENGTH_SHORT).show();
                }
            }

    }

    /**
     * Searches for BLE devices if enabled.
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
     * Callback function for the bluetooth adapter BLE scan.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    /**
     * This method checks if the found Gatt service matches one of the saved ones inside the ServiceRegistry.
     * @return
     */
    private Class<?> checkForKnownServices()
    {
        List<BluetoothGattService>  tmpList = mBluetoothLeService.getSupportedGattServices();
        Class<?> choosenActivity = DeviceControlActivity.class;
        for(BluetoothGattService tmpGattService : tmpList)
        {
                if(serviceRegistry.getRegisteredServices().containsKey(tmpGattService.getUuid())) {
                    mBluetoothLeService.disconnect();
                    choosenActivity = serviceRegistry.getServiceClass(tmpGattService.getUuid());
                    break;
                }
        }
        mBluetoothLeService.disconnect();
        return choosenActivity;
    }

    /**
     * Sets the View of the BluetoothSwitch.
     */
    private void setBluetoothSwitch(){
        if (mBluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(true);
        }else if(!mBluetoothAdapter.isEnabled() ){
            bluetoothSwitch.setChecked(false);
        }
    }

    /**
     * Sets the View of the LocationSwitch.
     */
    private void setLocationSwitch(){
        locationPermisstions = isLocationEnabled(getApplicationContext());
        if(!locationPermisstions)
        {
            locationSwitch.setChecked(false);
        }else if(locationPermisstions){
            locationSwitch.setChecked(true);
        }
    }

    /**
     * Sets the View of the ScanButton.
     */
    private void setScanButton(){
        if(mBluetoothAdapter.isEnabled() && isLocationEnabled(getApplicationContext())){
            scanButton.setActivated(true);
            scanButton.setClickable(true);
            scanButton.setBackgroundColor(Color.rgb(45,45,45));
        }
        else{
            scanButton.setActivated(false);
            scanButton.setClickable(false);
            scanButton.setBackgroundColor(Color.rgb(220,220,220));
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
        Intent intent = new Intent(this, checkForKnownServices());

        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

            if (mScanning) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }
            mBluetoothLeService.disconnect();
        startActivity(intent);
    }

    /**
     * This method is called if data is available, but is not used for this activity.
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
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
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
    public static BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

}
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
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//class to make permissionhandling more clear
import static android.content.ContentValues.TAG;
import static de.tudarmstadt.es.able.PermissionUtils.isLocationEnabled;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
//public class DeviceScanActivity extends ListActivity implements View.OnClickListener {
public class DeviceScanActivity extends ListActivity{

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;
    private boolean mScanning;
    private Handler mHandler;
    LocationManager mLocationManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    boolean locationPermisstions = false;

    //additional elements to use switches and list
    private Switch bluetooth_switch;

    private Button bluetoothButton;
    private TextView bluetoothStatus;

    private Button locationButton;
    private TextView locationStatus;


    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST= 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    //----------------------------------------------------------------------------------------------
    //-Part of move of service----------------------------------------------------------------------
    //private BluetoothLeService mBluetoothLeService;
    private static BluetoothLeService mBluetoothLeService;
    BroadcastReceiverAndFilterDefinition DeviceScanActivityReiceiver;//probably always the same receiver..
    int someFreakyCounterCauseIDoNotSeeAnotherWay = 0;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    //----------------------------------------------------------------------------------------------

    private View.OnClickListener buttonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()) {

                case R.id.bluetoothButton:
                    //disable the bluetooth adapter
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                        bluetoothStatus.setText("BlueTooth is currently switched OFF");
                        bluetoothButton.setText("Switch ON Bluetooth");
                        //mLeDeviceListAdapter.clear();
                    }
                    //enable the bluetooth adapter
                    else {
                        mBluetoothAdapter.enable();
                        bluetoothStatus.setText("BlueTooth is currently switched ON");
                        bluetoothButton.setText("Switch OFF Bluetooth");
                    }
                    break;

                case R.id.locationButton:
                    //disable the bluetooth adapter
                    if (locationPermisstions) {
                        //disable stuff

                        //mBluetoothAdapter.disable();
                        //should start activity
                        //int result = 0;
                        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        //REQUEST for 0 not 1, however otherwise the it will return to the start screen, NOT mainActivity
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

                        //should set showed text to aimed status
                        //seems not to work, does not wait till resume, trying onResume

                        /*maybe startActivityForResult is what I was looking for...
                        //oneway to check for generall location
                        mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                        //more generic way to check for generall location
                        Context currentContext = getApplicationContext();
                        try {
                            Settings.Secure.getInt(currentContext.getContentResolver(), Settings.Secure.LOCATION_MODE);
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                        */

                        locationTextSet();

                    }

                    else {
                        //differend kind of permissions are handled differently, general/specific permission as seen here

                        //general location permission
                        //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        //REQUEST for 0 not 1, however otherwise the it will return to the start screen, NOT mainActivity
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

                        //Container to store all requests which should be permitted and are not available during startup
                        ArrayList<String> arrPerm = new ArrayList<>();

                        //TODO: might be a better way to request for permissions, USE isLocationEnabled to check and create array
                        //application specific location permission
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            arrPerm.add(Manifest.permission.ACCESS_FINE_LOCATION);
                        }

                        //application specific location permission
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            arrPerm.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                        }

                        //permissions get requested, index by index
                        if(!arrPerm.isEmpty()) {
                            String[] permissions = new String[arrPerm.size()];
                            permissions = arrPerm.toArray(permissions);
                            ActivityCompat.
                                    requestPermissions(DeviceScanActivity.this, permissions, MY_PERMISSIONS_REQUEST);
                        }

                        locationTextSet();
                        //mLeDeviceListAdapter.clear();

                    }
                    break;
                // More buttons go here (if any) ...
            }
        }
    };


    public BluetoothLeService getmBluetoothLeServiceFromDeviceScanActivity() {
        return mBluetoothLeService;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();
        setContentView(R.layout.permission_handling);


        //mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //Context.getSystemService(LocationManager.class) -> available from API23

        //reference to the buttons
        bluetoothButton = new Button(this);
        bluetoothButton = findViewById(R.id.bluetoothButton);
        bluetoothButton.setOnClickListener(buttonListener);

        locationButton = new Button(this);
        locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(buttonListener);

        //reference to the text views
        bluetoothStatus = findViewById(R.id.bluetoothStatus);
        locationStatus = findViewById(R.id.locationStatus);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        //assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //bluetoothadapter handling
        if(mBluetoothAdapter == null){
            bluetoothStatus.setText("BlueTooth adapter not found");
            bluetoothButton.setText("BlueTooth Disabled");
            bluetoothButton.setEnabled(false);
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();

            //some final options to end program, without bluetooth there is no functionality
            //finish();
            //return;
        }

        //check the status and set the button text accordingly
        else {
            if (mBluetoothAdapter.isEnabled()) {
                bluetoothStatus.setText("BlueTooth is currently switched ON");
                bluetoothButton.setText("Switch OFF Bluetooth");
            }else{
                bluetoothStatus.setText("BlueTooth is currently switched OFF");
                bluetoothButton.setText("Switch ON Bluetooth");
            }
        }
        locationTextSet();


        //LeDeviceListAdapter needs to be created, was recreated onResume before.
        mLeDeviceListAdapter = new LeDeviceListAdapter(DeviceScanActivity.this.getLayoutInflater());
        setListAdapter(mLeDeviceListAdapter);

        //Commented because of move to MainActivity-------------------------------------------------
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //------------------------------------------------------------------------------------------


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //break condition, scan should only be available if all requirements fulfilled
                //make it visible to the user via shown text.
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

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.

            /*this statement changes to system permission windows but NEVER lets you return AND just checkes for Bluetoothadapter
            //if this is gonna be reused it needs some proper statement adjustment and conclusions
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                startActivityForResult(enableLocationIntent, REQUEST_ENABLE_LOCATION);
            }
            */

        // Initializes list view adapter.
        //mLeDeviceListAdapter = new LeDeviceListAdapter(DeviceScanActivity.this.getLayoutInflater());
        //setListAdapter(mLeDeviceListAdapter);

        //no scanning without interaction
        //scanLeDevice(true);


        bluetoothTextSet();
        locationTextSet();

        DeviceScanActivityReiceiver = new BroadcastReceiverAndFilterDefinition(mConnected);
        //Commented because of move to MainActivity-------------------------------------------------
        registerReceiver(DeviceScanActivityReiceiver ,
                BroadcastReceiverAndFilterDefinition.makeGattUpdateIntentFilter());
        //Commented because of move to MainActivity-------------------------------------------------

        /*registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
*/
        //------------------------------------------------------------------------------------------
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        if (requestCode == resultCode) {
            Log.d("ActivityResult", "so expected");

            locationTextSet();

            onRestart();
            return;
        }

        if (requestCode != resultCode) {
            Log.d("ActivityResult", "expected does not match received");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);

        locationTextSet();

        //not clearing the devicelist
        //mLeDeviceListAdapter.clear();


        //Commented because of move to MainActivity-------------------------------------------------
        //unregisterReceiver(mGattUpdateReceiver);
        //------------------------------------------------------------------------------------------
    }


    //after pressing back the application seems to disappear(not crashing), needs to be accessed again
    @Override
    protected void onStop() {
        //probably going to use later
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Commented because of move to MainActivity-------------------------------------------------
        unbindService(mServiceConnection);
        //mBluetoothLeService = null;
        //------------------------------------------------------------------------------------------
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        if(someFreakyCounterCauseIDoNotSeeAnotherWay <= 2)
        {
            Toast.makeText(this, "Next step is to trigger a search for specific services.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onListItemClick: counter works fine...");
            someFreakyCounterCauseIDoNotSeeAnotherWay++;
            return;
        }
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

        //-----
        mDeviceName = device.getName();
        mDeviceAddress = device.getAddress();
        //-----
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }

        //This is going to be the section, where the service checks for all


        someFreakyCounterCauseIDoNotSeeAnotherWay = 0;
        startActivity(intent);
    }



    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
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

            //mBluetoothAdapter.getBluetoothLeScanner()
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
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

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    private void locationTextSet()
    {

        locationPermisstions = isLocationEnabled(getApplicationContext());
        if(!locationPermisstions)
        {
            locationStatus.setText("location is currently OFF");
            locationButton.setText("Switch ON location");
        }else{
            locationStatus.setText("location is currently ON");
            locationButton.setText("Switch OFF location");
        }

    }

    private void bluetoothTextSet()
    {
        if (mBluetoothAdapter.isEnabled()) {
            bluetoothStatus.setText("BlueTooth is currently switched ON");
            bluetoothButton.setText("Switch OFF Bluetooth");
        }else{
            bluetoothStatus.setText("BlueTooth is currently switched OFF");
            bluetoothButton.setText("Switch ON Bluetooth");
        }
    }







    //----------------------------------------------------------------------------------------------
    //First step is to move initializing and handling of bleService to MainActivity-----------------
    //----------------------------------------------------------------------------------------------
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "onServiceConnected: WE BOUND OUR SERVICE !");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Log.d(TAG, "onServiceConnected: WE UN....BOUND OUR SERVICE !");
        }
    };

    public static BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }




}
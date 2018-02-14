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
import android.app.ActivityManager;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
//public class DeviceScanActivity extends ListActivity implements View.OnClickListener {
    public class DeviceScanActivity extends ListActivity{

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    LocationManager locationManager = null;
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
                        mBluetoothAdapter.disable();
                        locationStatus.setText("BlueTooth is currently switched OFF");
                        bluetoothButton.setText("Switch ON Bluetooth");
                    }

                    else {
                        //differend kind of permissions are handled differently, general/specific permission as seen here

                        //general location permission
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

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
                            try {
                                ActivityCompat.
                                        requestPermissions(getActivity(), permissions, MY_PERMISSIONS_REQUEST);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }
                        }

                        locationStatus.setText("locations are currently ON");
                        locationButton.setText("Switch OFF locations");
                    }
                    break;
                // More buttons go here (if any) ...
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();
        setContentView(R.layout.permission_handling);

        //reference to the buttons
        bluetoothButton = new Button(this);
        bluetoothButton = (Button) findViewById(R.id.bluetoothButton);
        //bluetoothButton.setOnClickListener((View.OnClickListener) this.bluetoothButton);
        bluetoothButton.setOnClickListener(buttonListener);

        locationButton = new Button(this);
        locationButton = (Button) findViewById(R.id.locationButton);
        locationButton.setOnClickListener(buttonListener);

        //reference to the text views
        bluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        locationStatus = (TextView) findViewById(R.id.locationStatus);

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

        //location permissiontext handling
        locationPermisstions = isLocationEnabled(getApplicationContext());
        if(!locationPermisstions){
            locationStatus.setText("location is currently OFF");
            locationButton.setText("Switch ON location");
        }else{
            locationStatus.setText("location is currently ON");
            locationButton.setText("Switch OFF location");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            //NEED TO REMOVE BEFORE Pushing finally
            // DEBUGGING LOG
            Log.d("SCANNING", "mScanning was true therefore scan should run...");
            //NEED TO REMOVE CLOSE
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    //was static before
    //TODO: reuse this method for checking, not only during startup but extend to buttons, therefore needs to return array<strings>
    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        int permissionCheckFine = 1;
        int permissionCheckCoarse = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                permissionCheckFine = ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION);
                permissionCheckCoarse = ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            boolean result = (locationMode != Settings.Secure.LOCATION_MODE_OFF)&&
                    (0 == permissionCheckFine) && (0 == permissionCheckCoarse);
            return result;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    /*
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bluetoothButton:
                //disable the bluetooth adapter
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                    status.setText("BlueTooth is currently switched OFF");
                    bluetoothButton.setText("Switch ON Bluetooth");
                }
                //enable the bluetooth adapter
                else {
                    mBluetoothAdapter.enable();
                    status.setText("BlueTooth is currently switched ON");
                    bluetoothButton.setText("Switch OFF Bluetooth");
                }
                break;
            // More buttons go here (if any) ...
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
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
        mLeDeviceListAdapter = new LeDeviceListAdapter(DeviceScanActivity.this.getLayoutInflater());
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    public static Activity getActivity() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = null;
        try {
            activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);


        Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
        if (activities == null)
            return null;

        for (Object activityRecord : activities.values()) {
            Class activityRecordClass = activityRecord.getClass();
            Field pausedField = activityRecordClass.getDeclaredField("paused");
            pausedField.setAccessible(true);
            if (!pausedField.getBoolean(activityRecord)) {
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }

        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
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
}
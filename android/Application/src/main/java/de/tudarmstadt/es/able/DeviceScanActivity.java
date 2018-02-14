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
    private TextView status;
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

                case R.id.locationButton:
                    //disable the bluetooth adapter
                    if (locationPermisstions) {
                        //disable stuff
                        mBluetoothAdapter.disable();
                        locationStatus.setText("BlueTooth is currently switched OFF");
                        bluetoothButton.setText("Switch ON Bluetooth");
                    }

                    else {
                        //enable stuff
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        //Container to store all requests which should be permitted and are not available during startup
                        ArrayList<String> arrPerm = new ArrayList<>();

                        //BEST NOT PRACTICE... refactore might be useful...
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            arrPerm.add(Manifest.permission.ACCESS_FINE_LOCATION);
                        }

                        //this one shall fix settings.secure.LOCATION_MODE,2)..
                        /*
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.LOCATION_HARDWARE)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            arrPerm.add(Manifest.permission.LOCATION_HARDWARE);
                        }*/
                        //


                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            arrPerm.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                        }


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



                        //Settings.Secure.
                        //mBluetoothAdapter.enable();
                        locationStatus.setText("locations are currently ON");
                        bluetoothButton.setText("Switch OFF locations");
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
        status = (TextView) findViewById(R.id.status);
        locationStatus = (TextView) findViewById(R.id.locationStatus);

        //USING SWITCHES AND LISTACTIVITIES SEEMS next to impossible...
//    bluetooth_switch = (Switch) findViewById(R.id.bluetooth_switch);
//    bluetooth_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if(isChecked) {
//                bluetooth_switch.setText("Only Today's");  //To change the text near to switch
//                Log.d("You are :", "Checked");
//            }
//            else {
//                bluetooth_switch.setText("All List");  //To change the text near to switch
//                Log.d("You are :", " Not Checked");
//            }
//        }
//    });

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }


        //final LocationManager
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        //assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //bluetooth permission handling
        if(mBluetoothAdapter == null){
            status.setText("BlueTooth adapter not found");
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
                status.setText("BlueTooth is currently switched ON");
                bluetoothButton.setText("Switch OFF Bluetooth");
            }else{
                status.setText("BlueTooth is currently switched OFF");
                bluetoothButton.setText("Switch ON Bluetooth");
            }
        }

        //location permission handling
        locationPermisstions = isLocationEnabled(getApplicationContext());
        if(!locationPermisstions){
            locationStatus.setText("location is currently OFF");
            locationButton.setText("Switch ON location");
        }else{
            locationStatus.setText("location is currently ON");
            locationButton.setText("Switch OFF location");
        }


        //NEED TO REMOVE BEFORE Pushing finally
        //Debugging check if there is an BluetootAdapterobject,
//        if(mBluetoothAdapter != null)
//        {
//            // DEBUGGING LOG
//            Log.d("BluetoothAdapter", "an BluetoothAdapterobject was created.");
//            Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show();
//        }//Object seems to get created
        //NEED TO REMOVE CLOSE

        // Checks if Bluetooth is supported on the device.

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

        //outter if seems checked twice, maybe somekind of necessary but assuming typo
        //if (!mBluetoothAdapter.isEnabled()) {

            /*
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                Intent enableLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                startActivityForResult(enableLocationIntent, REQUEST_ENABLE_LOCATION);
            }
            */

        //}

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
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

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
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
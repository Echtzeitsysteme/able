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
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
 */
//public class DeviceScanActivity extends ListActivity implements View.OnClickListener {
public class DeviceScanActivity extends ListActivity implements BLEServiceListener{

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothScanner;
    private boolean mScanning;
    private Handler mHandler;
    boolean locationPermisstions = false;



    private Button scanButton;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST= 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

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
    //----------------------------------------------------------------------------------------------

    private View.OnClickListener buttonListener = new View.OnClickListener(){
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
                    //enable the bluetooth adapter
                    else {
                        scanButton.setActivated(false);
                        scanButton.setText(scanButtonStart);
                        mLeDeviceListAdapter.clear();
                        mLeDeviceListAdapter.notifyDataSetInvalidated();
                        scanLeDevice(false);
                    }
                break;
                // Add more buttons for the UI here ...
            }

        }
    };


    public BluetoothLeService getmBluetoothLeServiceFromDeviceScanActivity() {
        return mBluetoothLeService;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Include drawable for top bar, to differentiate between it and the app icon
        //getActionBar().setBackgroundDrawable(getDrawable(R.drawable.es_fg_logo_able));
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();
        setContentView(R.layout.permission_handling);

        serviceRegistry = ServiceRegistry.getInstance();

        //mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //Context.getSystemService(LocationManager.class) -> available from API23


        scanButton = new Button(this);
        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(buttonListener);


        //reference to switches
        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);
        locationSwitch = findViewById(R.id.locationSwitch);

        // This will be called when the Bluetooth ON/OFF switch is touched
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBluetoothAdapter.enable();
                    //mLeDeviceListAdapter.clear();
                    // TODO: DEBUG THIS SECTION
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
                    // TODO: BUG app doesn't work if this is activated
                    // mLeDeviceListAdapter.clear();
                }

            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    setLocationSwitch();
                    // TODO: DEBUG THIS SECTION
                    if(mBluetoothAdapter.isEnabled()){
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
                    //different kind of permissions are handled differently, general/specific permission as seen here

                    //general location permission
                    //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    //REQUEST for 0 not 1, however otherwise the it will return to the start screen, NOT mainActivity
                    //startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    //Container to store all requests which should be permitted and are not available during startup
                    ArrayList<String> arrPerm = new ArrayList<>();

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
                    setLocationSwitch();
                    // TODO: DEBUG THIS SECTION
                    scanButton.setActivated(false);
                    scanButton.setClickable(false);
                    scanButton.setBackgroundColor(Color.rgb(220,220,220));
                    //mLeDeviceListAdapter.clear();

                }
            }
        });

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
            bluetoothSwitch.setChecked(false);
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();

            //some final options to end program, without bluetooth there is no functionality
            //finish();
            //return;
        }

        //check the status and set the button text accordingly
        else {
            setBluetoothSwitch();
        }
        setLocationSwitch();

        // TODO: DEBUG THIS SECTION
        setScanButton();

        //LeDeviceListAdapter needs to be created, was recreated onResume before.
        mLeDeviceListAdapter = new LeDeviceListAdapter(DeviceScanActivity.this.getLayoutInflater());
        setListAdapter(mLeDeviceListAdapter);

        //(un-)Bind only happens during the main activity, service can be reused in all activities--
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //------------------------------------------------------------------------------------------


    }
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_stop).setVisible(false);
        menu.findItem(R.id.menu_scan).setVisible(false);


        if (!mScanning) {
            //menu.findItem(R.id.menu_stop).setVisible(false);
            //menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
            scanButton.setText(scanButtonStart);
        } else {
            //menu.findItem(R.id.menu_stop).setVisible(true);
            //menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
            scanButton.setText(scanButtonStop);
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

        setBluetoothSwitch();
        setLocationSwitch();


        //receiver gets registered and unregistered depending on this activity is running or not
        deviceScanActivityReiceiver = new BLEBroadcastReceiver(this);
        registerReceiver(deviceScanActivityReiceiver,
                deviceScanActivityReiceiver.makeGattUpdateIntentFilter());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // this needs to be overriden to use startActivityForResult(...)
        // should be tweaked for better behavior in according to userbehaviour
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        if (requestCode == resultCode) {
            Log.d("ActivityResult", "so expected");

            setLocationSwitch();

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

        setLocationSwitch();
        unregisterReceiver(deviceScanActivityReiceiver);


    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

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

    private Class<?> checkForKnownServices()
    {
        List<BluetoothGattService>  tmpList = mBluetoothLeService.getSupportedGattServices();
        //Toast.makeText(this, "Looking for "+ CapLEDConstants.CAPLED_SERVICE_UUID , Toast.LENGTH_SHORT).show();
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

    private void setBluetoothSwitch(){
        if (mBluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(true);
        }else{
            bluetoothSwitch.setChecked(false);
        }
    }

    private void setLocationSwitch(){
        locationPermisstions = isLocationEnabled(getApplicationContext());
        if(!locationPermisstions)
        {
            locationSwitch.setChecked(false);
        }else{
            locationSwitch.setChecked(true);
        }
    }

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

    //methods which needed to be implemented because of the BLEServiceListener interface
    @Override
    public void gattConnected() {
        //Toast.makeText(this, "Connected to gatt, heard from broadcastReceiver.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gattDisconnected() {

    }

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

    @Override
    public void dataAvailable(Intent intent) {

    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }



    // This manages the Service lifecycle.
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
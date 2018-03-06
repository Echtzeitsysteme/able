package de.tudarmstadt.es.able;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 27.02.18.
 */

public class CapLedActivity extends Activity implements BLEServiceListener  {
    private final static String TAG = CapLedActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;

    private BluetoothLeService mBluetoothLeService;

    BLEBroadcastReceiver thisReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        mConnectionState = findViewById(R.id.connection_state);
        mDataField = findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName + " specific. TAM TAM TAM");
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        thisReceiver= new BLEBroadcastReceiver(this);
        registerReceiver(thisReceiver ,
                thisReceiver.makeGattUpdateIntentFilter());
        mBluetoothLeService = DeviceScanActivity.getmBluetoothLeService();



        if (mBluetoothLeService != null) {
            Toast.makeText(this, "BluetoothLeService is static, hope that works out :)", Toast.LENGTH_SHORT).show();
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        //updateConnectionState()
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(thisReceiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        mBluetoothLeService = null;
    }

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                //Toast.makeText(this, mDeviceAddress, Toast.LENGTH_SHORT).show();
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













    void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    @Override
    public void gattConnected() {
        mConnected = true;
        updateConnectionState(R.string.connected);
        Toast.makeText(this, "gattConnected ", Toast.LENGTH_SHORT).show();
        invalidateOptionsMenu();

    }

    @Override
    public void gattDisconnected() {
        mConnected = false;
        updateConnectionState(R.string.disconnected);
        mDataField.setText(R.string.no_data);
        invalidateOptionsMenu();
    }

    @Override
    public void gattServicesDiscovered() {
        Toast.makeText(this, "gattServicesDiscovered...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dataAvailable(Intent intent) {

    }
}

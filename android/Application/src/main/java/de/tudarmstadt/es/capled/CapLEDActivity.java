/*
        CapLEDAcitivity Class for the example application of an Bluetooth IoT device with a capacitive sensor and a LED.
 */
package de.tudarmstadt.es.capled;

/*
        IMPORTS
 */
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import de.tudarmstadt.es.able.BLEBroadcastReceiver;
import de.tudarmstadt.es.able.BluetoothLeService;
import de.tudarmstadt.es.able.DeviceScanActivity;
import de.tudarmstadt.es.able.R;
import de.tudarmstadt.es.able.BLEServiceListener;


public class CapLEDActivity extends Activity implements BLEServiceListener {
    private final static String TAG = CapLEDActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;

    private BluetoothLeService mBluetoothLeService;
    //write and read values is called by BluetoothGattObject
    private static TextView mCapsenseValue;
    private static BluetoothGatt mBluetoothGatt;
    private static Switch led_switch;
    private static Switch cap_switch;
    private static boolean mLedSwitchState = false;
    public static BluetoothGattCharacteristic mLedCharacteristic;

    private static BluetoothGattCharacteristic mCapsenseCharacteristic;
    private static BluetoothGattDescriptor mCapSenseCccd;

    // Keep track of whether CapSense Notifications are on or off
    private static boolean CapSenseNotifyState = false;
    private static ImageView mCapsenseView;
    private static String mCapSenseValue = "-1"; // This is the No Touch value (0xFFFF)

    BLEBroadcastReceiver thisReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capled_activity);


        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        mConnectionState = findViewById(R.id.connection_state);
        mDataField = findViewById(R.id.data_value);
        led_switch = findViewById(R.id.led_switch);
        cap_switch = findViewById(R.id.capsense_switch);
        // Set up a variable to point to the CapSense value on the display
        mCapsenseView = findViewById(R.id.capsense_view);
        mCapsenseValue = findViewById(R.id.capsense_value);

        getActionBar().setTitle("");
        getActionBar().setDisplayHomeAsUpEnabled(true);



        /* This will be called when the LED On/Off switch is touched */
        led_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn the LED on or OFF based on the state of the switch
                writeLedCharacteristic(isChecked);
            }
        });

        /* This will be called when the CapSense Notify On/Off switch is touched */
        cap_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn CapSense Notifications on/off based on the state of the switch
                writeCapSenseNotification(isChecked);
                CapSenseNotifyState = isChecked;  // Keep track of CapSense notification state
                if(isChecked) { // Notifications are now on so text has to say "No Touch"
                    mCapsenseValue.setText(R.string.NoTouch);
                    mCapsenseView.setImageResource(R.drawable.capsense05);
                } else { // Notifications are now off so text has to say "Notify Off"
                    mCapsenseValue.setText(R.string.NotifyOff);
                    mCapsenseView.setImageResource(R.drawable.capsenseoff);
                }
            }
        });
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
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            //Log.d(TAG, "Connect request result=" + result);
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

    public void writeLedCharacteristic(boolean value) {
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = (byte) (1);
        } else {
            byteVal[0] = (byte) (0);
        }
        Log.i(TAG, "LED " + value);
        mLedSwitchState = value;
        mLedCharacteristic.setValue(byteVal);
        BluetoothLeService.genericWriteCharacteristic(mLedCharacteristic);
    }

    public void readLedCharacteristic() {
        if (BluetoothLeService.existBluetoothAdapter() == false ||
                BluetoothLeService.existBluetoothGatt() == false) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothLeService.genericReadCharacteristic(mLedCharacteristic);
    }

    /**
     * This method enables or disables notifications for the CapSense slider
     *
     * @param value Turns notifications on (1) or off (0)
     */
    public void writeCapSenseNotification(boolean value) {
        // Set notifications locally in the CCCD
        BluetoothLeService.mBluetoothGatt.setCharacteristicNotification(mCapsenseCharacteristic, value);

        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = 1;
        } else {
            byteVal[0] = 0;
        }
        // Write Notification value to the device
        Log.i(TAG, "CapSense Notification " + value);
        mCapSenseCccd.setValue(byteVal);
        BluetoothLeService.mBluetoothGatt.writeDescriptor(mCapSenseCccd);
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
        invalidateOptionsMenu();

    }

    @Override
    public void gattDisconnected() {
        mConnected = false;
        updateConnectionState(R.string.disconnected);
        mDataField.setText(R.string.no_data);
        invalidateOptionsMenu();
        led_switch.setChecked(false);
        led_switch.setEnabled(false);
        cap_switch.setChecked(false);
        cap_switch.setEnabled(false);
    }

    @Override
    public void gattServicesDiscovered() {
        //Toast.makeText(this, "gattServicesDiscovered...", Toast.LENGTH_SHORT).show();
        BluetoothGattService mService = BluetoothLeService.mBluetoothGatt.getService(CapLEDConstants.CAPLED_SERVICE_UUID);

        mLedCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_LED_CHARACTERISTIC_UUID);
        mCapsenseCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_CAP_CHARACTERISTIC_UUID);

        /* Get the CapSense CCCD */
        mCapSenseCccd = mCapsenseCharacteristic.getDescriptor(CapLEDConstants.CccdUUID);

        readLedCharacteristic();

        led_switch.setEnabled(true);
        cap_switch.setEnabled(true);

    }

    @Override
    public void dataAvailable(Intent intent) {
        if(mLedSwitchState){
            led_switch.setChecked(true);
        } else {
            led_switch.setChecked(false);
        }

        String uuid = BluetoothLeService.getmCharacteristicToPass().getUuid().toString();
        if(uuid.equals(CapLEDConstants.CAPLED_CAP_CHARACTERISTIC_UUID.toString()))
        {
            mCapSenseValue = BluetoothLeService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16,0).toString();
        }

        // Get CapSense Slider Value
        String CapSensePos = mCapSenseValue;
        int capSensePosition = Integer.parseInt(mCapSenseValue);
        if (CapSensePos.equals("-1")) {  // No Touch returns 0xFFFF which is -1
            if(!CapSenseNotifyState) { // Notifications are off
                //TODO: CLEAN UP
                mCapsenseValue.setText(R.string.NotifyOff);
                mCapsenseView.setImageResource(R.drawable.capsenseoff);
            } else { // Notifications are on but there is no finger on the slider
                mCapsenseValue.setText(R.string.NoTouch);
                mCapsenseView.setImageResource(R.drawable.capsense05);
            }
        } else { // Valid CapSense value is returned
            //TODO: CLEAN UP
            if(capSensePosition>=0 && capSensePosition<20)
                mCapsenseView.setImageResource(R.drawable.capsense15);
            else if(capSensePosition>=20 && capSensePosition<40)
                mCapsenseView.setImageResource(R.drawable.capsense25);
            else if(capSensePosition>=40 && capSensePosition<60)
                mCapsenseView.setImageResource(R.drawable.capsense35);
            else if(capSensePosition>=60 && capSensePosition<80)
                mCapsenseView.setImageResource(R.drawable.capsense45);
            else if(capSensePosition>=80)
                mCapsenseView.setImageResource(R.drawable.capsense55);
            mCapsenseValue.setText(CapSensePos);
        }
    }
}

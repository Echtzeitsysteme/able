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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

/**
 * This activity is started, if its registered in the ServiceRegistry with a matching UUID.
 * If started this activity can be used to controll the LED and read the CapSense of the Cypress® Cypress® CY8CKIT 042 BLE A.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.1
 */

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

    private static BluetoothGatt mBluetoothGatt;
    private static Switch led_switch;
    private static Switch cap_switch;
    private static boolean mLedSwitchState = false;
    public static BluetoothGattCharacteristic mLedCharacteristic;

    private static BluetoothGattCharacteristic mCapsenseCharacteristic;
    private static BluetoothGattDescriptor mCapSenseCccd;

    private static boolean CapSenseNotifyState = false;
    private static ImageView mCapsenseView;
    private static String mCapSenseValue = "-1"; // This is the No Touch value (0xFFFF)
    private static Button connectButton;


    BLEBroadcastReceiver thisReceiver;

    /**
     * This a the GUI button listener.
     */
    private View.OnClickListener buttonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.capledConnect:
                    setScanButton();
                    break;
                case android.R.id.home:
                    mBluetoothLeService.disconnect();
                    onBackPressed();
                    break;
            }

        }
    };

    /**
     * Initializes activity and GUI objects.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capled_activity);


        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        mConnectionState = findViewById(R.id.connection_state);
        mDataField = findViewById(R.id.data_value);
        led_switch = findViewById(R.id.led_switch);
        cap_switch = findViewById(R.id.capsense_switch);
        mCapsenseView = findViewById(R.id.capsense_view);

        getActionBar().setTitle("");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        connectButton = new Button(this);
        connectButton = findViewById(R.id.capledConnect);
        connectButton.setOnClickListener(buttonListener);

        /**
         *  This will be called when the LED On/Off switch is touched
         */
        led_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeLedCharacteristic(isChecked);
            }
        });

        /**
         * This will be called when the CapSense Notify On/Off switch is touched
         */
        cap_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeCapSenseNotification(isChecked);
                CapSenseNotifyState = isChecked;
                if(isChecked) {
                    mCapsenseView.setImageResource(R.drawable.capsense05);
                } else {
                    mCapsenseView.setImageResource(R.drawable.capsenseoff);
                }
            }
        });
        connectButton.setBackgroundColor(Color.rgb(42,42,42));
    }

    /**
     * Called after onCreate(..) and initializes BLE variables.
     */
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
        }
        setScanButton();
    }

    /**
     * Called if the application is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(thisReceiver);
    }

    /**
     * Called if the application is closed.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mBluetoothLeService = null;
    }


    /**
     * Sets the LED characteristic to the boolean value.
     * @param value if 1 then LED is on and if 0 LED is off.
     */
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

    /**
     * Reads if the LED is on or off.
     */
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
        BluetoothLeService.mBluetoothGatt.setCharacteristicNotification(mCapsenseCharacteristic, value);
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = 1;
        } else {
            byteVal[0] = 0;
        }
        Log.i(TAG, "CapSense Notification " + value);
        mCapSenseCccd.setValue(byteVal);
        BluetoothLeService.mBluetoothGatt.writeDescriptor(mCapSenseCccd);
    }

    /**
     * Updates a GUI TextView object for the connection state.
     * @param resourceId
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
        mDataField.setText(R.string.no_data);
        invalidateOptionsMenu();
        led_switch.setChecked(false);
        led_switch.setEnabled(false);
        cap_switch.setChecked(false);
        cap_switch.setEnabled(false);
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        BluetoothGattService mService = BluetoothLeService.mBluetoothGatt.getService(CapLEDConstants.CAPLED_SERVICE_UUID);

        mLedCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_LED_CHARACTERISTIC_UUID);
        mCapsenseCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_CAP_CHARACTERISTIC_UUID);
        mCapSenseCccd = mCapsenseCharacteristic.getDescriptor(CapLEDConstants.CccdUUID);

        readLedCharacteristic();

        led_switch.setEnabled(true);
        cap_switch.setEnabled(true);
    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     * @param intent
     */
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

        int capSensePosition = Integer.parseInt(mCapSenseValue);
        if (mCapSenseValue.equals("-1")) {
            if(!CapSenseNotifyState) {
                mCapsenseView.setImageResource(R.drawable.capsenseoff);
            } else {
                mCapsenseView.setImageResource(R.drawable.capsense05);
            }
        } else {
            setCapSenseView(capSensePosition);
        }
    }

    /**
     * Sets the CapSense GUI picture.
     */
    public void setCapSenseView(int capSensePosition){
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
    }

    /**
     * Sets the GUI ScanButton.
     */
    void setScanButton(){
        if(!mConnected) {
            if (mBluetoothLeService == null) {
                //TODO: CHANGED
                Toast.makeText(this, "this should not happen, as this object is static", Toast.LENGTH_SHORT).show();
            }
            mBluetoothLeService.connect(mDeviceAddress);
            connectButton.setText(R.string.menu_disconnect);
            connectButton.setBackgroundColor(Color.rgb(237,34,34));
        }
        else if(mConnected) {
            mBluetoothLeService.disconnect();
            connectButton.setText(R.string.menu_connect);
            connectButton.setBackgroundColor(Color.rgb(42,42,42));
        }
    }
}

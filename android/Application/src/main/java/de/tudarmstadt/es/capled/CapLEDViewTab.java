/*
        CapLEDAcitivity Class for the example application of an Bluetooth IoT device with a capacitive sensor and a LED.
 */
package de.tudarmstadt.es.capled;

/*
        IMPORTS
 */
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import de.tudarmstadt.es.able.BluetoothLeService;
import de.tudarmstadt.es.able.BLEBroadcastReceiver;
import de.tudarmstadt.es.able.DeviceScanActivity;
import de.tudarmstadt.es.able.R;
import de.tudarmstadt.es.able.BLEServiceListener;

import android.view.LayoutInflater;
import android.view.ViewGroup;
/**
 * This activity is started, if its registered in the ServiceRegistry with a matching UUID.
 * If started this activity can be used to controll the LED and read the CapSense of the Cypress® Cypress® CY8CKIT 042 BLE A.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.2
 */

public class CapLEDViewTab extends Fragment implements BLEServiceListener {
    private final static String TAG = CapLEDViewTab.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private boolean mConnected = false;
    private String mDeviceAddress;

    private BluetoothLeService mAbleBLEService;
    private static BluetoothGatt mBluetoothGatt;
    public static BluetoothGattCharacteristic mLedCharacteristic;
    public static BluetoothGattCharacteristic mGreenLedCharacteristic;

    private static BluetoothGattCharacteristic mCapsenseCharacteristic;
    private static BluetoothGattDescriptor mCapsenseNotification;

    private static Switch led_switch;
    private static Switch green_led_switch;
    private static boolean mLedSwitchState = false;
    private static boolean mGreenLedSwitchState = false;

    private static String mCapSenseValue = "-1"; // This is the No Touch value (0xFFFF)
    private static ProgressBar capSenseProgressBar;
    private static TextView capSenseDataView;


    BLEBroadcastReceiver thisReceiver;

    public static CapLEDViewTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("mDeviceName", mDeviceName);
        args.putString("mDeviceAddress", mDeviceAddress);
        CapLEDViewTab fragment = new CapLEDViewTab();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes activity and GUI objects.
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.capled_tab_view, container, false);
        Activity act = getActivity();
        mDeviceAddress = getArguments().getString("mDeviceAddress");

        led_switch = rootView.findViewById(R.id.led_switch);
        green_led_switch = rootView.findViewById(R.id.green_led_switch);
        capSenseProgressBar = rootView.findViewById(R.id.capledProgressBar);
        capSenseDataView = rootView.findViewById(R.id.capSenseValue);
        capSenseProgressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0, 159, 227)));
        capSenseProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(19, 77, 101)));
        capSenseProgressBar.setProgress(0);


        /**
         *  This will be called when the LED On/Off switch is touched
         */
        led_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeLedCharacteristic(isChecked);
            }
        });

        green_led_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeGreenLedCharacteristic(isChecked);
            }
        });
        return rootView;
    }


    /**
     * Called after onCreate(..) and initializes BLE variables.
     */
    @Override
    public void onResume() {
        super.onResume();

        thisReceiver = new BLEBroadcastReceiver(this);
        getActivity().registerReceiver(thisReceiver,
                thisReceiver.makeGattUpdateIntentFilter());
        mAbleBLEService = DeviceScanActivity.getmBluetoothLeService();

        if (mAbleBLEService != null) {
            final boolean result = mAbleBLEService.connect(mDeviceAddress);
        }
    }

    /**
     * Called if the application is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(thisReceiver);
    }

    /**
     * Called if the application is closed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAbleBLEService = null;
    }


    /**
     * Sets the LED characteristic to the boolean value.
     *
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

    public void writeGreenLedCharacteristic(boolean value) {
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = (byte) (1);
        } else {
            byteVal[0] = (byte) (0);
        }
        Log.i(TAG, "LED " + value);
        mGreenLedSwitchState = value;
        mGreenLedCharacteristic.setValue(byteVal);
        BluetoothLeService.genericWriteCharacteristic(mGreenLedCharacteristic);
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
        mCapsenseNotification.setValue(byteVal);
        BluetoothLeService.mBluetoothGatt.writeDescriptor(mCapsenseNotification);
    }

    /**
     * Called when GATT connection starts.
     */
    @Override
    public void gattConnected() {
        mConnected = true;
        getActivity().invalidateOptionsMenu();
    }

    /**
     * Called when GATT connection ends.
     */
    @Override
    public void gattDisconnected() {
        mConnected = false;
        getActivity().invalidateOptionsMenu();
        led_switch.setChecked(false);
        led_switch.setEnabled(false);
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        BluetoothGattService mService = BluetoothLeService.mBluetoothGatt.getService(CapLEDConstants.CAPLED_SERVICE_UUID);

        mLedCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_LED_CHARACTERISTIC_UUID);
        mGreenLedCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_GREEN_LED_CHARACTERISTIC_UUID);
        mCapsenseCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_CAP_CHARACTERISTIC_UUID);
        mCapsenseNotification = mCapsenseCharacteristic.getDescriptor(CapLEDConstants.CAPLED_CAP_NOTIFICATION);

        readLedCharacteristic();

        led_switch.setEnabled(true);
    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     *
     * @param intent
     */
    @Override
    public void dataAvailable(Intent intent) {
        if (mLedSwitchState) {
            led_switch.setChecked(true);
        } else {
            led_switch.setChecked(false);
        }
        if(mGreenLedSwitchState)
            green_led_switch.setChecked(true);
        else
            green_led_switch.setChecked(false);

        String uuid = BluetoothLeService.getmCharacteristicToPass().getUuid().toString();
        if (uuid.equals(CapLEDConstants.CAPLED_CAP_CHARACTERISTIC_UUID.toString())) {
            mCapSenseValue = BluetoothLeService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0).toString();
        }

        int capSensePosition = Integer.parseInt(mCapSenseValue);

        setCapSenseView(capSensePosition);
    }

    /**
     * Sets the CapSense GUI picture.
     */
    public void setCapSenseView(int capSensePosition) {
        if (mCapSenseValue.equals("-1")) {
            capSenseProgressBar.setProgress(0);
            capSenseDataView.setText(R.string.no_data);
        } else {
            capSenseProgressBar.setProgress(capSensePosition);
            capSenseDataView.setText(mCapSenseValue);
        }
    }
}
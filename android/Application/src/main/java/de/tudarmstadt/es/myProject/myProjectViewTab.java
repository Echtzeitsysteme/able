/*
        CapLEDAcitivity Class for the example application of an Bluetooth IoT device with a capacitive sensor and a LED.
 */
package de.tudarmstadt.es.myProject;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import de.tudarmstadt.es.able.BLEBroadcastReceiver;
import de.tudarmstadt.es.able.BLEServiceListener;
import de.tudarmstadt.es.able.BluetoothLeService;
import de.tudarmstadt.es.able.DeviceScanActivity;
import de.tudarmstadt.es.able.R;

/**
 * This activity is started, if its registered in the ServiceRegistry with a matching UUID.
 * If started this activity can be used to controll the LED and read the CapSense of the Cypress® Cypress® CY8CKIT 042 BLE A.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.2
 */

public class myProjectViewTab extends Fragment implements BLEServiceListener {
    private final static String TAG = myProjectViewTab.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private boolean mConnected = false;
    private String mDeviceAddress;

    private BluetoothLeService mAbleBLEService;
    public static BluetoothGattCharacteristic mLedCharacteristic;
    public static BluetoothGattCharacteristic mGreenLedCharacteristic;
    private static BluetoothGattCharacteristic mCapsenseCharacteristic;
    private static BluetoothGattDescriptor mCapsenseNotification;

    // TODO: Declare your GUI elements here ...
    /*
    private static Switch led_switch;
    private static Switch green_led_switch;
    private static boolean mLedSwitchState = false;
    private static boolean mGreenLedSwitchState = false;

    private static String mCapSenseValue = "-1"; // This is the No Touch value (0xFFFF)
    private static ProgressBar capSenseProgressBar;
    private static TextView capSenseDataView;
    */

    BLEBroadcastReceiver thisReceiver;

    public static myProjectViewTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("mDeviceName", mDeviceName);
        args.putString("mDeviceAddress", mDeviceAddress);
        myProjectViewTab fragment = new myProjectViewTab();
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

        View rootView = inflater.inflate(R.layout.myproject_tab_view, container, false);
        Activity act = getActivity();
        mDeviceAddress = getArguments().getString("mDeviceAddress");

        //TODO: Initialize your UI elements here ...

        /*
        led_switch = rootView.findViewById(R.id.myProject_led_switch);
        green_led_switch = rootView.findViewById(R.id.myProject_green_led_switch);
        capSenseProgressBar = rootView.findViewById(R.id.myProject_capledProgressBar);
        capSenseDataView = rootView.findViewById(R.id.myProject_capSenseValue);
        capSenseProgressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0, 159, 227)));
        capSenseProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(19, 77, 101)));
        capSenseProgressBar.setProgress(0);



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
        */
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
        /*
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
        */
    }

    public void writeGreenLedCharacteristic(boolean value) {
        /*
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
        */
    }

    /**
     * Reads if the LED is on or off.
     */
    public void readLedCharacteristic() {
        /*
        if (BluetoothLeService.existBluetoothAdapter() == false ||
                BluetoothLeService.existBluetoothGatt() == false) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothLeService.genericReadCharacteristic(mLedCharacteristic);
        */
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
        // TODO: Activate this optionally
        /*
        led_switch.setChecked(false);
        led_switch.setEnabled(false);
        */
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        // TODO: Initialize your UUIDS here ...
        /*
        BluetoothGattService mService = BluetoothLeService.mBluetoothGatt.getService(myProjectConstants.MYPROJECT_SERVICE_UUID);

        mLedCharacteristic = mService.getCharacteristic(myProjectConstants.MYPROJECT_LED_CHARACTERISTIC_UUID);
        mGreenLedCharacteristic = mService.getCharacteristic(myProjectConstants.MYPROJECT_GREEN_LED_CHARACTERISTIC_UUID);
        mCapsenseCharacteristic = mService.getCharacteristic(myProjectConstants.MYPROJECT_CAP_CHARACTERISTIC_UUID);
        mCapsenseNotification = mCapsenseCharacteristic.getDescriptor(myProjectConstants.MYPROJECT_NOTIFICATION);

        readLedCharacteristic();

        led_switch.setEnabled(true);
        */
    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     *
     * @param intent
     */
    @Override
    public void dataAvailable(Intent intent) {
        //TODO CUSTOM ABLE PROJECT: Get BLE data here ...
        /*
        // TODO(ME): check this part ...
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

        if (uuid.equals(myProjectConstants.MYPROJECT_CAP_CHARACTERISTIC_UUID.toString())) {
            mCapSenseValue = BluetoothLeService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0).toString();
            int capSensePosition = Integer.parseInt(mCapSenseValue);
            setCapSenseView(capSensePosition);
        }
        // TODO CUSTOM ABLE PROJECT:: Check here for more UUIDs
        */
    }

    /**
     * Sets the CapSense GUI picture.
     */
    public void setCapSenseView(int capSensePosition) {
        /*
        if (mCapSenseValue.equals("-1")) {
            capSenseProgressBar.setProgress(0);
            capSenseDataView.setText(R.string.no_data);
        } else {
            capSenseProgressBar.setProgress(capSensePosition);
            capSenseDataView.setText(mCapSenseValue);
        }
        */
    }
}
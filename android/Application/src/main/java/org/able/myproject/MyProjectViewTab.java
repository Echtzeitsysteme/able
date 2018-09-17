/*
        CapLEDAcitivity Class for the example application of an Bluetooth IoT device with a capacitive sensor and a LED.
 */
package org.able.myproject;

/*
        IMPORTS
 */

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.able.core.BLEBroadcastReceiver;
import org.able.core.BLEServiceListener;
import org.able.core.BluetoothLeService;
import org.able.core.AbleDeviceScanActivity;
import org.able.core.R;

/**
 * This activity is started, if its registered in the ServiceRegistry with a matching UUID.
 * If started this activity can be used to controll the LED and read the CapSense of the Cypress® Cypress® CY8CKIT 042 BLE A.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.2
 */

public class MyProjectViewTab extends Fragment implements BLEServiceListener {
    private final static String TAG = MyProjectViewTab.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private boolean mConnected = false;
    private String mDeviceAddress;

    private BluetoothLeService mAbleBLEService;
    public static BluetoothGattCharacteristic mLedCharacteristic;
    public static BluetoothGattCharacteristic mGreenLedCharacteristic;
    private static BluetoothGattCharacteristic mCapsenseCharacteristic;
    private static BluetoothGattDescriptor mCapsenseNotification;

    // TODO CUSTOM ABLE PROJECT: Declare your GUI elements here ...
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

    public static MyProjectViewTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("mDeviceName", mDeviceName);
        args.putString("mDeviceAddress", mDeviceAddress);
        MyProjectViewTab fragment = new MyProjectViewTab();
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

        //TODO CUSTOM ABLE PROJECT: Initialize your UI elements here ...

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
        mAbleBLEService = AbleDeviceScanActivity.getmBluetoothLeService();

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
        // TODO CUSTOM ABLE PROJECT: Activate this optionally
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
        // TODO CUSTOM ABLE PROJECT: Initialize your UUIDS here ...
        /*
        BluetoothGattService mService = BluetoothLeService.mBluetoothGatt.getService(MyProjectConstants.MYPROJECT_SERVICE_UUID);

        mLedCharacteristic = mService.getCharacteristic(MyProjectConstants.MYPROJECT_LED_CHARACTERISTIC_UUID);
        mGreenLedCharacteristic = mService.getCharacteristic(MyProjectConstants.MYPROJECT_GREEN_LED_CHARACTERISTIC_UUID);
        mCapsenseCharacteristic = mService.getCharacteristic(MyProjectConstants.MYPROJECT_CAP_CHARACTERISTIC_UUID);
        mCapsenseNotification = mCapsenseCharacteristic.getDescriptor(MyProjectConstants.MYPROJECT_NOTIFICATION);

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

        if (uuid.equals(MyProjectConstants.MYPROJECT_CAP_CHARACTERISTIC_UUID.toString())) {
            mCapSenseValue = BluetoothLeService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0).toString();
            int capSensePosition = Integer.parseInt(mCapSenseValue);
            setCapSenseView(capSensePosition);
        }
        // TODO CUSTOM ABLE PROJECT: Check here for more UUIDs
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
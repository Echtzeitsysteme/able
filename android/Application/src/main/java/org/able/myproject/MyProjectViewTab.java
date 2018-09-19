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
import android.widget.Switch;

import org.able.core.BLEBroadcastReceiver;
import org.able.core.BLEServiceListener;
import org.able.core.BLEService;
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

    private BLEService mAbleBLEService;
    private static BluetoothGattCharacteristic sRedLedCharacteristic;
    private static BluetoothGattCharacteristic sGreenLedCharacteristic;
    private static BluetoothGattCharacteristic sCapsenseCharacteristic;
    private static BluetoothGattDescriptor sCapsenseNotification;

    // TODO CUSTOM ABLE PROJECT: Declare your GUI elements here ...
    /*
    private static Switch sRedLedSwitch;
    private static Switch sGreenLedSwitch;
    private static boolean sRedLedSwitchState = false;
    private static boolean sGreenLedSwitchState = false;

    private static String sCapSenseValue = "-1"; // This is the No Touch value (0xFFFF)
    private static ProgressBar sCapSenseProgressBar;
    private static TextView sCapSenseDataView;
    */

    BLEBroadcastReceiver thisReceiver;

    /**
     * Construction of the Tab witch parameters of the parent FragmentActivity-
     * @param mDeviceName name of the BLE device
     * @param mDeviceAddress mac adress of the BLE device
     * @return Fragment object of the intialized Tab
     */
    public static MyProjectViewTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("sDeviceName", mDeviceName);
        args.putString("sDeviceAddress", mDeviceAddress);
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
        mDeviceAddress = getArguments().getString("sDeviceAddress");

        //TODO CUSTOM ABLE PROJECT: Initialize your UI elements here ...

        /*
        sRedLedSwitch = rootView.findViewById(R.id.myProject_led_switch);
        sGreenLedSwitch = rootView.findViewById(R.id.myProject_green_led_switch);
        sCapSenseProgressBar = rootView.findViewById(R.id.myProject_capledProgressBar);
        sCapSenseDataView = rootView.findViewById(R.id.myProject_capSenseValue);
        sCapSenseProgressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0, 159, 227)));
        sCapSenseProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(19, 77, 101)));
        sCapSenseProgressBar.setProgress(0);



        sRedLedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeLedCharacteristic(isChecked);
            }
        });

        sGreenLedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        //TODO CUSTOM ABLE PROJECT: BLE write the redLed on
        /*
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = (byte) (1);
        } else {
            byteVal[0] = (byte) (0);
        }
        Log.i(TAG, "LED " + value);
        sRedLedSwitchState = value;
        sRedLedCharacteristic.setValue(byteVal);
        BluetoothLeService.genericWriteCharacteristic(sRedLedCharacteristic);
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
        sGreenLedSwitchState = value;
        sGreenLedCharacteristic.setValue(byteVal);
        BluetoothLeService.genericWriteCharacteristic(sGreenLedCharacteristic);
        */
    }

    /**
     * Reads if the LED is on or off.
     */
    public void readLedCharacteristic() {
        //TODO CUSTOM ABLE PROJECT: BLE read
        /*
        if (BluetoothLeService.existBluetoothAdapter() == false ||
                BluetoothLeService.existBluetoothGatt() == false) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothLeService.genericReadCharacteristic(sRedLedCharacteristic);
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
        sRedLedSwitch.setChecked(false);
        sRedLedSwitch.setEnabled(false);
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

        sRedLedCharacteristic = mService.getCharacteristic(MyProjectConstants.MYPROJECT_LED_CHARACTERISTIC_UUID);
        sGreenLedCharacteristic = mService.getCharacteristic(MyProjectConstants.MYPROJECT_GREEN_LED_CHARACTERISTIC_UUID);
        sCapsenseCharacteristic = mService.getCharacteristic(MyProjectConstants.MYPROJECT_CAP_CHARACTERISTIC_UUID);
        sCapsenseNotification = sCapsenseCharacteristic.getDescriptor(MyProjectConstants.MYPROJECT_NOTIFICATION);

        readLedCharacteristic();

        sRedLedSwitch.setEnabled(true);
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
        if (sRedLedSwitchState) {
            sRedLedSwitch.setChecked(true);
        } else {
            sRedLedSwitch.setChecked(false);
        }
        if(sGreenLedSwitchState)
            sGreenLedSwitch.setChecked(true);
        else
            sGreenLedSwitch.setChecked(false);

        String uuid = BluetoothLeService.getmCharacteristicToPass().getUuid().toString();

        if (uuid.equals(MyProjectConstants.MYPROJECT_CAP_CHARACTERISTIC_UUID.toString())) {
            sCapSenseValue = BluetoothLeService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0).toString();
            int capSensePosition = Integer.parseInt(sCapSenseValue);
            setCapSenseView(capSensePosition);
        }
        // TODO CUSTOM ABLE PROJECT: Check here for more UUIDs
        */
    }

    /**
     * Sets the CapSense GUI picture.
     */
    public void setCapSenseView(int capSensePosition) {
        //TODO CUSTOM ABLE PROJECT: Change GUI for the CapSense progress bar here ...
        /*
        if (sCapSenseValue.equals("-1")) {
            sCapSenseProgressBar.setProgress(0);
            sCapSenseDataView.setText(R.string.no_data);
        } else {
            sCapSenseProgressBar.setProgress(capSensePosition);
            sCapSenseDataView.setText(sCapSenseValue);
        }
        */
    }
}
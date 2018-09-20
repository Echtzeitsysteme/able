package org.able.cppp;

import android.app.Activity;
import android.app.Fragment;
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

import org.able.core.AbleDeviceScanActivity;
import org.able.core.BLEService;
import org.able.core.BLEBroadcastReceiver;
import org.able.core.R;
import org.able.core.BLEServiceListener;

import android.view.LayoutInflater;
import android.view.ViewGroup;


public class CPPPViewTab extends Fragment implements BLEServiceListener  {
    private final static String TAG = CPPPViewTab.class.getSimpleName();


    private boolean mConnected = false;
    private String mDeviceAddress;
    private BLEService mAbleBLEService;
    BLEBroadcastReceiver thisReceiver;

    private static BluetoothGattCharacteristic sRedLedCharacteristic;
    private static BluetoothGattCharacteristic sJoystick1Characteristic;
    private static BluetoothGattDescriptor sJoystickNotification;

    private static Switch sRedLedSwitch;
    private static boolean sRedLedSwitchState = false;

    private static TextView sJoystick1XTextView;
    private static String sJoystick1XValue = "-1";
    private static Switch sNotifySwitch;
    private static ProgressBar sSensorDataProgressBar;
    private static boolean sNotifyState = false;

    /**
     * Construction of the Tab witch parameters of the parent FragmentActivity-
     * @param mDeviceName name of the BLE device
     * @param mDeviceAddress mac adress of the BLE device
     * @return Fragment object of the intialized Tab
     */
    public static CPPPViewTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("sDeviceName", mDeviceName);
        args.putString("sDeviceAddress", mDeviceAddress);
        CPPPViewTab fragment = new CPPPViewTab();
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

        View rootView = inflater.inflate(R.layout.cppp_tab_view, container, false);
        Activity act = getActivity();
        mDeviceAddress = getArguments().getString("sDeviceAddress");
        sJoystick1XTextView = rootView.findViewById(R.id.CPPP_JOYSTICK_1_X_VALUE);
        sRedLedSwitch = rootView.findViewById(R.id.cpppRedLedSwitch);
        sRedLedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeRedLedCharacteristic(isChecked);
            }
        });
        sNotifySwitch = rootView.findViewById(R.id.cpppNotifySwitch);
        sNotifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeNotification(isChecked);
                sNotifyState = isChecked;
            }
        });
        sSensorDataProgressBar = rootView.findViewById(R.id.cpppProgressBar);
        sSensorDataProgressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0, 159, 227)));
        sSensorDataProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(19, 77, 101)));
        sSensorDataProgressBar.setProgress(0);

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

    @Override
    public void gattConnected() {
        mConnected = true;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void gattDisconnected() {
        mConnected = false;
        getActivity().invalidateOptionsMenu();
        //sSensorDataProgressBar.setEnabled(false);
        sRedLedSwitch.setChecked(false);
        sRedLedSwitch.setEnabled(false);
    }

    @Override
    public void gattServicesDiscovered() {
        BluetoothGattService mService = BLEService.mBluetoothGatt.getService(CPPPConstants.CPPP_SERVICE_UUID);
        sRedLedCharacteristic = mService.getCharacteristic(CPPPConstants.CPPP_RED_LED_CHARACTERISTIC_UUID);
        sJoystick1Characteristic = mService.getCharacteristic(CPPPConstants.CPPP_JOYSTICK_1_CHARACTERISTIC_UUID);
        sJoystickNotification = sJoystick1Characteristic.getDescriptor(CPPPConstants.CPPP_NOTIFICATION);
        sNotifySwitch.setEnabled(true);

        readJoystick1Characteristic();
        sRedLedSwitch.setEnabled(true);
        sSensorDataProgressBar.setEnabled(true);
    }

    @Override
    public void dataAvailable(Intent intent) {
        sJoystick1XValue = "-1";
        String uuid = BLEService.getmCharacteristicToPass().getUuid().toString();
        if (uuid.equals(CPPPConstants.CPPP_JOYSTICK_1_CHARACTERISTIC_UUID.toString())) {
            sJoystick1XValue = BLEService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString();
        }
        sJoystick1XTextView.setText(sJoystick1XValue);
        int sensorDataValue = Integer.parseInt(sJoystick1XValue);

        setSensorDataProgressBar(sensorDataValue);
    }

    public void writeNotification(boolean value) {
        BLEService.mBluetoothGatt.setCharacteristicNotification(sJoystick1Characteristic, value);
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = 1;
        } else {
            byteVal[0] = 0;
        }
        Log.i(TAG, "CapSense Notification " + value);
        sJoystickNotification.setValue(byteVal);
        BLEService.mBluetoothGatt.writeDescriptor(sJoystickNotification);
    }

    /**
     * Read position of Joystick 1
     */
    public void readJoystick1Characteristic() {
        if (BLEService.existBluetoothAdapter() == false ||
                BLEService.existBluetoothGatt() == false) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BLEService.genericReadCharacteristic(sJoystick1Characteristic);
    }

    /**
     * Sets the LED characteristic to the boolean value.
     *
     * @param value if 1 then LED is on and if 0 LED is off.
     */
    public void writeRedLedCharacteristic(boolean value) {
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = (byte) (1);
        } else {
            byteVal[0] = (byte) (0);
        }
        Log.i(TAG, "LED " + value);
        sRedLedSwitchState = value;
        sRedLedCharacteristic.setValue(byteVal);
        BLEService.genericWriteCharacteristic(sRedLedCharacteristic);
    }


    /**
     * Sets the CapSense GUI picture.
     */
    public void setSensorDataProgressBar(int capSensePosition) {
        if (sJoystick1XValue.equals("-1")) {
            sSensorDataProgressBar.setProgress(0);
            sJoystick1XTextView.setText(R.string.no_data);
        } else {
            sSensorDataProgressBar.setProgress(capSensePosition);
            sJoystick1XTextView.setText(sJoystick1XValue);
        }
    }
}

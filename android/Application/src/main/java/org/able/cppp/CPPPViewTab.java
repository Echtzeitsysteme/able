package org.able.cppp;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
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
    private BLEService mAbleBLEService;
    BLEBroadcastReceiver thisReceiver;

    private static BluetoothGattCharacteristic sJoystick1Characteristic;
    private static BluetoothGattDescriptor sJoystickNotification;

    private TextView sJoystick1XTextView;
    private Switch sNotifySwitch;
    private boolean sNotifyState = false;

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
        sJoystick1XTextView = rootView.findViewById(R.id.CPPP_JOYSTICK_1_X_VALUE);
        sNotifySwitch = rootView.findViewById(R.id.cpppNotifySwitch);

        sNotifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeNotification(isChecked);
                sNotifyState = isChecked;
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
    }

    @Override
    public void gattServicesDiscovered() {
        BluetoothGattService mService = BLEService.mBluetoothGatt.getService(CPPPConstants.CPPP_SERVICE_UUID);
        sJoystick1Characteristic = mService.getCharacteristic(CPPPConstants.CPPP_JOYSTICK_1_CHARACTERISTIC_UUID);
        sJoystickNotification = sJoystick1Characteristic.getDescriptor(CPPPConstants.CPPP_NOTIFICATION);
        sNotifySwitch.setEnabled(true);

        readJoystick1Characteristic();

    }

    @Override
    public void dataAvailable(Intent intent) {
        String sJoystick1XValue = "-1";
        String uuid = BLEService.getmCharacteristicToPass().getUuid().toString();
        if (uuid.equals(CPPPConstants.CPPP_JOYSTICK_1_CHARACTERISTIC_UUID.toString())) {
            sJoystick1XValue = BLEService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString();
        }
        sJoystick1XTextView.setText(sJoystick1XValue);
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

    public void readJoystick1Characteristic() {
        if (BLEService.existBluetoothAdapter() == false ||
                BLEService.existBluetoothGatt() == false) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BLEService.genericReadCharacteristic(sJoystick1Characteristic);
    }
}

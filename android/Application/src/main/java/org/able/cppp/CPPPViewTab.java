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
import org.able.core.BluetoothLeService;
import org.able.core.BLEBroadcastReceiver;
import org.able.core.R;
import org.able.core.BLEServiceListener;

import android.view.LayoutInflater;
import android.view.ViewGroup;


public class CPPPViewTab extends Fragment implements BLEServiceListener  {
    private final static String TAG = CPPPViewTab.class.getSimpleName();


    private boolean mConnected = false;
    private String mDeviceAddress;
    private BluetoothLeService mAbleBLEService;
    BLEBroadcastReceiver thisReceiver;

    public static BluetoothGattCharacteristic CPPP_JOYSTICK_1_CHARACTERISTIC;
    private static BluetoothGattDescriptor CPPP_JOYSTICK_NOTIFCATION;

    private static TextView CPPP_JOYSTICK_1_X_VALUE;
    private static String Joystick1XValue = "-1";
    private static Switch notify_switch;
    private static boolean notifyState = false;

    public static CPPPViewTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("mDeviceName", mDeviceName);
        args.putString("mDeviceAddress", mDeviceAddress);
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
        mDeviceAddress = getArguments().getString("mDeviceAddress");
        CPPP_JOYSTICK_1_X_VALUE = rootView.findViewById(R.id.CPPP_JOYSTICK_1_X_VALUE);
        notify_switch = rootView.findViewById(R.id.cpppNotifySwitch);

        notify_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeNotification(isChecked);
                notifyState = isChecked;
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
        BluetoothGattService mService = BluetoothLeService.mBluetoothGatt.getService(CPPPConstants.CPPP_SERVICE_UUID);
        CPPP_JOYSTICK_1_CHARACTERISTIC = mService.getCharacteristic(CPPPConstants.CPPP_JOYSTICK_1_CHARACTERISTIC_UUID);
        CPPP_JOYSTICK_NOTIFCATION = CPPP_JOYSTICK_1_CHARACTERISTIC.getDescriptor(CPPPConstants.CPPP_NOTIFICATION);
        notify_switch.setEnabled(true);

        readJoystick1Characteristic();

    }

    @Override
    public void dataAvailable(Intent intent) {
        Joystick1XValue = "-1";
        String uuid = BluetoothLeService.getmCharacteristicToPass().getUuid().toString();
        if (uuid.equals(CPPPConstants.CPPP_JOYSTICK_1_CHARACTERISTIC_UUID.toString())) {
            Joystick1XValue = BluetoothLeService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString();
        }
        CPPP_JOYSTICK_1_X_VALUE.setText(Joystick1XValue);
    }

    public void writeNotification(boolean value) {
        BluetoothLeService.mBluetoothGatt.setCharacteristicNotification(CPPP_JOYSTICK_1_CHARACTERISTIC, value);
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = 1;
        } else {
            byteVal[0] = 0;
        }
        Log.i(TAG, "CapSense Notification " + value);
        CPPP_JOYSTICK_NOTIFCATION.setValue(byteVal);
        BluetoothLeService.mBluetoothGatt.writeDescriptor(CPPP_JOYSTICK_NOTIFCATION);
    }

    public void readJoystick1Characteristic() {
        if (BluetoothLeService.existBluetoothAdapter() == false ||
                BluetoothLeService.existBluetoothGatt() == false) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothLeService.genericReadCharacteristic(CPPP_JOYSTICK_1_CHARACTERISTIC);
    }
}

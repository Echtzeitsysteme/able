package de.tudarmstadt.es.cppp;

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


public class CPPPViewTab extends Fragment implements BLEServiceListener  {
    private final static String TAG = CPPPViewTab.class.getSimpleName();


    private boolean mConnected = false;
    private String mDeviceAddress;
    private BluetoothLeService mAbleBLEService;
    BLEBroadcastReceiver thisReceiver;

    public static BluetoothGattCharacteristic CPPP_JOYSTICK_1_CHARACTERISTIC;
    private static TextView CPPP_JOYSTICK_1_X_VALUE;

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


    }

    @Override
    public void dataAvailable(Intent intent) {

    }
}

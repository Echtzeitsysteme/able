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
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
 * @version 1.1
 */

public class MyProjectSettingsTab extends Fragment implements BLEServiceListener {
    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceAddress;
    private boolean mConnected = false;

    private BLEService mAbleBLEService;

    public BluetoothGattCharacteristic mLedCharacteristic;
    private BluetoothGattCharacteristic mCapsenseCharacteristic;
    private BluetoothGattDescriptor mCapsenseNotification;

    // TODO CUSTOM ABLE PROJECT: Declare your GUI elements here ...
    /*
    private static Switch sCapSenseSwitch;
    private static boolean sCapSenseNotifyState = false;
    */

    private Button sConnectButton;


    private BLEBroadcastReceiver thisReceiver;

    /**
     * This a the GUI button listener.
     */
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.capledConnect:
                    setScanButton();
                    break;
                case android.R.id.home:
                    mAbleBLEService.disconnect();
                    getActivity().onBackPressed();
                    break;
            }

        }
    };

    /**
     * Construction of the Tab witch parameters of the parent FragmentActivity-
     * @param mDeviceName name of the BLE device
     * @param mDeviceAddress mac adress of the BLE device
     * @return Fragment object of the intialized Tab
     */
    public static MyProjectSettingsTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("sDeviceName", mDeviceName);
        args.putString("sDeviceAddress", mDeviceAddress);
        MyProjectSettingsTab fragment = new MyProjectSettingsTab();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes activity and GUI objects.
     *
     * @param savedInstanceState the instance state
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.myproject_tab_settings, container, false);
        Activity act = getActivity();
        mDeviceAddress = getArguments().getString("sDeviceAddress");

        ((TextView) rootView.findViewById(R.id.myProject_device_address)).setText(mDeviceAddress);

        mConnectionState = rootView.findViewById(R.id.myProject_connection_state);
        mDataField = rootView.findViewById(R.id.myProject_data_value);

        sConnectButton = new Button(act);
        sConnectButton = rootView.findViewById(R.id.myProject_capled_connect);
        sConnectButton.setOnClickListener(buttonListener);
        sConnectButton.setBackgroundColor(Color.rgb(42, 42, 42));

        // TODO CUSTOM ABLE PROJECT: Insert your Setting switches here ...

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
        setScanButton();
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
     * This method enables or disables notifications for the CapSense slider
     *
     * @param value Turns notifications on (1) or off (0)
     */
    public void writeCapSenseNotification(boolean value) {
        //TODO CUSTOM ABLE PROJECT: Activate/Deactivate Capsense notification here ...
    }

    /**
     * Updates a GUI TextView object for the connection state.
     *
     * @param resourceId the connection state ID
     */
    void updateConnectionState(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
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
        getActivity().invalidateOptionsMenu();
    }

    /**
     * Called when GATT connection ends.
     */
    @Override
    public void gattDisconnected() {
        mConnected = false;
        updateConnectionState(R.string.disconnected);
        mDataField.setText(R.string.no_data);
        getActivity().invalidateOptionsMenu();
        //TODO CUSTOM ABLE PROJECT: you can activate this part optionally

    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        //TODO CUSTOM ABLE PROJECT: Initialize your UUIDs here ...

    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     *
     * @param intent the intent
     */
    @Override
    public void dataAvailable(Intent intent) {
    }


    /**
     * Sets the GUI ScanButton.
     */
    private void setScanButton() {
        if (!mConnected) {
            if (mAbleBLEService == null) {
                Toast.makeText(getActivity(), "this should not happen, as this object is static", Toast.LENGTH_SHORT).show();
            }
            mAbleBLEService.connect(mDeviceAddress);
            sConnectButton.setText(R.string.menu_disconnect);
            sConnectButton.setBackgroundColor(Color.rgb(237, 34, 34));
        } else {
            mAbleBLEService.disconnect();
            sConnectButton.setText(R.string.menu_connect);
            sConnectButton.setBackgroundColor(Color.rgb(42, 42, 42));
        }
    }



}
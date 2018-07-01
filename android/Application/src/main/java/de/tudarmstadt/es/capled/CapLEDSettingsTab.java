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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
 * @version 1.1
 */

public class CapLEDSettingsTab extends Fragment implements BLEServiceListener {
    private final static String TAG = CapLEDSettingsTab.class.getSimpleName();

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;

    private BluetoothLeService mAbleBLEService;

    private static BluetoothGatt mBluetoothGatt;
    public static BluetoothGattCharacteristic mLedCharacteristic;
    private static BluetoothGattCharacteristic mCapsenseCharacteristic;
    private static BluetoothGattDescriptor mCapSenseCccd;

    private static Switch cap_switch;
    private static boolean CapSenseNotifyState = false;
    private static Button connectButton;


    BLEBroadcastReceiver thisReceiver;

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
    private int capled_tab_view;

    public static CapLEDSettingsTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("mDeviceName", mDeviceName);
        args.putString("mDeviceAddress", mDeviceAddress);
        CapLEDSettingsTab fragment = new CapLEDSettingsTab();
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

        View rootView = inflater.inflate(R.layout.capled_tab_settings, container, false);
        Activity act = getActivity();
        mDeviceName = getArguments().getString("mDeviceName");
        mDeviceAddress = getArguments().getString("mDeviceAddress");

        ((TextView) rootView.findViewById(R.id.device_address)).setText(mDeviceAddress);

        mConnectionState = rootView.findViewById(R.id.connection_state);
        mDataField = rootView.findViewById(R.id.data_value);
        cap_switch = rootView.findViewById(R.id.capsense_switch);
        connectButton = new Button(act);
        connectButton = rootView.findViewById(R.id.capledConnect);
        connectButton.setOnClickListener(buttonListener);

        /**
         * This will be called when the CapSense Notify On/Off switch is touched
         */
        cap_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeCapSenseNotification(isChecked);
                CapSenseNotifyState = isChecked;
            }
        });
        connectButton.setBackgroundColor(Color.rgb(42, 42, 42));
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
     *
     * @param resourceId
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
        cap_switch.setEnabled(true);
    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     *
     * @param intent
     */
    @Override
    public void dataAvailable(Intent intent) {
        String uuid = BluetoothLeService.getmCharacteristicToPass().getUuid().toString();
    }


    /**
     * Sets the GUI ScanButton.
     */
    void setScanButton() {
        if (!mConnected) {
            if (mAbleBLEService == null) {
                Toast.makeText(getActivity(), "this should not happen, as this object is static", Toast.LENGTH_SHORT).show();
            }
            mAbleBLEService.connect(mDeviceAddress);
            connectButton.setText(R.string.menu_disconnect);
            connectButton.setBackgroundColor(Color.rgb(237, 34, 34));
        } else if (mConnected) {
            mAbleBLEService.disconnect();
            connectButton.setText(R.string.menu_connect);
            connectButton.setBackgroundColor(Color.rgb(42, 42, 42));
        }
    }



}
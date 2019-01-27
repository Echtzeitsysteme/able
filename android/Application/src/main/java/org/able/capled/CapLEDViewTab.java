package org.able.capled;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.able.core.BLEService;
import org.able.core.BLEBroadcastReceiver;
import org.able.core.AbleDeviceScanActivity;
import org.able.core.R;
import org.able.core.BLEServiceListener;

import android.view.LayoutInflater;
import android.view.ViewGroup;
/**
 * This activity is started, if its registered in the ServiceRegistry with a matching UUID.
 * If started this activity can be used to control the LED and read the CapSense of the Cypress® Cypress® CY8CKIT 042 BLE A.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.2
 */

public class CapLEDViewTab extends Fragment implements BLEServiceListener {
    private final static String TAG = CapLEDViewTab.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private boolean mConnected = false;

    private BLEService mAbleBLEService;
    private static BluetoothGattCharacteristic sLedCharacteristic;
    private static BluetoothGattCharacteristic sGreenLedCharacteristic;

    private static BluetoothGattCharacteristic sCapsenseCharacteristic;
    private static BluetoothGattDescriptor sCapsenseNotification;

    private Switch sRedLedSwitch;
    private Switch sGreenLedSwitch;
    private static boolean sRedLedSwitchState = false;
    private static boolean sGreenLedSwitchState = false;

    private String mCapSenseValue = "-1"; // This is the No Touch value (0xFFFF)
    private ProgressBar sCapSenseProgressBar;
    private TextView sCapSenseDataView;

    /**
     * Construction of the Tab witch parameters of the parent FragmentActivity-
     * @param mDeviceName name of the BLE device
     * @param mDeviceAddress mac adress of the BLE device
     * @return Fragment object of the intialized Tab
     */
    public static CapLEDViewTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("sDeviceName", mDeviceName);
        args.putString("sDeviceAddress", mDeviceAddress);
        CapLEDViewTab fragment = new CapLEDViewTab();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes activity and GUI objects.
     *
     * @param savedInstanceState currently not used
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.capled_tab_view, container, false);
        Activity act = getActivity();

        sRedLedSwitch = rootView.findViewById(R.id.led_switch);
        sGreenLedSwitch = rootView.findViewById(R.id.green_led_switch);
        sCapSenseProgressBar = rootView.findViewById(R.id.capledProgressBar);
        sCapSenseDataView = rootView.findViewById(R.id.capSenseValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sCapSenseProgressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0, 159, 227)));
            sCapSenseProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(19, 77, 101)));
        }
        sCapSenseProgressBar.setProgress(0);


        /*
         *  This will be called when the LED On/Off switch is touched
         */
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
        return rootView;
    }


    /**
     * Called after onCreate(..) and initializes BLE variables.
     */
    @Override
    public void onResume() {
        super.onResume();
        mAbleBLEService = AbleDeviceScanActivity.getmBluetoothLeService();
    }

    /**
     * Called if the application is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
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
    private void writeLedCharacteristic(boolean value) {
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = (byte) (1);
        } else {
            byteVal[0] = (byte) (0);
        }
        Log.i(TAG, "LED " + value);
        sRedLedSwitchState = value;
        sLedCharacteristic.setValue(byteVal);
        BLEService.genericWriteCharacteristic(sLedCharacteristic);
    }

    private void writeGreenLedCharacteristic(boolean value) {
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = (byte) (1);
        } else {
            byteVal[0] = (byte) (0);
        }
        Log.i(TAG, "LED " + value);
        sGreenLedSwitchState = value;
        sGreenLedCharacteristic.setValue(byteVal);
        BLEService.genericWriteCharacteristic(sGreenLedCharacteristic);
    }

    /**
     * Reads if the LED is on or off.
     */
    private void readLedCharacteristic() {
        if (!BLEService.existBluetoothAdapter() ||
                !BLEService.existBluetoothGatt()) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BLEService.genericReadCharacteristic(sLedCharacteristic);
    }

    /**
     * This method enables or disables notifications for the CapSense slider
     *
     * @param value Turns notifications on (1) or off (0)
     */
    public void writeCapSenseNotification(boolean value) {
        BLEService.mBluetoothGatt.setCharacteristicNotification(sCapsenseCharacteristic, value);
        byte[] byteVal = new byte[1];
        if (value) {
            byteVal[0] = 1;
        } else {
            byteVal[0] = 0;
        }
        Log.i(TAG, "CapSense Notification " + value);
        sCapsenseNotification.setValue(byteVal);
        BLEService.mBluetoothGatt.writeDescriptor(sCapsenseNotification);
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
        sRedLedSwitch.setChecked(false);
        sRedLedSwitch.setEnabled(false);
        sGreenLedSwitch.setChecked(false);
        sGreenLedSwitch.setEnabled(false);
        sCapSenseProgressBar.setEnabled(false);
    }

    /**
     * Called when a GATT Service is discovered.
     */
    @Override
    public void gattServicesDiscovered() {
        BluetoothGattService mService = BLEService.mBluetoothGatt.getService(CapLEDConstants.CAPLED_SERVICE_UUID);

        sLedCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_LED_CHARACTERISTIC_UUID);
        sGreenLedCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_GREEN_LED_CHARACTERISTIC_UUID);
        sCapsenseCharacteristic = mService.getCharacteristic(CapLEDConstants.CAPLED_CAP_CHARACTERISTIC_UUID);
        sCapsenseNotification = sCapsenseCharacteristic.getDescriptor(CapLEDConstants.CAPLED_CAP_NOTIFICATION);

        readLedCharacteristic();

        sRedLedSwitch.setEnabled(true);
        sGreenLedSwitch.setEnabled(true);
        sCapSenseProgressBar.setEnabled(true);
    }

    /**
     * This method is called if data is available for the CapLED Service.
     * Then the LED switch button GUI is refreshed and the CapSense GUI View refreshed.
     *
     * @param intent currently not used
     */
    @Override
    public void dataAvailable(Intent intent) {
        if (sRedLedSwitchState) {
            sRedLedSwitch.setChecked(true);
        } else {
            sRedLedSwitch.setChecked(false);
        }
        if(sGreenLedSwitchState)
            sGreenLedSwitch.setChecked(true);
        else
            sGreenLedSwitch.setChecked(false);

        String uuid = BLEService.getmCharacteristicToPass().getUuid().toString();
        if (uuid.equals(CapLEDConstants.CAPLED_CAP_CHARACTERISTIC_UUID.toString())) {
            mCapSenseValue = BLEService.getmCharacteristicToPass().getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0).toString();
        }

        int capSensePosition = Integer.parseInt(mCapSenseValue);

        setCapSenseView(capSensePosition);
    }

    /**
     * Sets the CapSense GUI picture.
     */
    private void setCapSenseView(int capSensePosition) {
        if (mCapSenseValue.equals("-1")) {
            sCapSenseProgressBar.setProgress(0);
            sCapSenseDataView.setText(R.string.no_data);
        } else {
            sCapSenseProgressBar.setProgress(capSensePosition);
            sCapSenseDataView.setText(mCapSenseValue);
        }
    }
}
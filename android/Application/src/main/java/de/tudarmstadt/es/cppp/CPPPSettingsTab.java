package de.tudarmstadt.es.cppp;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import de.tudarmstadt.es.able.BLEBroadcastReceiver;
import de.tudarmstadt.es.able.BLEServiceListener;
import de.tudarmstadt.es.able.BluetoothLeService;
import de.tudarmstadt.es.able.DeviceScanActivity;
import de.tudarmstadt.es.able.R;

public class CPPPSettingsTab extends Fragment implements BLEServiceListener {
    private final static String TAG = CPPPSettingsTab.class.getSimpleName();

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private BluetoothLeService mAbleBLEService;
    BLEBroadcastReceiver thisReceiver;

    private static Button connectButton;


    public static CPPPSettingsTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("mDeviceName", mDeviceName);
        args.putString("mDeviceAddress", mDeviceAddress);
        CPPPSettingsTab fragment = new CPPPSettingsTab();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * This a the GUI button listener.
     */
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cppp_connect:
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
     * Initializes activity and GUI objects.
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.cppp_tab_settings, container, false);
        Activity act = getActivity();
        mDeviceName = getArguments().getString("mDeviceName");
        mDeviceAddress = getArguments().getString("mDeviceAddress");

        ((TextView) rootView.findViewById(R.id.cppp_device_address)).setText(mDeviceAddress);

        mConnectionState = rootView.findViewById(R.id.cppp_connection_state);
        mDataField = rootView.findViewById(R.id.cppp_data_value);
        connectButton = new Button(act);
        connectButton = rootView.findViewById(R.id.cppp_connect);
        connectButton.setOnClickListener(buttonListener);

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
    @Override
    public void gattConnected() {
        mConnected = true;
        updateConnectionState(R.string.connected);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void gattDisconnected() {
        mConnected = false;
        updateConnectionState(R.string.disconnected);
        mDataField.setText(R.string.no_data);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void gattServicesDiscovered() {
        BluetoothGattService mService = BluetoothLeService.mBluetoothGatt.getService(CPPPConstants.CPPP_SERVICE_UUID);

    }

    @Override
    public void dataAvailable(Intent intent) {

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

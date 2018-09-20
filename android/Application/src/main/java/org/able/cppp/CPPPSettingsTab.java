package org.able.cppp;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothGattService;
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

public class CPPPSettingsTab extends Fragment implements BLEServiceListener {
    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private BLEService mAbleBLEService;
    private BLEBroadcastReceiver thisReceiver;

    private Button sConnectButton;

    /**
     * Construction of the Tab witch parameters of the parent FragmentActivity-
     * @param mDeviceName name of the BLE device
     * @param mDeviceAddress mac adress of the BLE device
     * @return Fragment object of the intialized Tab
     */
    public static CPPPSettingsTab newInstance(String mDeviceName, String mDeviceAddress) {
        Bundle args = new Bundle();
        args.putString("sDeviceName", mDeviceName);
        args.putString("sDeviceAddress", mDeviceAddress);
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
     * @param savedInstanceState instance state
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.cppp_tab_settings, container, false);
        final Activity act = getActivity();
        mDeviceAddress = getArguments().getString("sDeviceAddress");

        ((TextView) rootView.findViewById(R.id.cppp_device_address)).setText(mDeviceAddress);

        mConnectionState = rootView.findViewById(R.id.cppp_connection_state);
        sConnectButton = new Button(act);
        sConnectButton = rootView.findViewById(R.id.cppp_connect);
        sConnectButton.setOnClickListener(buttonListener);

        sConnectButton.setBackgroundColor(Color.rgb(42, 42, 42));
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
     * Updates a GUI TextView object for the connection state.
     *
     * @param resourceId
     */
    private void updateConnectionState(final int resourceId) {
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
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void gattServicesDiscovered() {
        BluetoothGattService mService = BLEService.mBluetoothGatt.getService(CPPPConstants.CPPP_SERVICE_UUID);

    }

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

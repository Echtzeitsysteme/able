package de.tudarmstadt.es.able;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.SimpleExpandableListAdapter;

import static android.support.v4.app.ActivityCompat.invalidateOptionsMenu;
import static de.tudarmstadt.es.able.DeviceControlActivity.*;

/**
 * Created by user on 23.02.18.
 */

public class BroadcastReceiverAndFilterDefinition extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverAndFil";

    private boolean mConnected = false;
    //private BluetoothLeService mBluetoothLeService;
    //private DeviceControlActivity fakeActivity = null;


    public BroadcastReceiverAndFilterDefinition(boolean mConnected) {
        this.mConnected = mConnected;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (isGattConnected(action))
        {
            mConnected = true;
            getInstaceOfDeviceControlActivity().updateConnectionState(R.string.connected);
            getInstaceOfDeviceControlActivity().invalidateOptionsMenu();
        }else

        if (isGattDisconnected(action))
        {
            mConnected = false;
            getInstaceOfDeviceControlActivity().updateConnectionState(R.string.disconnected);
            getInstaceOfDeviceControlActivity().invalidateOptionsMenu();
            getInstaceOfDeviceControlActivity().clearUI();
        } else

        if (hasDiscoveredGattservice(action))
        {
            // Show all the supported services and characteristics on the user interface.
            //if(mBluetoothLeService.getSupportedGattServices() == null){
            //    Log.d(TAG, "getSupportedGattServices: DUDE WE GOT NULL SERVICES ATTENTION");
            //}
            getInstaceOfDeviceControlActivity().displayGattServices(
                    getInstaceOfDeviceControlActivity().getmBluetoothLeService().getSupportedGattServices());
        } else

        if (availableData(action))
        {
            getInstaceOfDeviceControlActivity().displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
        }
    }

    /**
     * Documentation for
     * isGattConnected
     * isGattDisconnected
     * hasDiscoveredGassservice
     * availableData
     * methods created to make behaviour of broadcastreceiver more understandable/readable
     * @param action
     * @return
     */
    private boolean isGattConnected(String action)
    {
        return BluetoothLeService.ACTION_GATT_CONNECTED.equals(action);
    }

    private boolean isGattDisconnected(String action)
    {
        return BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action);
    }

    private boolean hasDiscoveredGattservice(String action)
    {
        return BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action);
    }

    private boolean availableData(String action)
    {
        return BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action);
    }

    /**
     * method to check changes using broadcastreceiver
     * @return filterobject which contains all "keyphrases" the broadcaster shall listen to
     */
    static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /*private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }*/


}

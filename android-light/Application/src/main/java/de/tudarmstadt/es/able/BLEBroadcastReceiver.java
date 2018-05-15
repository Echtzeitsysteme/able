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
 * This class sets up the filter and the BroadcastReceiver which uses the filter
 */

public class BLEBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverAndFil";

    private BLEServiceListener listener;

    public BLEBroadcastReceiver(BLEServiceListener listener) {
        this.listener = listener;
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
            listener.gattConnected();
        }else

        if (isGattDisconnected(action))
        {
            listener.gattDisconnected();
        } else

        if (hasDiscoveredGattservice(action))
        {
            listener.gattServicesDiscovered();
        } else

        if (availableData(action))
        {
            listener.dataAvailable(intent);
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
    public IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


}

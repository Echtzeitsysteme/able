package org.able.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * This class sets up the filter and the BroadcastReceiver which uses the filter.
 * These events are handled:
 * ACTION_GATT_CONNECTED: connected to a GATT server
 * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
 * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
 * ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */
public class BLEBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiverAndFil";

    private BLEServiceListener listener;

    public BLEBroadcastReceiver(BLEServiceListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (isGattConnected(action)) {
            listener.gattConnected();
        } else if (isGattDisconnected(action)) {
            listener.gattDisconnected();
        } else if (hasDiscoveredGattservice(action)) {
            listener.gattServicesDiscovered();
        } else if (availableData(action)) {
            listener.dataAvailable(intent);
        }
    }

    /**
     * isGattConnected checks if the GATT Server connection is successful.
     *
     * @param action
     * @return boolean true if connection was successful
     */
    private boolean isGattConnected(String action) {
        return BLEService.ACTION_GATT_CONNECTED.equals(action);
    }

    /**
     * isGattDisconnected checks if the GATT Server is disconnected successful.
     *
     * @param action
     * @return boolean true if disconnection was successful
     */
    private boolean isGattDisconnected(String action) {
        return BLEService.ACTION_GATT_DISCONNECTED.equals(action);
    }

    /**
     * hasDiscoveredGattservice checks if a GATT service was discovered matching the given action.
     *
     * @param action
     * @return boolean true
     */
    private boolean hasDiscoveredGattservice(String action) {
        return BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action);
    }

    /**
     * availableData checks if Data is available for the given action.
     *
     * @param action
     * @return boolean true
     */
    private boolean availableData(String action) {
        return BLEService.ACTION_DATA_AVAILABLE.equals(action);
    }

    /**
     * makeGattUpdateIntentFilter checks changes using BroadcastReceiver.
     *
     * @return IntentFilter object which contains all "keyphrases" the broadcaster shall listen to
     */
    public IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


}

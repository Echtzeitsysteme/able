package org.able.core;

import android.content.Intent;

/**
 * This interface needs to be implemented by every activity you want to create,
 * this measure makes sure you handle every indispensable situation considering a bluetooth connection.
 * For a better understanding take a look at the BLEBroadcastReceiver.class.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
*/
public interface BLEServiceListener {

    /**
     * Called if connection to a GATT server was successful.
     */
    void gattConnected();

    /**
     * Called if a connection to a GATT server is closed.
     */
    void gattDisconnected();

    /**
     * Called if a GATT server is discovered.
     */
    void gattServicesDiscovered();

    /**
     * Called if data from a GATT server is available.
     * @param intent incoming data from a GATT server
     */
    void dataAvailable(Intent intent);

}

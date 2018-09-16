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

    void gattConnected();

    void gattDisconnected();

    void gattServicesDiscovered();

    void dataAvailable(Intent intent);

}

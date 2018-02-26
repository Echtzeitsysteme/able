package de.tudarmstadt.es.able;

import android.content.Intent;

/**
 * Created by user on 26.02.18.
 */

public interface BLEServiceListener {

    void gattConnected();

    void gattDisconnected();

    void gattServicesDiscovered();

    void dataAvailable(Intent intent);


}

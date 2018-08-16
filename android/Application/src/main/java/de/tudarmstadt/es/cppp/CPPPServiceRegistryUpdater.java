package de.tudarmstadt.es.cppp;

import android.app.Activity;

import java.util.UUID;

import de.tudarmstadt.es.able.ServiceRegistryUpdatingBroadcastReceiver;


/**
 * This class registers the UUID-to-activity mapping for the Capled device.
 */
public class CPPPServiceRegistryUpdater extends ServiceRegistryUpdatingBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivityClass() {
        return CPPPActivity.class;
    }

    @Override
    protected UUID getUuid() {
        return CPPPConstants.CPPP_SERVICE_UUID;
    }
}

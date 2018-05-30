package de.tudarmstadt.es.capled;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.UUID;

import de.tudarmstadt.es.able.ServiceRegistryUpdatingBroadcastReceiver;

/**
 * This class registers the UUID-to-activity mapping for the Capled device.
 */
public class CapledServiceRegistryUpdater extends ServiceRegistryUpdatingBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivityClass() {
        return CapLEDActivity.class;
    }

    @Override
    protected UUID getUuid() {
        return CapLEDConstants.CAPLED_SERVICE_UUID;
    }
}

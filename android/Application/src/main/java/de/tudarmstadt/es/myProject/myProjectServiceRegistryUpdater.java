package de.tudarmstadt.es.myProject;

import android.app.Activity;

import java.util.UUID;

import de.tudarmstadt.es.able.ServiceRegistryUpdatingBroadcastReceiver;
import de.tudarmstadt.es.capled.CapLEDActivity;
import de.tudarmstadt.es.capled.CapLEDConstants;

/**
 * This class registers the UUID-to-activity mapping for the Capled device.
 */
public class myProjectServiceRegistryUpdater extends ServiceRegistryUpdatingBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivityClass() {
        return myProjectActivity.class;
    }

    @Override
    protected UUID getUuid() {
        // TODO: return your Service UUID here ...
        // EXAMPLE: return myProjectConstants.MYPROJECT_SERVICE_UUID;
        return null;
    }
}

package org.able.capled;

import android.app.Activity;

import java.util.UUID;

import org.able.core.AbleServiceRegistryUpdatingBroadcastReceiver;

/**
 * This class registers the UUID-to-activity mapping for the Capled device.
 */
public class CapLEDServiceRegistryUpdater extends AbleServiceRegistryUpdatingBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivityClass() {
        return CapLEDActivity.class;
    }

    @Override
    protected UUID getUuid() {
        return CapLEDConstants.CAPLED_SERVICE_UUID;
    }
}

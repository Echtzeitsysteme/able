package org.able.capled;

import android.app.Activity;

import java.util.UUID;

import org.able.core.AbleDeviceControlActivity;
import org.able.core.ServiceRegistryUpdatingBroadcastReceiver;

/**
 * This class registers the UUID-to-activity mapping for the Capled device.
 */
public class CapLEDServiceRegistryUpdater extends ServiceRegistryUpdatingBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivityClass() {
        return AbleDeviceControlActivity.class;
    }

    @Override
    protected UUID getUuid() {
        return CapLEDConstants.CAPLED_SERVICE_UUID;
    }
}

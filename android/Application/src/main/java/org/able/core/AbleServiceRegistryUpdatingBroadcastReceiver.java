package org.able.core;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

/**
 * This class is the implementation class for registering device-specific UUID-to-activity mappings.
 *
 * @author Roland Kluge
 */
public abstract class AbleServiceRegistryUpdatingBroadcastReceiver extends BroadcastReceiver {

    /**
     * The action that this {@link BroadcastReceiver} listens to.
     */
    public static final String INTENT_ACTION_UPDATE_UUID_MAPPING = "org.able.core.action.RegisterUuidToActivityMapping";

    /**
     * The {@link UUID} of the service to be registered
     *
     * @return the UUID. Must be non-null.
     */
    @NonNull
    protected abstract UUID getUuid();

    /**
     * Returns the class of the {@link Activity} to be launched if a service with the UUID returned by {@link #getUuid()} is available
     *
     * @return the activity to be launched. Must be non-null.
     */
    @NonNull
    protected abstract Class<? extends Activity> getActivityClass();


    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!INTENT_ACTION_UPDATE_UUID_MAPPING.equals(intent.getAction())) {
            Log.w("ABLE Service Registrty", String.format("Received an unexpected intent: '%s'. Ignoring it.", intent));
            return;
        }

        final UUID serviceUuid = getUuid();
        final Class<? extends Activity> activity = getActivityClass();
        AbleServiceRegistry.getInstance().registerActivity(serviceUuid, activity);
        Log.i("ABLE Service Registrty", String.format("Registered UUID mapping: %s->%s", serviceUuid, activity));
    }

}

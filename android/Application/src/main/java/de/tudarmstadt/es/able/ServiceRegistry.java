package de.tudarmstadt.es.able;

import android.app.Service;
import android.bluetooth.BluetoothGattService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import de.tudarmstadt.es.capled.CapLEDActivity;

/**
 * Service Registry saves the UUID and Activity Class Tuples which are later used by DeviceScanActivity in checkForKnownServices().
 * Edit the constructor  to add your own UUID Device and Activity class.
 *
 * @author Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.0
 */

public class ServiceRegistry {
    private Map<UUID, Class<?>> registeredServices;
    private static ServiceRegistry serviceRegistryINSTANCE;

    public ServiceRegistry(){
        registeredServices = new HashMap<>();

        // TODO: Insert your (UUID,Acvity) tuple here
        registeredServices.put(UUID.fromString("00000000-0000-1000-8000-00805f9b34f0"), CapLEDActivity.class);
    }

    public Map<UUID, Class<?>> getRegisteredServices () {
        return registeredServices;
    }

    public Class<?> getServiceClass(UUID uuid){
        if(registeredServices.containsKey(uuid))
            return registeredServices.get(uuid);
        return DeviceControlActivity.class;
    }

}

package de.tudarmstadt.es.able;

import android.bluetooth.BluetoothGattService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServiceRegistry {
    private Map<UUID, Class<?>> registeredServices;

    public ServiceRegistry(){
        registeredServices = new HashMap<>();

        // Here all UUIDs and their Acivities are saved in the registeredServices <UUID, Class<?>> HashMap
        // TODO: Insert your (UUID,Acvity) tuple here
        registeredServices.put(UUID.fromString("00000000-0000-1000-8000-00805f9b34f0"), CapLedActivity.class);
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

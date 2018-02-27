package de.tudarmstadt.es.able;

import java.util.HashMap;

/**
 * Created by user on 27.02.18.
 */

public enum UUID_Enum {


    baseUUID                    ("00000000-0000-1000-8000-00805f9b34f"),
    capsenseLedServiceUUID      ("00000000-0000-1000-8000-00805f9b34f0"),
    ledCharacteristicUUID       ("00000000-0000-1000-8000-00805f9b34f1"),
    capsenseCharacteristicUUID  ("00000000-0000-1000-8000-00805f9b34f2");

    private final String representation;

    UUID_Enum(String representation)
    {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

}

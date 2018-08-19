package de.tudarmstadt.es.cppp;

import java.util.UUID;

/**
 * This interfaces contains all the relevant UUIDs for the CapLED example.
 *
 * @author A. Poljakow, Puria Izady (puria.izady@stud.tu-darmstadt.de)
 * @version 1.1
 */

public interface CPPPConstants {
    UUID CPPP_SERVICE_UUID = UUID.fromString("C1505E24-C4C2-4B54-B6A4-2D552258F062");
    UUID CPPP_JOYSTICK_1_CHARACTERISTIC_UUID = UUID.fromString("F880E872-7135-46CB-AFFE-4F07C1C8AB33");
    UUID CPPP_NOTIFICATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
}

package de.tudarmstadt.es.able;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 23.02.18.
 */

public class characteristicSorterClass {

    private List<HashMap<String, String>> gattServiceData = null;
    private List<ArrayList<HashMap<String, String>>> gattCharacteristicData = null;
    List<List<BluetoothGattCharacteristic>> characteristicList = null;

    public characteristicSorterClass(List<HashMap<String, String>> gattServiceData,
                                     List<ArrayList<HashMap<String, String>>> gattCharacteristicData,
                                     List<List<BluetoothGattCharacteristic>> characteristicList)
    {
        this.gattServiceData = gattServiceData;
        this.gattCharacteristicData = gattCharacteristicData;
        this.characteristicList= characteristicList;
    }



    static characteristicSorterClass settingUpServices(List<BluetoothGattService> gattServices, String listName, String listUUID,
                                                       List<List<BluetoothGattCharacteristic>> characteristicList, Context context) {
        String uuid = null;
        String unknownServiceString = context.getResources().getString(R.string.unknown_service);
        String unknownCharaString = context.getResources().getString(R.string.unknown_characteristic);
        List<HashMap<String, String>> gattServiceData = new ArrayList<>();
        List<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<>();
        characteristicList = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    listName, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(listUUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        listName, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(listUUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            characteristicList.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        characteristicSorterClass objectToReturn = new characteristicSorterClass(gattServiceData, gattCharacteristicData,characteristicList);

        return objectToReturn;
    }


    public List<List<BluetoothGattCharacteristic>> getCharacteristicList() {
        return characteristicList;
    }

    public List<HashMap<String, String>> getGattServiceData() {
        return gattServiceData;
    }

    public void setGattServiceData(List<HashMap<String, String>> gattServiceData) {
        this.gattServiceData = gattServiceData;
    }

    public List<ArrayList<HashMap<String, String>>> getGattCharacteristicData() {
        return gattCharacteristicData;
    }

    public void setGattCharacteristicData(List<ArrayList<HashMap<String, String>>> gattCharacteristicData) {
        this.gattCharacteristicData = gattCharacteristicData;
    }


}
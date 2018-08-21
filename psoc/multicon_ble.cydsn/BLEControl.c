/* ========================================
 *
 * Copyright YOUR COMPANY, THE YEAR
 * All Rights Reserved
 * UNPUBLISHED, LICENSED SOFTWARE.
 *
 * CONFIDENTIAL AND PROPRIETARY INFORMATION
 * WHICH IS THE PROPERTY OF your company.
 *
 * ========================================
*/

/* [] END OF FILE */

#include "BLEControl.h"

void testUpdateCharacteristic(){
    CYBLE_GATTS_HANDLE_VALUE_NTF_T 	tempHandle;
    
    joystickPos = data;
    /*
    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
        return; */
    
    tempHandle.attrHandle = CYBLE_PERIPHERY_JOYSTICK_1_CHAR_HANDLE;
  	tempHandle.value.val = (uint8 *) &data;
    tempHandle.value.len = 1;
    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED);//information gets written to the GATT-Database  
    if (joystickNotify && (joystickPos != joystickPosOld) )
        CyBle_GattsNotification(cyBle_connHandle,&tempHandle);//this method sends an notification via bluetooth to the client, that something has changed
        joystickPosOld = joystickPos;
    if(data > 255)
        data = 0;
}

void BleCallBack(uint32 event, void* eventParam){
    CYBLE_GATTS_WRITE_REQ_PARAM_T *wrReqParam;
    switch(event){
        /* if there is a disconnect or the stack just turned on from a reset then start the advertising and turn on the LED blinking */
        case CYBLE_EVT_STACK_ON:
            CyBle_GappStartAdvertisement(CYBLE_ADVERTISING_FAST);
            ledPWM_Start();
            break;
        case CYBLE_EVT_GAP_DEVICE_DISCONNECTED://both cases get same handling
            CyBle_GappStartAdvertisement(CYBLE_ADVERTISING_FAST);//start advertising the database again
            ledPWM_Start();
            break;
        case CYBLE_EVT_GATT_CONNECT_IND:
            testUpdateCharacteristic();
            ledPWM_Stop();
            break;
        /* handle a write request */
        case CYBLE_EVT_GATTS_WRITE_REQ://this event gets active, if remoteside wants to write data into GATT database
            wrReqParam = (CYBLE_GATTS_WRITE_REQ_PARAM_T *) eventParam;//the stack tells which characteristic its trying to write
             /* request to update the joystick notification */
            if(wrReqParam->handleValPair.attrHandle == CYBLE_PERIPHERY_JOYSTICK_1_CLIENT_CHARACTERISTIC_CONFIGURATION_DESC_HANDLE)//changing capsense 
            {
                CyBle_GattsWriteAttributeValue(&wrReqParam->handleValPair, 0, &cyBle_connHandle, CYBLE_GATT_DB_PEER_INITIATED);
                joystickNotify = wrReqParam->handleValPair.value.val[0] & 0x01;
                //send an response 
                CyBle_GattsWriteRsp(cyBle_connHandle);
            }		
            break;
        default: break;
    }
}
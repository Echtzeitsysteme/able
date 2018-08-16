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
   
    uint8 data = 1u;
    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
        return;
    
    tempHandle.attrHandle = CYBLE_PERIPHERY_JOYSTICK_1_CHAR_HANDLE;
  	tempHandle.value.val = (uint8 *) &data;
    tempHandle.value.len = 1;
    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED);//information gets written to the GATT-Database  
}

void BleCallBack(uint32 event, void* eventParam){
    CYBLE_GATTS_WRITE_REQ_PARAM_T *wrReqParam;
    switch(event){
        /* if there is a disconnect or the stack just turned on from a reset then start the advertising and turn on the LED blinking */
        case CYBLE_EVT_STACK_ON:
            CyBle_GappStartAdvertisement(CYBLE_ADVERTISING_FAST);
            break;
        case CYBLE_EVT_GAP_DEVICE_DISCONNECTED://both cases get same handling
            CyBle_GappStartAdvertisement(CYBLE_ADVERTISING_FAST);//start advertising the database again
            break;
        case CYBLE_EVT_GATT_CONNECT_IND:
            testUpdateCharacteristic();
            break;
    }
}
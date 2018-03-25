#include <project.h>

uint16 fingerPos    = 0xFFFF;
uint16 fingerPosOld = 0xFFFF;

int capsenseNotify;//on if notifications are on, off otherwise

/***************************************************************
 * Function to update the LED state in the GATT database
 **************************************************************/
void updateLed()
{
    CYBLE_GATTS_HANDLE_VALUE_NTF_T 	tempHandle;
   
    uint8 red_State = !red_Read();//led is active low, 0 means on, to not confuse its inverted so 1 is on
    //CyBLE_getState() checks if the ble is conntected, if connected it is reasonable to update the database, not otherwise
    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
        return;
    
    tempHandle.attrHandle = CYBLE_LEDCAPSENSE_LED_CHAR_HANDLE;
  	tempHandle.value.val = (uint8 *) &red_State;
    tempHandle.value.len = 1;
    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED);//information gets written to the GATT-Database  
}

/***************************************************************
 * Function to update the CapSesnse state in the GATT database
 **************************************************************/
void updateCapsense()//similiar to upper method but for capsense sensor
{
    if(CyBle_GetState() != CYBLE_STATE_CONNECTED)
        return;
    
	CYBLE_GATTS_HANDLE_VALUE_NTF_T 	tempHandle;
    
    tempHandle.attrHandle = CYBLE_LEDCAPSENSE_CAPSENSE_CHAR_HANDLE;
  	tempHandle.value.val = (uint8 *)&fingerPos;//cast to uint8 integerpointer
    tempHandle.value.len = 2; 
    CyBle_GattsWriteAttributeValue(&tempHandle,0,&cyBle_connHandle,CYBLE_GATT_DB_LOCALLY_INITIATED );//writing current values of capsense to database 
    //ble accepts clients which connect and register for notifications and therefore sends those changes to the client
    //capsenseNotify gets set if client characteristiy configuration descriptor gets set , option we chose earlier in ble schematic
    /* send notification to client if notifications are enabled and finger location has changed */
    if (capsenseNotify && (fingerPos != fingerPosOld) )
        CyBle_GattsNotification(cyBle_connHandle,&tempHandle);//this method sends an notification via bluetooth to the client, that something has changed
        fingerPosOld = fingerPos;
}

/***************************************************************
 * Function to handle the BLE stack
 **************************************************************/
void BleCallBack(uint32 event, void* eventParam)//ble event handler, statemachine 
{
    CYBLE_GATTS_WRITE_REQ_PARAM_T *wrReqParam;

    switch(event)
    {
        /* if there is a disconnect or the stack just turned on from a reset then start the advertising and turn on the LED blinking */
        case CYBLE_EVT_STACK_ON:
        case CYBLE_EVT_GAP_DEVICE_DISCONNECTED://both cases get same handling
            capsenseNotify = 0;//turn off notifications 
            CyBle_GappStartAdvertisement(CYBLE_ADVERTISING_FAST);//start advertising the database again
            pwm_Start();//start the pwm to drive the led 
        break;
        
        /* when a connection is made, update the LED and Capsense states in the GATT database and stop blinking the LED */    
        case CYBLE_EVT_GATT_CONNECT_IND://what happens if the connection is established by remoteside
            updateLed();//update database with current state of led
            updateCapsense();  //update database with current state of capsense#
            //both functions enable the remote side to read the state of the sensors, handled by blestack firmware-> check how this stack works in packages
            
            pwm_Stop();//somehow this stops the blinking bluelight, probably because the default only triggers blue led, no other
		break;

        /* handle a write request */
        case CYBLE_EVT_GATTS_WRITE_REQ://this event gets active, if remoteside wants to write data into GATT database
            wrReqParam = (CYBLE_GATTS_WRITE_REQ_PARAM_T *) eventParam;//the stack tells which characteristic its trying to write
			      
            /* request write the LED value */
            if(wrReqParam->handleValPair.attrHandle == CYBLE_LEDCAPSENSE_LED_CHAR_HANDLE)//check if the characteristic is trying to write led values
            //does the remote tries to change the state of the led
            {
                /* only update the value and write the response if the requested write is allowed */
                if(CYBLE_GATT_ERR_NONE == CyBle_GattsWriteAttributeValue(&wrReqParam->handleValPair, 0, &cyBle_connHandle, CYBLE_GATT_DB_PEER_INITIATED))
                {   
                    //if no error the write operation is allowed 
                    red_Write(!wrReqParam->handleValPair.value.val[0]);
                    //turn on or off the led considering the wished value, red led is active 0, have to invert the values
                    
                    //send an response 
                    CyBle_GattsWriteRsp(cyBle_connHandle);
                }
            }
            
            /* request to update the CapSense notification */
            if(wrReqParam->handleValPair.attrHandle == CYBLE_LEDCAPSENSE_CAPSENSE_CAPSENSECCCD_DESC_HANDLE)//changing capsense 
            {
                
                CyBle_GattsWriteAttributeValue(&wrReqParam->handleValPair, 0, &cyBle_connHandle, CYBLE_GATT_DB_PEER_INITIATED);
                capsenseNotify = wrReqParam->handleValPair.value.val[0] & 0x01;
                
                //send an response 
                CyBle_GattsWriteRsp(cyBle_connHandle);
            }		
			break;  
        
        default:
            break;
    }
} 

/***************************************************************
 * Main
 **************************************************************/
int main()
{   
    //initialize: turn on global interrupts, done by CyGlobalIntEnable macro
    CyGlobalIntEnable; 
    
    //starting capsense components, and initilizebaseline noise removes the noise from the sensor
    capsense_Start();
    capsense_InitializeEnabledBaselines();
    
    /* Start BLE stack and register the callback function */
    CyBle_Start(BleCallBack);

    for(;;)
    {        
        /* if Capsense scan is done, read the value and start another scan */
        if(!capsense_IsBusy())
        {
            fingerPos=capsense_GetCentroidPos(capsense_LINEARSLIDER0__LS);//get fingerposition value
            capsense_UpdateEnabledBaselines();//update baselines?
            capsense_ScanEnabledWidgets();//start the scan
            updateCapsense();//call function which is described above,get value , write to database make notification if its set
        }
   
        CyBle_ProcessEvents();//blackbox somehow processhandling via method
        CyBle_EnterLPM(CYBLE_BLESS_DEEPSLEEP); // change to deepsleep to save power if its possible  
    }
}

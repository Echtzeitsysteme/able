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
#include <project.h>


void delay(){
 for(int i=0; i<1000000;i++){}  
}

void LED_ON(){
    LED_Write(0u);
}

void LED_OFF(){
    LED_Write(1u);
}
void sendStringViaUART(char* text){
  while (*text != '\0')
  { 
    UART_UartPutChar((char) *text++);
  }
}
void flashLED(){
    while(1u){
        LED_ON();
        sendStringViaUART("hello world");
        //UART_UartPutString("hello world");
        //testPin_Write(0u);
        delay();
        LED_OFF();
        //UART_UartPutString("world");
        //testPin_Write(1u);
        delay();
    }
}

void BleCallBack(uint32 event, void* eventParam){
    switch(event){
        /* if there is a disconnect or the stack just turned on from a reset then start the advertising and turn on the LED blinking */
        case CYBLE_EVT_STACK_ON:
        case CYBLE_EVT_GAP_DEVICE_DISCONNECTED://both cases get same handling
            CyBle_GappStartAdvertisement(CYBLE_ADVERTISING_FAST);//start advertising the database again
        break;
    }
}

int main (void)
{
    /* CyGlobalIntEnable; */ /* Uncomment this line to enable global interrupts. */
    UART_Start();
    //flashLED();
    CyBle_Start(BleCallBack);
    while(1u){
      CyBle_ProcessEvents();
    }
    
    
}
/* [] END OF FILE */

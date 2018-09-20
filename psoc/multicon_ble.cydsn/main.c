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
#include "BLEControl.h"
#include "LEDControl.h"
#include "UARTControl.h"


int main (void)
{
    CyGlobalIntEnable;  /* Uncomment this line to enable global interrupts. */
    UART_Start();
    UARTISR_StartEx(UARTRX);
    //UARTISR_Enable();
    //CyBle_Start(BleCallBack);
    joystickPosOld = 0;
    data = 25;
    while(1u){
        //LEDRainbow();
        /*
        uint8_t tmp = uartRead();
        if(tmp > 0)
            data = tmp;
        testUpdateCharacteristic();
        CyBle_ProcessEvents();
        CyBle_EnterLPM(CYBLE_BLESS_DEEPSLEEP);
        */
          
        //uint8_t brightness = uartRead();
        //uint8_t joystick1X = 3;
        //uint8_t joystick1Y = 4;
        
        //uint8_t tmp = 45;
        //UART_UartPutChar(tmp);
        
        //UART_UartPutChar(joystick1X);
        //UART_UartPutChar(joystick1Y); 
    }
    
    
}
/* [] END OF FILE */

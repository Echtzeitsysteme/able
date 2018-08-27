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
    CyBle_Start(BleCallBack);
    joystickPosOld = 0;
    data = 25;
    while(1u){
        //LEDRainbow();
        uint8_t tmp = uartRead();
        if(tmp > 0)
            data = tmp;
        testUpdateCharacteristic();
        CyBle_ProcessEvents();
        CyBle_EnterLPM(CYBLE_BLESS_DEEPSLEEP);
    }
    
    
}
/* [] END OF FILE */

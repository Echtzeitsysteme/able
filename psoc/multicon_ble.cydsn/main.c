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
    testUpdateCharacteristic();
    while(1u){
      //LEDRainbow();
      CyBle_ProcessEvents();
    }
    
    
}
/* [] END OF FILE */

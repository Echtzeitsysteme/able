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

void flashLED(){
    while(1u){
        LED_ON();
        testPin_Write(0u);
        delay();
        LED_OFF();
        testPin_Write(1u);
        delay();
        UART_UartPutChar('a');
    }
}

int main (void)
{
    /* CyGlobalIntEnable; */ /* Uncomment this line to enable global interrupts. */
    UART_Start();
    //testPin2_Write(0u);
    //testPin3_Write(0u);
    flashLED();
    
    
}
/* [] END OF FILE */

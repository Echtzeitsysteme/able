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

#include "LEDControl.h"

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
        delay();
        LED_OFF();
        delay();
    }
}
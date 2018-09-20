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
#include "UARTControl.h"
#include <project.h>
#include <stdlib.h>

void LEDRainbow(){
    UART_UartPutChar(0u);
    LED_ON();
    delay();
    UART_UartPutChar(1u);
    LED_OFF();
    delay();
    UART_UartPutChar(2u);
    LED_OFF();
    delay();
    UART_UartPutChar(3u);
    LED_OFF();
    delay();
}

void testListen(){
    uint8_t data = UART_UartGetChar();
    while(data != 'a'){
        data = UART_UartGetChar();
    }
    flashLED();
    
}

uint8_t uartRead(){
    uint8_t data = UART_UartGetChar();
    return data;
}

CY_ISR(UARTRX){
    UARTISR_Disable();
    uint8_t brightness = UART_UartGetChar();
    UART_UartPutChar(brightness);
    UART_ClearRxInterruptSource(UART_INTR_RX_NOT_EMPTY);
    UARTISR_Enable();
}

/* [] END OF FILE */

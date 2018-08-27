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

/* [] END OF FILE */

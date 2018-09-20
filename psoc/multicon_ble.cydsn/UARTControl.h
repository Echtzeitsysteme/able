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
#include "LEDControl.h"
#ifndef __UART_CONTROL_H__
#define __UART_CONTROL_H__
void LEDRainbow();
void testListen();
uint8_t uartRead();
CY_ISR(UARTRX);
#endif

/* [] END OF FILE */

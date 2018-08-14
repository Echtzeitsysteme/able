/***************************************************************************//**
* \file SCB_1_SPI_UART_PVT.h
* \version 3.20
*
* \brief
*  This private file provides constants and parameter values for the
*  SCB Component in SPI and UART modes.
*  Please do not use this file or its content in your project.
*
* Note:
*
********************************************************************************
* \copyright
* Copyright 2013-2016, Cypress Semiconductor Corporation. All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_SCB_SPI_UART_PVT_SCB_1_H)
#define CY_SCB_SPI_UART_PVT_SCB_1_H

#include "SCB_1_SPI_UART.h"


/***************************************
*     Internal Global Vars
***************************************/

#if (SCB_1_INTERNAL_RX_SW_BUFFER_CONST)
    extern volatile uint32  SCB_1_rxBufferHead;
    extern volatile uint32  SCB_1_rxBufferTail;
    
    /**
    * \addtogroup group_globals
    * @{
    */
    
    /** Sets when internal software receive buffer overflow
     *  was occurred.
    */  
    extern volatile uint8   SCB_1_rxBufferOverflow;
    /** @} globals */
#endif /* (SCB_1_INTERNAL_RX_SW_BUFFER_CONST) */

#if (SCB_1_INTERNAL_TX_SW_BUFFER_CONST)
    extern volatile uint32  SCB_1_txBufferHead;
    extern volatile uint32  SCB_1_txBufferTail;
#endif /* (SCB_1_INTERNAL_TX_SW_BUFFER_CONST) */

#if (SCB_1_INTERNAL_RX_SW_BUFFER)
    extern volatile uint8 SCB_1_rxBufferInternal[SCB_1_INTERNAL_RX_BUFFER_SIZE];
#endif /* (SCB_1_INTERNAL_RX_SW_BUFFER) */

#if (SCB_1_INTERNAL_TX_SW_BUFFER)
    extern volatile uint8 SCB_1_txBufferInternal[SCB_1_TX_BUFFER_SIZE];
#endif /* (SCB_1_INTERNAL_TX_SW_BUFFER) */


/***************************************
*     Private Function Prototypes
***************************************/

void SCB_1_SpiPostEnable(void);
void SCB_1_SpiStop(void);

#if (SCB_1_SCB_MODE_SPI_CONST_CFG)
    void SCB_1_SpiInit(void);
#endif /* (SCB_1_SCB_MODE_SPI_CONST_CFG) */

#if (SCB_1_SPI_WAKE_ENABLE_CONST)
    void SCB_1_SpiSaveConfig(void);
    void SCB_1_SpiRestoreConfig(void);
#endif /* (SCB_1_SPI_WAKE_ENABLE_CONST) */

void SCB_1_UartPostEnable(void);
void SCB_1_UartStop(void);

#if (SCB_1_SCB_MODE_UART_CONST_CFG)
    void SCB_1_UartInit(void);
#endif /* (SCB_1_SCB_MODE_UART_CONST_CFG) */

#if (SCB_1_UART_WAKE_ENABLE_CONST)
    void SCB_1_UartSaveConfig(void);
    void SCB_1_UartRestoreConfig(void);
#endif /* (SCB_1_UART_WAKE_ENABLE_CONST) */


/***************************************
*         UART API Constants
***************************************/

/* UART RX and TX position to be used in SCB_1_SetPins() */
#define SCB_1_UART_RX_PIN_ENABLE    (SCB_1_UART_RX)
#define SCB_1_UART_TX_PIN_ENABLE    (SCB_1_UART_TX)

/* UART RTS and CTS position to be used in  SCB_1_SetPins() */
#define SCB_1_UART_RTS_PIN_ENABLE    (0x10u)
#define SCB_1_UART_CTS_PIN_ENABLE    (0x20u)


/***************************************
* The following code is DEPRECATED and
* must not be used.
***************************************/

/* Interrupt processing */
#define SCB_1_SpiUartEnableIntRx(intSourceMask)  SCB_1_SetRxInterruptMode(intSourceMask)
#define SCB_1_SpiUartEnableIntTx(intSourceMask)  SCB_1_SetTxInterruptMode(intSourceMask)
uint32  SCB_1_SpiUartDisableIntRx(void);
uint32  SCB_1_SpiUartDisableIntTx(void);


#endif /* (CY_SCB_SPI_UART_PVT_SCB_1_H) */


/* [] END OF FILE */

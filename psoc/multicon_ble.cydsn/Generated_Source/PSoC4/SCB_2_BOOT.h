/***************************************************************************//**
* \file SCB_2_BOOT.h
* \version 3.20
*
* \brief
*  This file provides constants and parameter values of the bootloader
*  communication APIs for the SCB Component.
*
* Note:
*
********************************************************************************
* \copyright
* Copyright 2014-2016, Cypress Semiconductor Corporation. All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_SCB_BOOT_SCB_2_H)
#define CY_SCB_BOOT_SCB_2_H

#include "SCB_2_PVT.h"

#if (SCB_2_SCB_MODE_I2C_INC)
    #include "SCB_2_I2C.h"
#endif /* (SCB_2_SCB_MODE_I2C_INC) */

#if (SCB_2_SCB_MODE_EZI2C_INC)
    #include "SCB_2_EZI2C.h"
#endif /* (SCB_2_SCB_MODE_EZI2C_INC) */

#if (SCB_2_SCB_MODE_SPI_INC || SCB_2_SCB_MODE_UART_INC)
    #include "SCB_2_SPI_UART.h"
#endif /* (SCB_2_SCB_MODE_SPI_INC || SCB_2_SCB_MODE_UART_INC) */


/***************************************
*  Conditional Compilation Parameters
****************************************/

/* Bootloader communication interface enable */
#define SCB_2_BTLDR_COMM_ENABLED ((CYDEV_BOOTLOADER_IO_COMP == CyBtldr_SCB_2) || \
                                             (CYDEV_BOOTLOADER_IO_COMP == CyBtldr_Custom_Interface))

/* Enable I2C bootloader communication */
#if (SCB_2_SCB_MODE_I2C_INC)
    #define SCB_2_I2C_BTLDR_COMM_ENABLED     (SCB_2_BTLDR_COMM_ENABLED && \
                                                            (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG || \
                                                             SCB_2_I2C_SLAVE_CONST))
#else
     #define SCB_2_I2C_BTLDR_COMM_ENABLED    (0u)
#endif /* (SCB_2_SCB_MODE_I2C_INC) */

/* EZI2C does not support bootloader communication. Provide empty APIs */
#if (SCB_2_SCB_MODE_EZI2C_INC)
    #define SCB_2_EZI2C_BTLDR_COMM_ENABLED   (SCB_2_BTLDR_COMM_ENABLED && \
                                                         SCB_2_SCB_MODE_UNCONFIG_CONST_CFG)
#else
    #define SCB_2_EZI2C_BTLDR_COMM_ENABLED   (0u)
#endif /* (SCB_2_EZI2C_BTLDR_COMM_ENABLED) */

/* Enable SPI bootloader communication */
#if (SCB_2_SCB_MODE_SPI_INC)
    #define SCB_2_SPI_BTLDR_COMM_ENABLED     (SCB_2_BTLDR_COMM_ENABLED && \
                                                            (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG || \
                                                             SCB_2_SPI_SLAVE_CONST))
#else
        #define SCB_2_SPI_BTLDR_COMM_ENABLED (0u)
#endif /* (SCB_2_SPI_BTLDR_COMM_ENABLED) */

/* Enable UART bootloader communication */
#if (SCB_2_SCB_MODE_UART_INC)
       #define SCB_2_UART_BTLDR_COMM_ENABLED    (SCB_2_BTLDR_COMM_ENABLED && \
                                                            (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG || \
                                                             (SCB_2_UART_RX_DIRECTION && \
                                                              SCB_2_UART_TX_DIRECTION)))
#else
     #define SCB_2_UART_BTLDR_COMM_ENABLED   (0u)
#endif /* (SCB_2_UART_BTLDR_COMM_ENABLED) */

/* Enable bootloader communication */
#define SCB_2_BTLDR_COMM_MODE_ENABLED    (SCB_2_I2C_BTLDR_COMM_ENABLED   || \
                                                     SCB_2_SPI_BTLDR_COMM_ENABLED   || \
                                                     SCB_2_EZI2C_BTLDR_COMM_ENABLED || \
                                                     SCB_2_UART_BTLDR_COMM_ENABLED)


/***************************************
*        Function Prototypes
***************************************/

#if defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_I2C_BTLDR_COMM_ENABLED)
    /* I2C Bootloader physical layer functions */
    void SCB_2_I2CCyBtldrCommStart(void);
    void SCB_2_I2CCyBtldrCommStop (void);
    void SCB_2_I2CCyBtldrCommReset(void);
    cystatus SCB_2_I2CCyBtldrCommRead       (uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);
    cystatus SCB_2_I2CCyBtldrCommWrite(const uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);

    /* Map I2C specific bootloader communication APIs to SCB specific APIs */
    #if (SCB_2_SCB_MODE_I2C_CONST_CFG)
        #define SCB_2_CyBtldrCommStart   SCB_2_I2CCyBtldrCommStart
        #define SCB_2_CyBtldrCommStop    SCB_2_I2CCyBtldrCommStop
        #define SCB_2_CyBtldrCommReset   SCB_2_I2CCyBtldrCommReset
        #define SCB_2_CyBtldrCommRead    SCB_2_I2CCyBtldrCommRead
        #define SCB_2_CyBtldrCommWrite   SCB_2_I2CCyBtldrCommWrite
    #endif /* (SCB_2_SCB_MODE_I2C_CONST_CFG) */

#endif /* defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_I2C_BTLDR_COMM_ENABLED) */


#if defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_EZI2C_BTLDR_COMM_ENABLED)
    /* Bootloader physical layer functions */
    void SCB_2_EzI2CCyBtldrCommStart(void);
    void SCB_2_EzI2CCyBtldrCommStop (void);
    void SCB_2_EzI2CCyBtldrCommReset(void);
    cystatus SCB_2_EzI2CCyBtldrCommRead       (uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);
    cystatus SCB_2_EzI2CCyBtldrCommWrite(const uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);

    /* Map EZI2C specific bootloader communication APIs to SCB specific APIs */
    #if (SCB_2_SCB_MODE_EZI2C_CONST_CFG)
        #define SCB_2_CyBtldrCommStart   SCB_2_EzI2CCyBtldrCommStart
        #define SCB_2_CyBtldrCommStop    SCB_2_EzI2CCyBtldrCommStop
        #define SCB_2_CyBtldrCommReset   SCB_2_EzI2CCyBtldrCommReset
        #define SCB_2_CyBtldrCommRead    SCB_2_EzI2CCyBtldrCommRead
        #define SCB_2_CyBtldrCommWrite   SCB_2_EzI2CCyBtldrCommWrite
    #endif /* (SCB_2_SCB_MODE_EZI2C_CONST_CFG) */

#endif /* defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_EZI2C_BTLDR_COMM_ENABLED) */

#if defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_SPI_BTLDR_COMM_ENABLED)
    /* SPI Bootloader physical layer functions */
    void SCB_2_SpiCyBtldrCommStart(void);
    void SCB_2_SpiCyBtldrCommStop (void);
    void SCB_2_SpiCyBtldrCommReset(void);
    cystatus SCB_2_SpiCyBtldrCommRead       (uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);
    cystatus SCB_2_SpiCyBtldrCommWrite(const uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);

    /* Map SPI specific bootloader communication APIs to SCB specific APIs */
    #if (SCB_2_SCB_MODE_SPI_CONST_CFG)
        #define SCB_2_CyBtldrCommStart   SCB_2_SpiCyBtldrCommStart
        #define SCB_2_CyBtldrCommStop    SCB_2_SpiCyBtldrCommStop
        #define SCB_2_CyBtldrCommReset   SCB_2_SpiCyBtldrCommReset
        #define SCB_2_CyBtldrCommRead    SCB_2_SpiCyBtldrCommRead
        #define SCB_2_CyBtldrCommWrite   SCB_2_SpiCyBtldrCommWrite
    #endif /* (SCB_2_SCB_MODE_SPI_CONST_CFG) */

#endif /* defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_SPI_BTLDR_COMM_ENABLED) */

#if defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_UART_BTLDR_COMM_ENABLED)
    /* UART Bootloader physical layer functions */
    void SCB_2_UartCyBtldrCommStart(void);
    void SCB_2_UartCyBtldrCommStop (void);
    void SCB_2_UartCyBtldrCommReset(void);
    cystatus SCB_2_UartCyBtldrCommRead       (uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);
    cystatus SCB_2_UartCyBtldrCommWrite(const uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);

    /* Map UART specific bootloader communication APIs to SCB specific APIs */
    #if (SCB_2_SCB_MODE_UART_CONST_CFG)
        #define SCB_2_CyBtldrCommStart   SCB_2_UartCyBtldrCommStart
        #define SCB_2_CyBtldrCommStop    SCB_2_UartCyBtldrCommStop
        #define SCB_2_CyBtldrCommReset   SCB_2_UartCyBtldrCommReset
        #define SCB_2_CyBtldrCommRead    SCB_2_UartCyBtldrCommRead
        #define SCB_2_CyBtldrCommWrite   SCB_2_UartCyBtldrCommWrite
    #endif /* (SCB_2_SCB_MODE_UART_CONST_CFG) */

#endif /* defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_UART_BTLDR_COMM_ENABLED) */

/**
* \addtogroup group_bootloader
* @{
*/

#if defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_BTLDR_COMM_ENABLED)
    #if (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG)
        /* Bootloader physical layer functions */
        void SCB_2_CyBtldrCommStart(void);
        void SCB_2_CyBtldrCommStop (void);
        void SCB_2_CyBtldrCommReset(void);
        cystatus SCB_2_CyBtldrCommRead       (uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);
        cystatus SCB_2_CyBtldrCommWrite(const uint8 pData[], uint16 size, uint16 * count, uint8 timeOut);
    #endif /* (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG) */

    /* Map SCB specific bootloader communication APIs to common APIs */
    #if (CYDEV_BOOTLOADER_IO_COMP == CyBtldr_SCB_2)
        #define CyBtldrCommStart    SCB_2_CyBtldrCommStart
        #define CyBtldrCommStop     SCB_2_CyBtldrCommStop
        #define CyBtldrCommReset    SCB_2_CyBtldrCommReset
        #define CyBtldrCommWrite    SCB_2_CyBtldrCommWrite
        #define CyBtldrCommRead     SCB_2_CyBtldrCommRead
    #endif /* (CYDEV_BOOTLOADER_IO_COMP == CyBtldr_SCB_2) */

#endif /* defined(CYDEV_BOOTLOADER_IO_COMP) && (SCB_2_BTLDR_COMM_ENABLED) */

/** @} group_bootloader */

/***************************************
*           API Constants
***************************************/

/* Timeout unit in milliseconds */
#define SCB_2_WAIT_1_MS  (1u)

/* Return number of bytes to copy into bootloader buffer */
#define SCB_2_BYTES_TO_COPY(actBufSize, bufSize) \
                            ( ((uint32)(actBufSize) < (uint32)(bufSize)) ? \
                                ((uint32) (actBufSize)) : ((uint32) (bufSize)) )

/* Size of Read/Write buffers for I2C bootloader  */
#define SCB_2_I2C_BTLDR_SIZEOF_READ_BUFFER   (64u)
#define SCB_2_I2C_BTLDR_SIZEOF_WRITE_BUFFER  (64u)

/* Byte to byte time interval: calculated basing on current component
* data rate configuration, can be defined in project if required.
*/
#ifndef SCB_2_SPI_BYTE_TO_BYTE
    #define SCB_2_SPI_BYTE_TO_BYTE   (160u)
#endif

/* Byte to byte time interval: calculated basing on current component
* baud rate configuration, can be defined in the project if required.
*/
#ifndef SCB_2_UART_BYTE_TO_BYTE
    #define SCB_2_UART_BYTE_TO_BYTE  (2500u)
#endif /* SCB_2_UART_BYTE_TO_BYTE */

#endif /* (CY_SCB_BOOT_SCB_2_H) */


/* [] END OF FILE */

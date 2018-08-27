/***************************************************************************//**
* \file .h
* \version 3.20
*
* \brief
*  This private file provides constants and parameter values for the
*  SCB Component.
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

#if !defined(CY_SCB_PVT_SCB_2_H)
#define CY_SCB_PVT_SCB_2_H

#include "SCB_2.h"


/***************************************
*     Private Function Prototypes
***************************************/

/* APIs to service INTR_I2C_EC register */
#define SCB_2_SetI2CExtClkInterruptMode(interruptMask) SCB_2_WRITE_INTR_I2C_EC_MASK(interruptMask)
#define SCB_2_ClearI2CExtClkInterruptSource(interruptMask) SCB_2_CLEAR_INTR_I2C_EC(interruptMask)
#define SCB_2_GetI2CExtClkInterruptSource()                (SCB_2_INTR_I2C_EC_REG)
#define SCB_2_GetI2CExtClkInterruptMode()                  (SCB_2_INTR_I2C_EC_MASK_REG)
#define SCB_2_GetI2CExtClkInterruptSourceMasked()          (SCB_2_INTR_I2C_EC_MASKED_REG)

#if (!SCB_2_CY_SCBIP_V1)
    /* APIs to service INTR_SPI_EC register */
    #define SCB_2_SetSpiExtClkInterruptMode(interruptMask) \
                                                                SCB_2_WRITE_INTR_SPI_EC_MASK(interruptMask)
    #define SCB_2_ClearSpiExtClkInterruptSource(interruptMask) \
                                                                SCB_2_CLEAR_INTR_SPI_EC(interruptMask)
    #define SCB_2_GetExtSpiClkInterruptSource()                 (SCB_2_INTR_SPI_EC_REG)
    #define SCB_2_GetExtSpiClkInterruptMode()                   (SCB_2_INTR_SPI_EC_MASK_REG)
    #define SCB_2_GetExtSpiClkInterruptSourceMasked()           (SCB_2_INTR_SPI_EC_MASKED_REG)
#endif /* (!SCB_2_CY_SCBIP_V1) */

#if(SCB_2_SCB_MODE_UNCONFIG_CONST_CFG)
    extern void SCB_2_SetPins(uint32 mode, uint32 subMode, uint32 uartEnableMask);
#endif /* (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG) */


/***************************************
*     Vars with External Linkage
***************************************/

#if (SCB_2_SCB_IRQ_INTERNAL)
#if !defined (CY_REMOVE_SCB_2_CUSTOM_INTR_HANDLER)
    extern cyisraddress SCB_2_customIntrHandler;
#endif /* !defined (CY_REMOVE_SCB_2_CUSTOM_INTR_HANDLER) */
#endif /* (SCB_2_SCB_IRQ_INTERNAL) */

extern SCB_2_BACKUP_STRUCT SCB_2_backup;

#if(SCB_2_SCB_MODE_UNCONFIG_CONST_CFG)
    /* Common configuration variables */
    extern uint8 SCB_2_scbMode;
    extern uint8 SCB_2_scbEnableWake;
    extern uint8 SCB_2_scbEnableIntr;

    /* I2C configuration variables */
    extern uint8 SCB_2_mode;
    extern uint8 SCB_2_acceptAddr;

    /* SPI/UART configuration variables */
    extern volatile uint8 * SCB_2_rxBuffer;
    extern uint8   SCB_2_rxDataBits;
    extern uint32  SCB_2_rxBufferSize;

    extern volatile uint8 * SCB_2_txBuffer;
    extern uint8   SCB_2_txDataBits;
    extern uint32  SCB_2_txBufferSize;

    /* EZI2C configuration variables */
    extern uint8 SCB_2_numberOfAddr;
    extern uint8 SCB_2_subAddrSize;
#endif /* (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG) */

#if (! (SCB_2_SCB_MODE_I2C_CONST_CFG || \
        SCB_2_SCB_MODE_EZI2C_CONST_CFG))
    extern uint16 SCB_2_IntrTxMask;
#endif /* (! (SCB_2_SCB_MODE_I2C_CONST_CFG || \
              SCB_2_SCB_MODE_EZI2C_CONST_CFG)) */


/***************************************
*        Conditional Macro
****************************************/

#if(SCB_2_SCB_MODE_UNCONFIG_CONST_CFG)
    /* Defines run time operation mode */
    #define SCB_2_SCB_MODE_I2C_RUNTM_CFG     (SCB_2_SCB_MODE_I2C      == SCB_2_scbMode)
    #define SCB_2_SCB_MODE_SPI_RUNTM_CFG     (SCB_2_SCB_MODE_SPI      == SCB_2_scbMode)
    #define SCB_2_SCB_MODE_UART_RUNTM_CFG    (SCB_2_SCB_MODE_UART     == SCB_2_scbMode)
    #define SCB_2_SCB_MODE_EZI2C_RUNTM_CFG   (SCB_2_SCB_MODE_EZI2C    == SCB_2_scbMode)
    #define SCB_2_SCB_MODE_UNCONFIG_RUNTM_CFG \
                                                        (SCB_2_SCB_MODE_UNCONFIG == SCB_2_scbMode)

    /* Defines wakeup enable */
    #define SCB_2_SCB_WAKE_ENABLE_CHECK       (0u != SCB_2_scbEnableWake)
#endif /* (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG) */

/* Defines maximum number of SCB pins */
#if (!SCB_2_CY_SCBIP_V1)
    #define SCB_2_SCB_PINS_NUMBER    (7u)
#else
    #define SCB_2_SCB_PINS_NUMBER    (2u)
#endif /* (!SCB_2_CY_SCBIP_V1) */

#endif /* (CY_SCB_PVT_SCB_2_H) */


/* [] END OF FILE */

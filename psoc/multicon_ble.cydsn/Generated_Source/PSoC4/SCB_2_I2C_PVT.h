/***************************************************************************//**
* \file .h
* \version 3.20
*
* \brief
*  This private file provides constants and parameter values for the
*  SCB Component in I2C mode.
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

#if !defined(CY_SCB_I2C_PVT_SCB_2_H)
#define CY_SCB_I2C_PVT_SCB_2_H

#include "SCB_2_I2C.h"


/***************************************
*     Private Global Vars
***************************************/

extern volatile uint8 SCB_2_state; /* Current state of I2C FSM */

#if(SCB_2_I2C_SLAVE_CONST)
    extern volatile uint8 SCB_2_slStatus;          /* Slave Status */

    /* Receive buffer variables */
    extern volatile uint8 * SCB_2_slWrBufPtr;      /* Pointer to Receive buffer  */
    extern volatile uint32  SCB_2_slWrBufSize;     /* Slave Receive buffer size  */
    extern volatile uint32  SCB_2_slWrBufIndex;    /* Slave Receive buffer Index */

    /* Transmit buffer variables */
    extern volatile uint8 * SCB_2_slRdBufPtr;      /* Pointer to Transmit buffer  */
    extern volatile uint32  SCB_2_slRdBufSize;     /* Slave Transmit buffer size  */
    extern volatile uint32  SCB_2_slRdBufIndex;    /* Slave Transmit buffer Index */
    extern volatile uint32  SCB_2_slRdBufIndexTmp; /* Slave Transmit buffer Index Tmp */
    extern volatile uint8   SCB_2_slOverFlowCount; /* Slave Transmit Overflow counter */
#endif /* (SCB_2_I2C_SLAVE_CONST) */

#if(SCB_2_I2C_MASTER_CONST)
    extern volatile uint16 SCB_2_mstrStatus;      /* Master Status byte  */
    extern volatile uint8  SCB_2_mstrControl;     /* Master Control byte */

    /* Receive buffer variables */
    extern volatile uint8 * SCB_2_mstrRdBufPtr;   /* Pointer to Master Read buffer */
    extern volatile uint32  SCB_2_mstrRdBufSize;  /* Master Read buffer size       */
    extern volatile uint32  SCB_2_mstrRdBufIndex; /* Master Read buffer Index      */

    /* Transmit buffer variables */
    extern volatile uint8 * SCB_2_mstrWrBufPtr;   /* Pointer to Master Write buffer */
    extern volatile uint32  SCB_2_mstrWrBufSize;  /* Master Write buffer size       */
    extern volatile uint32  SCB_2_mstrWrBufIndex; /* Master Write buffer Index      */
    extern volatile uint32  SCB_2_mstrWrBufIndexTmp; /* Master Write buffer Index Tmp */
#endif /* (SCB_2_I2C_MASTER_CONST) */

#if (SCB_2_I2C_CUSTOM_ADDRESS_HANDLER_CONST)
    extern uint32 (*SCB_2_customAddressHandler) (void);
#endif /* (SCB_2_I2C_CUSTOM_ADDRESS_HANDLER_CONST) */

/***************************************
*     Private Function Prototypes
***************************************/

#if(SCB_2_SCB_MODE_I2C_CONST_CFG)
    void SCB_2_I2CInit(void);
#endif /* (SCB_2_SCB_MODE_I2C_CONST_CFG) */

void SCB_2_I2CStop(void);
void SCB_2_I2CSaveConfig(void);
void SCB_2_I2CRestoreConfig(void);

#if(SCB_2_I2C_MASTER_CONST)
    void SCB_2_I2CReStartGeneration(void);
#endif /* (SCB_2_I2C_MASTER_CONST) */

#endif /* (CY_SCB_I2C_PVT_SCB_2_H) */


/* [] END OF FILE */

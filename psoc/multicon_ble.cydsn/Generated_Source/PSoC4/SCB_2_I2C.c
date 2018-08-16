/***************************************************************************//**
* \file SCB_2_I2C.c
* \version 3.20
*
* \brief
*  This file provides the source code to the API for the SCB Component in
*  I2C mode.
*
* Note:
*
*******************************************************************************
* \copyright
* Copyright 2013-2016, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
*******************************************************************************/

#include "SCB_2_PVT.h"
#include "SCB_2_I2C_PVT.h"


/***************************************
*      I2C Private Vars
***************************************/

volatile uint8 SCB_2_state;  /* Current state of I2C FSM */

#if(SCB_2_SCB_MODE_UNCONFIG_CONST_CFG)

    /***************************************
    *  Configuration Structure Initialization
    ***************************************/

    /* Constant configuration of I2C */
    const SCB_2_I2C_INIT_STRUCT SCB_2_configI2C =
    {
        SCB_2_I2C_MODE,
        SCB_2_I2C_OVS_FACTOR_LOW,
        SCB_2_I2C_OVS_FACTOR_HIGH,
        SCB_2_I2C_MEDIAN_FILTER_ENABLE,
        SCB_2_I2C_SLAVE_ADDRESS,
        SCB_2_I2C_SLAVE_ADDRESS_MASK,
        SCB_2_I2C_ACCEPT_ADDRESS,
        SCB_2_I2C_WAKE_ENABLE,
        SCB_2_I2C_BYTE_MODE_ENABLE,
        SCB_2_I2C_DATA_RATE,
        SCB_2_I2C_ACCEPT_GENERAL_CALL,
    };

    /*******************************************************************************
    * Function Name: SCB_2_I2CInit
    ****************************************************************************//**
    *
    *
    *  Configures the SCB_2 for I2C operation.
    *
    *  This function is intended specifically to be used when the SCB_2 
    *  configuration is set to “Unconfigured SCB_2” in the customizer. 
    *  After initializing the SCB_2 in I2C mode using this function, 
    *  the component can be enabled using the SCB_2_Start() or 
    * SCB_2_Enable() function.
    *  This function uses a pointer to a structure that provides the configuration 
    *  settings. This structure contains the same information that would otherwise 
    *  be provided by the customizer settings.
    *
    *  \param config: pointer to a structure that contains the following list of 
    *   fields. These fields match the selections available in the customizer. 
    *   Refer to the customizer for further description of the settings.
    *
    *******************************************************************************/
    void SCB_2_I2CInit(const SCB_2_I2C_INIT_STRUCT *config)
    {
        uint32 medianFilter;
        uint32 locEnableWake;

        if(NULL == config)
        {
            CYASSERT(0u != 0u); /* Halt execution due to bad function parameter */
        }
        else
        {
            /* Configure pins */
            SCB_2_SetPins(SCB_2_SCB_MODE_I2C, SCB_2_DUMMY_PARAM,
                                     SCB_2_DUMMY_PARAM);

            /* Store internal configuration */
            SCB_2_scbMode       = (uint8) SCB_2_SCB_MODE_I2C;
            SCB_2_scbEnableWake = (uint8) config->enableWake;
            SCB_2_scbEnableIntr = (uint8) SCB_2_SCB_IRQ_INTERNAL;

            SCB_2_mode          = (uint8) config->mode;
            SCB_2_acceptAddr    = (uint8) config->acceptAddr;

        #if (SCB_2_CY_SCBIP_V0)
            /* Adjust SDA filter settings. Ticket ID#150521 */
            SCB_2_SET_I2C_CFG_SDA_FILT_TRIM(SCB_2_EC_AM_I2C_CFG_SDA_FILT_TRIM);
        #endif /* (SCB_2_CY_SCBIP_V0) */

            /* Adjust AF and DF filter settings. Ticket ID#176179 */
            if (((SCB_2_I2C_MODE_SLAVE != config->mode) &&
                 (config->dataRate <= SCB_2_I2C_DATA_RATE_FS_MODE_MAX)) ||
                 (SCB_2_I2C_MODE_SLAVE == config->mode))
            {
                /* AF = 1, DF = 0 */
                SCB_2_I2C_CFG_ANALOG_FITER_ENABLE;
                medianFilter = SCB_2_DIGITAL_FILTER_DISABLE;
            }
            else
            {
                /* AF = 0, DF = 1 */
                SCB_2_I2C_CFG_ANALOG_FITER_DISABLE;
                medianFilter = SCB_2_DIGITAL_FILTER_ENABLE;
            }

        #if (!SCB_2_CY_SCBIP_V0)
            locEnableWake = (SCB_2_I2C_MULTI_MASTER_SLAVE) ? (0u) : (config->enableWake);
        #else
            locEnableWake = config->enableWake;
        #endif /* (!SCB_2_CY_SCBIP_V0) */

            /* Configure I2C interface */
            SCB_2_CTRL_REG     = SCB_2_GET_CTRL_BYTE_MODE  (config->enableByteMode) |
                                            SCB_2_GET_CTRL_ADDR_ACCEPT(config->acceptAddr)     |
                                            SCB_2_GET_CTRL_EC_AM_MODE (locEnableWake);

            SCB_2_I2C_CTRL_REG = SCB_2_GET_I2C_CTRL_HIGH_PHASE_OVS(config->oversampleHigh) |
                    SCB_2_GET_I2C_CTRL_LOW_PHASE_OVS (config->oversampleLow)                          |
                    SCB_2_GET_I2C_CTRL_S_GENERAL_IGNORE((uint32)(0u == config->acceptGeneralAddr))    |
                    SCB_2_GET_I2C_CTRL_SL_MSTR_MODE  (config->mode);

            /* Configure RX direction */
            SCB_2_RX_CTRL_REG      = SCB_2_GET_RX_CTRL_MEDIAN(medianFilter) |
                                                SCB_2_I2C_RX_CTRL;
            SCB_2_RX_FIFO_CTRL_REG = SCB_2_CLEAR_REG;

            /* Set default address and mask */
            SCB_2_RX_MATCH_REG    = ((SCB_2_I2C_SLAVE) ?
                                                (SCB_2_GET_I2C_8BIT_ADDRESS(config->slaveAddr) |
                                                 SCB_2_GET_RX_MATCH_MASK(config->slaveAddrMask)) :
                                                (SCB_2_CLEAR_REG));


            /* Configure TX direction */
            SCB_2_TX_CTRL_REG      = SCB_2_I2C_TX_CTRL;
            SCB_2_TX_FIFO_CTRL_REG = SCB_2_CLEAR_REG;

            /* Configure interrupt with I2C handler but do not enable it */
            CyIntDisable    (SCB_2_ISR_NUMBER);
            CyIntSetPriority(SCB_2_ISR_NUMBER, SCB_2_ISR_PRIORITY);
            (void) CyIntSetVector(SCB_2_ISR_NUMBER, &SCB_2_I2C_ISR);

            /* Configure interrupt sources */
        #if(!SCB_2_CY_SCBIP_V1)
            SCB_2_INTR_SPI_EC_MASK_REG = SCB_2_NO_INTR_SOURCES;
        #endif /* (!SCB_2_CY_SCBIP_V1) */

            SCB_2_INTR_I2C_EC_MASK_REG = SCB_2_NO_INTR_SOURCES;
            SCB_2_INTR_RX_MASK_REG     = SCB_2_NO_INTR_SOURCES;
            SCB_2_INTR_TX_MASK_REG     = SCB_2_NO_INTR_SOURCES;

            SCB_2_INTR_SLAVE_MASK_REG  = ((SCB_2_I2C_SLAVE) ?
                            (SCB_2_GET_INTR_SLAVE_I2C_GENERAL(config->acceptGeneralAddr) |
                             SCB_2_I2C_INTR_SLAVE_MASK) : (SCB_2_CLEAR_REG));

            SCB_2_INTR_MASTER_MASK_REG = ((SCB_2_I2C_MASTER) ?
                                                     (SCB_2_I2C_INTR_MASTER_MASK) :
                                                     (SCB_2_CLEAR_REG));

            /* Configure global variables */
            SCB_2_state = SCB_2_I2C_FSM_IDLE;

            /* Internal slave variables */
            SCB_2_slStatus        = 0u;
            SCB_2_slRdBufIndex    = 0u;
            SCB_2_slWrBufIndex    = 0u;
            SCB_2_slOverFlowCount = 0u;

            /* Internal master variables */
            SCB_2_mstrStatus     = 0u;
            SCB_2_mstrRdBufIndex = 0u;
            SCB_2_mstrWrBufIndex = 0u;
        }
    }

#else

    /*******************************************************************************
    * Function Name: SCB_2_I2CInit
    ****************************************************************************//**
    *
    *  Configures the SCB for the I2C operation.
    *
    *******************************************************************************/
    void SCB_2_I2CInit(void)
    {
    #if(SCB_2_CY_SCBIP_V0)
        /* Adjust SDA filter settings. Ticket ID#150521 */
        SCB_2_SET_I2C_CFG_SDA_FILT_TRIM(SCB_2_EC_AM_I2C_CFG_SDA_FILT_TRIM);
    #endif /* (SCB_2_CY_SCBIP_V0) */

        /* Adjust AF and DF filter settings. Ticket ID#176179 */
        SCB_2_I2C_CFG_ANALOG_FITER_ENABLE_ADJ;

        /* Configure I2C interface */
        SCB_2_CTRL_REG     = SCB_2_I2C_DEFAULT_CTRL;
        SCB_2_I2C_CTRL_REG = SCB_2_I2C_DEFAULT_I2C_CTRL;

        /* Configure RX direction */
        SCB_2_RX_CTRL_REG      = SCB_2_I2C_DEFAULT_RX_CTRL;
        SCB_2_RX_FIFO_CTRL_REG = SCB_2_I2C_DEFAULT_RX_FIFO_CTRL;

        /* Set default address and mask */
        SCB_2_RX_MATCH_REG     = SCB_2_I2C_DEFAULT_RX_MATCH;

        /* Configure TX direction */
        SCB_2_TX_CTRL_REG      = SCB_2_I2C_DEFAULT_TX_CTRL;
        SCB_2_TX_FIFO_CTRL_REG = SCB_2_I2C_DEFAULT_TX_FIFO_CTRL;

        /* Configure interrupt with I2C handler but do not enable it */
        CyIntDisable    (SCB_2_ISR_NUMBER);
        CyIntSetPriority(SCB_2_ISR_NUMBER, SCB_2_ISR_PRIORITY);
    #if(!SCB_2_I2C_EXTERN_INTR_HANDLER)
        (void) CyIntSetVector(SCB_2_ISR_NUMBER, &SCB_2_I2C_ISR);
    #endif /* (SCB_2_I2C_EXTERN_INTR_HANDLER) */

        /* Configure interrupt sources */
    #if(!SCB_2_CY_SCBIP_V1)
        SCB_2_INTR_SPI_EC_MASK_REG = SCB_2_I2C_DEFAULT_INTR_SPI_EC_MASK;
    #endif /* (!SCB_2_CY_SCBIP_V1) */

        SCB_2_INTR_I2C_EC_MASK_REG = SCB_2_I2C_DEFAULT_INTR_I2C_EC_MASK;
        SCB_2_INTR_SLAVE_MASK_REG  = SCB_2_I2C_DEFAULT_INTR_SLAVE_MASK;
        SCB_2_INTR_MASTER_MASK_REG = SCB_2_I2C_DEFAULT_INTR_MASTER_MASK;
        SCB_2_INTR_RX_MASK_REG     = SCB_2_I2C_DEFAULT_INTR_RX_MASK;
        SCB_2_INTR_TX_MASK_REG     = SCB_2_I2C_DEFAULT_INTR_TX_MASK;

        /* Configure global variables */
        SCB_2_state = SCB_2_I2C_FSM_IDLE;

    #if(SCB_2_I2C_SLAVE)
        /* Internal slave variable */
        SCB_2_slStatus        = 0u;
        SCB_2_slRdBufIndex    = 0u;
        SCB_2_slWrBufIndex    = 0u;
        SCB_2_slOverFlowCount = 0u;
    #endif /* (SCB_2_I2C_SLAVE) */

    #if(SCB_2_I2C_MASTER)
    /* Internal master variable */
        SCB_2_mstrStatus     = 0u;
        SCB_2_mstrRdBufIndex = 0u;
        SCB_2_mstrWrBufIndex = 0u;
    #endif /* (SCB_2_I2C_MASTER) */
    }
#endif /* (SCB_2_SCB_MODE_UNCONFIG_CONST_CFG) */


/*******************************************************************************
* Function Name: SCB_2_I2CStop
****************************************************************************//**
*
*  Resets the I2C FSM into the default state.
*
*******************************************************************************/
void SCB_2_I2CStop(void)
{
    SCB_2_state = SCB_2_I2C_FSM_IDLE;
}


#if(SCB_2_I2C_WAKE_ENABLE_CONST)
    /*******************************************************************************
    * Function Name: SCB_2_I2CSaveConfig
    ****************************************************************************//**
    *
    *  Enables SCB_2_INTR_I2C_EC_WAKE_UP interrupt source. This interrupt
    *  triggers on address match and wakes up device.
    *
    *******************************************************************************/
    void SCB_2_I2CSaveConfig(void)
    {
    #if (!SCB_2_CY_SCBIP_V0)
        #if (SCB_2_I2C_MULTI_MASTER_SLAVE_CONST && SCB_2_I2C_WAKE_ENABLE_CONST)
            /* Enable externally clocked address match if it was not enabled before.
            * This applicable only for Multi-Master-Slave. Ticket ID#192742 */
            if (0u == (SCB_2_CTRL_REG & SCB_2_CTRL_EC_AM_MODE))
            {
                /* Enable external address match logic */
                SCB_2_Stop();
                SCB_2_CTRL_REG |= SCB_2_CTRL_EC_AM_MODE;
                SCB_2_Enable();
            }
        #endif /* (SCB_2_I2C_MULTI_MASTER_SLAVE_CONST) */

        #if (SCB_2_SCB_CLK_INTERNAL)
            /* Disable clock to internal address match logic. Ticket ID#187931 */
            SCB_2_SCBCLK_Stop();
        #endif /* (SCB_2_SCB_CLK_INTERNAL) */
    #endif /* (!SCB_2_CY_SCBIP_V0) */

        SCB_2_SetI2CExtClkInterruptMode(SCB_2_INTR_I2C_EC_WAKE_UP);
    }


    /*******************************************************************************
    * Function Name: SCB_2_I2CRestoreConfig
    ****************************************************************************//**
    *
    *  Disables SCB_2_INTR_I2C_EC_WAKE_UP interrupt source. This interrupt
    *  triggers on address match and wakes up device.
    *
    *******************************************************************************/
    void SCB_2_I2CRestoreConfig(void)
    {
        /* Disable wakeup interrupt on address match */
        SCB_2_SetI2CExtClkInterruptMode(SCB_2_NO_INTR_SOURCES);

    #if (!SCB_2_CY_SCBIP_V0)
        #if (SCB_2_SCB_CLK_INTERNAL)
            /* Enable clock to internal address match logic. Ticket ID#187931 */
            SCB_2_SCBCLK_Start();
        #endif /* (SCB_2_SCB_CLK_INTERNAL) */
    #endif /* (!SCB_2_CY_SCBIP_V0) */
    }
#endif /* (SCB_2_I2C_WAKE_ENABLE_CONST) */


/* [] END OF FILE */

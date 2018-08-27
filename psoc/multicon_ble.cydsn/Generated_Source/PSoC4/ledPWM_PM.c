/*******************************************************************************
* File Name: ledPWM_PM.c
* Version 3.30
*
* Description:
*  This file provides the power management source code to API for the
*  PWM.
*
* Note:
*
********************************************************************************
* Copyright 2008-2014, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
*******************************************************************************/

#include "ledPWM.h"

static ledPWM_backupStruct ledPWM_backup;


/*******************************************************************************
* Function Name: ledPWM_SaveConfig
********************************************************************************
*
* Summary:
*  Saves the current user configuration of the component.
*
* Parameters:
*  None
*
* Return:
*  None
*
* Global variables:
*  ledPWM_backup:  Variables of this global structure are modified to
*  store the values of non retention configuration registers when Sleep() API is
*  called.
*
*******************************************************************************/
void ledPWM_SaveConfig(void) 
{

    #if(!ledPWM_UsingFixedFunction)
        #if(!ledPWM_PWMModeIsCenterAligned)
            ledPWM_backup.PWMPeriod = ledPWM_ReadPeriod();
        #endif /* (!ledPWM_PWMModeIsCenterAligned) */
        ledPWM_backup.PWMUdb = ledPWM_ReadCounter();
        #if (ledPWM_UseStatus)
            ledPWM_backup.InterruptMaskValue = ledPWM_STATUS_MASK;
        #endif /* (ledPWM_UseStatus) */

        #if(ledPWM_DeadBandMode == ledPWM__B_PWM__DBM_256_CLOCKS || \
            ledPWM_DeadBandMode == ledPWM__B_PWM__DBM_2_4_CLOCKS)
            ledPWM_backup.PWMdeadBandValue = ledPWM_ReadDeadTime();
        #endif /*  deadband count is either 2-4 clocks or 256 clocks */

        #if(ledPWM_KillModeMinTime)
             ledPWM_backup.PWMKillCounterPeriod = ledPWM_ReadKillTime();
        #endif /* (ledPWM_KillModeMinTime) */

        #if(ledPWM_UseControl)
            ledPWM_backup.PWMControlRegister = ledPWM_ReadControlRegister();
        #endif /* (ledPWM_UseControl) */
    #endif  /* (!ledPWM_UsingFixedFunction) */
}


/*******************************************************************************
* Function Name: ledPWM_RestoreConfig
********************************************************************************
*
* Summary:
*  Restores the current user configuration of the component.
*
* Parameters:
*  None
*
* Return:
*  None
*
* Global variables:
*  ledPWM_backup:  Variables of this global structure are used to
*  restore the values of non retention registers on wakeup from sleep mode.
*
*******************************************************************************/
void ledPWM_RestoreConfig(void) 
{
        #if(!ledPWM_UsingFixedFunction)
            #if(!ledPWM_PWMModeIsCenterAligned)
                ledPWM_WritePeriod(ledPWM_backup.PWMPeriod);
            #endif /* (!ledPWM_PWMModeIsCenterAligned) */

            ledPWM_WriteCounter(ledPWM_backup.PWMUdb);

            #if (ledPWM_UseStatus)
                ledPWM_STATUS_MASK = ledPWM_backup.InterruptMaskValue;
            #endif /* (ledPWM_UseStatus) */

            #if(ledPWM_DeadBandMode == ledPWM__B_PWM__DBM_256_CLOCKS || \
                ledPWM_DeadBandMode == ledPWM__B_PWM__DBM_2_4_CLOCKS)
                ledPWM_WriteDeadTime(ledPWM_backup.PWMdeadBandValue);
            #endif /* deadband count is either 2-4 clocks or 256 clocks */

            #if(ledPWM_KillModeMinTime)
                ledPWM_WriteKillTime(ledPWM_backup.PWMKillCounterPeriod);
            #endif /* (ledPWM_KillModeMinTime) */

            #if(ledPWM_UseControl)
                ledPWM_WriteControlRegister(ledPWM_backup.PWMControlRegister);
            #endif /* (ledPWM_UseControl) */
        #endif  /* (!ledPWM_UsingFixedFunction) */
    }


/*******************************************************************************
* Function Name: ledPWM_Sleep
********************************************************************************
*
* Summary:
*  Disables block's operation and saves the user configuration. Should be called
*  just prior to entering sleep.
*
* Parameters:
*  None
*
* Return:
*  None
*
* Global variables:
*  ledPWM_backup.PWMEnableState:  Is modified depending on the enable
*  state of the block before entering sleep mode.
*
*******************************************************************************/
void ledPWM_Sleep(void) 
{
    #if(ledPWM_UseControl)
        if(ledPWM_CTRL_ENABLE == (ledPWM_CONTROL & ledPWM_CTRL_ENABLE))
        {
            /*Component is enabled */
            ledPWM_backup.PWMEnableState = 1u;
        }
        else
        {
            /* Component is disabled */
            ledPWM_backup.PWMEnableState = 0u;
        }
    #endif /* (ledPWM_UseControl) */

    /* Stop component */
    ledPWM_Stop();

    /* Save registers configuration */
    ledPWM_SaveConfig();
}


/*******************************************************************************
* Function Name: ledPWM_Wakeup
********************************************************************************
*
* Summary:
*  Restores and enables the user configuration. Should be called just after
*  awaking from sleep.
*
* Parameters:
*  None
*
* Return:
*  None
*
* Global variables:
*  ledPWM_backup.pwmEnable:  Is used to restore the enable state of
*  block on wakeup from sleep mode.
*
*******************************************************************************/
void ledPWM_Wakeup(void) 
{
     /* Restore registers values */
    ledPWM_RestoreConfig();

    if(ledPWM_backup.PWMEnableState != 0u)
    {
        /* Enable component's operation */
        ledPWM_Enable();
    } /* Do nothing if component's block was disabled before */

}


/* [] END OF FILE */

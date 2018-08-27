/*******************************************************************************
* File Name: ledPWM.c
* Version 3.30
*
* Description:
*  The PWM User Module consist of an 8 or 16-bit counter with two 8 or 16-bit
*  comparitors. Each instance of this user module is capable of generating
*  two PWM outputs with the same period. The pulse width is selectable between
*  1 and 255/65535. The period is selectable between 2 and 255/65536 clocks.
*  The compare value output may be configured to be active when the present
*  counter is less than or less than/equal to the compare value.
*  A terminal count output is also provided. It generates a pulse one clock
*  width wide when the counter is equal to zero.
*
* Note:
*
*******************************************************************************
* Copyright 2008-2014, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
********************************************************************************/

#include "ledPWM.h"

/* Error message for removed <resource> through optimization */
#ifdef ledPWM_PWMUDB_genblk1_ctrlreg__REMOVED
    #error PWM_v3_30 detected with a constant 0 for the enable or \
         constant 1 for reset. This will prevent the component from operating.
#endif /* ledPWM_PWMUDB_genblk1_ctrlreg__REMOVED */

uint8 ledPWM_initVar = 0u;


/*******************************************************************************
* Function Name: ledPWM_Start
********************************************************************************
*
* Summary:
*  The start function initializes the pwm with the default values, the
*  enables the counter to begin counting.  It does not enable interrupts,
*  the EnableInt command should be called if interrupt generation is required.
*
* Parameters:
*  None
*
* Return:
*  None
*
* Global variables:
*  ledPWM_initVar: Is modified when this function is called for the
*   first time. Is used to ensure that initialization happens only once.
*
*******************************************************************************/
void ledPWM_Start(void) 
{
    /* If not Initialized then initialize all required hardware and software */
    if(ledPWM_initVar == 0u)
    {
        ledPWM_Init();
        ledPWM_initVar = 1u;
    }
    ledPWM_Enable();

}


/*******************************************************************************
* Function Name: ledPWM_Init
********************************************************************************
*
* Summary:
*  Initialize component's parameters to the parameters set by user in the
*  customizer of the component placed onto schematic. Usually called in
*  ledPWM_Start().
*
* Parameters:
*  None
*
* Return:
*  None
*
*******************************************************************************/
void ledPWM_Init(void) 
{
    #if (ledPWM_UsingFixedFunction || ledPWM_UseControl)
        uint8 ctrl;
    #endif /* (ledPWM_UsingFixedFunction || ledPWM_UseControl) */

    #if(!ledPWM_UsingFixedFunction)
        #if(ledPWM_UseStatus)
            /* Interrupt State Backup for Critical Region*/
            uint8 ledPWM_interruptState;
        #endif /* (ledPWM_UseStatus) */
    #endif /* (!ledPWM_UsingFixedFunction) */

    #if (ledPWM_UsingFixedFunction)
        /* You are allowed to write the compare value (FF only) */
        ledPWM_CONTROL |= ledPWM_CFG0_MODE;
        #if (ledPWM_DeadBand2_4)
            ledPWM_CONTROL |= ledPWM_CFG0_DB;
        #endif /* (ledPWM_DeadBand2_4) */

        ctrl = ledPWM_CONTROL3 & ((uint8 )(~ledPWM_CTRL_CMPMODE1_MASK));
        ledPWM_CONTROL3 = ctrl | ledPWM_DEFAULT_COMPARE1_MODE;

         /* Clear and Set SYNCTC and SYNCCMP bits of RT1 register */
        ledPWM_RT1 &= ((uint8)(~ledPWM_RT1_MASK));
        ledPWM_RT1 |= ledPWM_SYNC;

        /*Enable DSI Sync all all inputs of the PWM*/
        ledPWM_RT1 &= ((uint8)(~ledPWM_SYNCDSI_MASK));
        ledPWM_RT1 |= ledPWM_SYNCDSI_EN;

    #elif (ledPWM_UseControl)
        /* Set the default compare mode defined in the parameter */
        ctrl = ledPWM_CONTROL & ((uint8)(~ledPWM_CTRL_CMPMODE2_MASK)) &
                ((uint8)(~ledPWM_CTRL_CMPMODE1_MASK));
        ledPWM_CONTROL = ctrl | ledPWM_DEFAULT_COMPARE2_MODE |
                                   ledPWM_DEFAULT_COMPARE1_MODE;
    #endif /* (ledPWM_UsingFixedFunction) */

    #if (!ledPWM_UsingFixedFunction)
        #if (ledPWM_Resolution == 8)
            /* Set FIFO 0 to 1 byte register for period*/
            ledPWM_AUX_CONTROLDP0 |= (ledPWM_AUX_CTRL_FIFO0_CLR);
        #else /* (ledPWM_Resolution == 16)*/
            /* Set FIFO 0 to 1 byte register for period */
            ledPWM_AUX_CONTROLDP0 |= (ledPWM_AUX_CTRL_FIFO0_CLR);
            ledPWM_AUX_CONTROLDP1 |= (ledPWM_AUX_CTRL_FIFO0_CLR);
        #endif /* (ledPWM_Resolution == 8) */

        ledPWM_WriteCounter(ledPWM_INIT_PERIOD_VALUE);
    #endif /* (!ledPWM_UsingFixedFunction) */

    ledPWM_WritePeriod(ledPWM_INIT_PERIOD_VALUE);

        #if (ledPWM_UseOneCompareMode)
            ledPWM_WriteCompare(ledPWM_INIT_COMPARE_VALUE1);
        #else
            ledPWM_WriteCompare1(ledPWM_INIT_COMPARE_VALUE1);
            ledPWM_WriteCompare2(ledPWM_INIT_COMPARE_VALUE2);
        #endif /* (ledPWM_UseOneCompareMode) */

        #if (ledPWM_KillModeMinTime)
            ledPWM_WriteKillTime(ledPWM_MinimumKillTime);
        #endif /* (ledPWM_KillModeMinTime) */

        #if (ledPWM_DeadBandUsed)
            ledPWM_WriteDeadTime(ledPWM_INIT_DEAD_TIME);
        #endif /* (ledPWM_DeadBandUsed) */

    #if (ledPWM_UseStatus || ledPWM_UsingFixedFunction)
        ledPWM_SetInterruptMode(ledPWM_INIT_INTERRUPTS_MODE);
    #endif /* (ledPWM_UseStatus || ledPWM_UsingFixedFunction) */

    #if (ledPWM_UsingFixedFunction)
        /* Globally Enable the Fixed Function Block chosen */
        ledPWM_GLOBAL_ENABLE |= ledPWM_BLOCK_EN_MASK;
        /* Set the Interrupt source to come from the status register */
        ledPWM_CONTROL2 |= ledPWM_CTRL2_IRQ_SEL;
    #else
        #if(ledPWM_UseStatus)

            /* CyEnterCriticalRegion and CyExitCriticalRegion are used to mark following region critical*/
            /* Enter Critical Region*/
            ledPWM_interruptState = CyEnterCriticalSection();
            /* Use the interrupt output of the status register for IRQ output */
            ledPWM_STATUS_AUX_CTRL |= ledPWM_STATUS_ACTL_INT_EN_MASK;

             /* Exit Critical Region*/
            CyExitCriticalSection(ledPWM_interruptState);

            /* Clear the FIFO to enable the ledPWM_STATUS_FIFOFULL
                   bit to be set on FIFO full. */
            ledPWM_ClearFIFO();
        #endif /* (ledPWM_UseStatus) */
    #endif /* (ledPWM_UsingFixedFunction) */
}


/*******************************************************************************
* Function Name: ledPWM_Enable
********************************************************************************
*
* Summary:
*  Enables the PWM block operation
*
* Parameters:
*  None
*
* Return:
*  None
*
* Side Effects:
*  This works only if software enable mode is chosen
*
*******************************************************************************/
void ledPWM_Enable(void) 
{
    /* Globally Enable the Fixed Function Block chosen */
    #if (ledPWM_UsingFixedFunction)
        ledPWM_GLOBAL_ENABLE |= ledPWM_BLOCK_EN_MASK;
        ledPWM_GLOBAL_STBY_ENABLE |= ledPWM_BLOCK_STBY_EN_MASK;
    #endif /* (ledPWM_UsingFixedFunction) */

    /* Enable the PWM from the control register  */
    #if (ledPWM_UseControl || ledPWM_UsingFixedFunction)
        ledPWM_CONTROL |= ledPWM_CTRL_ENABLE;
    #endif /* (ledPWM_UseControl || ledPWM_UsingFixedFunction) */
}


/*******************************************************************************
* Function Name: ledPWM_Stop
********************************************************************************
*
* Summary:
*  The stop function halts the PWM, but does not change any modes or disable
*  interrupts.
*
* Parameters:
*  None
*
* Return:
*  None
*
* Side Effects:
*  If the Enable mode is set to Hardware only then this function
*  has no effect on the operation of the PWM
*
*******************************************************************************/
void ledPWM_Stop(void) 
{
    #if (ledPWM_UseControl || ledPWM_UsingFixedFunction)
        ledPWM_CONTROL &= ((uint8)(~ledPWM_CTRL_ENABLE));
    #endif /* (ledPWM_UseControl || ledPWM_UsingFixedFunction) */

    /* Globally disable the Fixed Function Block chosen */
    #if (ledPWM_UsingFixedFunction)
        ledPWM_GLOBAL_ENABLE &= ((uint8)(~ledPWM_BLOCK_EN_MASK));
        ledPWM_GLOBAL_STBY_ENABLE &= ((uint8)(~ledPWM_BLOCK_STBY_EN_MASK));
    #endif /* (ledPWM_UsingFixedFunction) */
}

#if (ledPWM_UseOneCompareMode)
    #if (ledPWM_CompareMode1SW)


        /*******************************************************************************
        * Function Name: ledPWM_SetCompareMode
        ********************************************************************************
        *
        * Summary:
        *  This function writes the Compare Mode for the pwm output when in Dither mode,
        *  Center Align Mode or One Output Mode.
        *
        * Parameters:
        *  comparemode:  The new compare mode for the PWM output. Use the compare types
        *                defined in the H file as input arguments.
        *
        * Return:
        *  None
        *
        *******************************************************************************/
        void ledPWM_SetCompareMode(uint8 comparemode) 
        {
            #if(ledPWM_UsingFixedFunction)

                #if(0 != ledPWM_CTRL_CMPMODE1_SHIFT)
                    uint8 comparemodemasked = ((uint8)((uint8)comparemode << ledPWM_CTRL_CMPMODE1_SHIFT));
                #else
                    uint8 comparemodemasked = comparemode;
                #endif /* (0 != ledPWM_CTRL_CMPMODE1_SHIFT) */

                ledPWM_CONTROL3 &= ((uint8)(~ledPWM_CTRL_CMPMODE1_MASK)); /*Clear Existing Data */
                ledPWM_CONTROL3 |= comparemodemasked;

            #elif (ledPWM_UseControl)

                #if(0 != ledPWM_CTRL_CMPMODE1_SHIFT)
                    uint8 comparemode1masked = ((uint8)((uint8)comparemode << ledPWM_CTRL_CMPMODE1_SHIFT)) &
                                                ledPWM_CTRL_CMPMODE1_MASK;
                #else
                    uint8 comparemode1masked = comparemode & ledPWM_CTRL_CMPMODE1_MASK;
                #endif /* (0 != ledPWM_CTRL_CMPMODE1_SHIFT) */

                #if(0 != ledPWM_CTRL_CMPMODE2_SHIFT)
                    uint8 comparemode2masked = ((uint8)((uint8)comparemode << ledPWM_CTRL_CMPMODE2_SHIFT)) &
                                               ledPWM_CTRL_CMPMODE2_MASK;
                #else
                    uint8 comparemode2masked = comparemode & ledPWM_CTRL_CMPMODE2_MASK;
                #endif /* (0 != ledPWM_CTRL_CMPMODE2_SHIFT) */

                /*Clear existing mode */
                ledPWM_CONTROL &= ((uint8)(~(ledPWM_CTRL_CMPMODE1_MASK |
                                            ledPWM_CTRL_CMPMODE2_MASK)));
                ledPWM_CONTROL |= (comparemode1masked | comparemode2masked);

            #else
                uint8 temp = comparemode;
            #endif /* (ledPWM_UsingFixedFunction) */
        }
    #endif /* ledPWM_CompareMode1SW */

#else /* UseOneCompareMode */

    #if (ledPWM_CompareMode1SW)


        /*******************************************************************************
        * Function Name: ledPWM_SetCompareMode1
        ********************************************************************************
        *
        * Summary:
        *  This function writes the Compare Mode for the pwm or pwm1 output
        *
        * Parameters:
        *  comparemode:  The new compare mode for the PWM output. Use the compare types
        *                defined in the H file as input arguments.
        *
        * Return:
        *  None
        *
        *******************************************************************************/
        void ledPWM_SetCompareMode1(uint8 comparemode) 
        {
            #if(0 != ledPWM_CTRL_CMPMODE1_SHIFT)
                uint8 comparemodemasked = ((uint8)((uint8)comparemode << ledPWM_CTRL_CMPMODE1_SHIFT)) &
                                           ledPWM_CTRL_CMPMODE1_MASK;
            #else
                uint8 comparemodemasked = comparemode & ledPWM_CTRL_CMPMODE1_MASK;
            #endif /* (0 != ledPWM_CTRL_CMPMODE1_SHIFT) */

            #if (ledPWM_UseControl)
                ledPWM_CONTROL &= ((uint8)(~ledPWM_CTRL_CMPMODE1_MASK)); /*Clear existing mode */
                ledPWM_CONTROL |= comparemodemasked;
            #endif /* (ledPWM_UseControl) */
        }
    #endif /* ledPWM_CompareMode1SW */

#if (ledPWM_CompareMode2SW)


    /*******************************************************************************
    * Function Name: ledPWM_SetCompareMode2
    ********************************************************************************
    *
    * Summary:
    *  This function writes the Compare Mode for the pwm or pwm2 output
    *
    * Parameters:
    *  comparemode:  The new compare mode for the PWM output. Use the compare types
    *                defined in the H file as input arguments.
    *
    * Return:
    *  None
    *
    *******************************************************************************/
    void ledPWM_SetCompareMode2(uint8 comparemode) 
    {

        #if(0 != ledPWM_CTRL_CMPMODE2_SHIFT)
            uint8 comparemodemasked = ((uint8)((uint8)comparemode << ledPWM_CTRL_CMPMODE2_SHIFT)) &
                                                 ledPWM_CTRL_CMPMODE2_MASK;
        #else
            uint8 comparemodemasked = comparemode & ledPWM_CTRL_CMPMODE2_MASK;
        #endif /* (0 != ledPWM_CTRL_CMPMODE2_SHIFT) */

        #if (ledPWM_UseControl)
            ledPWM_CONTROL &= ((uint8)(~ledPWM_CTRL_CMPMODE2_MASK)); /*Clear existing mode */
            ledPWM_CONTROL |= comparemodemasked;
        #endif /* (ledPWM_UseControl) */
    }
    #endif /*ledPWM_CompareMode2SW */

#endif /* UseOneCompareMode */


#if (!ledPWM_UsingFixedFunction)


    /*******************************************************************************
    * Function Name: ledPWM_WriteCounter
    ********************************************************************************
    *
    * Summary:
    *  Writes a new counter value directly to the counter register. This will be
    *  implemented for that currently running period and only that period. This API
    *  is valid only for UDB implementation and not available for fixed function
    *  PWM implementation.
    *
    * Parameters:
    *  counter:  The period new period counter value.
    *
    * Return:
    *  None
    *
    * Side Effects:
    *  The PWM Period will be reloaded when a counter value will be a zero
    *
    *******************************************************************************/
    void ledPWM_WriteCounter(uint8 counter) \
                                       
    {
        CY_SET_REG8(ledPWM_COUNTER_LSB_PTR, counter);
    }


    /*******************************************************************************
    * Function Name: ledPWM_ReadCounter
    ********************************************************************************
    *
    * Summary:
    *  This function returns the current value of the counter.  It doesn't matter
    *  if the counter is enabled or running.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  The current value of the counter.
    *
    *******************************************************************************/
    uint8 ledPWM_ReadCounter(void) 
    {
        /* Force capture by reading Accumulator */
        /* Must first do a software capture to be able to read the counter */
        /* It is up to the user code to make sure there isn't already captured data in the FIFO */
          (void)CY_GET_REG8(ledPWM_COUNTERCAP_LSB_PTR_8BIT);

        /* Read the data from the FIFO */
        return (CY_GET_REG8(ledPWM_CAPTURE_LSB_PTR));
    }

    #if (ledPWM_UseStatus)


        /*******************************************************************************
        * Function Name: ledPWM_ClearFIFO
        ********************************************************************************
        *
        * Summary:
        *  This function clears all capture data from the capture FIFO
        *
        * Parameters:
        *  None
        *
        * Return:
        *  None
        *
        *******************************************************************************/
        void ledPWM_ClearFIFO(void) 
        {
            while(0u != (ledPWM_ReadStatusRegister() & ledPWM_STATUS_FIFONEMPTY))
            {
                (void)ledPWM_ReadCapture();
            }
        }

    #endif /* ledPWM_UseStatus */

#endif /* !ledPWM_UsingFixedFunction */


/*******************************************************************************
* Function Name: ledPWM_WritePeriod
********************************************************************************
*
* Summary:
*  This function is used to change the period of the counter.  The new period
*  will be loaded the next time terminal count is detected.
*
* Parameters:
*  period:  Period value. May be between 1 and (2^Resolution)-1.  A value of 0
*           will result in the counter remaining at zero.
*
* Return:
*  None
*
*******************************************************************************/
void ledPWM_WritePeriod(uint8 period) 
{
    #if(ledPWM_UsingFixedFunction)
        CY_SET_REG16(ledPWM_PERIOD_LSB_PTR, (uint16)period);
    #else
        CY_SET_REG8(ledPWM_PERIOD_LSB_PTR, period);
    #endif /* (ledPWM_UsingFixedFunction) */
}

#if (ledPWM_UseOneCompareMode)


    /*******************************************************************************
    * Function Name: ledPWM_WriteCompare
    ********************************************************************************
    *
    * Summary:
    *  This funtion is used to change the compare1 value when the PWM is in Dither
    *  mode. The compare output will reflect the new value on the next UDB clock.
    *  The compare output will be driven high when the present counter value is
    *  compared to the compare value based on the compare mode defined in
    *  Dither Mode.
    *
    * Parameters:
    *  compare:  New compare value.
    *
    * Return:
    *  None
    *
    * Side Effects:
    *  This function is only available if the PWM mode parameter is set to
    *  Dither Mode, Center Aligned Mode or One Output Mode
    *
    *******************************************************************************/
    void ledPWM_WriteCompare(uint8 compare) \
                                       
    {
        #if(ledPWM_UsingFixedFunction)
            CY_SET_REG16(ledPWM_COMPARE1_LSB_PTR, (uint16)compare);
        #else
            CY_SET_REG8(ledPWM_COMPARE1_LSB_PTR, compare);
        #endif /* (ledPWM_UsingFixedFunction) */

        #if (ledPWM_PWMMode == ledPWM__B_PWM__DITHER)
            #if(ledPWM_UsingFixedFunction)
                CY_SET_REG16(ledPWM_COMPARE2_LSB_PTR, (uint16)(compare + 1u));
            #else
                CY_SET_REG8(ledPWM_COMPARE2_LSB_PTR, (compare + 1u));
            #endif /* (ledPWM_UsingFixedFunction) */
        #endif /* (ledPWM_PWMMode == ledPWM__B_PWM__DITHER) */
    }


#else


    /*******************************************************************************
    * Function Name: ledPWM_WriteCompare1
    ********************************************************************************
    *
    * Summary:
    *  This funtion is used to change the compare1 value.  The compare output will
    *  reflect the new value on the next UDB clock.  The compare output will be
    *  driven high when the present counter value is less than or less than or
    *  equal to the compare register, depending on the mode.
    *
    * Parameters:
    *  compare:  New compare value.
    *
    * Return:
    *  None
    *
    *******************************************************************************/
    void ledPWM_WriteCompare1(uint8 compare) \
                                        
    {
        #if(ledPWM_UsingFixedFunction)
            CY_SET_REG16(ledPWM_COMPARE1_LSB_PTR, (uint16)compare);
        #else
            CY_SET_REG8(ledPWM_COMPARE1_LSB_PTR, compare);
        #endif /* (ledPWM_UsingFixedFunction) */
    }


    /*******************************************************************************
    * Function Name: ledPWM_WriteCompare2
    ********************************************************************************
    *
    * Summary:
    *  This funtion is used to change the compare value, for compare1 output.
    *  The compare output will reflect the new value on the next UDB clock.
    *  The compare output will be driven high when the present counter value is
    *  less than or less than or equal to the compare register, depending on the
    *  mode.
    *
    * Parameters:
    *  compare:  New compare value.
    *
    * Return:
    *  None
    *
    *******************************************************************************/
    void ledPWM_WriteCompare2(uint8 compare) \
                                        
    {
        #if(ledPWM_UsingFixedFunction)
            CY_SET_REG16(ledPWM_COMPARE2_LSB_PTR, compare);
        #else
            CY_SET_REG8(ledPWM_COMPARE2_LSB_PTR, compare);
        #endif /* (ledPWM_UsingFixedFunction) */
    }
#endif /* UseOneCompareMode */

#if (ledPWM_DeadBandUsed)


    /*******************************************************************************
    * Function Name: ledPWM_WriteDeadTime
    ********************************************************************************
    *
    * Summary:
    *  This function writes the dead-band counts to the corresponding register
    *
    * Parameters:
    *  deadtime:  Number of counts for dead time
    *
    * Return:
    *  None
    *
    *******************************************************************************/
    void ledPWM_WriteDeadTime(uint8 deadtime) 
    {
        /* If using the Dead Band 1-255 mode then just write the register */
        #if(!ledPWM_DeadBand2_4)
            CY_SET_REG8(ledPWM_DEADBAND_COUNT_PTR, deadtime);
        #else
            /* Otherwise the data has to be masked and offset */
            /* Clear existing data */
            ledPWM_DEADBAND_COUNT &= ((uint8)(~ledPWM_DEADBAND_COUNT_MASK));

            /* Set new dead time */
            #if(ledPWM_DEADBAND_COUNT_SHIFT)
                ledPWM_DEADBAND_COUNT |= ((uint8)((uint8)deadtime << ledPWM_DEADBAND_COUNT_SHIFT)) &
                                                    ledPWM_DEADBAND_COUNT_MASK;
            #else
                ledPWM_DEADBAND_COUNT |= deadtime & ledPWM_DEADBAND_COUNT_MASK;
            #endif /* (ledPWM_DEADBAND_COUNT_SHIFT) */

        #endif /* (!ledPWM_DeadBand2_4) */
    }


    /*******************************************************************************
    * Function Name: ledPWM_ReadDeadTime
    ********************************************************************************
    *
    * Summary:
    *  This function reads the dead-band counts from the corresponding register
    *
    * Parameters:
    *  None
    *
    * Return:
    *  Dead Band Counts
    *
    *******************************************************************************/
    uint8 ledPWM_ReadDeadTime(void) 
    {
        /* If using the Dead Band 1-255 mode then just read the register */
        #if(!ledPWM_DeadBand2_4)
            return (CY_GET_REG8(ledPWM_DEADBAND_COUNT_PTR));
        #else

            /* Otherwise the data has to be masked and offset */
            #if(ledPWM_DEADBAND_COUNT_SHIFT)
                return ((uint8)(((uint8)(ledPWM_DEADBAND_COUNT & ledPWM_DEADBAND_COUNT_MASK)) >>
                                                                           ledPWM_DEADBAND_COUNT_SHIFT));
            #else
                return (ledPWM_DEADBAND_COUNT & ledPWM_DEADBAND_COUNT_MASK);
            #endif /* (ledPWM_DEADBAND_COUNT_SHIFT) */
        #endif /* (!ledPWM_DeadBand2_4) */
    }
#endif /* DeadBandUsed */

#if (ledPWM_UseStatus || ledPWM_UsingFixedFunction)


    /*******************************************************************************
    * Function Name: ledPWM_SetInterruptMode
    ********************************************************************************
    *
    * Summary:
    *  This function configures the interrupts mask control of theinterrupt
    *  source status register.
    *
    * Parameters:
    *  uint8 interruptMode: Bit field containing the interrupt sources enabled
    *
    * Return:
    *  None
    *
    *******************************************************************************/
    void ledPWM_SetInterruptMode(uint8 interruptMode) 
    {
        CY_SET_REG8(ledPWM_STATUS_MASK_PTR, interruptMode);
    }


    /*******************************************************************************
    * Function Name: ledPWM_ReadStatusRegister
    ********************************************************************************
    *
    * Summary:
    *  This function returns the current state of the status register.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  uint8 : Current status register value. The status register bits are:
    *  [7:6] : Unused(0)
    *  [5]   : Kill event output
    *  [4]   : FIFO not empty
    *  [3]   : FIFO full
    *  [2]   : Terminal count
    *  [1]   : Compare output 2
    *  [0]   : Compare output 1
    *
    *******************************************************************************/
    uint8 ledPWM_ReadStatusRegister(void) 
    {
        return (CY_GET_REG8(ledPWM_STATUS_PTR));
    }

#endif /* (ledPWM_UseStatus || ledPWM_UsingFixedFunction) */


#if (ledPWM_UseControl)


    /*******************************************************************************
    * Function Name: ledPWM_ReadControlRegister
    ********************************************************************************
    *
    * Summary:
    *  Returns the current state of the control register. This API is available
    *  only if the control register is not removed.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  uint8 : Current control register value
    *
    *******************************************************************************/
    uint8 ledPWM_ReadControlRegister(void) 
    {
        uint8 result;

        result = CY_GET_REG8(ledPWM_CONTROL_PTR);
        return (result);
    }


    /*******************************************************************************
    * Function Name: ledPWM_WriteControlRegister
    ********************************************************************************
    *
    * Summary:
    *  Sets the bit field of the control register. This API is available only if
    *  the control register is not removed.
    *
    * Parameters:
    *  uint8 control: Control register bit field, The status register bits are:
    *  [7]   : PWM Enable
    *  [6]   : Reset
    *  [5:3] : Compare Mode2
    *  [2:0] : Compare Mode2
    *
    * Return:
    *  None
    *
    *******************************************************************************/
    void ledPWM_WriteControlRegister(uint8 control) 
    {
        CY_SET_REG8(ledPWM_CONTROL_PTR, control);
    }

#endif /* (ledPWM_UseControl) */


#if (!ledPWM_UsingFixedFunction)


    /*******************************************************************************
    * Function Name: ledPWM_ReadCapture
    ********************************************************************************
    *
    * Summary:
    *  Reads the capture value from the capture FIFO.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  uint8/uint16: The current capture value
    *
    *******************************************************************************/
    uint8 ledPWM_ReadCapture(void) 
    {
        return (CY_GET_REG8(ledPWM_CAPTURE_LSB_PTR));
    }

#endif /* (!ledPWM_UsingFixedFunction) */


#if (ledPWM_UseOneCompareMode)


    /*******************************************************************************
    * Function Name: ledPWM_ReadCompare
    ********************************************************************************
    *
    * Summary:
    *  Reads the compare value for the compare output when the PWM Mode parameter is
    *  set to Dither mode, Center Aligned mode, or One Output mode.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  uint8/uint16: Current compare value
    *
    *******************************************************************************/
    uint8 ledPWM_ReadCompare(void) 
    {
        #if(ledPWM_UsingFixedFunction)
            return ((uint8)CY_GET_REG16(ledPWM_COMPARE1_LSB_PTR));
        #else
            return (CY_GET_REG8(ledPWM_COMPARE1_LSB_PTR));
        #endif /* (ledPWM_UsingFixedFunction) */
    }

#else


    /*******************************************************************************
    * Function Name: ledPWM_ReadCompare1
    ********************************************************************************
    *
    * Summary:
    *  Reads the compare value for the compare1 output.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  uint8/uint16: Current compare value.
    *
    *******************************************************************************/
    uint8 ledPWM_ReadCompare1(void) 
    {
        return (CY_GET_REG8(ledPWM_COMPARE1_LSB_PTR));
    }


    /*******************************************************************************
    * Function Name: ledPWM_ReadCompare2
    ********************************************************************************
    *
    * Summary:
    *  Reads the compare value for the compare2 output.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  uint8/uint16: Current compare value.
    *
    *******************************************************************************/
    uint8 ledPWM_ReadCompare2(void) 
    {
        return (CY_GET_REG8(ledPWM_COMPARE2_LSB_PTR));
    }

#endif /* (ledPWM_UseOneCompareMode) */


/*******************************************************************************
* Function Name: ledPWM_ReadPeriod
********************************************************************************
*
* Summary:
*  Reads the period value used by the PWM hardware.
*
* Parameters:
*  None
*
* Return:
*  uint8/16: Period value
*
*******************************************************************************/
uint8 ledPWM_ReadPeriod(void) 
{
    #if(ledPWM_UsingFixedFunction)
        return ((uint8)CY_GET_REG16(ledPWM_PERIOD_LSB_PTR));
    #else
        return (CY_GET_REG8(ledPWM_PERIOD_LSB_PTR));
    #endif /* (ledPWM_UsingFixedFunction) */
}

#if ( ledPWM_KillModeMinTime)


    /*******************************************************************************
    * Function Name: ledPWM_WriteKillTime
    ********************************************************************************
    *
    * Summary:
    *  Writes the kill time value used by the hardware when the Kill Mode
    *  is set to Minimum Time.
    *
    * Parameters:
    *  uint8: Minimum Time kill counts
    *
    * Return:
    *  None
    *
    *******************************************************************************/
    void ledPWM_WriteKillTime(uint8 killtime) 
    {
        CY_SET_REG8(ledPWM_KILLMODEMINTIME_PTR, killtime);
    }


    /*******************************************************************************
    * Function Name: ledPWM_ReadKillTime
    ********************************************************************************
    *
    * Summary:
    *  Reads the kill time value used by the hardware when the Kill Mode is set
    *  to Minimum Time.
    *
    * Parameters:
    *  None
    *
    * Return:
    *  uint8: The current Minimum Time kill counts
    *
    *******************************************************************************/
    uint8 ledPWM_ReadKillTime(void) 
    {
        return (CY_GET_REG8(ledPWM_KILLMODEMINTIME_PTR));
    }

#endif /* ( ledPWM_KillModeMinTime) */

/* [] END OF FILE */

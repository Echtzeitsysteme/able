/*******************************************************************************
* File Name: testPin.c  
* Version 2.20
*
* Description:
*  This file contains APIs to set up the Pins component for low power modes.
*
* Note:
*
********************************************************************************
* Copyright 2015, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions, 
* disclaimers, and limitations in the end user license agreement accompanying 
* the software package with which this file was provided.
*******************************************************************************/

#include "cytypes.h"
#include "testPin.h"

static testPin_BACKUP_STRUCT  testPin_backup = {0u, 0u, 0u};


/*******************************************************************************
* Function Name: testPin_Sleep
****************************************************************************//**
*
* \brief Stores the pin configuration and prepares the pin for entering chip 
*  deep-sleep/hibernate modes. This function applies only to SIO and USBIO pins.
*  It should not be called for GPIO or GPIO_OVT pins.
*
* <b>Note</b> This function is available in PSoC 4 only.
*
* \return 
*  None 
*  
* \sideeffect
*  For SIO pins, this function configures the pin input threshold to CMOS and
*  drive level to Vddio. This is needed for SIO pins when in device 
*  deep-sleep/hibernate modes.
*
* \funcusage
*  \snippet testPin_SUT.c usage_testPin_Sleep_Wakeup
*******************************************************************************/
void testPin_Sleep(void)
{
    #if defined(testPin__PC)
        testPin_backup.pcState = testPin_PC;
    #else
        #if (CY_PSOC4_4200L)
            /* Save the regulator state and put the PHY into suspend mode */
            testPin_backup.usbState = testPin_CR1_REG;
            testPin_USB_POWER_REG |= testPin_USBIO_ENTER_SLEEP;
            testPin_CR1_REG &= testPin_USBIO_CR1_OFF;
        #endif
    #endif
    #if defined(CYIPBLOCK_m0s8ioss_VERSION) && defined(testPin__SIO)
        testPin_backup.sioState = testPin_SIO_REG;
        /* SIO requires unregulated output buffer and single ended input buffer */
        testPin_SIO_REG &= (uint32)(~testPin_SIO_LPM_MASK);
    #endif  
}


/*******************************************************************************
* Function Name: testPin_Wakeup
****************************************************************************//**
*
* \brief Restores the pin configuration that was saved during Pin_Sleep(). This 
* function applies only to SIO and USBIO pins. It should not be called for
* GPIO or GPIO_OVT pins.
*
* For USBIO pins, the wakeup is only triggered for falling edge interrupts.
*
* <b>Note</b> This function is available in PSoC 4 only.
*
* \return 
*  None
*  
* \funcusage
*  Refer to testPin_Sleep() for an example usage.
*******************************************************************************/
void testPin_Wakeup(void)
{
    #if defined(testPin__PC)
        testPin_PC = testPin_backup.pcState;
    #else
        #if (CY_PSOC4_4200L)
            /* Restore the regulator state and come out of suspend mode */
            testPin_USB_POWER_REG &= testPin_USBIO_EXIT_SLEEP_PH1;
            testPin_CR1_REG = testPin_backup.usbState;
            testPin_USB_POWER_REG &= testPin_USBIO_EXIT_SLEEP_PH2;
        #endif
    #endif
    #if defined(CYIPBLOCK_m0s8ioss_VERSION) && defined(testPin__SIO)
        testPin_SIO_REG = testPin_backup.sioState;
    #endif
}


/* [] END OF FILE */

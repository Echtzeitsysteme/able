/*******************************************************************************
* File Name: SCB_1_tx.h  
* Version 2.20
*
* Description:
*  This file contains the Alias definitions for Per-Pin APIs in cypins.h. 
*  Information on using these APIs can be found in the System Reference Guide.
*
* Note:
*
********************************************************************************
* Copyright 2008-2015, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions, 
* disclaimers, and limitations in the end user license agreement accompanying 
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_PINS_SCB_1_tx_ALIASES_H) /* Pins SCB_1_tx_ALIASES_H */
#define CY_PINS_SCB_1_tx_ALIASES_H

#include "cytypes.h"
#include "cyfitter.h"
#include "cypins.h"


/***************************************
*              Constants        
***************************************/
#define SCB_1_tx_0			(SCB_1_tx__0__PC)
#define SCB_1_tx_0_PS		(SCB_1_tx__0__PS)
#define SCB_1_tx_0_PC		(SCB_1_tx__0__PC)
#define SCB_1_tx_0_DR		(SCB_1_tx__0__DR)
#define SCB_1_tx_0_SHIFT	(SCB_1_tx__0__SHIFT)
#define SCB_1_tx_0_INTR	((uint16)((uint16)0x0003u << (SCB_1_tx__0__SHIFT*2u)))

#define SCB_1_tx_INTR_ALL	 ((uint16)(SCB_1_tx_0_INTR))


#endif /* End Pins SCB_1_tx_ALIASES_H */


/* [] END OF FILE */

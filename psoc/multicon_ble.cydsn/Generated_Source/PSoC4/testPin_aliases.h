/*******************************************************************************
* File Name: testPin.h  
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

#if !defined(CY_PINS_testPin_ALIASES_H) /* Pins testPin_ALIASES_H */
#define CY_PINS_testPin_ALIASES_H

#include "cytypes.h"
#include "cyfitter.h"
#include "cypins.h"


/***************************************
*              Constants        
***************************************/
#define testPin_0			(testPin__0__PC)
#define testPin_0_PS		(testPin__0__PS)
#define testPin_0_PC		(testPin__0__PC)
#define testPin_0_DR		(testPin__0__DR)
#define testPin_0_SHIFT	(testPin__0__SHIFT)
#define testPin_0_INTR	((uint16)((uint16)0x0003u << (testPin__0__SHIFT*2u)))

#define testPin_INTR_ALL	 ((uint16)(testPin_0_INTR))


#endif /* End Pins testPin_ALIASES_H */


/* [] END OF FILE */

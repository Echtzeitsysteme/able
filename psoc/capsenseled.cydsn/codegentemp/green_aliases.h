/*******************************************************************************
* File Name: green.h  
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

#if !defined(CY_PINS_green_ALIASES_H) /* Pins green_ALIASES_H */
#define CY_PINS_green_ALIASES_H

#include "cytypes.h"
#include "cyfitter.h"
#include "cypins.h"


/***************************************
*              Constants        
***************************************/
#define green_0			(green__0__PC)
#define green_0_PS		(green__0__PS)
#define green_0_PC		(green__0__PC)
#define green_0_DR		(green__0__DR)
#define green_0_SHIFT	(green__0__SHIFT)
#define green_0_INTR	((uint16)((uint16)0x0003u << (green__0__SHIFT*2u)))

#define green_INTR_ALL	 ((uint16)(green_0_INTR))


#endif /* End Pins green_ALIASES_H */


/* [] END OF FILE */

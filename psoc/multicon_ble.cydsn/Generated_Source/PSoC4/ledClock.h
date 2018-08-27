/*******************************************************************************
* File Name: ledClock.h
* Version 2.20
*
*  Description:
*   Provides the function and constant definitions for the clock component.
*
*  Note:
*
********************************************************************************
* Copyright 2008-2012, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions, 
* disclaimers, and limitations in the end user license agreement accompanying 
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_CLOCK_ledClock_H)
#define CY_CLOCK_ledClock_H

#include <cytypes.h>
#include <cyfitter.h>


/***************************************
*        Function Prototypes
***************************************/
#if defined CYREG_PERI_DIV_CMD

void ledClock_StartEx(uint32 alignClkDiv);
#define ledClock_Start() \
    ledClock_StartEx(ledClock__PA_DIV_ID)

#else

void ledClock_Start(void);

#endif/* CYREG_PERI_DIV_CMD */

void ledClock_Stop(void);

void ledClock_SetFractionalDividerRegister(uint16 clkDivider, uint8 clkFractional);

uint16 ledClock_GetDividerRegister(void);
uint8  ledClock_GetFractionalDividerRegister(void);

#define ledClock_Enable()                         ledClock_Start()
#define ledClock_Disable()                        ledClock_Stop()
#define ledClock_SetDividerRegister(clkDivider, reset)  \
    ledClock_SetFractionalDividerRegister((clkDivider), 0u)
#define ledClock_SetDivider(clkDivider)           ledClock_SetDividerRegister((clkDivider), 1u)
#define ledClock_SetDividerValue(clkDivider)      ledClock_SetDividerRegister((clkDivider) - 1u, 1u)


/***************************************
*             Registers
***************************************/
#if defined CYREG_PERI_DIV_CMD

#define ledClock_DIV_ID     ledClock__DIV_ID

#define ledClock_CMD_REG    (*(reg32 *)CYREG_PERI_DIV_CMD)
#define ledClock_CTRL_REG   (*(reg32 *)ledClock__CTRL_REGISTER)
#define ledClock_DIV_REG    (*(reg32 *)ledClock__DIV_REGISTER)

#define ledClock_CMD_DIV_SHIFT          (0u)
#define ledClock_CMD_PA_DIV_SHIFT       (8u)
#define ledClock_CMD_DISABLE_SHIFT      (30u)
#define ledClock_CMD_ENABLE_SHIFT       (31u)

#define ledClock_CMD_DISABLE_MASK       ((uint32)((uint32)1u << ledClock_CMD_DISABLE_SHIFT))
#define ledClock_CMD_ENABLE_MASK        ((uint32)((uint32)1u << ledClock_CMD_ENABLE_SHIFT))

#define ledClock_DIV_FRAC_MASK  (0x000000F8u)
#define ledClock_DIV_FRAC_SHIFT (3u)
#define ledClock_DIV_INT_MASK   (0xFFFFFF00u)
#define ledClock_DIV_INT_SHIFT  (8u)

#else 

#define ledClock_DIV_REG        (*(reg32 *)ledClock__REGISTER)
#define ledClock_ENABLE_REG     ledClock_DIV_REG
#define ledClock_DIV_FRAC_MASK  ledClock__FRAC_MASK
#define ledClock_DIV_FRAC_SHIFT (16u)
#define ledClock_DIV_INT_MASK   ledClock__DIVIDER_MASK
#define ledClock_DIV_INT_SHIFT  (0u)

#endif/* CYREG_PERI_DIV_CMD */

#endif /* !defined(CY_CLOCK_ledClock_H) */

/* [] END OF FILE */

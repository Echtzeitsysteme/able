/*******************************************************************************
* File Name: green.h  
* Version 2.20
*
* Description:
*  This file contains Pin function prototypes and register defines
*
********************************************************************************
* Copyright 2008-2015, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions, 
* disclaimers, and limitations in the end user license agreement accompanying 
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_PINS_green_H) /* Pins green_H */
#define CY_PINS_green_H

#include "cytypes.h"
#include "cyfitter.h"
#include "green_aliases.h"


/***************************************
*     Data Struct Definitions
***************************************/

/**
* \addtogroup group_structures
* @{
*/
    
/* Structure for sleep mode support */
typedef struct
{
    uint32 pcState; /**< State of the port control register */
    uint32 sioState; /**< State of the SIO configuration */
    uint32 usbState; /**< State of the USBIO regulator */
} green_BACKUP_STRUCT;

/** @} structures */


/***************************************
*        Function Prototypes             
***************************************/
/**
* \addtogroup group_general
* @{
*/
uint8   green_Read(void);
void    green_Write(uint8 value);
uint8   green_ReadDataReg(void);
#if defined(green__PC) || (CY_PSOC4_4200L) 
    void    green_SetDriveMode(uint8 mode);
#endif
void    green_SetInterruptMode(uint16 position, uint16 mode);
uint8   green_ClearInterrupt(void);
/** @} general */

/**
* \addtogroup group_power
* @{
*/
void green_Sleep(void); 
void green_Wakeup(void);
/** @} power */


/***************************************
*           API Constants        
***************************************/
#if defined(green__PC) || (CY_PSOC4_4200L) 
    /* Drive Modes */
    #define green_DRIVE_MODE_BITS        (3)
    #define green_DRIVE_MODE_IND_MASK    (0xFFFFFFFFu >> (32 - green_DRIVE_MODE_BITS))

    /**
    * \addtogroup group_constants
    * @{
    */
        /** \addtogroup driveMode Drive mode constants
         * \brief Constants to be passed as "mode" parameter in the green_SetDriveMode() function.
         *  @{
         */
        #define green_DM_ALG_HIZ         (0x00u) /**< \brief High Impedance Analog   */
        #define green_DM_DIG_HIZ         (0x01u) /**< \brief High Impedance Digital  */
        #define green_DM_RES_UP          (0x02u) /**< \brief Resistive Pull Up       */
        #define green_DM_RES_DWN         (0x03u) /**< \brief Resistive Pull Down     */
        #define green_DM_OD_LO           (0x04u) /**< \brief Open Drain, Drives Low  */
        #define green_DM_OD_HI           (0x05u) /**< \brief Open Drain, Drives High */
        #define green_DM_STRONG          (0x06u) /**< \brief Strong Drive            */
        #define green_DM_RES_UPDWN       (0x07u) /**< \brief Resistive Pull Up/Down  */
        /** @} driveMode */
    /** @} group_constants */
#endif

/* Digital Port Constants */
#define green_MASK               green__MASK
#define green_SHIFT              green__SHIFT
#define green_WIDTH              1u

/**
* \addtogroup group_constants
* @{
*/
    /** \addtogroup intrMode Interrupt constants
     * \brief Constants to be passed as "mode" parameter in green_SetInterruptMode() function.
     *  @{
     */
        #define green_INTR_NONE      ((uint16)(0x0000u)) /**< \brief Disabled             */
        #define green_INTR_RISING    ((uint16)(0x5555u)) /**< \brief Rising edge trigger  */
        #define green_INTR_FALLING   ((uint16)(0xaaaau)) /**< \brief Falling edge trigger */
        #define green_INTR_BOTH      ((uint16)(0xffffu)) /**< \brief Both edge trigger    */
    /** @} intrMode */
/** @} group_constants */

/* SIO LPM definition */
#if defined(green__SIO)
    #define green_SIO_LPM_MASK       (0x03u)
#endif

/* USBIO definitions */
#if !defined(green__PC) && (CY_PSOC4_4200L)
    #define green_USBIO_ENABLE               ((uint32)0x80000000u)
    #define green_USBIO_DISABLE              ((uint32)(~green_USBIO_ENABLE))
    #define green_USBIO_SUSPEND_SHIFT        CYFLD_USBDEVv2_USB_SUSPEND__OFFSET
    #define green_USBIO_SUSPEND_DEL_SHIFT    CYFLD_USBDEVv2_USB_SUSPEND_DEL__OFFSET
    #define green_USBIO_ENTER_SLEEP          ((uint32)((1u << green_USBIO_SUSPEND_SHIFT) \
                                                        | (1u << green_USBIO_SUSPEND_DEL_SHIFT)))
    #define green_USBIO_EXIT_SLEEP_PH1       ((uint32)~((uint32)(1u << green_USBIO_SUSPEND_SHIFT)))
    #define green_USBIO_EXIT_SLEEP_PH2       ((uint32)~((uint32)(1u << green_USBIO_SUSPEND_DEL_SHIFT)))
    #define green_USBIO_CR1_OFF              ((uint32)0xfffffffeu)
#endif


/***************************************
*             Registers        
***************************************/
/* Main Port Registers */
#if defined(green__PC)
    /* Port Configuration */
    #define green_PC                 (* (reg32 *) green__PC)
#endif
/* Pin State */
#define green_PS                     (* (reg32 *) green__PS)
/* Data Register */
#define green_DR                     (* (reg32 *) green__DR)
/* Input Buffer Disable Override */
#define green_INP_DIS                (* (reg32 *) green__PC2)

/* Interrupt configuration Registers */
#define green_INTCFG                 (* (reg32 *) green__INTCFG)
#define green_INTSTAT                (* (reg32 *) green__INTSTAT)

/* "Interrupt cause" register for Combined Port Interrupt (AllPortInt) in GSRef component */
#if defined (CYREG_GPIO_INTR_CAUSE)
    #define green_INTR_CAUSE         (* (reg32 *) CYREG_GPIO_INTR_CAUSE)
#endif

/* SIO register */
#if defined(green__SIO)
    #define green_SIO_REG            (* (reg32 *) green__SIO)
#endif /* (green__SIO_CFG) */

/* USBIO registers */
#if !defined(green__PC) && (CY_PSOC4_4200L)
    #define green_USB_POWER_REG       (* (reg32 *) CYREG_USBDEVv2_USB_POWER_CTRL)
    #define green_CR1_REG             (* (reg32 *) CYREG_USBDEVv2_CR1)
    #define green_USBIO_CTRL_REG      (* (reg32 *) CYREG_USBDEVv2_USB_USBIO_CTRL)
#endif    
    
    
/***************************************
* The following code is DEPRECATED and 
* must not be used in new designs.
***************************************/
/**
* \addtogroup group_deprecated
* @{
*/
#define green_DRIVE_MODE_SHIFT       (0x00u)
#define green_DRIVE_MODE_MASK        (0x07u << green_DRIVE_MODE_SHIFT)
/** @} deprecated */

#endif /* End Pins green_H */


/* [] END OF FILE */

/*******************************************************************************
* File Name: SW5.h  
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

#if !defined(CY_PINS_SW5_H) /* Pins SW5_H */
#define CY_PINS_SW5_H

#include "cytypes.h"
#include "cyfitter.h"
#include "SW5_aliases.h"


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
} SW5_BACKUP_STRUCT;

/** @} structures */


/***************************************
*        Function Prototypes             
***************************************/
/**
* \addtogroup group_general
* @{
*/
uint8   SW5_Read(void);
void    SW5_Write(uint8 value);
uint8   SW5_ReadDataReg(void);
#if defined(SW5__PC) || (CY_PSOC4_4200L) 
    void    SW5_SetDriveMode(uint8 mode);
#endif
void    SW5_SetInterruptMode(uint16 position, uint16 mode);
uint8   SW5_ClearInterrupt(void);
/** @} general */

/**
* \addtogroup group_power
* @{
*/
void SW5_Sleep(void); 
void SW5_Wakeup(void);
/** @} power */


/***************************************
*           API Constants        
***************************************/
#if defined(SW5__PC) || (CY_PSOC4_4200L) 
    /* Drive Modes */
    #define SW5_DRIVE_MODE_BITS        (3)
    #define SW5_DRIVE_MODE_IND_MASK    (0xFFFFFFFFu >> (32 - SW5_DRIVE_MODE_BITS))

    /**
    * \addtogroup group_constants
    * @{
    */
        /** \addtogroup driveMode Drive mode constants
         * \brief Constants to be passed as "mode" parameter in the SW5_SetDriveMode() function.
         *  @{
         */
        #define SW5_DM_ALG_HIZ         (0x00u) /**< \brief High Impedance Analog   */
        #define SW5_DM_DIG_HIZ         (0x01u) /**< \brief High Impedance Digital  */
        #define SW5_DM_RES_UP          (0x02u) /**< \brief Resistive Pull Up       */
        #define SW5_DM_RES_DWN         (0x03u) /**< \brief Resistive Pull Down     */
        #define SW5_DM_OD_LO           (0x04u) /**< \brief Open Drain, Drives Low  */
        #define SW5_DM_OD_HI           (0x05u) /**< \brief Open Drain, Drives High */
        #define SW5_DM_STRONG          (0x06u) /**< \brief Strong Drive            */
        #define SW5_DM_RES_UPDWN       (0x07u) /**< \brief Resistive Pull Up/Down  */
        /** @} driveMode */
    /** @} group_constants */
#endif

/* Digital Port Constants */
#define SW5_MASK               SW5__MASK
#define SW5_SHIFT              SW5__SHIFT
#define SW5_WIDTH              1u

/**
* \addtogroup group_constants
* @{
*/
    /** \addtogroup intrMode Interrupt constants
     * \brief Constants to be passed as "mode" parameter in SW5_SetInterruptMode() function.
     *  @{
     */
        #define SW5_INTR_NONE      ((uint16)(0x0000u)) /**< \brief Disabled             */
        #define SW5_INTR_RISING    ((uint16)(0x5555u)) /**< \brief Rising edge trigger  */
        #define SW5_INTR_FALLING   ((uint16)(0xaaaau)) /**< \brief Falling edge trigger */
        #define SW5_INTR_BOTH      ((uint16)(0xffffu)) /**< \brief Both edge trigger    */
    /** @} intrMode */
/** @} group_constants */

/* SIO LPM definition */
#if defined(SW5__SIO)
    #define SW5_SIO_LPM_MASK       (0x03u)
#endif

/* USBIO definitions */
#if !defined(SW5__PC) && (CY_PSOC4_4200L)
    #define SW5_USBIO_ENABLE               ((uint32)0x80000000u)
    #define SW5_USBIO_DISABLE              ((uint32)(~SW5_USBIO_ENABLE))
    #define SW5_USBIO_SUSPEND_SHIFT        CYFLD_USBDEVv2_USB_SUSPEND__OFFSET
    #define SW5_USBIO_SUSPEND_DEL_SHIFT    CYFLD_USBDEVv2_USB_SUSPEND_DEL__OFFSET
    #define SW5_USBIO_ENTER_SLEEP          ((uint32)((1u << SW5_USBIO_SUSPEND_SHIFT) \
                                                        | (1u << SW5_USBIO_SUSPEND_DEL_SHIFT)))
    #define SW5_USBIO_EXIT_SLEEP_PH1       ((uint32)~((uint32)(1u << SW5_USBIO_SUSPEND_SHIFT)))
    #define SW5_USBIO_EXIT_SLEEP_PH2       ((uint32)~((uint32)(1u << SW5_USBIO_SUSPEND_DEL_SHIFT)))
    #define SW5_USBIO_CR1_OFF              ((uint32)0xfffffffeu)
#endif


/***************************************
*             Registers        
***************************************/
/* Main Port Registers */
#if defined(SW5__PC)
    /* Port Configuration */
    #define SW5_PC                 (* (reg32 *) SW5__PC)
#endif
/* Pin State */
#define SW5_PS                     (* (reg32 *) SW5__PS)
/* Data Register */
#define SW5_DR                     (* (reg32 *) SW5__DR)
/* Input Buffer Disable Override */
#define SW5_INP_DIS                (* (reg32 *) SW5__PC2)

/* Interrupt configuration Registers */
#define SW5_INTCFG                 (* (reg32 *) SW5__INTCFG)
#define SW5_INTSTAT                (* (reg32 *) SW5__INTSTAT)

/* "Interrupt cause" register for Combined Port Interrupt (AllPortInt) in GSRef component */
#if defined (CYREG_GPIO_INTR_CAUSE)
    #define SW5_INTR_CAUSE         (* (reg32 *) CYREG_GPIO_INTR_CAUSE)
#endif

/* SIO register */
#if defined(SW5__SIO)
    #define SW5_SIO_REG            (* (reg32 *) SW5__SIO)
#endif /* (SW5__SIO_CFG) */

/* USBIO registers */
#if !defined(SW5__PC) && (CY_PSOC4_4200L)
    #define SW5_USB_POWER_REG       (* (reg32 *) CYREG_USBDEVv2_USB_POWER_CTRL)
    #define SW5_CR1_REG             (* (reg32 *) CYREG_USBDEVv2_CR1)
    #define SW5_USBIO_CTRL_REG      (* (reg32 *) CYREG_USBDEVv2_USB_USBIO_CTRL)
#endif    
    
    
/***************************************
* The following code is DEPRECATED and 
* must not be used in new designs.
***************************************/
/**
* \addtogroup group_deprecated
* @{
*/
#define SW5_DRIVE_MODE_SHIFT       (0x00u)
#define SW5_DRIVE_MODE_MASK        (0x07u << SW5_DRIVE_MODE_SHIFT)
/** @} deprecated */

#endif /* End Pins SW5_H */


/* [] END OF FILE */

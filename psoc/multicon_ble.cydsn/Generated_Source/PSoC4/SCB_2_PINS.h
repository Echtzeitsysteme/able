/***************************************************************************//**
* \file SCB_2_PINS.h
* \version 3.20
*
* \brief
*  This file provides constants and parameter values for the pin components
*  buried into SCB Component.
*
* Note:
*
********************************************************************************
* \copyright
* Copyright 2013-2016, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
*******************************************************************************/

#if !defined(CY_SCB_PINS_SCB_2_H)
#define CY_SCB_PINS_SCB_2_H

#include "cydevice_trm.h"
#include "cyfitter.h"
#include "cytypes.h"


/***************************************
*   Conditional Compilation Parameters
****************************************/

/* Unconfigured pins */
#define SCB_2_REMOVE_RX_WAKE_SDA_MOSI_PIN  (1u)
#define SCB_2_REMOVE_RX_SDA_MOSI_PIN      (1u)
#define SCB_2_REMOVE_TX_SCL_MISO_PIN      (1u)
#define SCB_2_REMOVE_CTS_SCLK_PIN      (1u)
#define SCB_2_REMOVE_RTS_SS0_PIN      (1u)
#define SCB_2_REMOVE_SS1_PIN                 (1u)
#define SCB_2_REMOVE_SS2_PIN                 (1u)
#define SCB_2_REMOVE_SS3_PIN                 (1u)

/* Mode defined pins */
#define SCB_2_REMOVE_I2C_PINS                (0u)
#define SCB_2_REMOVE_SPI_MASTER_PINS         (1u)
#define SCB_2_REMOVE_SPI_MASTER_SCLK_PIN     (1u)
#define SCB_2_REMOVE_SPI_MASTER_MOSI_PIN     (1u)
#define SCB_2_REMOVE_SPI_MASTER_MISO_PIN     (1u)
#define SCB_2_REMOVE_SPI_MASTER_SS0_PIN      (1u)
#define SCB_2_REMOVE_SPI_MASTER_SS1_PIN      (1u)
#define SCB_2_REMOVE_SPI_MASTER_SS2_PIN      (1u)
#define SCB_2_REMOVE_SPI_MASTER_SS3_PIN      (1u)
#define SCB_2_REMOVE_SPI_SLAVE_PINS          (1u)
#define SCB_2_REMOVE_SPI_SLAVE_MOSI_PIN      (1u)
#define SCB_2_REMOVE_SPI_SLAVE_MISO_PIN      (1u)
#define SCB_2_REMOVE_UART_TX_PIN             (1u)
#define SCB_2_REMOVE_UART_RX_TX_PIN          (1u)
#define SCB_2_REMOVE_UART_RX_PIN             (1u)
#define SCB_2_REMOVE_UART_RX_WAKE_PIN        (1u)
#define SCB_2_REMOVE_UART_RTS_PIN            (1u)
#define SCB_2_REMOVE_UART_CTS_PIN            (1u)

/* Unconfigured pins */
#define SCB_2_RX_WAKE_SDA_MOSI_PIN (0u == SCB_2_REMOVE_RX_WAKE_SDA_MOSI_PIN)
#define SCB_2_RX_SDA_MOSI_PIN     (0u == SCB_2_REMOVE_RX_SDA_MOSI_PIN)
#define SCB_2_TX_SCL_MISO_PIN     (0u == SCB_2_REMOVE_TX_SCL_MISO_PIN)
#define SCB_2_CTS_SCLK_PIN     (0u == SCB_2_REMOVE_CTS_SCLK_PIN)
#define SCB_2_RTS_SS0_PIN     (0u == SCB_2_REMOVE_RTS_SS0_PIN)
#define SCB_2_SS1_PIN                (0u == SCB_2_REMOVE_SS1_PIN)
#define SCB_2_SS2_PIN                (0u == SCB_2_REMOVE_SS2_PIN)
#define SCB_2_SS3_PIN                (0u == SCB_2_REMOVE_SS3_PIN)

/* Mode defined pins */
#define SCB_2_I2C_PINS               (0u == SCB_2_REMOVE_I2C_PINS)
#define SCB_2_SPI_MASTER_PINS        (0u == SCB_2_REMOVE_SPI_MASTER_PINS)
#define SCB_2_SPI_MASTER_SCLK_PIN    (0u == SCB_2_REMOVE_SPI_MASTER_SCLK_PIN)
#define SCB_2_SPI_MASTER_MOSI_PIN    (0u == SCB_2_REMOVE_SPI_MASTER_MOSI_PIN)
#define SCB_2_SPI_MASTER_MISO_PIN    (0u == SCB_2_REMOVE_SPI_MASTER_MISO_PIN)
#define SCB_2_SPI_MASTER_SS0_PIN     (0u == SCB_2_REMOVE_SPI_MASTER_SS0_PIN)
#define SCB_2_SPI_MASTER_SS1_PIN     (0u == SCB_2_REMOVE_SPI_MASTER_SS1_PIN)
#define SCB_2_SPI_MASTER_SS2_PIN     (0u == SCB_2_REMOVE_SPI_MASTER_SS2_PIN)
#define SCB_2_SPI_MASTER_SS3_PIN     (0u == SCB_2_REMOVE_SPI_MASTER_SS3_PIN)
#define SCB_2_SPI_SLAVE_PINS         (0u == SCB_2_REMOVE_SPI_SLAVE_PINS)
#define SCB_2_SPI_SLAVE_MOSI_PIN     (0u == SCB_2_REMOVE_SPI_SLAVE_MOSI_PIN)
#define SCB_2_SPI_SLAVE_MISO_PIN     (0u == SCB_2_REMOVE_SPI_SLAVE_MISO_PIN)
#define SCB_2_UART_TX_PIN            (0u == SCB_2_REMOVE_UART_TX_PIN)
#define SCB_2_UART_RX_TX_PIN         (0u == SCB_2_REMOVE_UART_RX_TX_PIN)
#define SCB_2_UART_RX_PIN            (0u == SCB_2_REMOVE_UART_RX_PIN)
#define SCB_2_UART_RX_WAKE_PIN       (0u == SCB_2_REMOVE_UART_RX_WAKE_PIN)
#define SCB_2_UART_RTS_PIN           (0u == SCB_2_REMOVE_UART_RTS_PIN)
#define SCB_2_UART_CTS_PIN           (0u == SCB_2_REMOVE_UART_CTS_PIN)


/***************************************
*             Includes
****************************************/

#if (SCB_2_RX_WAKE_SDA_MOSI_PIN)
    #include "SCB_2_uart_rx_wake_i2c_sda_spi_mosi.h"
#endif /* (SCB_2_RX_SDA_MOSI) */

#if (SCB_2_RX_SDA_MOSI_PIN)
    #include "SCB_2_uart_rx_i2c_sda_spi_mosi.h"
#endif /* (SCB_2_RX_SDA_MOSI) */

#if (SCB_2_TX_SCL_MISO_PIN)
    #include "SCB_2_uart_tx_i2c_scl_spi_miso.h"
#endif /* (SCB_2_TX_SCL_MISO) */

#if (SCB_2_CTS_SCLK_PIN)
    #include "SCB_2_uart_cts_spi_sclk.h"
#endif /* (SCB_2_CTS_SCLK) */

#if (SCB_2_RTS_SS0_PIN)
    #include "SCB_2_uart_rts_spi_ss0.h"
#endif /* (SCB_2_RTS_SS0_PIN) */

#if (SCB_2_SS1_PIN)
    #include "SCB_2_spi_ss1.h"
#endif /* (SCB_2_SS1_PIN) */

#if (SCB_2_SS2_PIN)
    #include "SCB_2_spi_ss2.h"
#endif /* (SCB_2_SS2_PIN) */

#if (SCB_2_SS3_PIN)
    #include "SCB_2_spi_ss3.h"
#endif /* (SCB_2_SS3_PIN) */

#if (SCB_2_I2C_PINS)
    #include "SCB_2_scl.h"
    #include "SCB_2_sda.h"
#endif /* (SCB_2_I2C_PINS) */

#if (SCB_2_SPI_MASTER_PINS)
#if (SCB_2_SPI_MASTER_SCLK_PIN)
    #include "SCB_2_sclk_m.h"
#endif /* (SCB_2_SPI_MASTER_SCLK_PIN) */

#if (SCB_2_SPI_MASTER_MOSI_PIN)
    #include "SCB_2_mosi_m.h"
#endif /* (SCB_2_SPI_MASTER_MOSI_PIN) */

#if (SCB_2_SPI_MASTER_MISO_PIN)
    #include "SCB_2_miso_m.h"
#endif /*(SCB_2_SPI_MASTER_MISO_PIN) */
#endif /* (SCB_2_SPI_MASTER_PINS) */

#if (SCB_2_SPI_SLAVE_PINS)
    #include "SCB_2_sclk_s.h"
    #include "SCB_2_ss_s.h"

#if (SCB_2_SPI_SLAVE_MOSI_PIN)
    #include "SCB_2_mosi_s.h"
#endif /* (SCB_2_SPI_SLAVE_MOSI_PIN) */

#if (SCB_2_SPI_SLAVE_MISO_PIN)
    #include "SCB_2_miso_s.h"
#endif /*(SCB_2_SPI_SLAVE_MISO_PIN) */
#endif /* (SCB_2_SPI_SLAVE_PINS) */

#if (SCB_2_SPI_MASTER_SS0_PIN)
    #include "SCB_2_ss0_m.h"
#endif /* (SCB_2_SPI_MASTER_SS0_PIN) */

#if (SCB_2_SPI_MASTER_SS1_PIN)
    #include "SCB_2_ss1_m.h"
#endif /* (SCB_2_SPI_MASTER_SS1_PIN) */

#if (SCB_2_SPI_MASTER_SS2_PIN)
    #include "SCB_2_ss2_m.h"
#endif /* (SCB_2_SPI_MASTER_SS2_PIN) */

#if (SCB_2_SPI_MASTER_SS3_PIN)
    #include "SCB_2_ss3_m.h"
#endif /* (SCB_2_SPI_MASTER_SS3_PIN) */

#if (SCB_2_UART_TX_PIN)
    #include "SCB_2_tx.h"
#endif /* (SCB_2_UART_TX_PIN) */

#if (SCB_2_UART_RX_TX_PIN)
    #include "SCB_2_rx_tx.h"
#endif /* (SCB_2_UART_RX_TX_PIN) */

#if (SCB_2_UART_RX_PIN)
    #include "SCB_2_rx.h"
#endif /* (SCB_2_UART_RX_PIN) */

#if (SCB_2_UART_RX_WAKE_PIN)
    #include "SCB_2_rx_wake.h"
#endif /* (SCB_2_UART_RX_WAKE_PIN) */

#if (SCB_2_UART_RTS_PIN)
    #include "SCB_2_rts.h"
#endif /* (SCB_2_UART_RTS_PIN) */

#if (SCB_2_UART_CTS_PIN)
    #include "SCB_2_cts.h"
#endif /* (SCB_2_UART_CTS_PIN) */


/***************************************
*              Registers
***************************************/

#if (SCB_2_RX_SDA_MOSI_PIN)
    #define SCB_2_RX_SDA_MOSI_HSIOM_REG   (*(reg32 *) SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM)
    #define SCB_2_RX_SDA_MOSI_HSIOM_PTR   ( (reg32 *) SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM)
    
    #define SCB_2_RX_SDA_MOSI_HSIOM_MASK      (SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM_MASK)
    #define SCB_2_RX_SDA_MOSI_HSIOM_POS       (SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM_SHIFT)
    #define SCB_2_RX_SDA_MOSI_HSIOM_SEL_GPIO  (SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM_GPIO)
    #define SCB_2_RX_SDA_MOSI_HSIOM_SEL_I2C   (SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM_I2C)
    #define SCB_2_RX_SDA_MOSI_HSIOM_SEL_SPI   (SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM_SPI)
    #define SCB_2_RX_SDA_MOSI_HSIOM_SEL_UART  (SCB_2_uart_rx_i2c_sda_spi_mosi__0__HSIOM_UART)
    
#elif (SCB_2_RX_WAKE_SDA_MOSI_PIN)
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG   (*(reg32 *) SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM)
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_PTR   ( (reg32 *) SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM)
    
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_MASK      (SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM_MASK)
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_POS       (SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM_SHIFT)
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_SEL_GPIO  (SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM_GPIO)
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_SEL_I2C   (SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM_I2C)
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_SEL_SPI   (SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM_SPI)
    #define SCB_2_RX_WAKE_SDA_MOSI_HSIOM_SEL_UART  (SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__HSIOM_UART)    
   
    #define SCB_2_RX_WAKE_SDA_MOSI_INTCFG_REG (*(reg32 *) SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__INTCFG)
    #define SCB_2_RX_WAKE_SDA_MOSI_INTCFG_PTR ( (reg32 *) SCB_2_uart_rx_wake_i2c_sda_spi_mosi__0__INTCFG)
    #define SCB_2_RX_WAKE_SDA_MOSI_INTCFG_TYPE_POS  (SCB_2_uart_rx_wake_i2c_sda_spi_mosi__SHIFT)
    #define SCB_2_RX_WAKE_SDA_MOSI_INTCFG_TYPE_MASK ((uint32) SCB_2_INTCFG_TYPE_MASK << \
                                                                           SCB_2_RX_WAKE_SDA_MOSI_INTCFG_TYPE_POS)
#else
    /* None of pins SCB_2_RX_SDA_MOSI_PIN or SCB_2_RX_WAKE_SDA_MOSI_PIN present.*/
#endif /* (SCB_2_RX_SDA_MOSI_PIN) */

#if (SCB_2_TX_SCL_MISO_PIN)
    #define SCB_2_TX_SCL_MISO_HSIOM_REG   (*(reg32 *) SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM)
    #define SCB_2_TX_SCL_MISO_HSIOM_PTR   ( (reg32 *) SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM)
    
    #define SCB_2_TX_SCL_MISO_HSIOM_MASK      (SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM_MASK)
    #define SCB_2_TX_SCL_MISO_HSIOM_POS       (SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM_SHIFT)
    #define SCB_2_TX_SCL_MISO_HSIOM_SEL_GPIO  (SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM_GPIO)
    #define SCB_2_TX_SCL_MISO_HSIOM_SEL_I2C   (SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM_I2C)
    #define SCB_2_TX_SCL_MISO_HSIOM_SEL_SPI   (SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM_SPI)
    #define SCB_2_TX_SCL_MISO_HSIOM_SEL_UART  (SCB_2_uart_tx_i2c_scl_spi_miso__0__HSIOM_UART)
#endif /* (SCB_2_TX_SCL_MISO_PIN) */

#if (SCB_2_CTS_SCLK_PIN)
    #define SCB_2_CTS_SCLK_HSIOM_REG   (*(reg32 *) SCB_2_uart_cts_spi_sclk__0__HSIOM)
    #define SCB_2_CTS_SCLK_HSIOM_PTR   ( (reg32 *) SCB_2_uart_cts_spi_sclk__0__HSIOM)
    
    #define SCB_2_CTS_SCLK_HSIOM_MASK      (SCB_2_uart_cts_spi_sclk__0__HSIOM_MASK)
    #define SCB_2_CTS_SCLK_HSIOM_POS       (SCB_2_uart_cts_spi_sclk__0__HSIOM_SHIFT)
    #define SCB_2_CTS_SCLK_HSIOM_SEL_GPIO  (SCB_2_uart_cts_spi_sclk__0__HSIOM_GPIO)
    #define SCB_2_CTS_SCLK_HSIOM_SEL_I2C   (SCB_2_uart_cts_spi_sclk__0__HSIOM_I2C)
    #define SCB_2_CTS_SCLK_HSIOM_SEL_SPI   (SCB_2_uart_cts_spi_sclk__0__HSIOM_SPI)
    #define SCB_2_CTS_SCLK_HSIOM_SEL_UART  (SCB_2_uart_cts_spi_sclk__0__HSIOM_UART)
#endif /* (SCB_2_CTS_SCLK_PIN) */

#if (SCB_2_RTS_SS0_PIN)
    #define SCB_2_RTS_SS0_HSIOM_REG   (*(reg32 *) SCB_2_uart_rts_spi_ss0__0__HSIOM)
    #define SCB_2_RTS_SS0_HSIOM_PTR   ( (reg32 *) SCB_2_uart_rts_spi_ss0__0__HSIOM)
    
    #define SCB_2_RTS_SS0_HSIOM_MASK      (SCB_2_uart_rts_spi_ss0__0__HSIOM_MASK)
    #define SCB_2_RTS_SS0_HSIOM_POS       (SCB_2_uart_rts_spi_ss0__0__HSIOM_SHIFT)
    #define SCB_2_RTS_SS0_HSIOM_SEL_GPIO  (SCB_2_uart_rts_spi_ss0__0__HSIOM_GPIO)
    #define SCB_2_RTS_SS0_HSIOM_SEL_I2C   (SCB_2_uart_rts_spi_ss0__0__HSIOM_I2C)
    #define SCB_2_RTS_SS0_HSIOM_SEL_SPI   (SCB_2_uart_rts_spi_ss0__0__HSIOM_SPI)
#if !(SCB_2_CY_SCBIP_V0 || SCB_2_CY_SCBIP_V1)
    #define SCB_2_RTS_SS0_HSIOM_SEL_UART  (SCB_2_uart_rts_spi_ss0__0__HSIOM_UART)
#endif /* !(SCB_2_CY_SCBIP_V0 || SCB_2_CY_SCBIP_V1) */
#endif /* (SCB_2_RTS_SS0_PIN) */

#if (SCB_2_SS1_PIN)
    #define SCB_2_SS1_HSIOM_REG  (*(reg32 *) SCB_2_spi_ss1__0__HSIOM)
    #define SCB_2_SS1_HSIOM_PTR  ( (reg32 *) SCB_2_spi_ss1__0__HSIOM)
    
    #define SCB_2_SS1_HSIOM_MASK     (SCB_2_spi_ss1__0__HSIOM_MASK)
    #define SCB_2_SS1_HSIOM_POS      (SCB_2_spi_ss1__0__HSIOM_SHIFT)
    #define SCB_2_SS1_HSIOM_SEL_GPIO (SCB_2_spi_ss1__0__HSIOM_GPIO)
    #define SCB_2_SS1_HSIOM_SEL_I2C  (SCB_2_spi_ss1__0__HSIOM_I2C)
    #define SCB_2_SS1_HSIOM_SEL_SPI  (SCB_2_spi_ss1__0__HSIOM_SPI)
#endif /* (SCB_2_SS1_PIN) */

#if (SCB_2_SS2_PIN)
    #define SCB_2_SS2_HSIOM_REG     (*(reg32 *) SCB_2_spi_ss2__0__HSIOM)
    #define SCB_2_SS2_HSIOM_PTR     ( (reg32 *) SCB_2_spi_ss2__0__HSIOM)
    
    #define SCB_2_SS2_HSIOM_MASK     (SCB_2_spi_ss2__0__HSIOM_MASK)
    #define SCB_2_SS2_HSIOM_POS      (SCB_2_spi_ss2__0__HSIOM_SHIFT)
    #define SCB_2_SS2_HSIOM_SEL_GPIO (SCB_2_spi_ss2__0__HSIOM_GPIO)
    #define SCB_2_SS2_HSIOM_SEL_I2C  (SCB_2_spi_ss2__0__HSIOM_I2C)
    #define SCB_2_SS2_HSIOM_SEL_SPI  (SCB_2_spi_ss2__0__HSIOM_SPI)
#endif /* (SCB_2_SS2_PIN) */

#if (SCB_2_SS3_PIN)
    #define SCB_2_SS3_HSIOM_REG     (*(reg32 *) SCB_2_spi_ss3__0__HSIOM)
    #define SCB_2_SS3_HSIOM_PTR     ( (reg32 *) SCB_2_spi_ss3__0__HSIOM)
    
    #define SCB_2_SS3_HSIOM_MASK     (SCB_2_spi_ss3__0__HSIOM_MASK)
    #define SCB_2_SS3_HSIOM_POS      (SCB_2_spi_ss3__0__HSIOM_SHIFT)
    #define SCB_2_SS3_HSIOM_SEL_GPIO (SCB_2_spi_ss3__0__HSIOM_GPIO)
    #define SCB_2_SS3_HSIOM_SEL_I2C  (SCB_2_spi_ss3__0__HSIOM_I2C)
    #define SCB_2_SS3_HSIOM_SEL_SPI  (SCB_2_spi_ss3__0__HSIOM_SPI)
#endif /* (SCB_2_SS3_PIN) */

#if (SCB_2_I2C_PINS)
    #define SCB_2_SCL_HSIOM_REG  (*(reg32 *) SCB_2_scl__0__HSIOM)
    #define SCB_2_SCL_HSIOM_PTR  ( (reg32 *) SCB_2_scl__0__HSIOM)
    
    #define SCB_2_SCL_HSIOM_MASK     (SCB_2_scl__0__HSIOM_MASK)
    #define SCB_2_SCL_HSIOM_POS      (SCB_2_scl__0__HSIOM_SHIFT)
    #define SCB_2_SCL_HSIOM_SEL_GPIO (SCB_2_sda__0__HSIOM_GPIO)
    #define SCB_2_SCL_HSIOM_SEL_I2C  (SCB_2_sda__0__HSIOM_I2C)
    
    #define SCB_2_SDA_HSIOM_REG  (*(reg32 *) SCB_2_sda__0__HSIOM)
    #define SCB_2_SDA_HSIOM_PTR  ( (reg32 *) SCB_2_sda__0__HSIOM)
    
    #define SCB_2_SDA_HSIOM_MASK     (SCB_2_sda__0__HSIOM_MASK)
    #define SCB_2_SDA_HSIOM_POS      (SCB_2_sda__0__HSIOM_SHIFT)
    #define SCB_2_SDA_HSIOM_SEL_GPIO (SCB_2_sda__0__HSIOM_GPIO)
    #define SCB_2_SDA_HSIOM_SEL_I2C  (SCB_2_sda__0__HSIOM_I2C)
#endif /* (SCB_2_I2C_PINS) */

#if (SCB_2_SPI_SLAVE_PINS)
    #define SCB_2_SCLK_S_HSIOM_REG   (*(reg32 *) SCB_2_sclk_s__0__HSIOM)
    #define SCB_2_SCLK_S_HSIOM_PTR   ( (reg32 *) SCB_2_sclk_s__0__HSIOM)
    
    #define SCB_2_SCLK_S_HSIOM_MASK      (SCB_2_sclk_s__0__HSIOM_MASK)
    #define SCB_2_SCLK_S_HSIOM_POS       (SCB_2_sclk_s__0__HSIOM_SHIFT)
    #define SCB_2_SCLK_S_HSIOM_SEL_GPIO  (SCB_2_sclk_s__0__HSIOM_GPIO)
    #define SCB_2_SCLK_S_HSIOM_SEL_SPI   (SCB_2_sclk_s__0__HSIOM_SPI)
    
    #define SCB_2_SS0_S_HSIOM_REG    (*(reg32 *) SCB_2_ss0_s__0__HSIOM)
    #define SCB_2_SS0_S_HSIOM_PTR    ( (reg32 *) SCB_2_ss0_s__0__HSIOM)
    
    #define SCB_2_SS0_S_HSIOM_MASK       (SCB_2_ss0_s__0__HSIOM_MASK)
    #define SCB_2_SS0_S_HSIOM_POS        (SCB_2_ss0_s__0__HSIOM_SHIFT)
    #define SCB_2_SS0_S_HSIOM_SEL_GPIO   (SCB_2_ss0_s__0__HSIOM_GPIO)  
    #define SCB_2_SS0_S_HSIOM_SEL_SPI    (SCB_2_ss0_s__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_SLAVE_PINS) */

#if (SCB_2_SPI_SLAVE_MOSI_PIN)
    #define SCB_2_MOSI_S_HSIOM_REG   (*(reg32 *) SCB_2_mosi_s__0__HSIOM)
    #define SCB_2_MOSI_S_HSIOM_PTR   ( (reg32 *) SCB_2_mosi_s__0__HSIOM)
    
    #define SCB_2_MOSI_S_HSIOM_MASK      (SCB_2_mosi_s__0__HSIOM_MASK)
    #define SCB_2_MOSI_S_HSIOM_POS       (SCB_2_mosi_s__0__HSIOM_SHIFT)
    #define SCB_2_MOSI_S_HSIOM_SEL_GPIO  (SCB_2_mosi_s__0__HSIOM_GPIO)
    #define SCB_2_MOSI_S_HSIOM_SEL_SPI   (SCB_2_mosi_s__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_SLAVE_MOSI_PIN) */

#if (SCB_2_SPI_SLAVE_MISO_PIN)
    #define SCB_2_MISO_S_HSIOM_REG   (*(reg32 *) SCB_2_miso_s__0__HSIOM)
    #define SCB_2_MISO_S_HSIOM_PTR   ( (reg32 *) SCB_2_miso_s__0__HSIOM)
    
    #define SCB_2_MISO_S_HSIOM_MASK      (SCB_2_miso_s__0__HSIOM_MASK)
    #define SCB_2_MISO_S_HSIOM_POS       (SCB_2_miso_s__0__HSIOM_SHIFT)
    #define SCB_2_MISO_S_HSIOM_SEL_GPIO  (SCB_2_miso_s__0__HSIOM_GPIO)
    #define SCB_2_MISO_S_HSIOM_SEL_SPI   (SCB_2_miso_s__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_SLAVE_MISO_PIN) */

#if (SCB_2_SPI_MASTER_MISO_PIN)
    #define SCB_2_MISO_M_HSIOM_REG   (*(reg32 *) SCB_2_miso_m__0__HSIOM)
    #define SCB_2_MISO_M_HSIOM_PTR   ( (reg32 *) SCB_2_miso_m__0__HSIOM)
    
    #define SCB_2_MISO_M_HSIOM_MASK      (SCB_2_miso_m__0__HSIOM_MASK)
    #define SCB_2_MISO_M_HSIOM_POS       (SCB_2_miso_m__0__HSIOM_SHIFT)
    #define SCB_2_MISO_M_HSIOM_SEL_GPIO  (SCB_2_miso_m__0__HSIOM_GPIO)
    #define SCB_2_MISO_M_HSIOM_SEL_SPI   (SCB_2_miso_m__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_MASTER_MISO_PIN) */

#if (SCB_2_SPI_MASTER_MOSI_PIN)
    #define SCB_2_MOSI_M_HSIOM_REG   (*(reg32 *) SCB_2_mosi_m__0__HSIOM)
    #define SCB_2_MOSI_M_HSIOM_PTR   ( (reg32 *) SCB_2_mosi_m__0__HSIOM)
    
    #define SCB_2_MOSI_M_HSIOM_MASK      (SCB_2_mosi_m__0__HSIOM_MASK)
    #define SCB_2_MOSI_M_HSIOM_POS       (SCB_2_mosi_m__0__HSIOM_SHIFT)
    #define SCB_2_MOSI_M_HSIOM_SEL_GPIO  (SCB_2_mosi_m__0__HSIOM_GPIO)
    #define SCB_2_MOSI_M_HSIOM_SEL_SPI   (SCB_2_mosi_m__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_MASTER_MOSI_PIN) */

#if (SCB_2_SPI_MASTER_SCLK_PIN)
    #define SCB_2_SCLK_M_HSIOM_REG   (*(reg32 *) SCB_2_sclk_m__0__HSIOM)
    #define SCB_2_SCLK_M_HSIOM_PTR   ( (reg32 *) SCB_2_sclk_m__0__HSIOM)
    
    #define SCB_2_SCLK_M_HSIOM_MASK      (SCB_2_sclk_m__0__HSIOM_MASK)
    #define SCB_2_SCLK_M_HSIOM_POS       (SCB_2_sclk_m__0__HSIOM_SHIFT)
    #define SCB_2_SCLK_M_HSIOM_SEL_GPIO  (SCB_2_sclk_m__0__HSIOM_GPIO)
    #define SCB_2_SCLK_M_HSIOM_SEL_SPI   (SCB_2_sclk_m__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_MASTER_SCLK_PIN) */

#if (SCB_2_SPI_MASTER_SS0_PIN)
    #define SCB_2_SS0_M_HSIOM_REG    (*(reg32 *) SCB_2_ss0_m__0__HSIOM)
    #define SCB_2_SS0_M_HSIOM_PTR    ( (reg32 *) SCB_2_ss0_m__0__HSIOM)
    
    #define SCB_2_SS0_M_HSIOM_MASK       (SCB_2_ss0_m__0__HSIOM_MASK)
    #define SCB_2_SS0_M_HSIOM_POS        (SCB_2_ss0_m__0__HSIOM_SHIFT)
    #define SCB_2_SS0_M_HSIOM_SEL_GPIO   (SCB_2_ss0_m__0__HSIOM_GPIO)
    #define SCB_2_SS0_M_HSIOM_SEL_SPI    (SCB_2_ss0_m__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_MASTER_SS0_PIN) */

#if (SCB_2_SPI_MASTER_SS1_PIN)
    #define SCB_2_SS1_M_HSIOM_REG    (*(reg32 *) SCB_2_ss1_m__0__HSIOM)
    #define SCB_2_SS1_M_HSIOM_PTR    ( (reg32 *) SCB_2_ss1_m__0__HSIOM)
    
    #define SCB_2_SS1_M_HSIOM_MASK       (SCB_2_ss1_m__0__HSIOM_MASK)
    #define SCB_2_SS1_M_HSIOM_POS        (SCB_2_ss1_m__0__HSIOM_SHIFT)
    #define SCB_2_SS1_M_HSIOM_SEL_GPIO   (SCB_2_ss1_m__0__HSIOM_GPIO)
    #define SCB_2_SS1_M_HSIOM_SEL_SPI    (SCB_2_ss1_m__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_MASTER_SS1_PIN) */

#if (SCB_2_SPI_MASTER_SS2_PIN)
    #define SCB_2_SS2_M_HSIOM_REG    (*(reg32 *) SCB_2_ss2_m__0__HSIOM)
    #define SCB_2_SS2_M_HSIOM_PTR    ( (reg32 *) SCB_2_ss2_m__0__HSIOM)
    
    #define SCB_2_SS2_M_HSIOM_MASK       (SCB_2_ss2_m__0__HSIOM_MASK)
    #define SCB_2_SS2_M_HSIOM_POS        (SCB_2_ss2_m__0__HSIOM_SHIFT)
    #define SCB_2_SS2_M_HSIOM_SEL_GPIO   (SCB_2_ss2_m__0__HSIOM_GPIO)
    #define SCB_2_SS2_M_HSIOM_SEL_SPI    (SCB_2_ss2_m__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_MASTER_SS2_PIN) */

#if (SCB_2_SPI_MASTER_SS3_PIN)
    #define SCB_2_SS3_M_HSIOM_REG    (*(reg32 *) SCB_2_ss3_m__0__HSIOM)
    #define SCB_2_SS3_M_HSIOM_PTR    ( (reg32 *) SCB_2_ss3_m__0__HSIOM)
    
    #define SCB_2_SS3_M_HSIOM_MASK      (SCB_2_ss3_m__0__HSIOM_MASK)
    #define SCB_2_SS3_M_HSIOM_POS       (SCB_2_ss3_m__0__HSIOM_SHIFT)
    #define SCB_2_SS3_M_HSIOM_SEL_GPIO  (SCB_2_ss3_m__0__HSIOM_GPIO)
    #define SCB_2_SS3_M_HSIOM_SEL_SPI   (SCB_2_ss3_m__0__HSIOM_SPI)
#endif /* (SCB_2_SPI_MASTER_SS3_PIN) */

#if (SCB_2_UART_RX_PIN)
    #define SCB_2_RX_HSIOM_REG   (*(reg32 *) SCB_2_rx__0__HSIOM)
    #define SCB_2_RX_HSIOM_PTR   ( (reg32 *) SCB_2_rx__0__HSIOM)
    
    #define SCB_2_RX_HSIOM_MASK      (SCB_2_rx__0__HSIOM_MASK)
    #define SCB_2_RX_HSIOM_POS       (SCB_2_rx__0__HSIOM_SHIFT)
    #define SCB_2_RX_HSIOM_SEL_GPIO  (SCB_2_rx__0__HSIOM_GPIO)
    #define SCB_2_RX_HSIOM_SEL_UART  (SCB_2_rx__0__HSIOM_UART)
#endif /* (SCB_2_UART_RX_PIN) */

#if (SCB_2_UART_RX_WAKE_PIN)
    #define SCB_2_RX_WAKE_HSIOM_REG   (*(reg32 *) SCB_2_rx_wake__0__HSIOM)
    #define SCB_2_RX_WAKE_HSIOM_PTR   ( (reg32 *) SCB_2_rx_wake__0__HSIOM)
    
    #define SCB_2_RX_WAKE_HSIOM_MASK      (SCB_2_rx_wake__0__HSIOM_MASK)
    #define SCB_2_RX_WAKE_HSIOM_POS       (SCB_2_rx_wake__0__HSIOM_SHIFT)
    #define SCB_2_RX_WAKE_HSIOM_SEL_GPIO  (SCB_2_rx_wake__0__HSIOM_GPIO)
    #define SCB_2_RX_WAKE_HSIOM_SEL_UART  (SCB_2_rx_wake__0__HSIOM_UART)
#endif /* (SCB_2_UART_WAKE_RX_PIN) */

#if (SCB_2_UART_CTS_PIN)
    #define SCB_2_CTS_HSIOM_REG   (*(reg32 *) SCB_2_cts__0__HSIOM)
    #define SCB_2_CTS_HSIOM_PTR   ( (reg32 *) SCB_2_cts__0__HSIOM)
    
    #define SCB_2_CTS_HSIOM_MASK      (SCB_2_cts__0__HSIOM_MASK)
    #define SCB_2_CTS_HSIOM_POS       (SCB_2_cts__0__HSIOM_SHIFT)
    #define SCB_2_CTS_HSIOM_SEL_GPIO  (SCB_2_cts__0__HSIOM_GPIO)
    #define SCB_2_CTS_HSIOM_SEL_UART  (SCB_2_cts__0__HSIOM_UART)
#endif /* (SCB_2_UART_CTS_PIN) */

#if (SCB_2_UART_TX_PIN)
    #define SCB_2_TX_HSIOM_REG   (*(reg32 *) SCB_2_tx__0__HSIOM)
    #define SCB_2_TX_HSIOM_PTR   ( (reg32 *) SCB_2_tx__0__HSIOM)
    
    #define SCB_2_TX_HSIOM_MASK      (SCB_2_tx__0__HSIOM_MASK)
    #define SCB_2_TX_HSIOM_POS       (SCB_2_tx__0__HSIOM_SHIFT)
    #define SCB_2_TX_HSIOM_SEL_GPIO  (SCB_2_tx__0__HSIOM_GPIO)
    #define SCB_2_TX_HSIOM_SEL_UART  (SCB_2_tx__0__HSIOM_UART)
#endif /* (SCB_2_UART_TX_PIN) */

#if (SCB_2_UART_RX_TX_PIN)
    #define SCB_2_RX_TX_HSIOM_REG   (*(reg32 *) SCB_2_rx_tx__0__HSIOM)
    #define SCB_2_RX_TX_HSIOM_PTR   ( (reg32 *) SCB_2_rx_tx__0__HSIOM)
    
    #define SCB_2_RX_TX_HSIOM_MASK      (SCB_2_rx_tx__0__HSIOM_MASK)
    #define SCB_2_RX_TX_HSIOM_POS       (SCB_2_rx_tx__0__HSIOM_SHIFT)
    #define SCB_2_RX_TX_HSIOM_SEL_GPIO  (SCB_2_rx_tx__0__HSIOM_GPIO)
    #define SCB_2_RX_TX_HSIOM_SEL_UART  (SCB_2_rx_tx__0__HSIOM_UART)
#endif /* (SCB_2_UART_RX_TX_PIN) */

#if (SCB_2_UART_RTS_PIN)
    #define SCB_2_RTS_HSIOM_REG      (*(reg32 *) SCB_2_rts__0__HSIOM)
    #define SCB_2_RTS_HSIOM_PTR      ( (reg32 *) SCB_2_rts__0__HSIOM)
    
    #define SCB_2_RTS_HSIOM_MASK     (SCB_2_rts__0__HSIOM_MASK)
    #define SCB_2_RTS_HSIOM_POS      (SCB_2_rts__0__HSIOM_SHIFT)    
    #define SCB_2_RTS_HSIOM_SEL_GPIO (SCB_2_rts__0__HSIOM_GPIO)
    #define SCB_2_RTS_HSIOM_SEL_UART (SCB_2_rts__0__HSIOM_UART)    
#endif /* (SCB_2_UART_RTS_PIN) */


/***************************************
*        Registers Constants
***************************************/

/* HSIOM switch values. */ 
#define SCB_2_HSIOM_DEF_SEL      (0x00u)
#define SCB_2_HSIOM_GPIO_SEL     (0x00u)
/* The HSIOM values provided below are valid only for SCB_2_CY_SCBIP_V0 
* and SCB_2_CY_SCBIP_V1. It is not recommended to use them for 
* SCB_2_CY_SCBIP_V2. Use pin name specific HSIOM constants provided 
* above instead for any SCB IP block version.
*/
#define SCB_2_HSIOM_UART_SEL     (0x09u)
#define SCB_2_HSIOM_I2C_SEL      (0x0Eu)
#define SCB_2_HSIOM_SPI_SEL      (0x0Fu)

/* Pins settings index. */
#define SCB_2_RX_WAKE_SDA_MOSI_PIN_INDEX   (0u)
#define SCB_2_RX_SDA_MOSI_PIN_INDEX       (0u)
#define SCB_2_TX_SCL_MISO_PIN_INDEX       (1u)
#define SCB_2_CTS_SCLK_PIN_INDEX       (2u)
#define SCB_2_RTS_SS0_PIN_INDEX       (3u)
#define SCB_2_SS1_PIN_INDEX                  (4u)
#define SCB_2_SS2_PIN_INDEX                  (5u)
#define SCB_2_SS3_PIN_INDEX                  (6u)

/* Pins settings mask. */
#define SCB_2_RX_WAKE_SDA_MOSI_PIN_MASK ((uint32) 0x01u << SCB_2_RX_WAKE_SDA_MOSI_PIN_INDEX)
#define SCB_2_RX_SDA_MOSI_PIN_MASK     ((uint32) 0x01u << SCB_2_RX_SDA_MOSI_PIN_INDEX)
#define SCB_2_TX_SCL_MISO_PIN_MASK     ((uint32) 0x01u << SCB_2_TX_SCL_MISO_PIN_INDEX)
#define SCB_2_CTS_SCLK_PIN_MASK     ((uint32) 0x01u << SCB_2_CTS_SCLK_PIN_INDEX)
#define SCB_2_RTS_SS0_PIN_MASK     ((uint32) 0x01u << SCB_2_RTS_SS0_PIN_INDEX)
#define SCB_2_SS1_PIN_MASK                ((uint32) 0x01u << SCB_2_SS1_PIN_INDEX)
#define SCB_2_SS2_PIN_MASK                ((uint32) 0x01u << SCB_2_SS2_PIN_INDEX)
#define SCB_2_SS3_PIN_MASK                ((uint32) 0x01u << SCB_2_SS3_PIN_INDEX)

/* Pin interrupt constants. */
#define SCB_2_INTCFG_TYPE_MASK           (0x03u)
#define SCB_2_INTCFG_TYPE_FALLING_EDGE   (0x02u)

/* Pin Drive Mode constants. */
#define SCB_2_PIN_DM_ALG_HIZ  (0u)
#define SCB_2_PIN_DM_DIG_HIZ  (1u)
#define SCB_2_PIN_DM_OD_LO    (4u)
#define SCB_2_PIN_DM_STRONG   (6u)


/***************************************
*          Macro Definitions
***************************************/

/* Return drive mode of the pin */
#define SCB_2_DM_MASK    (0x7u)
#define SCB_2_DM_SIZE    (3u)
#define SCB_2_GET_P4_PIN_DM(reg, pos) \
    ( ((reg) & (uint32) ((uint32) SCB_2_DM_MASK << (SCB_2_DM_SIZE * (pos)))) >> \
                                                              (SCB_2_DM_SIZE * (pos)) )

#if (SCB_2_TX_SCL_MISO_PIN)
    #define SCB_2_CHECK_TX_SCL_MISO_PIN_USED \
                (SCB_2_PIN_DM_ALG_HIZ != \
                    SCB_2_GET_P4_PIN_DM(SCB_2_uart_tx_i2c_scl_spi_miso_PC, \
                                                   SCB_2_uart_tx_i2c_scl_spi_miso_SHIFT))
#endif /* (SCB_2_TX_SCL_MISO_PIN) */

#if (SCB_2_RTS_SS0_PIN)
    #define SCB_2_CHECK_RTS_SS0_PIN_USED \
                (SCB_2_PIN_DM_ALG_HIZ != \
                    SCB_2_GET_P4_PIN_DM(SCB_2_uart_rts_spi_ss0_PC, \
                                                   SCB_2_uart_rts_spi_ss0_SHIFT))
#endif /* (SCB_2_RTS_SS0_PIN) */

/* Set bits-mask in register */
#define SCB_2_SET_REGISTER_BITS(reg, mask, pos, mode) \
                    do                                           \
                    {                                            \
                        (reg) = (((reg) & ((uint32) ~(uint32) (mask))) | ((uint32) ((uint32) (mode) << (pos)))); \
                    }while(0)

/* Set bit in the register */
#define SCB_2_SET_REGISTER_BIT(reg, mask, val) \
                    ((val) ? ((reg) |= (mask)) : ((reg) &= ((uint32) ~((uint32) (mask)))))

#define SCB_2_SET_HSIOM_SEL(reg, mask, pos, sel) SCB_2_SET_REGISTER_BITS(reg, mask, pos, sel)
#define SCB_2_SET_INCFG_TYPE(reg, mask, pos, intType) \
                                                        SCB_2_SET_REGISTER_BITS(reg, mask, pos, intType)
#define SCB_2_SET_INP_DIS(reg, mask, val) SCB_2_SET_REGISTER_BIT(reg, mask, val)

/* SCB_2_SET_I2C_SCL_DR(val) - Sets I2C SCL DR register.
*  SCB_2_SET_I2C_SCL_HSIOM_SEL(sel) - Sets I2C SCL HSIOM settings.
*/
/* SCB I2C: scl signal */
#if (SCB_2_CY_SCBIP_V0)
#if (SCB_2_I2C_PINS)
    #define SCB_2_SET_I2C_SCL_DR(val) SCB_2_scl_Write(val)

    #define SCB_2_SET_I2C_SCL_HSIOM_SEL(sel) \
                          SCB_2_SET_HSIOM_SEL(SCB_2_SCL_HSIOM_REG,  \
                                                         SCB_2_SCL_HSIOM_MASK, \
                                                         SCB_2_SCL_HSIOM_POS,  \
                                                         (sel))
    #define SCB_2_WAIT_SCL_SET_HIGH  (0u == SCB_2_scl_Read())

/* Unconfigured SCB: scl signal */
#elif (SCB_2_RX_WAKE_SDA_MOSI_PIN)
    #define SCB_2_SET_I2C_SCL_DR(val) \
                            SCB_2_uart_rx_wake_i2c_sda_spi_mosi_Write(val)

    #define SCB_2_SET_I2C_SCL_HSIOM_SEL(sel) \
                    SCB_2_SET_HSIOM_SEL(SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG,  \
                                                   SCB_2_RX_WAKE_SDA_MOSI_HSIOM_MASK, \
                                                   SCB_2_RX_WAKE_SDA_MOSI_HSIOM_POS,  \
                                                   (sel))

    #define SCB_2_WAIT_SCL_SET_HIGH  (0u == SCB_2_uart_rx_wake_i2c_sda_spi_mosi_Read())

#elif (SCB_2_RX_SDA_MOSI_PIN)
    #define SCB_2_SET_I2C_SCL_DR(val) \
                            SCB_2_uart_rx_i2c_sda_spi_mosi_Write(val)


    #define SCB_2_SET_I2C_SCL_HSIOM_SEL(sel) \
                            SCB_2_SET_HSIOM_SEL(SCB_2_RX_SDA_MOSI_HSIOM_REG,  \
                                                           SCB_2_RX_SDA_MOSI_HSIOM_MASK, \
                                                           SCB_2_RX_SDA_MOSI_HSIOM_POS,  \
                                                           (sel))

    #define SCB_2_WAIT_SCL_SET_HIGH  (0u == SCB_2_uart_rx_i2c_sda_spi_mosi_Read())

#else
    #define SCB_2_SET_I2C_SCL_DR(val)        do{ /* Does nothing */ }while(0)
    #define SCB_2_SET_I2C_SCL_HSIOM_SEL(sel) do{ /* Does nothing */ }while(0)

    #define SCB_2_WAIT_SCL_SET_HIGH  (0u)
#endif /* (SCB_2_I2C_PINS) */

/* SCB I2C: sda signal */
#if (SCB_2_I2C_PINS)
    #define SCB_2_WAIT_SDA_SET_HIGH  (0u == SCB_2_sda_Read())
/* Unconfigured SCB: sda signal */
#elif (SCB_2_TX_SCL_MISO_PIN)
    #define SCB_2_WAIT_SDA_SET_HIGH  (0u == SCB_2_uart_tx_i2c_scl_spi_miso_Read())
#else
    #define SCB_2_WAIT_SDA_SET_HIGH  (0u)
#endif /* (SCB_2_MOSI_SCL_RX_PIN) */
#endif /* (SCB_2_CY_SCBIP_V0) */

/* Clear UART wakeup source */
#if (SCB_2_RX_SDA_MOSI_PIN)
    #define SCB_2_CLEAR_UART_RX_WAKE_INTR        do{ /* Does nothing */ }while(0)
    
#elif (SCB_2_RX_WAKE_SDA_MOSI_PIN)
    #define SCB_2_CLEAR_UART_RX_WAKE_INTR \
            do{                                      \
                (void) SCB_2_uart_rx_wake_i2c_sda_spi_mosi_ClearInterrupt(); \
            }while(0)

#elif(SCB_2_UART_RX_WAKE_PIN)
    #define SCB_2_CLEAR_UART_RX_WAKE_INTR \
            do{                                      \
                (void) SCB_2_rx_wake_ClearInterrupt(); \
            }while(0)
#else
#endif /* (SCB_2_RX_SDA_MOSI_PIN) */


/***************************************
* The following code is DEPRECATED and
* must not be used.
***************************************/

/* Unconfigured pins */
#define SCB_2_REMOVE_MOSI_SCL_RX_WAKE_PIN    SCB_2_REMOVE_RX_WAKE_SDA_MOSI_PIN
#define SCB_2_REMOVE_MOSI_SCL_RX_PIN         SCB_2_REMOVE_RX_SDA_MOSI_PIN
#define SCB_2_REMOVE_MISO_SDA_TX_PIN         SCB_2_REMOVE_TX_SCL_MISO_PIN
#ifndef SCB_2_REMOVE_SCLK_PIN
#define SCB_2_REMOVE_SCLK_PIN                SCB_2_REMOVE_CTS_SCLK_PIN
#endif /* SCB_2_REMOVE_SCLK_PIN */
#ifndef SCB_2_REMOVE_SS0_PIN
#define SCB_2_REMOVE_SS0_PIN                 SCB_2_REMOVE_RTS_SS0_PIN
#endif /* SCB_2_REMOVE_SS0_PIN */

/* Unconfigured pins */
#define SCB_2_MOSI_SCL_RX_WAKE_PIN   SCB_2_RX_WAKE_SDA_MOSI_PIN
#define SCB_2_MOSI_SCL_RX_PIN        SCB_2_RX_SDA_MOSI_PIN
#define SCB_2_MISO_SDA_TX_PIN        SCB_2_TX_SCL_MISO_PIN
#ifndef SCB_2_SCLK_PIN
#define SCB_2_SCLK_PIN               SCB_2_CTS_SCLK_PIN
#endif /* SCB_2_SCLK_PIN */
#ifndef SCB_2_SS0_PIN
#define SCB_2_SS0_PIN                SCB_2_RTS_SS0_PIN
#endif /* SCB_2_SS0_PIN */

#if (SCB_2_MOSI_SCL_RX_WAKE_PIN)
    #define SCB_2_MOSI_SCL_RX_WAKE_HSIOM_REG     SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG
    #define SCB_2_MOSI_SCL_RX_WAKE_HSIOM_PTR     SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG
    #define SCB_2_MOSI_SCL_RX_WAKE_HSIOM_MASK    SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG
    #define SCB_2_MOSI_SCL_RX_WAKE_HSIOM_POS     SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG

    #define SCB_2_MOSI_SCL_RX_WAKE_INTCFG_REG    SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG
    #define SCB_2_MOSI_SCL_RX_WAKE_INTCFG_PTR    SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG

    #define SCB_2_MOSI_SCL_RX_WAKE_INTCFG_TYPE_POS   SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG
    #define SCB_2_MOSI_SCL_RX_WAKE_INTCFG_TYPE_MASK  SCB_2_RX_WAKE_SDA_MOSI_HSIOM_REG
#endif /* (SCB_2_RX_WAKE_SDA_MOSI_PIN) */

#if (SCB_2_MOSI_SCL_RX_PIN)
    #define SCB_2_MOSI_SCL_RX_HSIOM_REG      SCB_2_RX_SDA_MOSI_HSIOM_REG
    #define SCB_2_MOSI_SCL_RX_HSIOM_PTR      SCB_2_RX_SDA_MOSI_HSIOM_PTR
    #define SCB_2_MOSI_SCL_RX_HSIOM_MASK     SCB_2_RX_SDA_MOSI_HSIOM_MASK
    #define SCB_2_MOSI_SCL_RX_HSIOM_POS      SCB_2_RX_SDA_MOSI_HSIOM_POS
#endif /* (SCB_2_MOSI_SCL_RX_PIN) */

#if (SCB_2_MISO_SDA_TX_PIN)
    #define SCB_2_MISO_SDA_TX_HSIOM_REG      SCB_2_TX_SCL_MISO_HSIOM_REG
    #define SCB_2_MISO_SDA_TX_HSIOM_PTR      SCB_2_TX_SCL_MISO_HSIOM_REG
    #define SCB_2_MISO_SDA_TX_HSIOM_MASK     SCB_2_TX_SCL_MISO_HSIOM_REG
    #define SCB_2_MISO_SDA_TX_HSIOM_POS      SCB_2_TX_SCL_MISO_HSIOM_REG
#endif /* (SCB_2_MISO_SDA_TX_PIN_PIN) */

#if (SCB_2_SCLK_PIN)
    #ifndef SCB_2_SCLK_HSIOM_REG
    #define SCB_2_SCLK_HSIOM_REG     SCB_2_CTS_SCLK_HSIOM_REG
    #define SCB_2_SCLK_HSIOM_PTR     SCB_2_CTS_SCLK_HSIOM_PTR
    #define SCB_2_SCLK_HSIOM_MASK    SCB_2_CTS_SCLK_HSIOM_MASK
    #define SCB_2_SCLK_HSIOM_POS     SCB_2_CTS_SCLK_HSIOM_POS
    #endif /* SCB_2_SCLK_HSIOM_REG */
#endif /* (SCB_2_SCLK_PIN) */

#if (SCB_2_SS0_PIN)
    #ifndef SCB_2_SS0_HSIOM_REG
    #define SCB_2_SS0_HSIOM_REG      SCB_2_RTS_SS0_HSIOM_REG
    #define SCB_2_SS0_HSIOM_PTR      SCB_2_RTS_SS0_HSIOM_PTR
    #define SCB_2_SS0_HSIOM_MASK     SCB_2_RTS_SS0_HSIOM_MASK
    #define SCB_2_SS0_HSIOM_POS      SCB_2_RTS_SS0_HSIOM_POS
    #endif /* SCB_2_SS0_HSIOM_REG */
#endif /* (SCB_2_SS0_PIN) */

#define SCB_2_MOSI_SCL_RX_WAKE_PIN_INDEX SCB_2_RX_WAKE_SDA_MOSI_PIN_INDEX
#define SCB_2_MOSI_SCL_RX_PIN_INDEX      SCB_2_RX_SDA_MOSI_PIN_INDEX
#define SCB_2_MISO_SDA_TX_PIN_INDEX      SCB_2_TX_SCL_MISO_PIN_INDEX
#ifndef SCB_2_SCLK_PIN_INDEX
#define SCB_2_SCLK_PIN_INDEX             SCB_2_CTS_SCLK_PIN_INDEX
#endif /* SCB_2_SCLK_PIN_INDEX */
#ifndef SCB_2_SS0_PIN_INDEX
#define SCB_2_SS0_PIN_INDEX              SCB_2_RTS_SS0_PIN_INDEX
#endif /* SCB_2_SS0_PIN_INDEX */

#define SCB_2_MOSI_SCL_RX_WAKE_PIN_MASK SCB_2_RX_WAKE_SDA_MOSI_PIN_MASK
#define SCB_2_MOSI_SCL_RX_PIN_MASK      SCB_2_RX_SDA_MOSI_PIN_MASK
#define SCB_2_MISO_SDA_TX_PIN_MASK      SCB_2_TX_SCL_MISO_PIN_MASK
#ifndef SCB_2_SCLK_PIN_MASK
#define SCB_2_SCLK_PIN_MASK             SCB_2_CTS_SCLK_PIN_MASK
#endif /* SCB_2_SCLK_PIN_MASK */
#ifndef SCB_2_SS0_PIN_MASK
#define SCB_2_SS0_PIN_MASK              SCB_2_RTS_SS0_PIN_MASK
#endif /* SCB_2_SS0_PIN_MASK */

#endif /* (CY_SCB_PINS_SCB_2_H) */


/* [] END OF FILE */

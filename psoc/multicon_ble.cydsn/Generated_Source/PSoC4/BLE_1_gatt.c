/***************************************************************************//**
* \file CYBLE_gatt.c
* \version 3.30
* 
* \brief
*  This file contains the source code for the GATT API of the BLE Component.
* 
********************************************************************************
* \copyright
* Copyright 2014-2016, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
*******************************************************************************/


#include "BLE_1_eventHandler.h"


/***************************************
* Global variables
***************************************/

CYBLE_STATE_T cyBle_state;

#if ((CYBLE_MODE_PROFILE) && (CYBLE_BONDING_REQUIREMENT == CYBLE_BONDING_YES))
    
#if(CYBLE_MODE_PROFILE)
    #if defined(__ARMCC_VERSION)
        CY_ALIGN(CYDEV_FLS_ROW_SIZE) const CY_BLE_FLASH_STORAGE cyBle_flashStorage CY_SECTION(".cy_checksum_exclude") =
    #elif defined (__GNUC__)
        const CY_BLE_FLASH_STORAGE cyBle_flashStorage CY_SECTION(".cy_checksum_exclude")
            CY_ALIGN(CYDEV_FLS_ROW_SIZE) =
    #elif defined (__ICCARM__)
        #pragma data_alignment=CY_FLASH_SIZEOF_ROW
        #pragma location=".cy_checksum_exclude"
        const CY_BLE_FLASH_STORAGE cyBle_flashStorage =
    #endif  /* (__ARMCC_VERSION) */
    {
        { 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
        0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u }, 
        {{
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
        },
        {
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
        },
        {
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
        },
        {
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
        },
        {
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
            0x00u, 0x00u,
        }}, 
        0x0Cu, /* CYBLE_GATT_DB_CCCD_COUNT */ 
        0x05u, /* CYBLE_GAP_MAX_BONDED_DEVICE */ 
    };
#endif /* (CYBLE_MODE_PROFILE) */

#endif  /* (CYBLE_MODE_PROFILE) && (CYBLE_BONDING_REQUIREMENT == CYBLE_BONDING_YES) */

#if(CYBLE_GATT_ROLE_SERVER)
    
    const CYBLE_GATTS_T cyBle_gatts =
{
    0x0008u,    /* Handle of the GATT service */
    0x000Au,    /* Handle of the Service Changed characteristic */
    0x000Bu,    /* Handle of the Client Characteristic Configuration descriptor */
};
    
    static uint8 cyBle_attValues[0x4Fu] = {
    /* Device Name */
    (uint8)'B', (uint8)'L', (uint8)'E', (uint8)' ', (uint8)'M', (uint8)'o', (uint8)'u', (uint8)'s', (uint8)'e',

    /* Appearance */
    0xC2u, 0x03u,

    /* Peripheral Preferred Connection Parameters */
    0x06u, 0x00u, 0x28u, 0x00u, 0x00u, 0x00u, 0xE8u, 0x03u,

    /* Service Changed */
    0x00u, 0x00u, 0x00u, 0x00u,

    /* Protocol Mode */
    0x01u,

    /* Report */
    

    /* Report Reference */
    0x00u, 0x01u,

    /* Report Map */
    

    /* External Report Reference */
    

    /* Boot Keyboard Input Report */
    0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,

    /* Boot Keyboard Output Report */
    0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,

    /* Boot Mouse Input Report */
    0x00u, 0x00u, 0x00u,

    /* HID Information */
    0x00u, 0x00u, 0x00u, 0x00u,

    /* HID Control Point */
    0x00u,

    /* Manufacturer Name String */
    

    /* Model Number String */
    

    /* Serial Number String */
    

    /* Hardware Revision String */
    

    /* Firmware Revision String */
    

    /* Software Revision String */
    

    /* System ID */
    0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,

    /* IEEE 11073-20601 Regulatory Certification Data List */
    0x00u,

    /* PnP ID */
    0x01u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,

    /* Scan Interval Window */
    0x00u, 0x00u, 0x00u, 0x00u,

    /* Scan Refresh */
    0x00u,

    /* Battery Level */
    0x00u,

    /* Characteristic Presentation Format */
    0x00u, 0x00u, 0x33u, 0x27u, 0x01u, 0x00u, 0x00u,

};
#if(CYBLE_GATT_DB_CCCD_COUNT != 0u)
uint8 cyBle_attValuesCCCD[CYBLE_GATT_DB_CCCD_COUNT];
#endif /* CYBLE_GATT_DB_CCCD_COUNT != 0u */

CYBLE_GATTS_ATT_GEN_VAL_LEN_T cyBle_attValuesLen[CYBLE_GATT_DB_ATT_VAL_COUNT] = {
    { 0x0009u, (void *)&cyBle_attValues[0] }, /* Device Name */
    { 0x0002u, (void *)&cyBle_attValues[9] }, /* Appearance */
    { 0x0008u, (void *)&cyBle_attValues[11] }, /* Peripheral Preferred Connection Parameters */
    { 0x0004u, (void *)&cyBle_attValues[19] }, /* Service Changed */
    { 0x0002u, (void *)&cyBle_attValuesCCCD[0] }, /* Client Characteristic Configuration */
    { 0x0001u, (void *)&cyBle_attValues[23] }, /* Protocol Mode */
    { 0x0000u, (void *)&cyBle_attValues[24] }, /* Report */
    { 0x0002u, (void *)&cyBle_attValuesCCCD[2] }, /* Client Characteristic Configuration */
    { 0x0002u, (void *)&cyBle_attValues[24] }, /* Report Reference */
    { 0x0000u, (void *)&cyBle_attValues[26] }, /* Report Map */
    { 0x0002u, (void *)&cyBle_attValues[26] }, /* External Report Reference */
    { 0x0008u, (void *)&cyBle_attValues[26] }, /* Boot Keyboard Input Report */
    { 0x0002u, (void *)&cyBle_attValuesCCCD[4] }, /* Client Characteristic Configuration */
    { 0x0008u, (void *)&cyBle_attValues[34] }, /* Boot Keyboard Output Report */
    { 0x0003u, (void *)&cyBle_attValues[42] }, /* Boot Mouse Input Report */
    { 0x0002u, (void *)&cyBle_attValuesCCCD[6] }, /* Client Characteristic Configuration */
    { 0x0004u, (void *)&cyBle_attValues[45] }, /* HID Information */
    { 0x0001u, (void *)&cyBle_attValues[49] }, /* HID Control Point */
    { 0x0000u, (void *)&cyBle_attValues[50] }, /* Manufacturer Name String */
    { 0x0000u, (void *)&cyBle_attValues[50] }, /* Model Number String */
    { 0x0000u, (void *)&cyBle_attValues[50] }, /* Serial Number String */
    { 0x0000u, (void *)&cyBle_attValues[50] }, /* Hardware Revision String */
    { 0x0000u, (void *)&cyBle_attValues[50] }, /* Firmware Revision String */
    { 0x0000u, (void *)&cyBle_attValues[50] }, /* Software Revision String */
    { 0x0008u, (void *)&cyBle_attValues[50] }, /* System ID */
    { 0x0001u, (void *)&cyBle_attValues[58] }, /* IEEE 11073-20601 Regulatory Certification Data List */
    { 0x0007u, (void *)&cyBle_attValues[59] }, /* PnP ID */
    { 0x0004u, (void *)&cyBle_attValues[66] }, /* Scan Interval Window */
    { 0x0001u, (void *)&cyBle_attValues[70] }, /* Scan Refresh */
    { 0x0002u, (void *)&cyBle_attValuesCCCD[8] }, /* Client Characteristic Configuration */
    { 0x0001u, (void *)&cyBle_attValues[71] }, /* Battery Level */
    { 0x0007u, (void *)&cyBle_attValues[72] }, /* Characteristic Presentation Format */
    { 0x0002u, (void *)&cyBle_attValuesCCCD[10] }, /* Client Characteristic Configuration */
};

const CYBLE_GATTS_DB_T cyBle_gattDB[0x3Fu] = {
    { 0x0001u, 0x2800u /* Primary service                     */, 0x00000001u /*           */, 0x0007u, {{0x1800u, NULL}}                           },
    { 0x0002u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0003u, {{0x2A00u, NULL}}                           },
    { 0x0003u, 0x2A00u /* Device Name                         */, 0x01020001u /* rd        */, 0x0003u, {{0x0009u, (void *)&cyBle_attValuesLen[0]}} },
    { 0x0004u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0005u, {{0x2A01u, NULL}}                           },
    { 0x0005u, 0x2A01u /* Appearance                          */, 0x01020001u /* rd        */, 0x0005u, {{0x0002u, (void *)&cyBle_attValuesLen[1]}} },
    { 0x0006u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0007u, {{0x2A04u, NULL}}                           },
    { 0x0007u, 0x2A04u /* Peripheral Preferred Connection Par */, 0x01020001u /* rd        */, 0x0007u, {{0x0008u, (void *)&cyBle_attValuesLen[2]}} },
    { 0x0008u, 0x2800u /* Primary service                     */, 0x00000001u /*           */, 0x000Bu, {{0x1801u, NULL}}                           },
    { 0x0009u, 0x2803u /* Characteristic                      */, 0x00220001u /* rd,ind    */, 0x000Bu, {{0x2A05u, NULL}}                           },
    { 0x000Au, 0x2A05u /* Service Changed                     */, 0x01220001u /* rd,ind    */, 0x000Bu, {{0x0004u, (void *)&cyBle_attValuesLen[3]}} },
    { 0x000Bu, 0x2902u /* Client Characteristic Configuration */, 0x010A0101u /* rd,wr     */, 0x000Bu, {{0x0002u, (void *)&cyBle_attValuesLen[4]}} },
    { 0x000Cu, 0x2800u /* Primary service                     */, 0x00000001u /*           */, 0x0021u, {{0x1812u, NULL}}                           },
    { 0x000Du, 0x2803u /* Characteristic                      */, 0x00060001u /* rd,wwr    */, 0x000Eu, {{0x2A4Eu, NULL}}                           },
    { 0x000Eu, 0x2A4Eu /* Protocol Mode                       */, 0x01060101u /* rd,wwr    */, 0x000Eu, {{0x0001u, (void *)&cyBle_attValuesLen[5]}} },
    { 0x000Fu, 0x2803u /* Characteristic                      */, 0x00120001u /* rd,ntf    */, 0x0012u, {{0x2A4Du, NULL}}                           },
    { 0x0010u, 0x2A4Du /* Report                              */, 0x01120001u /* rd,ntf    */, 0x0012u, {{0x0000u, (void *)&cyBle_attValuesLen[6]}} },
    { 0x0011u, 0x2902u /* Client Characteristic Configuration */, 0x010A0101u /* rd,wr     */, 0x0011u, {{0x0002u, (void *)&cyBle_attValuesLen[7]}} },
    { 0x0012u, 0x2908u /* Report Reference                    */, 0x01020001u /* rd        */, 0x0012u, {{0x0002u, (void *)&cyBle_attValuesLen[8]}} },
    { 0x0013u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0015u, {{0x2A4Bu, NULL}}                           },
    { 0x0014u, 0x2A4Bu /* Report Map                          */, 0x01020001u /* rd        */, 0x0015u, {{0x0000u, (void *)&cyBle_attValuesLen[9]}} },
    { 0x0015u, 0x2907u /* External Report Reference           */, 0x01020001u /* rd        */, 0x0015u, {{0x0000u, (void *)&cyBle_attValuesLen[10]}} },
    { 0x0016u, 0x2803u /* Characteristic                      */, 0x00120001u /* rd,ntf    */, 0x0018u, {{0x2A22u, NULL}}                           },
    { 0x0017u, 0x2A22u /* Boot Keyboard Input Report          */, 0x01120001u /* rd,ntf    */, 0x0018u, {{0x0008u, (void *)&cyBle_attValuesLen[11]}} },
    { 0x0018u, 0x2902u /* Client Characteristic Configuration */, 0x010A0101u /* rd,wr     */, 0x0018u, {{0x0002u, (void *)&cyBle_attValuesLen[12]}} },
    { 0x0019u, 0x2803u /* Characteristic                      */, 0x000E0001u /* rd,wr,wwr */, 0x001Au, {{0x2A32u, NULL}}                           },
    { 0x001Au, 0x2A32u /* Boot Keyboard Output Report         */, 0x010E0101u /* rd,wr,wwr */, 0x001Au, {{0x0008u, (void *)&cyBle_attValuesLen[13]}} },
    { 0x001Bu, 0x2803u /* Characteristic                      */, 0x00120001u /* rd,ntf    */, 0x001Du, {{0x2A33u, NULL}}                           },
    { 0x001Cu, 0x2A33u /* Boot Mouse Input Report             */, 0x01120001u /* rd,ntf    */, 0x001Du, {{0x0003u, (void *)&cyBle_attValuesLen[14]}} },
    { 0x001Du, 0x2902u /* Client Characteristic Configuration */, 0x010A0101u /* rd,wr     */, 0x001Du, {{0x0002u, (void *)&cyBle_attValuesLen[15]}} },
    { 0x001Eu, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x001Fu, {{0x2A4Au, NULL}}                           },
    { 0x001Fu, 0x2A4Au /* HID Information                     */, 0x01020001u /* rd        */, 0x001Fu, {{0x0004u, (void *)&cyBle_attValuesLen[16]}} },
    { 0x0020u, 0x2803u /* Characteristic                      */, 0x00040001u /* wwr       */, 0x0021u, {{0x2A4Cu, NULL}}                           },
    { 0x0021u, 0x2A4Cu /* HID Control Point                   */, 0x01040100u /* wwr       */, 0x0021u, {{0x0001u, (void *)&cyBle_attValuesLen[17]}} },
    { 0x0022u, 0x2800u /* Primary service                     */, 0x00000001u /*           */, 0x0034u, {{0x180Au, NULL}}                           },
    { 0x0023u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0024u, {{0x2A29u, NULL}}                           },
    { 0x0024u, 0x2A29u /* Manufacturer Name String            */, 0x01020001u /* rd        */, 0x0024u, {{0x0000u, (void *)&cyBle_attValuesLen[18]}} },
    { 0x0025u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0026u, {{0x2A24u, NULL}}                           },
    { 0x0026u, 0x2A24u /* Model Number String                 */, 0x01020001u /* rd        */, 0x0026u, {{0x0000u, (void *)&cyBle_attValuesLen[19]}} },
    { 0x0027u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0028u, {{0x2A25u, NULL}}                           },
    { 0x0028u, 0x2A25u /* Serial Number String                */, 0x01020001u /* rd        */, 0x0028u, {{0x0000u, (void *)&cyBle_attValuesLen[20]}} },
    { 0x0029u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x002Au, {{0x2A27u, NULL}}                           },
    { 0x002Au, 0x2A27u /* Hardware Revision String            */, 0x01020001u /* rd        */, 0x002Au, {{0x0000u, (void *)&cyBle_attValuesLen[21]}} },
    { 0x002Bu, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x002Cu, {{0x2A26u, NULL}}                           },
    { 0x002Cu, 0x2A26u /* Firmware Revision String            */, 0x01020001u /* rd        */, 0x002Cu, {{0x0000u, (void *)&cyBle_attValuesLen[22]}} },
    { 0x002Du, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x002Eu, {{0x2A28u, NULL}}                           },
    { 0x002Eu, 0x2A28u /* Software Revision String            */, 0x01020001u /* rd        */, 0x002Eu, {{0x0000u, (void *)&cyBle_attValuesLen[23]}} },
    { 0x002Fu, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0030u, {{0x2A23u, NULL}}                           },
    { 0x0030u, 0x2A23u /* System ID                           */, 0x01020001u /* rd        */, 0x0030u, {{0x0008u, (void *)&cyBle_attValuesLen[24]}} },
    { 0x0031u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0032u, {{0x2A2Au, NULL}}                           },
    { 0x0032u, 0x2A2Au /* IEEE 11073-20601 Regulatory Certifi */, 0x01020001u /* rd        */, 0x0032u, {{0x0001u, (void *)&cyBle_attValuesLen[25]}} },
    { 0x0033u, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x0034u, {{0x2A50u, NULL}}                           },
    { 0x0034u, 0x2A50u /* PnP ID                              */, 0x01020001u /* rd        */, 0x0034u, {{0x0007u, (void *)&cyBle_attValuesLen[26]}} },
    { 0x0035u, 0x2800u /* Primary service                     */, 0x00000001u /*           */, 0x003Au, {{0x1813u, NULL}}                           },
    { 0x0036u, 0x2803u /* Characteristic                      */, 0x00040001u /* wwr       */, 0x0037u, {{0x2A4Fu, NULL}}                           },
    { 0x0037u, 0x2A4Fu /* Scan Interval Window                */, 0x01040100u /* wwr       */, 0x0037u, {{0x0004u, (void *)&cyBle_attValuesLen[27]}} },
    { 0x0038u, 0x2803u /* Characteristic                      */, 0x00100001u /* ntf       */, 0x003Au, {{0x2A31u, NULL}}                           },
    { 0x0039u, 0x2A31u /* Scan Refresh                        */, 0x01100000u /* ntf       */, 0x003Au, {{0x0001u, (void *)&cyBle_attValuesLen[28]}} },
    { 0x003Au, 0x2902u /* Client Characteristic Configuration */, 0x010A0101u /* rd,wr     */, 0x003Au, {{0x0002u, (void *)&cyBle_attValuesLen[29]}} },
    { 0x003Bu, 0x2800u /* Primary service                     */, 0x00000001u /*           */, 0x003Fu, {{0x180Fu, NULL}}                           },
    { 0x003Cu, 0x2803u /* Characteristic                      */, 0x00020001u /* rd        */, 0x003Fu, {{0x2A19u, NULL}}                           },
    { 0x003Du, 0x2A19u /* Battery Level                       */, 0x01020001u /* rd        */, 0x003Fu, {{0x0001u, (void *)&cyBle_attValuesLen[30]}} },
    { 0x003Eu, 0x2904u /* Characteristic Presentation Format  */, 0x01020001u /* rd        */, 0x003Eu, {{0x0007u, (void *)&cyBle_attValuesLen[31]}} },
    { 0x003Fu, 0x2902u /* Client Characteristic Configuration */, 0x010A0101u /* rd,wr     */, 0x003Fu, {{0x0002u, (void *)&cyBle_attValuesLen[32]}} },
};


#endif /* (CYBLE_GATT_ROLE_SERVER) */

#if(CYBLE_GATT_ROLE_CLIENT)
    
CYBLE_CLIENT_STATE_T cyBle_clientState;
CYBLE_GATTC_T cyBle_gattc;
CYBLE_GATT_ATTR_HANDLE_RANGE_T cyBle_gattcDiscoveryRange;
    
#endif /* (CYBLE_GATT_ROLE_CLIENT) */


#if(CYBLE_GATT_ROLE_SERVER)

/****************************************************************************** 
* Function Name: CyBle_GattsReInitGattDb
***************************************************************************//**
* 
*  Reinitializes the GATT database.
* 
*  \return
*  CYBLE_API_RESULT_T: An API result states if the API succeeded or failed with
*  error codes:

*  Errors codes                          | Description
*  ------------                          | -----------
*  CYBLE_ERROR_OK						 | GATT database was reinitialized successfully.
*  CYBLE_ERROR_INVALID_STATE             | If the function is called in any state except CYBLE_STATE_DISCONNECTED.
*  CYBLE_ERROR_INVALID_PARAMETER         | If the Database has zero entries or is a NULL pointer.
* 
******************************************************************************/
CYBLE_API_RESULT_T CyBle_GattsReInitGattDb(void)
{
    CYBLE_API_RESULT_T apiResult;
    
    if(CyBle_GetState() == CYBLE_STATE_DISCONNECTED)
    {
        apiResult = CyBle_GattsDbRegister(cyBle_gattDB, CYBLE_GATT_DB_INDEX_COUNT, CYBLE_GATT_DB_MAX_VALUE_LEN);
    }
    else
    {
        apiResult = CYBLE_ERROR_INVALID_STATE;
    }
    
    return(apiResult);
}


/****************************************************************************** 
* Function Name: CyBle_GattsWriteEventHandler
***************************************************************************//**
* 
*  Handles the Write Request Event for GATT service.
* 
*  \param eventParam: The pointer to the data structure specified by the event.
* 
*  \return
*  CYBLE_GATT_ERR_CODE_T: An API result returns one of the following status 
*  values.

*  Errors codes                          | Description
*  --------------------                  | -----------
*  CYBLE_GATT_ERR_NONE                   | Write is successful.
* 
******************************************************************************/
CYBLE_GATT_ERR_CODE_T CyBle_GattsWriteEventHandler(CYBLE_GATTS_WRITE_REQ_PARAM_T *eventParam)
{
    CYBLE_GATT_ERR_CODE_T gattErr = CYBLE_GATT_ERR_NONE;
    
    /* Client Characteristic Configuration descriptor write request */
    if(eventParam->handleValPair.attrHandle == cyBle_gatts.cccdHandle)
    {
        /* Store value to database */
        gattErr = CyBle_GattsWriteAttributeValue(&eventParam->handleValPair, 0u, 
                        &eventParam->connHandle, CYBLE_GATT_DB_PEER_INITIATED);
        
        if(gattErr == CYBLE_GATT_ERR_NONE)
        {
            if(CYBLE_IS_INDICATION_ENABLED_IN_PTR(eventParam->handleValPair.value.val))
            {
                CyBle_ApplCallback((uint32)CYBLE_EVT_GATTS_INDICATION_ENABLED, eventParam);
            }
            else
            {
                CyBle_ApplCallback((uint32)CYBLE_EVT_GATTS_INDICATION_DISABLED, eventParam);
            }
        }
        cyBle_eventHandlerFlag &= (uint8)~CYBLE_CALLBACK;
    }
    return (gattErr);
}


#endif /* (CYBLE_GATT_ROLE_SERVER) */

#if(CYBLE_GATT_ROLE_CLIENT)


/****************************************************************************** 
* Function Name: CyBle_GattcStartDiscovery
***************************************************************************//**
* 
*  Starts the automatic server discovery process. Two events may be generated 
*  after calling this function - CYBLE_EVT_GATTC_DISCOVERY_COMPLETE or 
*  CYBLE_EVT_GATTC_ERROR_RSP. The CYBLE_EVT_GATTC_DISCOVERY_COMPLETE event is 
*  generated when the remote device was successfully discovered. The
*  CYBLE_EVT_GATTC_ERROR_RSP is generated if the device discovery is failed.
* 
*  \param connHandle: The handle which consists of the device ID and ATT connection ID.
* 
* \return
*	CYBLE_API_RESULT_T : Return value indicates if the function succeeded or
*                        failed. Following are the possible error codes.
*
*   <table>	
*   <tr>
*	  <th>Errors codes</th>
*	  <th>Description</th>
*	</tr>
*	<tr>
*	  <td>CYBLE_ERROR_OK</td>
*	  <td>On successful operation</td>
*	</tr>
*	<tr>
*	  <td>CYBLE_ERROR_INVALID_PARAMETER</td>
*	  <td>'connHandle' value does not represent any existing entry.</td>
*	</tr>
*	<tr>
*	  <td>CYBLE_ERROR_INVALID_OPERATION</td>
*	  <td>The operation is not permitted</td>
*	</tr>
*   <tr>
*	  <td>CYBLE_ERROR_MEMORY_ALLOCATION_FAILED</td>
*	  <td>Memory allocation failed</td>
*	</tr>
*   <tr>
*	  <td>CYBLE_ERROR_INVALID_STATE</td>
*	  <td>If the function is called in any state except connected or discovered</td>
*	</tr>
*   </table>
* 
******************************************************************************/
CYBLE_API_RESULT_T CyBle_GattcStartDiscovery(CYBLE_CONN_HANDLE_T connHandle)
{
    uint8 j;
    CYBLE_API_RESULT_T apiResult;
    
    if((CyBle_GetState() != CYBLE_STATE_CONNECTED) || 
       ((CyBle_GetClientState() != CYBLE_CLIENT_STATE_CONNECTED) && 
        (CyBle_GetClientState() != CYBLE_CLIENT_STATE_DISCOVERED))) 
    {
        apiResult = CYBLE_ERROR_INVALID_STATE;
    }
    else
    {
        /* Clean old discovery information */
        for(j = 0u; j < (uint8) CYBLE_SRVI_COUNT; j++)
        {
            (void)memset(&cyBle_serverInfo[j].range, 0, sizeof(cyBle_serverInfo[0].range));
        }

        cyBle_connHandle = connHandle;
        cyBle_gattcDiscoveryRange.startHandle = CYBLE_GATT_ATTR_HANDLE_START_RANGE;
        cyBle_gattcDiscoveryRange.endHandle = CYBLE_GATT_ATTR_HANDLE_END_RANGE;
        
        CyBle_ServiceInit();
        
        apiResult = CyBle_GattcDiscoverAllPrimaryServices(connHandle);

        if(apiResult == CYBLE_ERROR_OK)
        {
            CyBle_SetClientState(CYBLE_CLIENT_STATE_SRVC_DISCOVERING);
            cyBle_eventHandlerFlag |= CYBLE_AUTO_DISCOVERY;
        }
    }
    
    return (apiResult);
}


/****************************************************************************** 
* Function Name: CyBle_GattcStartPartialDiscovery
***************************************************************************//**
* 
*  Starts the automatic server discovery process as per the range provided
*  on a GATT Server to which it is connected. This API could be used for 
*  partial server discovery after indication received to the Service Changed
*  Characteristic Value. Two events may be generated 
*  after calling this function - CYBLE_EVT_GATTC_DISCOVERY_COMPLETE or 
*  CYBLE_EVT_GATTC_ERROR_RSP. The CYBLE_EVT_GATTC_DISCOVERY_COMPLETE event is 
*  generated when the remote device was successfully discovered. The
*  CYBLE_EVT_GATTC_ERROR_RSP is generated if the device discovery is failed.
* 
*  \param connHandle: The handle which consists of the device ID and ATT connection ID.
*  \param startHandle: Start of affected attribute handle range.
*  \param endHandle: End of affected attribute handle range.
* 
*  \return
*	CYBLE_API_RESULT_T : Return value indicates if the function succeeded or
*                        failed. Following are the possible error codes.
*
*   <table>	
*   <tr>
*	  <th>Errors codes</th>
*	  <th>Description</th>
*	</tr>
*	<tr>
*	  <td>CYBLE_ERROR_OK</td>
*	  <td>On successful operation</td>
*	</tr>
*	<tr>
*	  <td>CYBLE_ERROR_INVALID_PARAMETER</td>
*	  <td>'connHandle' value does not represent any existing entry.</td>
*	</tr>
*	<tr>
*	  <td>CYBLE_ERROR_INVALID_OPERATION</td>
*	  <td>The operation is not permitted</td>
*	</tr>
*   <tr>
*	  <td>CYBLE_ERROR_MEMORY_ALLOCATION_FAILED</td>
*	  <td>Memory allocation failed</td>
*	</tr>
*   <tr>
*	  <td>CYBLE_ERROR_INVALID_STATE</td>
*	  <td>If the function is called in any state except connected or discovered</td>
*	</tr>
*   </table>
* 
******************************************************************************/
CYBLE_API_RESULT_T CyBle_GattcStartPartialDiscovery(CYBLE_CONN_HANDLE_T connHandle,
                        CYBLE_GATT_DB_ATTR_HANDLE_T startHandle, CYBLE_GATT_DB_ATTR_HANDLE_T endHandle)
{
    uint8 j;
    CYBLE_API_RESULT_T apiResult;
    
    if((CyBle_GetState() != CYBLE_STATE_CONNECTED) || 
       ((CyBle_GetClientState() != CYBLE_CLIENT_STATE_CONNECTED) && 
        (CyBle_GetClientState() != CYBLE_CLIENT_STATE_DISCOVERED))) 
    {
        apiResult = CYBLE_ERROR_INVALID_STATE;
    }
    else
    {
        /* Clean old discovery information of affected attribute range */
        for(j = 0u; j < (uint8) CYBLE_SRVI_COUNT; j++)
        {
            if((cyBle_serverInfo[j].range.startHandle >= startHandle) &&
               (cyBle_serverInfo[j].range.startHandle <= endHandle))
            {
                (void)memset(&cyBle_serverInfo[j].range, 0, sizeof(cyBle_serverInfo[0].range));
            }
        }

        cyBle_connHandle = connHandle;
        cyBle_gattcDiscoveryRange.startHandle = startHandle;
        cyBle_gattcDiscoveryRange.endHandle = endHandle;

        CyBle_ServiceInit();

        apiResult = CyBle_GattcDiscoverPrimaryServices(connHandle, &cyBle_gattcDiscoveryRange);

        if(apiResult == CYBLE_ERROR_OK)
        {
            CyBle_SetClientState(CYBLE_CLIENT_STATE_SRVC_DISCOVERING);
            cyBle_eventHandlerFlag |= CYBLE_AUTO_DISCOVERY;
        }
    }
    
    return (apiResult);
}


/******************************************************************************
* Function Name: CyBle_GattcDiscoverCharacteristicsEventHandler
***************************************************************************//**
* 
*  This function is called on receiving a "CYBLE_EVT_GATTC_READ_BY_TYPE_RSP"
*  event. Based on the service UUID, an appropriate data structure is populated
*  using the data received as part of the callback.
* 
*  \param *discCharInfo: The pointer to a characteristic information structure.
* 
* \return
*  None
* 
******************************************************************************/
void CyBle_GattcDiscoverCharacteristicsEventHandler(CYBLE_DISC_CHAR_INFO_T *discCharInfo)
{
    if(discCharInfo->uuid.uuid16 == CYBLE_UUID_CHAR_SERVICE_CHANGED)
    {
        CyBle_CheckStoreCharHandle(cyBle_gattc.serviceChanged);
    }
}


/******************************************************************************
* Function Name: CyBle_GattcDiscoverCharDescriptorsEventHandler
***************************************************************************//**
* 
*  This function is called on receiving a "CYBLE_EVT_GATTC_FIND_INFO_RSP" event.
*  Based on the descriptor UUID, an appropriate data structure is populated 
*  using the data received as part of the callback.
* 
*  \param *discDescrInfo: The pointer to a descriptor information structure.
*  \param discoveryService: The index of the service instance
* 
* \return
*  None
* 
******************************************************************************/
void CyBle_GattcDiscoverCharDescriptorsEventHandler(CYBLE_DISC_DESCR_INFO_T *discDescrInfo)
{
    if(discDescrInfo->uuid.uuid16 == CYBLE_UUID_CHAR_CLIENT_CONFIG)
    {
        CyBle_CheckStoreCharDescrHandle(cyBle_gattc.cccdHandle);
    }
}


/******************************************************************************
* Function Name: CyBle_GattcIndicationEventHandler
***************************************************************************//**
* 
*  Handles the Indication Event.
* 
*  \param *eventParam: The pointer to the data structure specified by the event.
* 
* \return
*  None.
* 
******************************************************************************/
void CyBle_GattcIndicationEventHandler(CYBLE_GATTC_HANDLE_VALUE_IND_PARAM_T *eventParam)
{
    if(cyBle_gattc.serviceChanged.valueHandle == eventParam->handleValPair.attrHandle)
    {
        CyBle_ApplCallback((uint32)CYBLE_EVT_GATTC_INDICATION, eventParam);
        cyBle_eventHandlerFlag &= (uint8)~CYBLE_CALLBACK;
    }
}


#endif /* (CYBLE_GATT_ROLE_CLIENT) */


/* [] END OF FILE */

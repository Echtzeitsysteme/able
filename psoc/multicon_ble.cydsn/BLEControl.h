/* ========================================
 *
 * Copyright YOUR COMPANY, THE YEAR
 * All Rights Reserved
 * UNPUBLISHED, LICENSED SOFTWARE.
 *
 * CONFIDENTIAL AND PROPRIETARY INFORMATION
 * WHICH IS THE PROPERTY OF your company.
 *
 * ========================================
*/

/* [] END OF FILE */
#include <project.h>
#ifndef __BLE_CONTROL_H__
#define __BLE_CONTROL_H__
    
uint16 joystickPos;
uint16 joystickPosOld;
int joystickNotify;//on if notifications are on, off otherwise
uint8 sensorData;
uint8 led;
    
void BleCallBack(uint32 event, void* eventParam);
void testUpdateCharacteristic();
#endif
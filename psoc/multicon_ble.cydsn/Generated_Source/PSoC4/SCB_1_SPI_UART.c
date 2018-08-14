/***************************************************************************//**
* \file SCB_1_SPI_UART.c
* \version 3.20
*
* \brief
*  This file provides the source code to the API for the SCB Component in
*  SPI and UART modes.
*
* Note:
*
*******************************************************************************
* \copyright
* Copyright 2013-2016, Cypress Semiconductor Corporation.  All rights reserved.
* You may use this file only in accordance with the license, terms, conditions,
* disclaimers, and limitations in the end user license agreement accompanying
* the software package with which this file was provided.
*******************************************************************************/

#include "SCB_1_PVT.h"
#include "SCB_1_SPI_UART_PVT.h"

/***************************************
*        SPI/UART Private Vars
***************************************/

#if(SCB_1_INTERNAL_RX_SW_BUFFER_CONST)
    /* Start index to put data into the software receive buffer.*/
    volatile uint32 SCB_1_rxBufferHead;
    /* Start index to get data from the software receive buffer.*/
    volatile uint32 SCB_1_rxBufferTail;
    /**
    * \addtogroup group_globals
    * \{
    */
    /** Sets when internal software receive buffer overflow
    *  was occurred.
    */
    volatile uint8  SCB_1_rxBufferOverflow;
    /** \} globals */
#endif /* (SCB_1_INTERNAL_RX_SW_BUFFER_CONST) */

#if(SCB_1_INTERNAL_TX_SW_BUFFER_CONST)
    /* Start index to put data into the software transmit buffer.*/
    volatile uint32 SCB_1_txBufferHead;
    /* Start index to get data from the software transmit buffer.*/
    volatile uint32 SCB_1_txBufferTail;
#endif /* (SCB_1_INTERNAL_TX_SW_BUFFER_CONST) */

#if(SCB_1_INTERNAL_RX_SW_BUFFER)
    /* Add one element to the buffer to receive full packet. One byte in receive buffer is always empty */
    volatile uint8 SCB_1_rxBufferInternal[SCB_1_INTERNAL_RX_BUFFER_SIZE];
#endif /* (SCB_1_INTERNAL_RX_SW_BUFFER) */

#if(SCB_1_INTERNAL_TX_SW_BUFFER)
    volatile uint8 SCB_1_txBufferInternal[SCB_1_TX_BUFFER_SIZE];
#endif /* (SCB_1_INTERNAL_TX_SW_BUFFER) */


#if(SCB_1_RX_DIRECTION)
    /*******************************************************************************
    * Function Name: SCB_1_SpiUartReadRxData
    ****************************************************************************//**
    *
    *  Retrieves the next data element from the receive buffer.
    *   - RX software buffer is disabled: Returns data element retrieved from
    *     RX FIFO. Undefined data will be returned if the RX FIFO is empty.
    *   - RX software buffer is enabled: Returns data element from the software
    *     receive buffer. Zero value is returned if the software receive buffer
    *     is empty.
    *
    * \return
    *  Next data element from the receive buffer. 
    *  The amount of data bits to be received depends on RX data bits selection 
    *  (the data bit counting starts from LSB of return value).
    *
    * \globalvars
    *  SCB_1_rxBufferHead - the start index to put data into the 
    *  software receive buffer.
    *  SCB_1_rxBufferTail - the start index to get data from the 
    *  software receive buffer.
    *
    *******************************************************************************/
    uint32 SCB_1_SpiUartReadRxData(void)
    {
        uint32 rxData = 0u;

    #if (SCB_1_INTERNAL_RX_SW_BUFFER_CONST)
        uint32 locTail;
    #endif /* (SCB_1_INTERNAL_RX_SW_BUFFER_CONST) */

        #if (SCB_1_CHECK_RX_SW_BUFFER)
        {
            if (SCB_1_rxBufferHead != SCB_1_rxBufferTail)
            {
                /* There is data in RX software buffer */

                /* Calculate index to read from */
                locTail = (SCB_1_rxBufferTail + 1u);

                if (SCB_1_INTERNAL_RX_BUFFER_SIZE == locTail)
                {
                    locTail = 0u;
                }

                /* Get data from RX software buffer */
                rxData = SCB_1_GetWordFromRxBuffer(locTail);

                /* Change index in the buffer */
                SCB_1_rxBufferTail = locTail;

                #if (SCB_1_CHECK_UART_RTS_CONTROL_FLOW)
                {
                    /* Check if RX Not Empty is disabled in the interrupt */
                    if (0u == (SCB_1_INTR_RX_MASK_REG & SCB_1_INTR_RX_NOT_EMPTY))
                    {
                        /* Enable RX Not Empty interrupt source to continue
                        * receiving data into software buffer.
                        */
                        SCB_1_INTR_RX_MASK_REG |= SCB_1_INTR_RX_NOT_EMPTY;
                    }
                }
                #endif

            }
        }
        #else
        {
            /* Read data from RX FIFO */
            rxData = SCB_1_RX_FIFO_RD_REG;
        }
        #endif

        return (rxData);
    }


    /*******************************************************************************
    * Function Name: SCB_1_SpiUartGetRxBufferSize
    ****************************************************************************//**
    *
    *  Returns the number of received data elements in the receive buffer.
    *   - RX software buffer disabled: returns the number of used entries in
    *     RX FIFO.
    *   - RX software buffer enabled: returns the number of elements which were
    *     placed in the receive buffer. This does not include the hardware RX FIFO.
    *
    * \return
    *  Number of received data elements.
    *
    * \globalvars
    *  SCB_1_rxBufferHead - the start index to put data into the 
    *  software receive buffer.
    *  SCB_1_rxBufferTail - the start index to get data from the 
    *  software receive buffer.
    *
    *******************************************************************************/
    uint32 SCB_1_SpiUartGetRxBufferSize(void)
    {
        uint32 size;
    #if (SCB_1_INTERNAL_RX_SW_BUFFER_CONST)
        uint32 locHead;
    #endif /* (SCB_1_INTERNAL_RX_SW_BUFFER_CONST) */

        #if (SCB_1_CHECK_RX_SW_BUFFER)
        {
            locHead = SCB_1_rxBufferHead;

            if(locHead >= SCB_1_rxBufferTail)
            {
                size = (locHead - SCB_1_rxBufferTail);
            }
            else
            {
                size = (locHead + (SCB_1_INTERNAL_RX_BUFFER_SIZE - SCB_1_rxBufferTail));
            }
        }
        #else
        {
            size = SCB_1_GET_RX_FIFO_ENTRIES;
        }
        #endif

        return (size);
    }


    /*******************************************************************************
    * Function Name: SCB_1_SpiUartClearRxBuffer
    ****************************************************************************//**
    *
    *  Clears the receive buffer and RX FIFO.
    *
    * \globalvars
    *  SCB_1_rxBufferHead - the start index to put data into the 
    *  software receive buffer.
    *  SCB_1_rxBufferTail - the start index to get data from the 
    *  software receive buffer.
    *
    *******************************************************************************/
    void SCB_1_SpiUartClearRxBuffer(void)
    {
        #if (SCB_1_CHECK_RX_SW_BUFFER)
        {
            /* Lock from component interruption */
            SCB_1_DisableInt();

            /* Flush RX software buffer */
            SCB_1_rxBufferHead = SCB_1_rxBufferTail;
            SCB_1_rxBufferOverflow = 0u;

            SCB_1_CLEAR_RX_FIFO;
            SCB_1_ClearRxInterruptSource(SCB_1_INTR_RX_ALL);

            #if (SCB_1_CHECK_UART_RTS_CONTROL_FLOW)
            {
                /* Enable RX Not Empty interrupt source to continue receiving
                * data into software buffer.
                */
                SCB_1_INTR_RX_MASK_REG |= SCB_1_INTR_RX_NOT_EMPTY;
            }
            #endif
            
            /* Release lock */
            SCB_1_EnableInt();
        }
        #else
        {
            SCB_1_CLEAR_RX_FIFO;
        }
        #endif
    }

#endif /* (SCB_1_RX_DIRECTION) */


#if(SCB_1_TX_DIRECTION)
    /*******************************************************************************
    * Function Name: SCB_1_SpiUartWriteTxData
    ****************************************************************************//**
    *
    *  Places a data entry into the transmit buffer to be sent at the next available
    *  bus time.
    *  This function is blocking and waits until there is space available to put the
    *  requested data in the transmit buffer.
    *
    *  \param txDataByte: the data to be transmitted.
    *   The amount of data bits to be transmitted depends on TX data bits selection 
    *   (the data bit counting starts from LSB of txDataByte).
    *
    * \globalvars
    *  SCB_1_txBufferHead - the start index to put data into the 
    *  software transmit buffer.
    *  SCB_1_txBufferTail - start index to get data from the software
    *  transmit buffer.
    *
    *******************************************************************************/
    void SCB_1_SpiUartWriteTxData(uint32 txData)
    {
    #if (SCB_1_INTERNAL_TX_SW_BUFFER_CONST)
        uint32 locHead;
    #endif /* (SCB_1_INTERNAL_TX_SW_BUFFER_CONST) */

        #if (SCB_1_CHECK_TX_SW_BUFFER)
        {
            /* Put data directly into the TX FIFO */
            if ((SCB_1_txBufferHead == SCB_1_txBufferTail) &&
                (SCB_1_SPI_UART_FIFO_SIZE != SCB_1_GET_TX_FIFO_ENTRIES))
            {
                /* TX software buffer is empty: put data directly in TX FIFO */
                SCB_1_TX_FIFO_WR_REG = txData;
            }
            /* Put data into TX software buffer */
            else
            {
                /* Head index to put data */
                locHead = (SCB_1_txBufferHead + 1u);

                /* Adjust TX software buffer index */
                if (SCB_1_TX_BUFFER_SIZE == locHead)
                {
                    locHead = 0u;
                }

                /* Wait for space in TX software buffer */
                while (locHead == SCB_1_txBufferTail)
                {
                }

                /* TX software buffer has at least one room */

                /* Clear old status of INTR_TX_NOT_FULL. It sets at the end of transfer when TX FIFO is empty. */
                SCB_1_ClearTxInterruptSource(SCB_1_INTR_TX_NOT_FULL);

                SCB_1_PutWordInTxBuffer(locHead, txData);

                SCB_1_txBufferHead = locHead;

                /* Check if TX Not Full is disabled in interrupt */
                if (0u == (SCB_1_INTR_TX_MASK_REG & SCB_1_INTR_TX_NOT_FULL))
                {
                    /* Enable TX Not Full interrupt source to transmit from software buffer */
                    SCB_1_INTR_TX_MASK_REG |= (uint32) SCB_1_INTR_TX_NOT_FULL;
                }
            }
        }
        #else
        {
            /* Wait until TX FIFO has space to put data element */
            while (SCB_1_SPI_UART_FIFO_SIZE == SCB_1_GET_TX_FIFO_ENTRIES)
            {
            }

            SCB_1_TX_FIFO_WR_REG = txData;
        }
        #endif
    }


    /*******************************************************************************
    * Function Name: SCB_1_SpiUartPutArray
    ****************************************************************************//**
    *
    *  Places an array of data into the transmit buffer to be sent.
    *  This function is blocking and waits until there is a space available to put
    *  all the requested data in the transmit buffer. The array size can be greater
    *  than transmit buffer size.
    *
    * \param wrBuf: pointer to an array of data to be placed in transmit buffer. 
    *  The width of the data to be transmitted depends on TX data width selection 
    *  (the data bit counting starts from LSB for each array element).
    * \param count: number of data elements to be placed in the transmit buffer.
    *
    * \globalvars
    *  SCB_1_txBufferHead - the start index to put data into the 
    *  software transmit buffer.
    *  SCB_1_txBufferTail - start index to get data from the software
    *  transmit buffer.
    *
    *******************************************************************************/
    void SCB_1_SpiUartPutArray(const uint8 wrBuf[], uint32 count)
    {
        uint32 i;

        for (i=0u; i < count; i++)
        {
            SCB_1_SpiUartWriteTxData((uint32) wrBuf[i]);
        }
    }


    /*******************************************************************************
    * Function Name: SCB_1_SpiUartGetTxBufferSize
    ****************************************************************************//**
    *
    *  Returns the number of elements currently in the transmit buffer.
    *   - TX software buffer is disabled: returns the number of used entries in
    *     TX FIFO.
    *   - TX software buffer is enabled: returns the number of elements currently
    *     used in the transmit buffer. This number does not include used entries in
    *     the TX FIFO. The transmit buffer size is zero until the TX FIFO is
    *     not full.
    *
    * \return
    *  Number of data elements ready to transmit.
    *
    * \globalvars
    *  SCB_1_txBufferHead - the start index to put data into the 
    *  software transmit buffer.
    *  SCB_1_txBufferTail - start index to get data from the software
    *  transmit buffer.
    *
    *******************************************************************************/
    uint32 SCB_1_SpiUartGetTxBufferSize(void)
    {
        uint32 size;
    #if (SCB_1_INTERNAL_TX_SW_BUFFER_CONST)
        uint32 locTail;
    #endif /* (SCB_1_INTERNAL_TX_SW_BUFFER_CONST) */

        #if (SCB_1_CHECK_TX_SW_BUFFER)
        {
            /* Get current Tail index */
            locTail = SCB_1_txBufferTail;

            if (SCB_1_txBufferHead >= locTail)
            {
                size = (SCB_1_txBufferHead - locTail);
            }
            else
            {
                size = (SCB_1_txBufferHead + (SCB_1_TX_BUFFER_SIZE - locTail));
            }
        }
        #else
        {
            size = SCB_1_GET_TX_FIFO_ENTRIES;
        }
        #endif

        return (size);
    }


    /*******************************************************************************
    * Function Name: SCB_1_SpiUartClearTxBuffer
    ****************************************************************************//**
    *
    *  Clears the transmit buffer and TX FIFO.
    *
    * \globalvars
    *  SCB_1_txBufferHead - the start index to put data into the 
    *  software transmit buffer.
    *  SCB_1_txBufferTail - start index to get data from the software
    *  transmit buffer.
    *
    *******************************************************************************/
    void SCB_1_SpiUartClearTxBuffer(void)
    {
        #if (SCB_1_CHECK_TX_SW_BUFFER)
        {
            /* Lock from component interruption */
            SCB_1_DisableInt();

            /* Flush TX software buffer */
            SCB_1_txBufferHead = SCB_1_txBufferTail;

            SCB_1_INTR_TX_MASK_REG &= (uint32) ~SCB_1_INTR_TX_NOT_FULL;
            SCB_1_CLEAR_TX_FIFO;
            SCB_1_ClearTxInterruptSource(SCB_1_INTR_TX_ALL);

            /* Release lock */
            SCB_1_EnableInt();
        }
        #else
        {
            SCB_1_CLEAR_TX_FIFO;
        }
        #endif
    }

#endif /* (SCB_1_TX_DIRECTION) */


/*******************************************************************************
* Function Name: SCB_1_SpiUartDisableIntRx
****************************************************************************//**
*
*  Disables the RX interrupt sources.
*
*  \return
*   Returns the RX interrupt sources enabled before the function call.
*
*******************************************************************************/
uint32 SCB_1_SpiUartDisableIntRx(void)
{
    uint32 intSource;

    intSource = SCB_1_GetRxInterruptMode();

    SCB_1_SetRxInterruptMode(SCB_1_NO_INTR_SOURCES);

    return (intSource);
}


/*******************************************************************************
* Function Name: SCB_1_SpiUartDisableIntTx
****************************************************************************//**
*
*  Disables TX interrupt sources.
*
*  \return
*   Returns TX interrupt sources enabled before function call.
*
*******************************************************************************/
uint32 SCB_1_SpiUartDisableIntTx(void)
{
    uint32 intSourceMask;

    intSourceMask = SCB_1_GetTxInterruptMode();

    SCB_1_SetTxInterruptMode(SCB_1_NO_INTR_SOURCES);

    return (intSourceMask);
}


#if(SCB_1_SCB_MODE_UNCONFIG_CONST_CFG)
    /*******************************************************************************
    * Function Name: SCB_1_PutWordInRxBuffer
    ****************************************************************************//**
    *
    *  Stores a byte/word into the RX buffer.
    *  Only available in the Unconfigured operation mode.
    *
    *  \param index:      index to store data byte/word in the RX buffer.
    *  \param rxDataByte: byte/word to store.
    *
    *******************************************************************************/
    void SCB_1_PutWordInRxBuffer(uint32 idx, uint32 rxDataByte)
    {
        /* Put data in buffer */
        if (SCB_1_ONE_BYTE_WIDTH == SCB_1_rxDataBits)
        {
            SCB_1_rxBuffer[idx] = ((uint8) rxDataByte);
        }
        else
        {
            SCB_1_rxBuffer[(uint32)(idx << 1u)]      = LO8(LO16(rxDataByte));
            SCB_1_rxBuffer[(uint32)(idx << 1u) + 1u] = HI8(LO16(rxDataByte));
        }
    }


    /*******************************************************************************
    * Function Name: SCB_1_GetWordFromRxBuffer
    ****************************************************************************//**
    *
    *  Reads byte/word from RX buffer.
    *  Only available in the Unconfigured operation mode.
    *
    *  \return
    *   Returns byte/word read from RX buffer.
    *
    *******************************************************************************/
    uint32 SCB_1_GetWordFromRxBuffer(uint32 idx)
    {
        uint32 value;

        if (SCB_1_ONE_BYTE_WIDTH == SCB_1_rxDataBits)
        {
            value = SCB_1_rxBuffer[idx];
        }
        else
        {
            value  = (uint32) SCB_1_rxBuffer[(uint32)(idx << 1u)];
            value |= (uint32) ((uint32)SCB_1_rxBuffer[(uint32)(idx << 1u) + 1u] << 8u);
        }

        return (value);
    }


    /*******************************************************************************
    * Function Name: SCB_1_PutWordInTxBuffer
    ****************************************************************************//**
    *
    *  Stores byte/word into the TX buffer.
    *  Only available in the Unconfigured operation mode.
    *
    *  \param idx:        index to store data byte/word in the TX buffer.
    *  \param txDataByte: byte/word to store.
    *
    *******************************************************************************/
    void SCB_1_PutWordInTxBuffer(uint32 idx, uint32 txDataByte)
    {
        /* Put data in buffer */
        if (SCB_1_ONE_BYTE_WIDTH == SCB_1_txDataBits)
        {
            SCB_1_txBuffer[idx] = ((uint8) txDataByte);
        }
        else
        {
            SCB_1_txBuffer[(uint32)(idx << 1u)]      = LO8(LO16(txDataByte));
            SCB_1_txBuffer[(uint32)(idx << 1u) + 1u] = HI8(LO16(txDataByte));
        }
    }


    /*******************************************************************************
    * Function Name: SCB_1_GetWordFromTxBuffer
    ****************************************************************************//**
    *
    *  Reads byte/word from the TX buffer.
    *  Only available in the Unconfigured operation mode.
    *
    *  \param idx: index to get data byte/word from the TX buffer.
    *
    *  \return
    *   Returns byte/word read from the TX buffer.
    *
    *******************************************************************************/
    uint32 SCB_1_GetWordFromTxBuffer(uint32 idx)
    {
        uint32 value;

        if (SCB_1_ONE_BYTE_WIDTH == SCB_1_txDataBits)
        {
            value = (uint32) SCB_1_txBuffer[idx];
        }
        else
        {
            value  = (uint32) SCB_1_txBuffer[(uint32)(idx << 1u)];
            value |= (uint32) ((uint32) SCB_1_txBuffer[(uint32)(idx << 1u) + 1u] << 8u);
        }

        return (value);
    }

#endif /* (SCB_1_SCB_MODE_UNCONFIG_CONST_CFG) */


/* [] END OF FILE */

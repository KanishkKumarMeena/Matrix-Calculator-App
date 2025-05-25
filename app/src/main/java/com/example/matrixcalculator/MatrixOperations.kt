package com.example.matrixcalculator

class MatrixOperations {
    companion object {
        init {
            System.loadLibrary("matrix-lib")
        }
    }

    // Native methods declared here, implemented in C++
    external fun addMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
    
    external fun subtractMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
    
    external fun multiplyMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
    
    external fun divideMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
} 
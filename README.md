# Matrix Calculator Android App
Android application that performs matrix operations including addition, subtraction, multiplication, and division using a hybrid architecture combining Kotlin and C++. This application satisfies all requirements for the Matrix Calculator assignment.

## Features

- Perform matrix addition, subtraction, multiplication, and division
- Support for matrices of any dimension
- Real-time validation for matrix operations
- Clear error messages when operations are not possible
- Matrix-themed UI with iconic green-on-black design

## Implementation Details

1. **Activity with UI**
   - Implemented in `MainActivity.kt` using Jetpack Compose
   - Provides a complete user interface for matrix operations
   - Includes dynamic dimension inputs, operation selection, and result display

2. **Interface to Accept Input**
   - Input fields for matrix dimensions and values
   - Operation selection buttons
   - Input validation and error handling
   - Dynamic UI updates based on selected operation

3. **C++ Library for Vector Operations**
   - Matrix operations implemented in `matrix_operations.cpp`
   - Uses STL vectors for efficient matrix representation
   - Implements all required operations (add, subtract, multiply, divide)

4. **Native Code and JNI Interface**
   - JNI bridge implemented in `MatrixOperations.kt`
   - C++ functions properly exposed to Kotlin
   - Efficient data transfer between Kotlin and C++
   - CMake configuration for native code compilation

## Project Structure

### Kotlin Files
- **`MainActivity.kt`**: Main activity containing the UI implementation using Jetpack Compose
- **`MatrixOperations.kt`**: JNI wrapper for native C++ functions

### C++ Files
- **`matrix_operations.cpp`**: Implementation of matrix operations in C++
- **`CMakeLists.txt`**: CMake configuration for building native code

### Resource Files
- **`drawable/ic_divide.xml`**: Custom vector drawable for division icon
- **`ui/theme/*.kt`**: Theme files for the Matrix-inspired UI

## Architecture

The application follows a hybrid architecture:

1. **UI Layer (Kotlin + Jetpack Compose)**
   - Handles user input and display
   - Performs input validation
   - Formats and displays results

2. **JNI Bridge Layer (Kotlin)**
   - Bridges between UI and native code
   - Handles data conversion between Kotlin and C++

3. **Computation Layer (C++)**
   - Implements core matrix operations
   - Optimized for performance
   - Handles edge cases and exceptions


## User Guide

1. Set the dimensions for Matrix A
2. Input the values for Matrix A
3. Set the dimensions for Matrix B
4. Input the values for Matrix B
5. Select the desired operation (Add, Subtract, Multiply, or Divide)
6. Click "CALCULATE"
7. View the result matrix

The application will validate the matrices for the selected operation and display appropriate error messages if the operation cannot be performed.

### Operation Constraints
- **Addition/Subtraction**: Both matrices must have the same dimensions
- **Multiplication**: Columns of Matrix A must equal rows of Matrix B
- **Division**: Matrix B must be square and non-singular (invertible) 

## App Screenshots
| Addition | Muliplication | Division |
|-|-|-|
| ![Addition](/Screenshots/Addition.png) |![Multiplication](/Screenshots/Multiplication.gif) |![Division](/Screenshots/Division.gif)|

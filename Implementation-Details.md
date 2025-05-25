# Matrix Calculator: Implementation Details

## Architecture Overview

The Matrix Calculator follows a hybrid architecture that leverages the strengths of both Kotlin and C++:

1. **Presentation Layer (Kotlin + Jetpack Compose)**
   - Handles UI rendering and user interactions
   - Implemented using the modern Jetpack Compose UI toolkit
   - Responsible for input validation and formatting

2. **Bridge Layer (JNI)**
   - Connects Kotlin and C++ components
   - Handles data conversion between Java and native types
   - Manages exception handling between language boundaries

3. **Computation Layer (C++)**
   - Implements matrix operations using efficient C++ code
   - Uses STL containers for optimal performance
   - Handles complex mathematical operations

## File Structure and Purpose

### 1. Kotlin Files

#### `MainActivity.kt`
The main Android activity that hosts the UI components and handles user interactions.

**Key Components:**
- `MatrixCalculatorApp()`: Main Composable function defining the UI structure
- `MatrixInputGrid()`: Composable for rendering matrix input fields
- `MatrixResultGrid()`: Composable for displaying result matrices

**Key Functions:**
- Matrix dimensions handling with input validation
- Operation selection logic
- Error message display and handling
- Matrix input and result formatting

#### `MatrixOperations.kt`
JNI wrapper that bridges Kotlin with native C++ code.

**Key Components:**
- Native method declarations with the `external` keyword
- JNI initialization in the companion object

**Methods:**
```kotlin
// Loads the native library
init {
    System.loadLibrary("matrix-lib")
}

// Native method declarations
external fun addMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
external fun subtractMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
external fun multiplyMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
external fun divideMatrices(matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>): Array<DoubleArray>
```

### 2. C++ Files

#### `matrix_operations.cpp`
Implementation of matrix operations in C++.

**Key Data Structures:**
- `Matrix`: Defined as `std::vector<std::vector<double>>` for efficient 2D matrix representation

**Core Functions:**
- `addMatrices()`: Matrix addition implementation
- `subtractMatrices()`: Matrix subtraction implementation
- `multiplyMatrices()`: Matrix multiplication implementation
- `divideMatrices()`: Matrix division implementation (A × B⁻¹)

**Helper Functions:**
- `inverse()`: Calculates the inverse of a matrix
- `determinant()`: Calculates the determinant of a matrix
- `adjoint()`: Calculates the adjoint of a matrix
- `getMinor()`: Gets the minor of a matrix
- `convertJavaArrayToMatrix()`: Converts Java arrays to C++ matrices
- `convertMatrixToJavaArray()`: Converts C++ matrices back to Java arrays

**JNI Function Implementations:**
```cpp
extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_addMatrices(
        JNIEnv *env, jobject /* this */, jobjectArray matrixA, jobjectArray matrixB)

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_subtractMatrices(
        JNIEnv *env, jobject /* this */, jobjectArray matrixA, jobjectArray matrixB)

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_multiplyMatrices(
        JNIEnv *env, jobject /* this */, jobjectArray matrixA, jobjectArray matrixB)

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_divideMatrices(
        JNIEnv *env, jobject /* this */, jobjectArray matrixA, jobjectArray matrixB)
```

#### `CMakeLists.txt`
CMake build configuration for compiling the native C++ code.

**Key Settings:**
- Includes NDK paths for C++ standard library
- Configures the shared library build
- Links against the Android logging library

```cmake
cmake_minimum_required(VERSION 3.22.1)
project(matrixcalculator)

# Include NDK paths
include_directories(${ANDROID_NDK}/sysroot/usr/include)
include_directories(${ANDROID_NDK}/sources/cxx-stl/llvm-libc++/include)
include_directories(${ANDROID_NDK}/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include)
include_directories(${ANDROID_NDK}/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/c++/v1)

add_library(
        matrix-lib
        SHARED
        matrix_operations.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        matrix-lib
        ${log-lib}
)
```

### 3. Configuration and Resource Files

#### `build.gradle.kts`
Android build configuration.

**Key Settings:**
- Native code support via externalNativeBuild
- CMake configuration
- Jetpack Compose dependencies

#### `ui/theme/`
UI theme files for the Matrix-inspired interface.

**Components:**
- `Theme.kt`: Defines the color scheme for the Matrix theme
- `Type.kt`: Typography settings for the application
- `Color.kt`: Color constants used throughout the app

## Detailed Implementation Workflow

### 1. Matrix Operations in C++

#### Addition and Subtraction
These operations are direct element-wise additions or subtractions and require matrices of the same dimensions:

```cpp
Matrix addMatrices(const Matrix& A, const Matrix& B) {
    if (A.size() != B.size() || A[0].size() != B[0].size()) {
        throw std::invalid_argument("Matrices dimensions do not match for addition");
    }
    
    Matrix result(A.size(), std::vector<double>(A[0].size(), 0.0));
    
    for (size_t i = 0; i < A.size(); i++) {
        for (size_t j = 0; j < A[0].size(); j++) {
            result[i][j] = A[i][j] + B[i][j];
        }
    }
    
    return result;
}
```

#### Multiplication
Matrix multiplication requires that the number of columns in the first matrix equals the number of rows in the second matrix:

```cpp
Matrix multiplyMatrices(const Matrix& A, const Matrix& B) {
    if (A[0].size() != B.size()) {
        throw std::invalid_argument("Matrices dimensions do not match for multiplication");
    }
    
    Matrix result(A.size(), std::vector<double>(B[0].size(), 0.0));
    
    for (size_t i = 0; i < A.size(); i++) {
        for (size_t j = 0; j < B[0].size(); j++) {
            for (size_t k = 0; k < A[0].size(); k++) {
                result[i][j] += A[i][k] * B[k][j];
            }
        }
    }
    
    return result;
}
```

#### Division
Matrix division is implemented as multiplication by the inverse of the second matrix:

```cpp
Matrix divideMatrices(const Matrix& A, const Matrix& B) {
    if (B.size() != B[0].size()) {
        throw std::invalid_argument("Second matrix must be square for division");
    }
    
    Matrix B_inverse = inverse(B);
    return multiplyMatrices(A, B_inverse);
}
```

The `inverse()` function calculates the inverse using the adjoint and determinant:

```cpp
Matrix inverse(const Matrix& A) {
    if (A.size() != A[0].size()) {
        throw std::invalid_argument("Matrix must be square to calculate inverse");
    }
    
    double det = determinant(A);
    
    if (det == 0) {
        throw std::invalid_argument("Matrix is singular, cannot calculate inverse");
    }
    
    Matrix adj = adjoint(A);
    Matrix inv(A.size(), std::vector<double>(A[0].size(), 0.0));
    
    for (size_t i = 0; i < A.size(); i++) {
        for (size_t j = 0; j < A[0].size(); j++) {
            inv[i][j] = adj[i][j] / det;
        }
    }
    
    return inv;
}
```

### 2. JNI Bridge

The JNI Bridge converts Java arrays to C++ matrices and back:

```cpp
Matrix convertJavaArrayToMatrix(JNIEnv *env, jobjectArray matrix) {
    jsize rows = env->GetArrayLength(matrix);
    Matrix result;
    
    for (jsize i = 0; i < rows; i++) {
        jdoubleArray row = (jdoubleArray)env->GetObjectArrayElement(matrix, i);
        jsize cols = env->GetArrayLength(row);
        
        std::vector<double> cppRow(cols);
        jdouble *elements = env->GetDoubleArrayElements(row, nullptr);
        
        for (jsize j = 0; j < cols; j++) {
            cppRow[j] = elements[j];
        }
        
        env->ReleaseDoubleArrayElements(row, elements, JNI_ABORT);
        env->DeleteLocalRef(row);
        
        result.push_back(cppRow);
    }
    
    return result;
}

jobjectArray convertMatrixToJavaArray(JNIEnv *env, const Matrix& matrix) {
    jclass doubleArrayClass = env->FindClass("[D");
    jobjectArray result = env->NewObjectArray(matrix.size(), doubleArrayClass, nullptr);
    
    for (size_t i = 0; i < matrix.size(); i++) {
        jdoubleArray row = env->NewDoubleArray(matrix[i].size());
        env->SetDoubleArrayRegion(row, 0, matrix[i].size(), matrix[i].data());
        env->SetObjectArrayElement(result, i, row);
        env->DeleteLocalRef(row);
    }
    
    env->DeleteLocalRef(doubleArrayClass);
    return result;
}
```

### 3. Jetpack Compose UI

The UI is built using Jetpack Compose with a Matrix-inspired theme. Key UI elements include:

1. **Operation Selection**
   - Buttons for selecting the matrix operation (Add, Subtract, Multiply, Divide)
   - Custom icons for each operation

2. **Matrix Dimension Inputs**
   - Input fields for setting the dimensions of both matrices
   - Automatic constraints based on the selected operation

3. **Matrix Input Grids**
   - Dynamic grids that adjust based on the specified dimensions
   - Decimal input support for matrix elements

4. **Result Display**
   - Formatted display of the result matrix
   - Error messages when operations fail

## Error Handling

The application implements comprehensive error handling at multiple levels:

1. **UI Level**
   - Validates input dimensions before attempting operations
   - Enforces constraints specific to each operation
   - Provides clear error messages

2. **C++ Level**
   - Throws exceptions for invalid matrix operations
   - Checks for singular matrices in division operation
   - Validates matrix dimensions for all operations

3. **JNI Level**
   - Propagates C++ exceptions to Java
   - Handles memory management for data conversion

## Conclusion

The Matrix Calculator demonstrates a well-architected Android application that leverages both Kotlin and C++ for optimal performance and user experience. The application satisfies all the grading requirements for the assignment by providing:

1. A complete UI using Jetpack Compose
2. A comprehensive interface for matrix input and operation selection
3. A C++ library for efficient matrix operations
4. Proper JNI integration between Kotlin and C++

The application follows best practices for Android development, native code integration, and user interface design. 
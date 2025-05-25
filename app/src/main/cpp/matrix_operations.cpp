#include <jni.h>
#include <string>
#include <vector>
#include <stdexcept>
#include <android/log.h>

using Matrix = std::vector<std::vector<double>>;

// Matrix addition
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

// Matrix subtraction
Matrix subtractMatrices(const Matrix& A, const Matrix& B) {
    if (A.size() != B.size() || A[0].size() != B[0].size()) {
        throw std::invalid_argument("Matrices dimensions do not match for subtraction");
    }
    
    Matrix result(A.size(), std::vector<double>(A[0].size(), 0.0));
    
    for (size_t i = 0; i < A.size(); i++) {
        for (size_t j = 0; j < A[0].size(); j++) {
            result[i][j] = A[i][j] - B[i][j];
        }
    }
    
    return result;
}

// Matrix multiplication
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

// Helper for scalar multiplication (for matrix division)
Matrix scalarMultiply(const Matrix& A, double scalar) {
    Matrix result = A;
    for (size_t i = 0; i < A.size(); i++) {
        for (size_t j = 0; j < A[0].size(); j++) {
            result[i][j] *= scalar;
        }
    }
    return result;
}

// Helper for matrix inversion
Matrix getMinor(const Matrix& A, size_t row, size_t col) {
    Matrix minor(A.size() - 1, std::vector<double>(A[0].size() - 1, 0.0));
    
    for (size_t i = 0, mi = 0; i < A.size(); i++) {
        if (i == row) continue;
        for (size_t j = 0, mj = 0; j < A[0].size(); j++) {
            if (j == col) continue;
            minor[mi][mj] = A[i][j];
            mj++;
        }
        mi++;
    }
    
    return minor;
}

// Calculate determinant (recursively)
double determinant(const Matrix& A) {
    if (A.size() != A[0].size()) {
        throw std::invalid_argument("Matrix must be square to calculate determinant");
    }
    
    if (A.size() == 1) {
        return A[0][0];
    }
    
    if (A.size() == 2) {
        return A[0][0] * A[1][1] - A[0][1] * A[1][0];
    }
    
    double det = 0;
    int sign = 1;
    
    for (size_t j = 0; j < A[0].size(); j++) {
        det += sign * A[0][j] * determinant(getMinor(A, 0, j));
        sign *= -1;
    }
    
    return det;
}

// Calculate adjoint of matrix
Matrix adjoint(const Matrix& A) {
    if (A.size() != A[0].size()) {
        throw std::invalid_argument("Matrix must be square to calculate adjoint");
    }
    
    Matrix adj(A.size(), std::vector<double>(A[0].size(), 0.0));
    
    for (size_t i = 0; i < A.size(); i++) {
        for (size_t j = 0; j < A[0].size(); j++) {
            int sign = ((i + j) % 2 == 0) ? 1 : -1;
            adj[j][i] = sign * determinant(getMinor(A, i, j)); // Notice the transpose: [j][i] instead of [i][j]
        }
    }
    
    return adj;
}

// Calculate inverse of matrix
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

// Matrix division (A / B = A * B^-1)
Matrix divideMatrices(const Matrix& A, const Matrix& B) {
    if (B.size() != B[0].size()) {
        throw std::invalid_argument("Second matrix must be square for division");
    }
    
    Matrix B_inverse = inverse(B);
    return multiplyMatrices(A, B_inverse);
}

// Convert Java double array to C++ matrix
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

// Convert C++ matrix to Java double array
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

// JNI implementation for addition
extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_addMatrices(
        JNIEnv *env, 
        jobject /* this */, 
        jobjectArray matrixA, 
        jobjectArray matrixB) {
    
    try {
        Matrix A = convertJavaArrayToMatrix(env, matrixA);
        Matrix B = convertJavaArrayToMatrix(env, matrixB);
        
        Matrix result = addMatrices(A, B);
        
        return convertMatrixToJavaArray(env, result);
    } catch (const std::exception& e) {
        jclass exceptionClass = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(exceptionClass, e.what());
        env->DeleteLocalRef(exceptionClass);
        return nullptr;
    }
}

// JNI implementation for subtraction
extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_subtractMatrices(
        JNIEnv *env, 
        jobject /* this */, 
        jobjectArray matrixA, 
        jobjectArray matrixB) {
    
    try {
        Matrix A = convertJavaArrayToMatrix(env, matrixA);
        Matrix B = convertJavaArrayToMatrix(env, matrixB);
        
        Matrix result = subtractMatrices(A, B);
        
        return convertMatrixToJavaArray(env, result);
    } catch (const std::exception& e) {
        jclass exceptionClass = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(exceptionClass, e.what());
        env->DeleteLocalRef(exceptionClass);
        return nullptr;
    }
}

// JNI implementation for multiplication
extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_multiplyMatrices(
        JNIEnv *env, 
        jobject /* this */, 
        jobjectArray matrixA, 
        jobjectArray matrixB) {
    
    try {
        Matrix A = convertJavaArrayToMatrix(env, matrixA);
        Matrix B = convertJavaArrayToMatrix(env, matrixB);
        
        Matrix result = multiplyMatrices(A, B);
        
        return convertMatrixToJavaArray(env, result);
    } catch (const std::exception& e) {
        jclass exceptionClass = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(exceptionClass, e.what());
        env->DeleteLocalRef(exceptionClass);
        return nullptr;
    }
}



// JNI implementation for division
extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_example_matrixcalculator_MatrixOperations_divideMatrices(
        JNIEnv *env, 
        jobject /* this */, 
        jobjectArray matrixA, 
        jobjectArray matrixB) {
    
    try {
        Matrix A = convertJavaArrayToMatrix(env, matrixA);
        Matrix B = convertJavaArrayToMatrix(env, matrixB);
        
        Matrix result = divideMatrices(A, B);
        
        return convertMatrixToJavaArray(env, result);
    } catch (const std::exception& e) {
        jclass exceptionClass = env->FindClass("java/lang/RuntimeException");
        env->ThrowNew(exceptionClass, e.what());
        env->DeleteLocalRef(exceptionClass);
        return nullptr;
    }
} 
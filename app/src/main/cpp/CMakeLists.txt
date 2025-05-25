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
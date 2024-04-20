cmake_minimum_required(VERSION 3.22)

#
# This file is generated only once,
# and is not re-generated if converter is called multiple times.
#
# User is free to modify the file as much as necessary
#

# Setup compiler settings
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_C_EXTENSIONS ON)


# Define the build type
if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE "Debug")
endif()

# Set the project name
set(CMAKE_PROJECT_NAME l4-disco-tc-agent-test)

# Include toolchain file
include("cmake/gcc-arm-none-eabi.cmake")

# Enable compile command to ease indexing with e.g. clangd
set(CMAKE_EXPORT_COMPILE_COMMANDS TRUE)

# Enable CMake support for ASM and C languages
enable_language(C ASM)

# Core project settings
project(${CMAKE_PROJECT_NAME})
message("Build type: " ${CMAKE_BUILD_TYPE})

# Create an executable object type
add_executable(${CMAKE_PROJECT_NAME})

# Add STM32CubeMX generated sources
add_subdirectory(cmake/stm32cubemx)

# Link directories setup
target_link_directories(${CMAKE_PROJECT_NAME} PRIVATE
    # Add user defined library search paths
)

# Add sources to executable
target_sources(${CMAKE_PROJECT_NAME} PRIVATE
    # Add user sources here
)

# Add include paths
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
    # Add user defined include paths
)

# Add project symbols (macros)
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
    # Add user defined symbols
)

#### BSP Library
add_library(stm32l476-discovery)
target_include_directories(stm32l476-discovery PUBLIC ${CMAKE_SOURCE_DIR}/Drivers/BSP/STM32L476G-Discovery)
target_sources(stm32l476-discovery PRIVATE
        ${CMAKE_SOURCE_DIR}/Drivers/BSP/STM32L476G-Discovery/stm32l476g_discovery.c
        ${CMAKE_SOURCE_DIR}/Drivers/BSP/STM32L476G-Discovery/stm32l476g_discovery_glass_lcd.c
)
target_link_libraries(stm32l476-discovery stm32cubemx)

# Add linked libraries
target_link_libraries(${CMAKE_PROJECT_NAME}
    stm32cubemx
    stm32l476-discovery
    # Add user defined libraries
)

#### CMSIS-DSP
add_library(cmsis-dsp INTERFACE)
target_include_directories(cmsis-dsp INTERFACE Drivers/CMSIS/DSP/Include)
target_link_options(cmsis-dsp INTERFACE -u _printf_float)
target_sources(cmsis-dsp INTERFACE
        Drivers/CMSIS/DSP/Source/StatisticsFunctions/arm_std_q31.c
        Drivers/CMSIS/DSP/Source/FastMathFunctions/arm_sqrt_q31.c
        Drivers/CMSIS/DSP/Source/StatisticsFunctions/arm_std_f32.c
)


#### Testing executables
include(on-target-testing.cmake)

add_openocd_test(
        NAME StdDev.CMSIS-DSP.STM32L476
        SOURCES Core/Test/stddev.c
        LIBRARIES stm32cubemx cmsis-dsp stm32l476-discovery
)

add_openocd_test(
        NAME StdDev.CMSIS-DSP.LCD.STM32L476
        SOURCES Core/Test/display.c
        LIBRARIES stm32cubemx cmsis-dsp stm32l476-discovery
)

add_openocd_test(
        NAME always-fail.STM32L476
        SOURCES Core/Test/always_fail.c
        LIBRARIES stm32cubemx cmsis-dsp stm32l476-discovery
)
#[[ does not work
add_qemu_test(
        NAME QEMU.fail
        SOURCES Core/Test/always_fail.c
        LIBRARIES stm32cubemx cmsis-dsp stm32l476-discovery
)

add_qemu_test(
        NAME QEMU.CMSIS-DSP.pass
        SOURCES Core/Test/stddev.c
        LIBRARIES stm32cubemx cmsis-dsp stm32l476-discovery
)

]]

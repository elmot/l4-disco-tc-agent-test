find_program(OPEN_OCD_BIN openocd HINTS "C:/tools/openocd-v0.12.0-i686-w64-mingw32/bin/")
find_program(QEMU_BIN qemu-system-arm HINTS "C:/Program Files/qemu" "C:/Program Files (x86)/qemu")

include(FetchContent)

FetchContent_Declare(
        unity
        GIT_REPOSITORY https://github.com/ThrowTheSwitch/Unity.git
        GIT_TAG v2.6.0
)

FetchContent_MakeAvailable(unity)

macro(__prepare_hw_test)
    cmake_parse_arguments(ADD_HW_TEST "" "NAME" "SOURCES;LIBRARIES" ${ARGN})
    # Semihosting
    add_executable(${ADD_HW_TEST_NAME} ${ADD_HW_TEST_SOURCES})
    target_link_libraries(${ADD_HW_TEST_NAME} ${ADD_HW_TEST_LIBRARIES})

    target_link_libraries(${ADD_HW_TEST_NAME} unity::framework)
    target_sources(${ADD_HW_TEST_NAME} PRIVATE Core/Test/test_main.c)
    target_link_options(${ADD_HW_TEST_NAME} PRIVATE --specs=rdimon.specs -lrdimon -Wl,--wrap=main)
    set_source_files_properties(Core/Src/syscalls.c PROPERTIES HEADER_FILE_ONLY 1)

endmacro()

macro(__finish_hw_test)
    set_tests_properties(${ADD_HW_TEST_NAME} PROPERTIES
            FAIL_REGULAR_EXPRESSION ":FAIL"
            PASS_REGULAR_EXPRESSION ":PASS"
            TIMEOUT 30)
endmacro()


# This function Creates an executable to test on-chip
#  format:  ADD_HW_TEST(NAME <name> SOURCES <src_file1> <src_file2> ... LIBRARIES <libname1> <libname2> )
function(add_openocd_test)
    if (NOT OPEN_OCD_BIN)
        message(WARNING "Openocd executable not found. Should be in PATH")
    endif ()

    __prepare_hw_test(${ARGN})
    add_test(NAME ${ADD_HW_TEST_NAME}
            COMMAND ${OPEN_OCD_BIN}
            -f interface/stlink.cfg
            -c "transport select hla_swd"
#            -c "set WORKAREASIZE 0x2000"
            -f target/stm32l4x.cfg
            -c "reset_config srst_only"
            -c "program $<TARGET_FILE:${ADD_HW_TEST_NAME}>"
            -c init
            -c "arm semihosting enable"
            -c "reset init"
            -c "reset run"
    )

    __finish_hw_test()
endfunction()

# This function Creates an executable to test on-chip
#  format:  add_qemu_test(NAME <name> SOURCES <src_file1> <src_file2> ... LIBRARIES <libname1> <libname2> )
function(add_qemu_test)
    if (NOT QEMU_BIN)
        message(WARNING "Qemu executable(qemu-system-arm) not found. Should be in PATH.")
    endif ()

    __prepare_hw_test(${ARGN})

    add_test(NAME ${ADD_HW_TEST_NAME}
            COMMAND ${QEMU_BIN}
            -machine ast1030-evb
            -nographic
            -monitor null
            -serial null
            -semihosting
            -kernel  $<TARGET_FILE:${ADD_HW_TEST_NAME}>
    )

    __finish_hw_test()
endfunction()

enable_testing()

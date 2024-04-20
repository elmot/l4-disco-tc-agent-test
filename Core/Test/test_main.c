#include <stdlib.h>
#include "unity.h"

extern int on_chip_test_main(void);

extern void initialise_monitor_handles(void);

__attribute__((weak)) void setUp(void) {}

__attribute__((weak)) void tearDown(void) {}

/**
 * Wrapped main for tests
 */

void __wrap_main(void) {
    initialise_monitor_handles();
    on_chip_test_main();
    exit(Unity.TestFailures);
}

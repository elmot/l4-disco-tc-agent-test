#include "unity.h"


static void fail(void) {
    TEST_FAIL_MESSAGE("Always fail here");
}

void on_chip_test_main(void) {
    UNITY_BEGIN();
    RUN_TEST(fail);
    UNITY_END();
}

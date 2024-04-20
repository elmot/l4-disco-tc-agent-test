#include "arm_math.h"
#include <stdio.h>
#include "unity.h"

const q31_t matrix[10] = {
        123456, 234567, 345678, 456789, 567890, 678901, 789012, 890123, 901234, 012345
};

void testStdDev() {
    q31_t stdDev;
    arm_std_q31((q31_t *) matrix, 10, &stdDev);
    printf("Standard Deviation: %ld\n", stdDev);
    TEST_ASSERT_EQUAL_INT(317697, stdDev);
}

int  on_chip_test_main(void) {
    UNITY_BEGIN();
    RUN_TEST(testStdDev);
    return UNITY_END();
}

#include "arm_math.h"
#include <stdio.h>
#include "unity.h"

const float32_t matrix[10] = {
        0.123456f, 2.34567f, 34.5678f, 4.56789f, 5.67890f, 6.78901f, 7.89012f, 0.890123f, 0.901234f, 0.12345f
};

void testStdDev(void) {
    float32_t stdDev;
    arm_std_f32((float32_t *) matrix, 10, &stdDev);
    printf("Standard Deviation: %f\n", stdDev);
    TEST_ASSERT_FLOAT_WITHIN_MESSAGE(0.01f, 31.7697f, stdDev, "Standard Deviation");
}

int  on_chip_test_main(void) {
    UNITY_BEGIN();
    RUN_TEST(testStdDev);
    return UNITY_END();
}

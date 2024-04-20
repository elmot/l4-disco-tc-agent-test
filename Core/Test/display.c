#include <stdio.h>
#include "unity.h"
#include "stm32l476g_discovery.h"
#include "main.h"
#include "stm32l476g_discovery_glass_lcd.h"


void SystemClock_Config(void);

__attribute__((unused)) void setUp(void) {
    HAL_Init();

    /* USER CODE BEGIN Init */

    /* USER CODE END Init */

    /* Configure the system clock */
    SystemClock_Config();

    /* USER CODE BEGIN SysInit */

    /* USER CODE END SysInit */

    /* Initialize all configured peripherals */
    MX_GPIO_Init();
    MX_LCD_Init();
    /* USER CODE BEGIN 2 */
    BSP_LCD_GLASS_Init();
    BSP_LCD_GLASS_Clear();
    BSP_LCD_GLASS_DisplayString((uint8_t *) "START");
}

void testDisplay(void) {
    uint16_t msg[6] = {'T', 'E', 'S', 'T' | DOUBLE_DOT, 'O', 'K'};
    BSP_LCD_GLASS_DisplayStrDeci(msg);
    TEST_PASS();
}

int on_chip_test_main(void) {
    UNITY_BEGIN();
    RUN_TEST(testDisplay);
    return UNITY_END();
}

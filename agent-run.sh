#/bin/sh
echo "##teamcity[blockOpened name='cmake-configure' description='CMake Configuration'"]
cmake --preset Debug --fresh -DRPI_GPIO=1
echo "##teamcity[blockClosed name='cmake-configure']"
echo "##teamcity[blockOpened name='cmake-build' description='CMake Build']"]
cmake --build --preset Debug
echo "##teamcity[blockClosed name='cmake-build']"
echo "##teamcity[blockOpened name='ctest-on-chip' description='CTest Running on-chip']"
ctest --preset on-chip-test
echo "##teamcity[blockClosed name='ctest-on-chip']"
echo "##teamcity[blockOpened name='ctest-on-chip-lcd' description='CTest Running on-chip LCD']"
ctest --preset on-chip-test
fswebcam -r 640x480 test-image.jpg
echo "##teamcity[blockClosed name='ctest-on-chip-lcd']"

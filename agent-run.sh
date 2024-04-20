#/bin/sh
echo "##teamcity[blockOpened name='cmake-configure' description='CMake Configuration'"
cmake --preset Debug --fresh
echo "##teamcity[blockClosed name='cmake-configure']"
echo "##teamcity[blockOpened name='cmake-build' description='CMake Build']"
cmake --build --preset Debug
echo "##teamcity[blockClosed name='cmake-build']"
echo "##teamcity[blockOpened name='ctest-on-chip' description='CTest Running on-chip']"
ctest --preset on-chip-test
echo "##teamcity[blockClosed name='ctest-on-chip']"

TeamCity example - unit testing on-chip
====

Raspberry Pi works as a build agent, its GPIOs are used as an SWD interface to communicate to the 
target device - [32L476GDISCOVERY](https://www.st.com/en/evaluation-tools/32l476gdiscovery.html) board.

There are three tests available:
 * _StdDev.CMSIS-DSP.STM32L476_ runs a simple calculation test using [CMSIS-DSP](https://arm-software.github.io/CMSIS_5/DSP/html/index.html) library
 * _always-fail.STM32L476_ intentionally always fail
 * _StdDev.CMSIS-DSP.LCD.STM32L476_ shows a run process and a _TEST:OK_ message on the board LCD screen

Minimal requirements:
* Raspberry Pi 4/4GB
* [TeamCity](https://www.jetbrains.com/teamcity/cloud/) cloud account
* 32L476GDISCOVERY board
* 5 jumper wires
* Optionally: a webcam
* Internet access

Steps to set the project up
---
 * Fork this repository
 * Set up your own cloud [TeamCity Server](https://www.jetbrains.com/teamcity/cloud/)
 * Install Raspberry Pi OS Lite 64bit
   * Set up network connectivity
   * Enable SSH service
   * Configure SSH access with password
 * Install necessary packages
   * Connect to Raspberry Pi via SSH and make sure 
   * Issue the command: `sudo apt update` 
   * Issue the command:
```shell
sudo apt install -y unzip git gcc binutils make libtool pkg-config autoconf automake texinfo libgpiod-dev libusb-1.0-0-dev libcapstone-dev libjim-dev libjaylink-dev fswebcam binutils-arm-none-eabi gcc-arm-none-eabi
``` 
* Build latest openocd
```shell
git clone --depth 1 git://git.code.sf.net/p/openocd/code openocd-git
cd openocd-git
./bootstrap
./configure --enable-linuxgpiod  --enable-bcm2835gpio --enable-sysfsgpio
make
sudo make install
```
* Install TeamCity agent
  * Copy [Minimal Zip file distribution](https://www.jetbrains.com/help/teamcity/install-teamcity-agent.html#How+to+Install+Agents+from+ZIP+Files) link
  * In Raspberry Pi ssh terminal issue the command `wget <copied link>`
  * Unpack and install your agent:
```shell
mkdir ~/tc-agent
unzip ~/buildAgent.zip
```
  * Add `serverUrl=<your teamcity server url>` and `system.discovery.connected=y` properties to `~/tc-agent/conf/buildAgent.properties` file
  * Configure [agent auto-start](https://www.jetbrains.com/help/teamcity/start-teamcity-agent.html#Automatic+Agent+Start+Under+Linux)
  * Reboot your Raspberry Pi
  * After a while the agent should appear in agents list at your TeamCity server. Click _Authorize_.
* Import the project
  * Open your TeamCity server main page
  * Open Projects and click _New Project..._
  * Select _From a repository URL_
  * Set up the project name and ID, configure git access to your forked repository 
  * Click _Proceed_
  * Select _ Import settings from .teamcity/settings.kts_
  * Click _Proceed_
  * Voilà!
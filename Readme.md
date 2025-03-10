# TeamCity Example: Unit Testing On-Chip

This guide demonstrates using a **Raspberry Pi** as a build agent for **unit testing on-chip**. The Raspberry Pi’s GPIO pins serve as an SWD interface to communicate with the target device – the [32L476GDISCOVERY](https://www.st.com/en/evaluation-tools/32l476gdiscovery.html) board.

## Available Tests

1. **_StdDev.CMSIS-DSP.STM32L476_**  
   Runs a simple calculation test using the [CMSIS-DSP](https://arm-software.github.io/CMSIS_5/DSP/html/index.html) library.

2. **_always-fail.STM32L476_**  
   Intentionally fails every time to validate error handling.

3. **_StdDev.CMSIS-DSP.LCD.STM32L476_**  
   Executes a test run and displays a `TEST:OK` message on the board’s LCD screen.

---

## Minimum Requirements

- **Raspberry Pi 4** (4GB recommended)
- [TeamCity Cloud Account](https://www.jetbrains.com/teamcity/cloud/)
- [32L476GDISCOVERY](https://www.st.com/en/evaluation-tools/32l476gdiscovery.html) board
- 5 jumper wires
- Webcam (optional, for visual feedback)
- Internet access

---

## Setup Instructions

### 1. Fork the Repository
Fork the relevant project repository into your GitHub account.

### 2. Set Up a TeamCity Server
Create your own TeamCity server via the [TeamCity Cloud](https://www.jetbrains.com/teamcity/cloud/).

### 3. Prepare the Raspberry Pi

- **Install Raspberry Pi OS Lite (64-bit)**
    - Enable network connectivity.
    - Activate the SSH service.
    - Configure password-based SSH access.

- **Install Necessary Packages**  
  After connecting to the Raspberry Pi via SSH, execute the following commands:
  ```bash
  sudo apt update
  sudo apt install -y unzip git gcc binutils make libtool pkg-config autoconf automake texinfo libgpiod-dev libusb-1.0-0-dev libcapstone-dev libjim-dev libjaylink-dev fswebcam binutils-arm-none-eabi gcc-arm-none-eabi
  ```

- **Build the Latest OpenOCD**  
  Compile and install OpenOCD with the commands below:
  ```bash
  git clone --depth 1 git://git.code.sf.net/p/openocd/code openocd-git
  cd openocd-git
  ./bootstrap
  ./configure --enable-linuxgpiod --enable-bcm2835gpio --enable-sysfsgpio
  make
  sudo make install
  ```

### 4. Install the TeamCity Agent

1. Go to the [Minimal Zip File Distribution](https://www.jetbrains.com/help/teamcity/install-teamcity-agent.html#How+to+Install+Agents+from+ZIP+Files) page and copy the relevant agent download link.
2. Download the TeamCity Agent:
   ```bash
   wget <copied link> -O ~/buildAgent.zip
   ```
3. Unpack and install the agent:
   ```bash
   mkdir ~/tc-agent
   unzip ~/buildAgent.zip -d ~/tc-agent
   ```
4. Configure the agent:  
   Add the following properties to the `~/tc-agent/conf/buildAgent.properties` file:
   ```
   serverUrl=<your_teamcity_server_url>
   system.discovery.connected=y
   ```
5. Set up [agent auto-start](https://www.jetbrains.com/help/teamcity/start-teamcity-agent.html#Automatic+Agent+Start+Under+Linux) and reboot your Raspberry Pi.
6. The agent should appear in the TeamCity server agent list. Click **Authorize** to activate it.

### 5. Import the Project

1. Log in to your TeamCity server.
2. Navigate to **Projects** and click **New Project...**.
3. Select **From a Repository URL**.
4. Provide a project name, ID, and the repository URL for your fork.
5. Click **Proceed** and select **Import settings from .teamcity/settings.kts**.
6. Click **Proceed** again to complete the setup.

---

Let me know if additional details or adjustments are needed!
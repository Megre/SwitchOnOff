# SwitchOnOff
![image-20231121145755749](https://renhao-picgo.oss-cn-beijing.aliyuncs.com/img/image-20231121145755749.png)

A USB-serial-port switch controller (Windows platform), which uses an [unsigned byte](#Byte Format) to represent the state of the switch.

The CPU [WaterCoolingControlModule](https://github.com/Megre/WaterCoolingControlModule) can be found [here](https://github.com/Megre/WaterCoolingControlModule).

## Usage

- Double click the `Port Name` to connect to the serial port.

- Click the `Auto` button to enable auto mode.

- Click the top-right `Settings` button to open Settings dialog.

![image-20231121150015236](https://renhao-picgo.oss-cn-beijing.aliyuncs.com/img/image-20231121150015236.png)

The `Settings` controls the On/Off condition of each switch, e.g. to switch on the `Fan` once the CPU temperature reaches 75 °C and switch off the `Fan` until the CPU is cooled down to 70 °C.



## Byte Format

### Control Command

Write a byte to the serial port to control the switch.

`bit 7`: 1 to control the switch; 0 to read the state of the switch.

When `bit 7` is 0, `bit 0 to 6` is useless. Otherwise, the `i`th bit controls the `i`th switch:

`bit 0 to 5`: 0 to close the `i`th switch; 1 to open the `i`th switch.

The byte `0xC0` means to close the serial port.



### Response

The serial port outputs a byte to identify the state of the switch.

`bit 7` is 0.

`bit 6` is useless.

`bit 0 to 5`: 0 for closed switch; 1 for open switch.

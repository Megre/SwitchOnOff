# SwitchOnOff
![image-20231101091802983](https://renhao-picgo.oss-cn-beijing.aliyuncs.com/img/image-20231101091802983.png)

A USB-serial-port switch controller, which uses an unsigned byte to represent the state of the switch.



## Byte Format

## Control Command

Write a byte to the serial port to control the switch.

`bit 7`: 1 to control the switch; 0 to read the state of the switch.

When `bit 7` is 0, `bit 0 to 6` is useless. Otherwise, the `i`th bit controls the `i`th switch:

`bit 0 to 5`: 0 to close the `i`th switch; 1 to open the `i`th switch.

The byte `0xC0` means to close the serial port.



## Response

The serial port outputs a byte to identify the state of the switch.

`bit 7` is 0.

`bit 6` is useless.

`bit 0 to 5`: 0 for closed switch; 1 for open switch.

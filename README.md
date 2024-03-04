# Code-House Bacnet4J wrapper

[![Maven build](https://github.com/Code-House/bacnet4j-wrapper/actions/workflows/maven.yml/badge.svg)](https://github.com/Code-House/bacnet4j-wrapper/actions/workflows/maven.yml)
[![Maven release](https://github.com/Code-House/bacnet4j-wrapper/actions/workflows/release.yml/badge.svg?event=release)](https://github.com/Code-House/bacnet4j-wrapper/actions/workflows/release.yml)

[![GitHub license](https://img.shields.io/github/license/Code-House/bacnet4j-wrapper.svg)](https://github.com/Code-House/bacnet4j-wrapper/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/release/Code-House/bacnet4j-wrapper.svg)](https://GitHub.com/Code-House/bacnet4j-wrapper/releases/)
[![Github all releases](https://img.shields.io/github/downloads/Code-House/bacnet4j-wrapper/total.svg)](https://GitHub.com/Code-House/bacnet4j-wrapper/releases/)

Bacnet4j is implementation of bacnet protocol in Java. Bacnet is an standard which is quite old and bacnet4j implementation
is quite awful when it comes to interacting with devices. To avoid headaches with bacnet4j calls we provided this simple
facade which hides most of weirdness's of bacnet4j. Thanks to this you can interact with devices, read and set property values,
in simpler way.

Many of initial surprises which most of developers experienced is cleaned up with 5.0 release (which is now in use), yet
API of Bacnet4j remains a mystery if you do not follow spec.
For this reason our wrapper is still maintained. Even if its documentation is not any better than original library, it still
provides basic operations in less confusing way. 

IP: variant
```java
BacNetClient client = new BacNetIpClient("<bind ip>", "<broadcast ip>", <client device id>);
client.start();
Set<Device> devices = client.discoverDevices(5000); // given number is timeout in millis
for (Device device : devices) {
    System.out.println(device);

    for (Property property : client.getDeviceProperties(device)) {
        System.out.println(property.getName() + " " + client.getPropertyValue(property));
    }
}

client.stop();
```

MSTP variant:
```java
BacNetClient client = new BacNetMstpClient("<serial port>", <serial node id>, <client device id>);
client.start();
Set<Device> devices = client.discoverDevices(5000); // given number is timeout in millis
for (Device device : devices) {
    System.out.println(device);

    for (Property property : client.getDeviceProperties(device)) {
        System.out.println(property.getName() + " " + client.getPropertyValue(property));
    }
}

client.stop();
```


## Supported features

This project is aimed to offer simple facade thus it is limited to device and following bacnet object types:

* analogInput
* analogOutput
* analogValue
* binaryInput
* binaryOutput
* binaryValue
* multiStateInput
* multiStateOutput
* multiStateValue

As of 1.2.x there is support for additional object types, however they are considered experimental:

* calendar
* pulseConverter
* schedule
* characterstringValue
* dateTimeValue
* largeAnalogValue
* octetstringValue
* timeValue
* dateValue
* integerValue
* positiveIntegerValue
* datetimePatternValue
* timePatternValue
* datePatternValue
* accumulator
* device
* trendLog
* file

As of 1.3.x version there is support for following object types:

* notificationClass

Most of them is still commendable and supports access through `present value` property, but some of them bring additional properties
which are not visible through wrapper APIs.

If you are interested in adding other types of objects to API feel free to submit PR.

Each property of type enlisted above can be read and attempted to set via `client.getPropertyValue` and `client.setPropertyValue` method calls.
There are two variants of set/get property methods - with and without converter argument.
If you know type of property then you can convert it upfront to bacnet4j structure.
By default, library will try to guess, however in most of the cases it may fail.

### How to build

To build project you need:
* Java 8
* Apache Maven 2+

## Links

* [Mango Automation BACnet4J](https://github.com/MangoAutomation/BACnet4J) - this is currently maintained version of BACnet4J used in this project.
* [Bacnet4J site](http://bacnet.sourceforge.net)
* [ConnectorIO Addons](https://github.com/connectorio/connectorio-addons) - consist a bacnet binding which integrates bacnet networks (mstp and ip) with openhab 2.x and above 
* [BACNet-openHAB-binding](https://github.com/openhab/org.openhab.binding.bacnet) - openhab 1.x binding which uses this project.

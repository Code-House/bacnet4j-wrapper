# Code-House Bacnet4J wrapper

Bacnet4j is implementation of bacnet protocol in Java. Bacnet is an standard which is quite old and bacnet4j implementation
is quite awful when it comes to interacting with devices. To avoid headaches with bacnet4j calls we provided this simple
facade which hides most of weirdness's of bacnet4j. Thanks to this you can interact with devices, read and set property values,
in simpler way.

```java
BacNetClient client = new BacNetIpClient("<bind ip>", "<broadcast ip>", <client device id>);
client.start();
Set<Device> devices = client.discoverDevices(500); // given number is timeout in millis
for (Device device : devices) {
    System.out.println(device);

    for (Property property : client.getDeviceProperties(device)) {
        System.out.println(property.getName() + " " + client.getPropertyValue(property));
    }
}

client.stop();
```

## Supported features

This project is aimed to offer simple facade thus it is limited to following bacnet object types:

* analogInput
* analogOutput
* analogValue
* binaryInput
* binaryOutput
* binaryValue

If you are interested in adding other types of objects to API feel free to submit PR.

Each property of type enlisted above can be read and attempted to set via `client.getPropertyValue` and `client.setPropertyValue` method calls. There are two variants of set/get property methods - with and without converter argument. If you know type of property then you can convert it upfront to bacnet4j structure. By default library will try to guess, however in most of cases it will fail.

### How to build

To build project you need:
* Java 8
* Apache Maven 2+

## Links

* [Frozenlock's Bacnet4J fork](https://github.com/Frozenlock/BACnet4J) - this is currently maintained version of bacnet4j used in this project.
* [Bacnet4J site](http://bacnet.sourceforge.net)
* [BACNet-openHAB-binding](https://github.com/Code-House/BACNet-openHAB-binding) - openhab binding which uses this project.

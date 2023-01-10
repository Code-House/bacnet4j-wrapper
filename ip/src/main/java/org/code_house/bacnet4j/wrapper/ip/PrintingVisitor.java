package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import org.code_house.bacnet4j.wrapper.api.BacNetObject;
import org.code_house.bacnet4j.wrapper.api.Device;

import java.io.PrintStream;
import org.code_house.bacnet4j.wrapper.device.ip.IpDevice;

class PrintingVisitor implements Visitor {

    private final PrintStream output;

    PrintingVisitor(PrintStream output) {
        this.output = output;
    }

    @Override
    public Flag visit(Device device) {
        output.println("  => Device id " + device.getInstanceNumber() + " (" + device.getClass().getSimpleName() + ")");
        output.println("     Metadata");
        if (device instanceof IpDevice) {
            IpDevice ipDevice = (IpDevice) device;
            output.println("       Address: " + ipDevice.getHostAddress() + ":" + ipDevice.getPort());
        } else {
            output.println("       Address: " + new OctetString(device.getAddress()).toString());
        }
        output.println("       Name: " + device.getName());
        output.println("       Model: " + device.getModelName());
        output.println("       Vendor: " + device.getVendorName());
        return Flag.CONTINUE;
    }

    @Override
    public Flag visit(BacNetObject object) {
        output.printf("          => Type %s id: %d%n",
            object.getType().name(),
            object.getId()
        );
        output.println("             Metadata");
        output.println("               Name: " + object.getName());
        output.println("               Units: " + object.getUnits());
        output.println("               Description: " + object.getDescription());

        return Flag.CONTINUE;
    }

    @Override
    public Flag visit(Encodable propertyValue) {
        output.printf("             Present value %s, type: %s%n",
            propertyValue,
            propertyValue != null ? propertyValue.getClass().getName() : "<null>"
        );

        return Flag.SKIP;
    }


    @Override
    public Flag visitProperty(String property, Encodable propertyValue) {
        output.printf("             Attribute '%s' value %s, type: %s%n",
            property,
            propertyValue,
            propertyValue != null ? propertyValue.getClass().getName() : "<null>"
        );

        return Flag.SKIP;
    }
}

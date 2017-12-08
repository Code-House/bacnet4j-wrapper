package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;

import java.io.PrintStream;

class PrintingVisitor implements Visitor {

    private final PrintStream output;

    PrintingVisitor(PrintStream output) {
        this.output = output;
    }

    @Override
    public Flag visit(Device device) {
        output.println("  => Device id " + device.getInstanceNumber());
        output.println("     Metadata");
        output.println("       Address: " + device.getHostAddress() + ":" + device.getPort());
        output.println("       Name: " + device.getName());
        output.println("       Model: " + device.getModelName());
        output.println("       Vendor: " + device.getVendorName());
        return Flag.CONTNUE;
    }

    @Override
    public Flag visit(Property property) {
        output.println(
            String.format("          => Type %s id: %d",
                property.getType().name(),
                property.getId()
            )
        );

        output.println("             Metadata");
        output.println("               Name: " + property.getName());
        output.println("               Units: " + property.getUnits());
        output.println("               Description: " + property.getDescription());

        return Flag.CONTNUE;
    }

    @Override
    public Flag visit(Encodable propertyValue) {
        output.println(
            String.format("             Present value %s, type: %s",
                propertyValue,
                propertyValue != null ? propertyValue.getClass().getName() : "<null>"
            )
        );

        return Flag.SKIP;
    }

}

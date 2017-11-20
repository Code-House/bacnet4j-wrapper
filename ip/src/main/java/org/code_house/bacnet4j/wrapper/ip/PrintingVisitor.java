package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;

class PrintingVisitor implements Visitor {

    @Override
    public Flag visit(Device device) {
        System.out.println("  => Device id " + device.getInstanceNumber());
        System.out.println("     Metadata");
        System.out.println("       Address: " + device.getHostAddress() + ":" + device.getPort());
        System.out.println("       Name: " + device.getName());
        System.out.println("       Model: " + device.getModelName());
        System.out.println("       Vendor: " + device.getVendorName());
        return Flag.CONTNUE;
    }

    @Override
    public Flag visit(Property property) {
        System.out.println(
            String.format("          => Type %s id: %d",
                property.getType().name(),
                property.getId()
            )
        );

        System.out.println("             Metadata");
        System.out.println("               Name: " + property.getName());
        System.out.println("               Units: " + property.getUnits());
        System.out.println("               Description: " + property.getDescription());

        return Flag.CONTNUE;
    }

    @Override
    public Flag visit(Encodable propertyValue) {
        System.out.println(
            String.format("             Present value %s, type: %s",
                propertyValue,
                propertyValue != null ? propertyValue.getClass().getName() : "<null>"
            )
        );

        return Flag.SKIP;
    }

}

/*
 * (C) Copyright 2016 Code-House and others.
 *
 * bacnet4j-wrapper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 *     https://www.gnu.org/licenses/gpl-2.0.txt
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.code_house.bacnet4j.wrapper.api.util;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.event.DeviceEventListener;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.confirmed.ReinitializeDeviceRequest;
import com.serotonin.bacnet4j.type.constructed.*;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Boolean;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * A messy way to allow event adapters/listeners execute additional requests when they receive information.
 *
 * By default notifications are handled by UDP transport thread which blocks outgoing communication. This simple bridge
 * is using given executor service to host ad-hoc threads handling notification.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class ForwardingAdapter extends DeviceEventAdapter {

    private final Logger logger = LoggerFactory.getLogger(ForwardingAdapter.class);
    private final ExecutorService executor;
    private final DeviceEventListener adapter;

    public ForwardingAdapter(ExecutorService executor, DeviceEventListener adapter) {
        this.executor = executor;
        this.adapter = adapter;
    }

    @Override
    public void listenerException(Throwable e) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.listenerException(e);
            }
        });
    }

    @Override
    public void iAmReceived(RemoteDevice d) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.iAmReceived(d);
            }
        });
    }

    @Override
    public boolean allowPropertyWrite(Address from, BACnetObject obj, PropertyValue pv) {
        try {
            executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return adapter.allowPropertyWrite(from, obj, pv);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Unable to complete operation", e);
        }
        return false;
    }

    @Override
    public void propertyWritten(Address from, BACnetObject obj, PropertyValue pv) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.propertyWritten(from, obj, pv);
            }
        });
    }

    @Override
    public void iHaveReceived(RemoteDevice d, RemoteObject o) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.iHaveReceived(d, o);
            }
        });
    }

    @Override
    public void covNotificationReceived(UnsignedInteger subscriberProcessIdentifier, RemoteDevice initiatingDevice, ObjectIdentifier monitoredObjectIdentifier, UnsignedInteger timeRemaining, SequenceOf<PropertyValue> listOfValues) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.covNotificationReceived(subscriberProcessIdentifier, initiatingDevice, monitoredObjectIdentifier, timeRemaining, listOfValues);
            }
        });
    }

    @Override
    public void eventNotificationReceived(UnsignedInteger processIdentifier, RemoteDevice initiatingDevice, ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp, UnsignedInteger notificationClass, UnsignedInteger priority, EventType eventType, CharacterString messageText, NotifyType notifyType, com.serotonin.bacnet4j.type.primitive.Boolean ackRequired, EventState fromState, EventState toState, NotificationParameters eventValues) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.eventNotificationReceived(processIdentifier, initiatingDevice, eventObjectIdentifier, timeStamp, notificationClass, priority, eventType, messageText, notifyType, ackRequired, fromState, toState, eventValues);
            }
        });
    }

    @Override
    public void textMessageReceived(RemoteDevice textMessageSourceDevice, Choice messageClass, MessagePriority messagePriority, CharacterString message) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.textMessageReceived(textMessageSourceDevice, messageClass, messagePriority, message);
            }
        });
    }

    @Override
    public void privateTransferReceived(Address from, UnsignedInteger vendorId, UnsignedInteger serviceNumber, Sequence serviceParameters) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.privateTransferReceived(from, vendorId, serviceNumber, serviceParameters);
            }
        });
    }

    @Override
    public void reinitializeDevice(Address from, ReinitializeDeviceRequest.ReinitializedStateOfDevice reinitializedStateOfDevice) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.reinitializeDevice(from, reinitializedStateOfDevice);
            }
        });
    }

    @Override
    public void synchronizeTime(Address from, DateTime dateTime, boolean utc) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                adapter.synchronizeTime(from, dateTime, utc);
            }
        });
    }
}

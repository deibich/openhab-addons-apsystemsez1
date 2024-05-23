/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.apsystemsez1.internal.dto;

/**
 * @author Tom David Eibich - Initial contribution
 *
 */
public class EZ1DeviceInfoResponseData extends EZ1ResponseData {

    public Data data;

    @Override
    public String toString() {
        return "EZ1DeviceInfoResponseData [message=" + message + ", deviceId=" + deviceId + ", data=" + data.toString()
                + "]";
    }

    public static class Data {
        public String deviceId;
        public String devVer;
        public String ssid;
        public String ipAddr;
        public String minPower;
        public String maxPower;

        @Override
        public String toString() {
            return "Data [deviceId=" + deviceId + ", devVer=" + devVer + ", ssid=" + ssid + ", ipAddr=" + ipAddr
                    + ", minPower=" + minPower + ", maxPower=" + maxPower + "]";
        }
    }
}

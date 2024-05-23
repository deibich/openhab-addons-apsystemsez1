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
package org.openhab.binding.apsystemsez1.internal;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1AlarmResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1DeviceInfoResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1MaxPowerResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1OnOffResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1OutputDataResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1ResponseData;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link APsystemsEZ1BindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Tom David Eibich - Initial contribution
 */
@NonNullByDefault
public class APsystemsEZ1BindingConstants {

    private static final String BINDING_ID = "apsystemsez1";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_EZ1_INVERTER = new ThingTypeUID(BINDING_ID, "ez1-inverter");

    // List of all Channel ids
    public static final String CHANNEL_GROUP_DEVICE = "device";
    public static final String CHANNEL_GROUP_DC1 = "dc1";
    public static final String CHANNEL_GROUP_DC2 = "dc2";

    // Power - W - float
    public static final String CHANNEL_POWER = "power";
    // Energy Startup - kWh - float
    public static final String CHANNEL_ENERGY_START = "energy-start";
    // Energy Lifetime - kWh - float
    public static final String CHANNEL_ENERGY_LIFETIME = "energy-lifetime";

    // Output Power Status
    public static final String CHANNEL_STATUS = "status";
    // Max Power Output - W
    public static final String CHANNEL_MAX_PWR = "max-power";

    // Alarm Output Grid
    public static final String CHANNEL_ALARM_OG = "alarm-og";
    // Alarm Input String
    public static final String CHANNEL_ALARM_ISCE = "alarm-isce";
    // Alarm Output Error
    public static final String CHANNEL_ALARM_OE = "alarm-oe";

    // List of all properties
    public static final String PROPERTY_DEVICE_ID = "deviceId";
    public static final String PROPERTY_DEVICE_VERSION = "deviceVersion";
    public static final String PROPERTY_SSID = "ssid";
    public static final String PROPERTY_IP_ADDRESS = "ipAddress";
    public static final String PROPERTY_MIN_POWER = "minPower";
    public static final String PROPERTY_MAX_POWER = "maxPower";

    // List of all API Endpoints
    public static final String API_ENDPOINT_GET_DEVICE_INFO = "/getDeviceInfo";
    public static final String API_ENDPOINT_GET_OUTPUT_DATA = "/getOutputData";
    public static final String API_ENDPOINT_GET_MAX_POWER = "/getMaxPower";
    public static final String API_ENDPOINT_SET_MAX_POWER = "/setMaxPower";
    public static final String API_ENDPOINT_GET_ALARM = "/getAlarm";
    public static final String API_ENDPOINT_GET_ON_OFF = "/getOnOff";
    public static final String API_ENDPOINT_SET_ON_OFF = "/setOnOff";

    public static final Map<Class<? extends EZ1ResponseData>, String> RESPONSE_DATA_GET_ENDPOINT_MAP = Map.ofEntries(
            Map.entry(EZ1DeviceInfoResponseData.class, API_ENDPOINT_GET_DEVICE_INFO),
            Map.entry(EZ1OutputDataResponseData.class, API_ENDPOINT_GET_OUTPUT_DATA),
            Map.entry(EZ1MaxPowerResponseData.class, API_ENDPOINT_GET_MAX_POWER),
            Map.entry(EZ1AlarmResponseData.class, API_ENDPOINT_GET_ALARM),
            Map.entry(EZ1OnOffResponseData.class, API_ENDPOINT_GET_ON_OFF));
}

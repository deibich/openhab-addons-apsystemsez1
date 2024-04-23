/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1AlarmResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1DeviceInfoResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1MaxPowerResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1OnOffResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1OutputDataResponseData;
import org.openhab.binding.apsystemsez1.internal.dto.EZ1ResponseData;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link APsystemsEZ1Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Tom David Eibich - Initial contribution
 */
@NonNullByDefault
public class APsystemsEZ1Handler extends BaseThingHandler {

    @SuppressWarnings("unused")
    private final Logger logger = LoggerFactory.getLogger(APsystemsEZ1Handler.class);

    private APsystemsEZ1Configuration config = new APsystemsEZ1Configuration();

    private final HttpClient httpClient;

    @Nullable
    private ScheduledFuture<?> pollingScheduled;

    private volatile boolean stopPolling = false;

    public APsystemsEZ1Handler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Allowed commands are setMaxPower and setOnOff state
        if (command instanceof RefreshType) {
            // TODO: update channels with cached values.
            return;
        }
        switch (channelUID.getIdWithoutGroup()) {
            case APsystemsEZ1BindingConstants.CHANNEL_MAX_PWR:
                if (command instanceof QuantityType<?>) {
                    var maxPowerVal = ((QuantityType<?>) command).toUnit(Units.WATT);
                    if (maxPowerVal == null) {
                        return;
                    }
                    var newValue = maxPowerVal.intValue();
                    newValue = newValue < 0 ? 0 : newValue;
                    var request = httpClient.newRequest(config.hostname, config.port)
                            .path(APsystemsEZ1BindingConstants.API_ENDPOINT_SET_MAX_POWER).method(HttpMethod.GET)
                            .timeout(2, TimeUnit.SECONDS).param("p", String.valueOf(newValue));
                    var response = sendRequest(request);
                    if (response == null) {
                        return;
                    }
                    var data = parseResponse(EZ1MaxPowerResponseData.class, response);
                    if (data == null || data.data == null) {
                        return;
                    }
                    updateState(channelUID, new QuantityType<Power>(data.data.maxPower, Units.WATT));
                }

                break;
            case APsystemsEZ1BindingConstants.CHANNEL_STATE:
                if (command instanceof OnOffType) {
                    var request = httpClient.newRequest(config.hostname, config.port)
                            .path(APsystemsEZ1BindingConstants.API_ENDPOINT_SET_ON_OFF).method(HttpMethod.GET)
                            .timeout(2, TimeUnit.SECONDS).param("status", command == OnOffType.ON ? "0" : "1");
                    var response = sendRequest(request);
                    if (response == null) {
                        return;
                    }
                    logger.info("parse");
                    var data = parseResponse(EZ1OnOffResponseData.class, response);
                    if (data == null || data.data == null) {
                        return;
                    }
                    updateState(channelUID, data.data.state.equalsIgnoreCase("0") ? OnOffType.ON : OnOffType.OFF);
                }
                break;
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(APsystemsEZ1Configuration.class);
        updateStatus(ThingStatus.UNKNOWN);

        stopPolling = false;
        pollingScheduled = scheduler.scheduleWithFixedDelay(this::pollDevice, 0, config.refreshIntervalSeconds,
                TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        super.dispose();
        stopPolling = true;
        if (pollingScheduled != null) {
            pollingScheduled.cancel(true);
        }
        pollingScheduled = null;
    }

    private void pollDevice() {
        if (stopPolling) {
            return;
        }

        // getDeviceInfo
        if (getThing().getStatus() != ThingStatus.ONLINE) {
            var responseData = pollEndpointFor(EZ1DeviceInfoResponseData.class);
            if (responseData != null) {
                if (stopPolling) {
                    return;
                }
                updateStatus(ThingStatus.ONLINE);
                getThing().setProperties(Map.ofEntries(
                        Map.entry(APsystemsEZ1BindingConstants.PROPERTY_DEVICE_ID, responseData.data.deviceId),
                        Map.entry(APsystemsEZ1BindingConstants.PROPERTY_DEVICE_VERSION, responseData.data.devVer),
                        Map.entry(APsystemsEZ1BindingConstants.PROPERTY_IP_ADDRESS, responseData.data.ipAddr),
                        Map.entry(APsystemsEZ1BindingConstants.PROPERTY_MIN_POWER, responseData.data.minPower),
                        Map.entry(APsystemsEZ1BindingConstants.PROPERTY_MAX_POWER, responseData.data.maxPower),
                        Map.entry(APsystemsEZ1BindingConstants.PROPERTY_SSID, responseData.data.ssid)));
            }
        }

        var deviceChannelGroupUID = new ChannelGroupUID(getThing().getUID(),
                APsystemsEZ1BindingConstants.CHANNEL_GROUP_DEVICE);
        var dc1ChannelGroupUID = new ChannelGroupUID(getThing().getUID(),
                APsystemsEZ1BindingConstants.CHANNEL_GROUP_DC1);
        var dc2ChannelGroupUID = new ChannelGroupUID(getThing().getUID(),
                APsystemsEZ1BindingConstants.CHANNEL_GROUP_DC2);

        // getOutputData
        {
            var responseData = pollEndpointFor(EZ1OutputDataResponseData.class);
            if (responseData != null) {
                if (stopPolling) {
                    return;
                }
                updateStatus(ThingStatus.ONLINE);

                updateState(new ChannelUID(dc1ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_POWER),
                        new QuantityType<Power>(responseData.data.p1, Units.WATT));
                updateState(new ChannelUID(dc1ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ENERGY_START),
                        new QuantityType<Energy>(responseData.data.e1, Units.KILOWATT_HOUR));
                updateState(new ChannelUID(dc1ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ENERGY_LIFETIME),
                        new QuantityType<Energy>(responseData.data.te1, Units.KILOWATT_HOUR));

                updateState(new ChannelUID(dc2ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_POWER),
                        new QuantityType<Power>(responseData.data.p2, Units.WATT));
                updateState(new ChannelUID(dc2ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ENERGY_START),
                        new QuantityType<Energy>(responseData.data.e2, Units.KILOWATT_HOUR));
                updateState(new ChannelUID(dc2ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ENERGY_LIFETIME),
                        new QuantityType<Energy>(responseData.data.te2, Units.KILOWATT_HOUR));
            }
        }

        // getMaxPower
        {
            var responseData = pollEndpointFor(EZ1MaxPowerResponseData.class);
            if (responseData != null) {
                if (stopPolling) {
                    return;
                }
                updateStatus(ThingStatus.ONLINE);
                updateState(new ChannelUID(deviceChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_MAX_PWR),
                        new QuantityType<Power>(responseData.data.maxPower, Units.WATT));
            }
        }

        // getAlarm
        {
            var responseData = pollEndpointFor(EZ1AlarmResponseData.class);
            if (responseData != null) {
                if (stopPolling) {
                    return;
                }
                updateStatus(ThingStatus.ONLINE);

                updateState(new ChannelUID(deviceChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ALARM_OE),
                        OnOffType.from(responseData.data.oe));
                updateState(new ChannelUID(deviceChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ALARM_OE),
                        OnOffType.from(responseData.data.og));

                updateState(new ChannelUID(dc1ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ALARM_ISCE),
                        OnOffType.from(responseData.data.isce1));
                updateState(new ChannelUID(dc2ChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_ALARM_ISCE),
                        OnOffType.from(responseData.data.isce2));
            }
        }

        // getOnOff
        {
            var responseData = pollEndpointFor(EZ1OnOffResponseData.class);
            if (responseData != null) {
                if (stopPolling) {
                    return;
                }
                updateStatus(ThingStatus.ONLINE);
                // 0 -> On, 1 -> Off
                updateState(new ChannelUID(deviceChannelGroupUID, APsystemsEZ1BindingConstants.CHANNEL_STATE),
                        responseData.data.state.equalsIgnoreCase("0") ? OnOffType.ON : OnOffType.OFF);
            }
        }
    }

    @Nullable
    private <T extends EZ1ResponseData> T pollEndpointFor(Class<T> type) {
        String endpoint = APsystemsEZ1BindingConstants.RESPONSE_DATA_GET_ENDPOINT_MAP.get(type);
        if (endpoint == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/error.invalid.endpoint");
            return null;
        }

        Request request = httpClient.newRequest(config.hostname, config.port).path(endpoint).method(HttpMethod.GET)
                .timeout(2, TimeUnit.SECONDS);
        var response = sendRequest(request);
        if (response == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/error.invalid.data");
            return null;
        }
        return parseResponse(type, response);
    }

    @Nullable
    private <T extends EZ1ResponseData> T parseResponse(Class<T> type, ContentResponse response) {
        var gsonObj = new Gson();
        var returnData = gsonObj.fromJson(response.getContentAsString(), type);
        if (returnData == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/error.invalid.data");
        }

        return returnData;
    }

    @Nullable
    private ContentResponse sendRequest(Request request) {
        try {
            var response = request.send();
            if (response.getStatus() == 200) {
                return response;
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/warn.api.statuscode");
            }

        } catch (JsonSyntaxException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/error.json");
        } catch (InterruptedException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/error.interrupted");
        } catch (ExecutionException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/error.execution");
        } catch (TimeoutException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "@text/error.timeout");
        }
        return null;
    }
}

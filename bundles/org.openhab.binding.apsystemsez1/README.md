# APsystems EZ1 Local API Binding

This binding integrades the local API for EZ1 inverters from [APsystems](https://apsystems.com/).

The inverters endpoint is polled in a fixed intervall.

## Supported Things

Only one thing is supported: The `APsystems EZ1 Inverter` Thing communicates with a Inverter with enabled local API.

## Discovery

Discovery is _not_ supported.

## Thing Configuration

The `APsystems EZ1 Inverter` Thing must be configured with:

| Name            | Type    | Description                            | Default | Required | Advanced |
| --------------- | ------- | -------------------------------------- | ------- | -------- | -------- |
| hostname        | text    | Hostname or IP address of the inverter | N/A     | yes      | no       |
| port            | integer | Port of the inverters local API        | 8050    | yes      | yes      |
| refreshInterval | integer | Interval the device is polled in sec.  | 10      | yes      | yes      |

The `APsystems EZ1 Inverter` Thing provides the following properties:

| Parameter     | Description              |
| ------------- | ------------------------ |
| deviceId      | ID of the device         |
| deviceVersion | Version of the device    |
| ssid          | WIFI SSID                |
| ipAddress     | IP address of the device |
| minPower      | Minimum Power            |
| maxPower      | Maximum Power            |

## Channels

The `APsystems EZ1 Inverter` Thing provides access to all data avilable as described in the API. 

| Channel                | Type          | Read/Write | Description                                                                                 |
| ---------------------- | ------------- | ---------- | ------------------------------------------------------------------------------------------- |
| device#state           | Switch        | RW         | Output power state. When set off, the device will stop outputting power to the grid.        |
| device#max-power       | Number:Power  | RW         | Maximum allowed Power in Watts which the inverter is allowed to output.                     |
| device#alarm-og        | Switch        | R          | Off-Grid alarm. When enabled: "Check whether the AC connection of the inverter is normal.". |
| device#alarm-oe        | Switch        | R          | Output fault. When enabled: "Check whether the AC connection is normal.".                   |
| device#power           | Number:Power  | R          | Current Output Power for in Watt.                                                           |
| device#energy-start    | Number:Energy | R          | Energy generation after startup in kWh.                                                     |
| device#energy-lifetime | Number:Energy | R          | Energy generation lifetime in kWh.                                                          |
| dc1#power              | Number:Power  | R          | Current Output Power for Channel 1 in Watt.                                                 |
| dc1#energy-start       | Number:Energy | R          | Energy generation after startup for Channel 1 in kWh.                                       |
| dc1#energy-lifetime    | Number:Energy | R          | Energy generation lifetime for Channel 1 in kWh.                                            |
| dc1#alarm-isce         | Switch        | R          | Short circuit alarm for Channel 1.                                                          |
| dc2#power              | Number:Power  | R          | Current Output Power for Channel 2 in Watt.                                                 |
| dc2#energy-start       | Number:Energy | R          | Energy generation after startup for Channel 2 in kWh.                                       |
| dc2#energy-lifetime    | Number:Energy | R          | Energy generation lifetime for Channel 2 in kWh.                                            |
| dc2#alarm-isce         | Switch        | R          | Short circuit alarm for Channel 2.                                                          |

## Full Example

_Provide a full usage example based on textual configuration files._
_*.things, *.items examples are mandatory as textual configuration is well used by many users._
_*.sitemap examples are optional._

### Thing Configuration

```java
Thing apsystemsez1:ez1-inverter:1 "EZ1 Inverter" [ hostname="127.0.0.1" ]
```

### Item Configuration

```java
Switch        EZ1_Inverter_Output_power_state    "Output power state"     { channel="apsystemsez1:ez1-inverter:1:device#state" }           
Number:Power  EZ1_Inverter_Device_Power          "Power"                  { channel="apsystemsez1:ez1-inverter:1:device#power" }           
Number:Energy EZ1_Inverter_Device_Energystart    "Energy since startup"   { channel="apsystemsez1:ez1-inverter:1:device#energy-start" }    
Number:Energy EZ1_Inverter_Device_Energylifetime "Energy Lifetime"        { channel="apsystemsez1:ez1-inverter:1:device#energy-lifetime" } 
Number:Power  EZ1_Inverter_Maximum_output_power  "Maximum output power"   { channel="apsystemsez1:ez1-inverter:1:device#max-power" }       
Switch        EZ1_Inverter_Alarm_offgrid         "Alarm offgrid"          { channel="apsystemsez1:ez1-inverter:1:device#alarm-og" }        
Switch        EZ1_Inverter_Output_fault          "Output fault"           { channel="apsystemsez1:ez1-inverter:1:device#alarm-oe" }        

Number:Power  EZ1_Inverter_Dc1_Power             "Power"                  { channel="apsystemsez1:ez1-inverter:1:dc1#power" }              
Number:Energy EZ1_Inverter_Dc1_Energystart       "Energy since startup"   { channel="apsystemsez1:ez1-inverter:1:dc1#energy-start" }       
Number:Energy EZ1_Inverter_Dc1_Energylifetime    "Energy Lifetime"        { channel="apsystemsez1:ez1-inverter:1:dc1#energy-lifetime" }    
Switch        EZ1_Inverter_Dc1_Alarmisce         "DC Short Circuit Error" { channel="apsystemsez1:ez1-inverter:1:dc1#alarm-isce" }         

Number:Power  EZ1_Inverter_Dc2_Power             "Power"                  { channel="apsystemsez1:ez1-inverter:1:dc2#power" }              
Number:Energy EZ1_Inverter_Dc2_Energystart       "Energy since startup"   { channel="apsystemsez1:ez1-inverter:1:dc2#energy-start" }       
Number:Energy EZ1_Inverter_Dc2_Energylifetime    "Energy Lifetime"        { channel="apsystemsez1:ez1-inverter:1:dc2#energy-lifetime" }    
Switch        EZ1_Inverter_Dc2_Alarmisce         "DC Short Circuit Error" { channel="apsystemsez1:ez1-inverter:1:dc2#alarm-isce" }     
```


# [Cordova Bluetooth A2DP Plugin] (https://github.com/JaviCastellvi/cordova-plugin-bluetootha2dp)

This plugin allows you to interact with Android bluetooth devices using the Advanced Audio Distribution Profile (A2DP).

This is an advanced streaming audio profile, known to allow streaming in stereo. For example to communicate a mobile phone and a Bluetooth hands-free, with sufficient quality to be able to use it as a music player. For this transmission to take place, as we have seen, both bluetooth devices must support the A2DP profile.

Android use Classic Bluetooth.

Note: Not all Bluetooth devices support A2DP. Please refer to the user manual of your Bluetooth device.

## Requirements ##

* Cordova 5.0.0 or higher
* Android Cordova library 5.0.0 or higher, target Android API 23/Platform 6.0 or higher (support for older Android versions should use versions 2.4.0 or below)


## Installation ##

Cordova

```cordova plugin add cordova-plugin-bluetootha2dp```

PhoneGap Build

```<gap:plugin name="cordova-plugin-bluetootha2dp" source="npm" />```

## Methods ##

* [bluetootha2dp.initialize](#initialize)
* [bluetootha2dp.discoverUnpaired](#discoverUnpaired)
* [bluetootha2dp.getBondedDevices](#getBondedDevices)
* [bluetootha2dp.getConnectedDevices](#getConnectedDevices)
* [bluetootha2dp.connect](#connect)
* [bluetootha2dp.disconnect](#disconnect)


### initialize ###
Start filter broadcast logging for the receiver when the a2dp profile connection state changes.

```javascript
bluetootha2dp.initialize();
```

##### Success #####
* status => connected = Bluetooth is connected
* status => connecting = Bluetooth is connecting
* status => disconnected = Bluetooth is disconnected

```javascript
{
    "name": "XG 5.0",
    "address": "00:06:66:4D:00:00",
    "id": "00:06:66:4D:00:00",
    "class": "1048",
    "status": "connected"
}
```

### discoverUnpaired ###
The DiscoverUnpaired function discovers unpaired Bluetooth audio devices such as wireless headphones with or without microphone. The successful callback is called with a list of objects similar to list, or an empty list if no unpaired devices are found.

```javascript
bluetootha2dp.discoverUnpaired(success, error);
```

```javascript
[{
    "name": "XG 5.0",
    "address": "00:06:66:4D:00:00",
    "id": "00:06:66:4D:00:00",
    "class": "1048"
},{
    "name": "Redmi AirDots 2",
    "address": "10:BF:48:CB:00:00",
    "id": "10:BF:48:CB:00:00",
    "class": "1048"
}]
```

The discovery process takes a while to happen. You can register notify callback with setDeviceDiscoveredListener. You may also want to show a progress indicator while waiting for the discover proces to finish, and the sucess callback to be invoked.

##### Error #####
* error = Error callback function, invoked when error occurs. [optional].

##### Success #####
Success callback function that is invoked with a list of unpaired devices.


### getBondedDevices ###
Lists bonded Bluetooth devices. The success callback is called with a list of object.

```javascript
bluetootha2dp.getBondedDevices(success, error);
```

```javascript
[{
    "name": "XG 5.0",
    "address": "00:06:66:4D:00:00",
    "id": "00:06:66:4D:00:00",
    "class": "1048"
},{
    "name": "Redmi AirDots 2",
    "address": "10:BF:48:CB:00:00",
    "id": "10:BF:48:CB:00:00",
    "class": "1048"
}]
```

##### Error #####
* error =  Error callback function, invoked when error occurs. [optional].

##### Success #####
Success callback function that is invoked with a list of bonded audio devices.


### getConnectedDevices ###
Lists connected Bluetooth audio devices that support a2dp profile. The successful callback is called with a list of objects.

```javascript
bluetootha2dp.getConnectedDevices(success, error);
```

```javascript
[{
    "name": "XG 5.0",
    "address": "00:06:66:4D:00:00",
    "id": "00:06:66:4D:00:00",
    "class": "1048"
}]
```

##### Error #####
* error =  Error callback function, invoked when error occurs. [optional].

##### Success #####
Success callback function that is invoked with a list of connected devices.

### connect ###
Connect to a Bluetooth audio device with A2DP profile. The callback is long running. Success will be called when the connection is successful. Failure is called if the connection fails, or later if the connection disconnects. An error message is passed to the failure callback.

```javascript
bluetootha2dp.connect(macAddress, success, error);
```

##### Params #####
* macAddress = The address/identifier of the remote device.


##### Success #####
* status => connected = Device connected
* status => connecting = Device connecting
* status => disconnected = Device unexpectedly disconnected

```javascript
{
  "name": "XG 5.0",
  "address": "00:06:66:4D:00:00",
  "id": "00:06:66:4D:00:00",
  "class": "1048",
  "status": "connecting"
}
{
  "name": "XG 5.0",
  "address": "00:06:66:4D:00:00",
  "id": "00:06:66:4D:00:00",
  "class": "1048",
  "status": "connected"
}

{
  "name": "XG 5.0",
  "address": "00:06:66:4D:00:00",
  "id": "00:06:66:4D:00:00",
  "class": "1048",
  "status": "disconnected"
}
```


### disconnect ###
Disconnect to a Bluetooth audio device that support A2DP profile. This Function disconnects the current connection.

```javascript
bluetootha2dp.disconnect(success, error);
```

##### Success #####
* status => disconnected = Device disconnected

```javascript
{
  "name": "XG 5.0",
  "address": "00:06:66:4D:00:00",
  "id": "00:06:66:4D:00:00",
  "class": "1048",
  "status": "disconnected"
}
```

## More information ##
* Author: Javier Castellvi Vera
* Email: <javier.castellvi.vera@gmail.com>
* Facebook: https://www.facebook.com/JaviCastellvi
* LinkedIn: https://www.linkedin.com/in/javier-castellví


## License ##
The MIT License (MIT)

Copyright (c) 2022 Javi Castellvi and contributors.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

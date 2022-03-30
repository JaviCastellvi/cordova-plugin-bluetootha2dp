var exec = require('cordova/exec');

exports.initialize = function (success, error) {
    exec(success, error, 'BluetoothA2dpPlugin', 'initialize');
};

exports.discoverUnpaired = function (success, error) {
    exec(success, error, 'BluetoothA2dpPlugin', 'discoverUnpaired');
};

exports.getBondedDevices = function (success, error) {
    exec(success, error, 'BluetoothA2dpPlugin', 'getBondedDevices');
};

exports.getConnectedDevices = function (success, error) {
    exec(success, error, 'BluetoothA2dpPlugin', 'getConnectedDevices');
};

exports.connect = function (macAddress, success, error) {
    exec(success, error, 'BluetoothA2dpPlugin', 'connect', [macAddress]);
};

exports.disconnect = function (macAddress, success, error) {
    exec(success, error, 'BluetoothA2dpPlugin', 'disconnect', [macAddress]);
};

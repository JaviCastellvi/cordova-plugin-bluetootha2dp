package cordova.plugin.bluetootha2dp;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

/**
 * This class echoes a string called from JavaScript.
 */
public class BluetoothA2dpPlugin extends CordovaPlugin {

  private CallbackContext deviceDiscoveredCallback;
  private CallbackContext initializeCallback;
  private CallbackContext connectCallback;
  private CallbackContext disconnectCallback;
  private BluetoothDevice device;
  private BluetoothAdapter bluetoothAdapter;
  private static final int CHECK_PERMISSIONS_REQ_CODE = 2;
  private Context mContext;
  private static final String TAG = "BluetoothA2dpPlugin";

  @Override
  public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

    mContext = this.cordova.getActivity().getApplicationContext();
    LOG.d(TAG, "action = " + action);

    if (bluetoothAdapter == null) {
      bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    if (action.equals("initialize")) {
      initializeCallback= callbackContext;
      cordova.getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
      return true;
    }
    if (action.equals("discoverUnpaired")) {
      if (cordova.hasPermission(ACCESS_COARSE_LOCATION)) {
        this.discoverUnpairedDevices(callbackContext);
      } else {
        cordova.requestPermission(this, CHECK_PERMISSIONS_REQ_CODE, ACCESS_COARSE_LOCATION);
      }
      return true;
    }
    if (action.equals("getBondedDevices")) {
      this.getBondedDevices(callbackContext);
      return true;
    }
    if (action.equals("getConnectedDevices")) {
      this.getConnectedDevices(callbackContext);
      return true;
    }
    if (action.equals("connect")) {
      connectCallback= callbackContext;
      this.connect(args, callbackContext);
      return true;
    }
    if (action.equals("disconnect")) {
      disconnectCallback= callbackContext;
      this.disconnect(args, callbackContext);
      return true;
    }
    return false;
  }

  private void discoverUnpairedDevices(final CallbackContext callbackContext) throws JSONException {

    final CallbackContext ddc = deviceDiscoveredCallback;

    final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {

      private JSONArray unpairedDevices = new JSONArray();

      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
          BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          try {
            if (device != null) {
              BluetoothClass bluetoothClass = device.getBluetoothClass();
              if (bluetoothClass != null) {
                if (bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES || bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
                  JSONObject o = deviceToJSON(device);
                  unpairedDevices.put(o);
                  if (ddc != null) {
                    PluginResult res = new PluginResult(PluginResult.Status.OK, o);
                    res.setKeepCallback(true);
                    ddc.sendPluginResult(res);
                  }
                }
              }
            }
          } catch (JSONException e) {
            // This shouldn't happen, log and ignore
            Log.e(TAG, "Problem converting device to JSON", e);
          }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
          callbackContext.success(unpairedDevices);
          cordova.getActivity().unregisterReceiver(this);
        }
      }
    };

    Activity activity = cordova.getActivity();
    activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    bluetoothAdapter.startDiscovery();
  }

  private void getBondedDevices(CallbackContext callbackContext) throws JSONException {
    JSONArray deviceList = new JSONArray();
    Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

    for (BluetoothDevice device : bondedDevices) {
      BluetoothClass bluetoothClass = device.getBluetoothClass();
      if (bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES || bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
        deviceList.put(deviceToJSON(device));
      }
    }
    callbackContext.success(deviceList);
  }

  private void getConnectedDevices(CallbackContext callbackContext) {
    JSONArray deviceList = new JSONArray();
    final BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
      @Override
      public void onServiceDisconnected(int profile) {
      }

      @Override
      public void onServiceConnected(int profile, BluetoothProfile proxy) {
        List<BluetoothDevice> devices= proxy.getConnectedDevices();
          for(BluetoothDevice device : devices) {
            try {
              BluetoothClass bluetoothClass = device.getBluetoothClass();
              if (bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES || bluetoothClass.getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
                deviceList.put(deviceToJSON(device));
              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        callbackContext.success(deviceList);
        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
      }
    };
    BluetoothAdapter.getDefaultAdapter().getProfileProxy(mContext, serviceListener, BluetoothProfile.A2DP);
  }

  private void connect(CordovaArgs args, CallbackContext callbackContext) throws JSONException {

    connectCallback=callbackContext;
    String macAddress = args.getString(0);
    device = bluetoothAdapter.getRemoteDevice(macAddress);


    final BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
      @Override
      public void onServiceDisconnected(int profile) {
      }

      @Override
      public void onServiceConnected(int profile, BluetoothProfile proxy) {
        try {
          proxy.getClass().getMethod("connect", BluetoothDevice.class).invoke(proxy, device);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException  e) {
          e.printStackTrace();
          connectCallback.error(e.getMessage());
        }
        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
      }
    };
    BluetoothAdapter.getDefaultAdapter().getProfileProxy(mContext, serviceListener, BluetoothProfile.A2DP);

  }

  private void disconnect(CordovaArgs args, CallbackContext callbackContext) throws JSONException {

    disconnectCallback= callbackContext;
    String macAddress = args.getString(0);
    device = bluetoothAdapter.getRemoteDevice(macAddress);

    final BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
      @Override
      public void onServiceDisconnected(int profile) {
      }

      @Override
      public void onServiceConnected(int profile, BluetoothProfile proxy) {
        try {
          proxy.getClass().getMethod("disconnect", BluetoothDevice.class).invoke(proxy, device);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          e.printStackTrace();
          disconnectCallback.error(e.getMessage());
        }
        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
      }
    };
    BluetoothAdapter.getDefaultAdapter().getProfileProxy(mContext, serviceListener, BluetoothProfile.A2DP);
  }

  private JSONObject deviceToJSON(BluetoothDevice device) throws JSONException {
    JSONObject json = new JSONObject();
    json.put("name", device.getName());
    json.put("address", device.getAddress());
    json.put("id", device.getAddress());
    if (device.getBluetoothClass() != null) {
      json.put("class", device.getBluetoothClass().getDeviceClass());
    }
    return json;
  }

  private void sendPluginResult(JSONObject json, CallbackContext callbackContext){
    PluginResult result = new PluginResult(PluginResult.Status.OK, json);
    result.setKeepCallback(true);
    callbackContext.sendPluginResult(result);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    cordova.getActivity().unregisterReceiver(mReceiver);
  }

  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

    public void onReceive(Context ctx, Intent intent) {
      String action = intent.getAction();
      Log.d(TAG, "receive intent for action : " + action);
      if (Objects.equals(action, BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
        int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (state == BluetoothA2dp.STATE_CONNECTED) {
          try {
            JSONObject json = deviceToJSON(Objects.requireNonNull(device));
            json.put("status", "connected");
            if (connectCallback == null) {
              sendPluginResult(json, initializeCallback);
            } else {
              sendPluginResult(json, connectCallback);
            }

          } catch (JSONException e) {
            e.printStackTrace();
            connectCallback.error(e.getMessage());
          }
        }  else if (state == BluetoothA2dp.STATE_CONNECTING) {
          try {
            JSONObject json = deviceToJSON(Objects.requireNonNull(device));
            json.put("status", "connecting");
            if (connectCallback == null) {
              sendPluginResult(json, initializeCallback);
            } else {
              sendPluginResult(json, connectCallback);
            }
          } catch (JSONException e) {
            e.printStackTrace();
            connectCallback.error(e.getMessage());
          }
        }else if (state == BluetoothA2dp.STATE_DISCONNECTED) {
          try {
            JSONObject json = deviceToJSON(Objects.requireNonNull(device));
            json.put("status", "disconnected");
            if (disconnectCallback == null) {
              sendPluginResult(json, initializeCallback);
            } else {
              sendPluginResult(json, disconnectCallback);
              disconnectCallback = null;
            }
          } catch (JSONException e) {
            e.printStackTrace();
            disconnectCallback.error(e.getMessage());
          }
        }
      }
    }

  };
}

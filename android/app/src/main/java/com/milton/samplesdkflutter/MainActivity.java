package com.milton.samplesdkflutter;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.milton.samplesdkflutter.sdk_impl.CapLogsImplEvents;
import com.water.water_io_sdk.ble.entities.DeviceLog;
import com.water.water_io_sdk.ble.enums.ScanRequest;
import com.water.water_io_sdk.ble.sdkEngine.configurations.WIOConfig;
import com.water.water_io_sdk.ble.sdkEngine.lifecycleModel.WIOApp;
import com.water.water_io_sdk.ble.sdkEngine.lifecycleModel.WIOAppFactory;
import com.water.water_io_sdk.ble.utilities.CapConfig;
import com.water.water_io_sdk.reminder_local_push.entities.Reminder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    private WIOApp mWIOApp;
    private static final int PERMISSION_REQUEST = 10;
    private static final int GPS_PERMISSION = 20;
    private static final int BLUETOOTH_PERMISSION = 30;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);

        askBatteryOptimizations();
        initWIOAppModel();
        loadEventsObservers();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean success = startScanning();

                Log.v("LOG", "SUCcESS ? - " + success);
            }
        }, 1000);
    }


    private void initWIOAppModel() {
        // building SDK configuration
        WIOConfig wioConfig = new WIOConfig()
                //Add if you want to support push messages from Water.io server
//                .setWIOServerPushImp(ServerPushImpl.class)
                .setDefaultProcedureDevice(DefaultCapProcedure.class)
                .setFirstProcedureDevice(FiresProcedure.class)
                /*Add this impl 'setWIODeviceEventsImp' for reading always events in background and foreground *only for business logic */
                .setWIODeviceEventsImp(CapLogsImplEvents.class)
                .build();

        mWIOApp = WIOAppFactory.getWioApp(this, wioConfig);
        getLifecycle().addObserver(mWIOApp);
    }

    private void askBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startActivityForResult(
                    new Intent(
                            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                            Uri.parse("package:" + getPackageName())
                    ), 0
            );
        }
    }


    // cancel all cap's reminders
    private void clearRemindersCap() {
        CapConfig.getInstance().reminderConfig().resetReminders();
    }


    //## Important this reminder command support only on vitamins cap, no on the hydration cap, so at hydration cap please no use it##
    // set reminders on cap (max 3)
    // must add to DefaultCapProcedure class this lines to
    // update cap at the change and ,make it work:
    //  if (CapConfig.getInstance().isRemindersChange())
    //       listCommands.add(new SetVitaminsReminderCommand());
    private void setRemindersCap() {
        CapConfig.getInstance().reminderConfig().setReminders(
                new Reminder(10, 30),
                new Reminder(20, 45)
        );
    }

    //TODO ones per change,
    // for example you can call this ones and call this again only if time changed
    //hour start day and hour finish day (from 3 am util 2 am tomorrow)
    // the time 24 hours format
    private void updateDailyUsageCap() {
        CapConfig.getInstance().updateDailyUsage(3, 2);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void askPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    },
                    PERMISSION_REQUEST);
        } else {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    },
                    PERMISSION_REQUEST);
        }
    }

    // observe all cap events
    private void loadEventsObservers() {
        mWIOApp.getWIOSmartDevice().getLogsDeviceLiveData()
                .observe(this, stringArrayListEntry -> {
                    String macAddress = stringArrayListEntry.getKey();
                    ArrayList<DeviceLog> deviceLogs = stringArrayListEntry.getValue();
/*
                    for (int i = 0; i < deviceLogs.size(); i++) {
                        DeviceLog log = deviceLogs.get(i);
                        if (log.getEventDesc().equals("Measurement"))
                            mTextViewCapStatus.append(deviceLogs.get(i).getEventDesc() + " - distance (" + deviceLogs.get(i).getMeasurement() + ") , ");
                        else
                            mTextViewCapStatus.append(deviceLogs.get(i).getEventDesc() + ", ");
                    }
                    mTextViewCapStatus.append("\n\n");*/
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_PERMISSION || requestCode == GPS_PERMISSION) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Module off, scan failed", Toast.LENGTH_SHORT).show();
            } else {
                startScanning();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST) {
            boolean killed = false;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    killed = true;
                }
            }
            if (!killed && grantResults.length != 0)
                startScanning();
            else
                Toast.makeText(this, "Permission denied, scan failed", Toast.LENGTH_SHORT).show();
        }
    }

    //start scanning for caps and receive events from it
    private boolean startScanning() {
        boolean isScanning = mWIOApp.getWIOSmartDevice().startScan(scanRequest -> {
            Log.d("WIO-SDK", scanRequest.name());
            switch (scanRequest) {
                case MODULE_OFF:
                    mWIOApp.handleOptionalToConnectDevice(
                            MainActivity.this,
                            BLUETOOTH_PERMISSION, GPS_PERMISSION
                    );
                    break;
                case DENIED_BACKGROUND_LOCATION_PERMISSION:
                case DENIED_LOCATION_PERMISSION:
                case MISSING_LOCATION_PERMISSION:
                case MISSING_BACKGROUND_LOCATION_PERMISSION:
                    askPermission();
                    break;
                case BLE_NOT_SUPPORTED:
                    Toast.makeText(this, "BLE_NOT_SUPPORTED", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case WAITING_INITIALIZATION_SDK:
                    new Handler().postDelayed(this::startScanning, 1000);
                    break;
            }
            updateScanStatusText((scanRequest == ScanRequest.SUCCESS) ? getString(R.string.scanning) : getString(R.string.error_scanning) + scanRequest.name());
        });
        return isScanning;
    }

    private void updateScanStatusText(String msg) {
        Log.e("msg", msg);
    }

    //stop scanning, the app would not response to cap events
    private boolean stopScanning() {
        boolean isStopScan = mWIOApp.getWIOSmartDevice().stopScan();
        if (isStopScan)
            updateScanStatusText(getString(R.string.stop_scanning_events_cap));
        return isStopScan;
    }

    //once you're already connected to a certain cap,
    //and you would like to connect another one,
    //you should 'forget' the current cap first.
    private void forgetCap() {
        mWIOApp.getWIOSmartDevice().forgetDevice();
        updateInfoText(getString(R.string.forget_cap_explanation));

    }

    private void updateInfoText(String msg) {
        Log.e("msg", msg);
    }

    private String getCapVersion() {
        String capVersion = mWIOApp.getWIOSmartDevice().getDeviceVersion();
        updateInfoText(getString(R.string.cap_version) + capVersion);
        return capVersion;
    }

    private String getMacAddress() {
        String macAddress = mWIOApp.getWIOSmartDevice().getMacAddress();
        updateInfoText(getString(R.string.mac_address) + macAddress);
        return macAddress;
    }

}

package com.milton.samplesdkflutter.sdk_impl;

import android.util.Log;

import com.water.water_io_sdk.ble.entities.DeviceLog;
import com.water.water_io_sdk.ble.handler_logs.categoties_client.CapEventImpl;

import java.util.ArrayList;

//This class called always, at foreground and background
// Here you call only the business logic for examples:
// insert to DB, or call http request
// * The class running on thread
// This class NO FOR GUI!, for GUI use at the observer at WIOApp => WIOSmartDevice

public class CapLogsImplEvents extends CapEventImpl /*VitaminEventImpl for vitamins cap*/ {

    public CapLogsImplEvents(String typeResponse, String data, String macAddress) {
        super(typeResponse, data, macAddress);
    }

    public CapLogsImplEvents(ArrayList<DeviceLog> logsDeviceList, String macAddress) {
        super(logsDeviceList, macAddress);
    }

    public CapLogsImplEvents(String macAddress, Boolean isNewAttachedDevice) {
        super(macAddress, isNewAttachedDevice);
    }

    @Override
    protected void onOpenCapLogEvent(DeviceLog deviceLog) {

    }

    @Override
    protected void onCloseCapLogEvent(DeviceLog deviceLog) {

    }

    @Override
    protected void onReminderCapLogEvent(DeviceLog deviceLog) {

    }

    @Override
    protected void onConnectLogEvent(DeviceLog deviceLog) {

    }

    @Override
    protected void onDisconnectLogEvent(DeviceLog deviceLog) {

    }

    @Override
    protected void onResetDeviceLogEvent(DeviceLog deviceLog) {

    }

    @Override
    protected void onNewAttachedDevice(String macAddress) {
        //This method called only ones per bounded connection (* until forget cap)
    }

    @Override
    public void handleListLogsEvent(ArrayList<DeviceLog> arrayLogEventsList, String macAddress) {
        //This method called every connection to cap in the end

        for (int i = 0; i < arrayLogEventsList.size(); i++) {
            Log.v("ImplCapEvents",arrayLogEventsList.get(i).getEventDesc());
        }
    }
}

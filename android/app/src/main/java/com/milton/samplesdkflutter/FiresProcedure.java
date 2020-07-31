package com.milton.samplesdkflutter;

import com.water.water_io_sdk.ble.connection.command.SetTimeBlinkCommand;
import com.water.water_io_sdk.ble.connection.command.SingleCommand;
import com.water.water_io_sdk.ble.connection.command.StartBlinkCommand;
import com.water.water_io_sdk.ble.connection.procedures.abstractions.ProcedureAbstractFirstConnection;

import java.util.ArrayList;

// These commands would be sent to cap only after firs connections * after forget cap and start new connection

// # Hydration cap commands support #
// - SetDailyUsageTimeCommand
// - SetTimeBlinkCommand
// - StartBlinkCommand
// - ClearDataCommand
//----------------------------------
// # Vitamins cap commands support #
// - SetDailyUsageTimeCommand
// - SetTimeBlinkCommand
// - StartBlinkCommand
// - ClearDataCommand
// - SetVitaminsReminderCommand
// - PlaySoundCommand


public class FiresProcedure extends ProcedureAbstractFirstConnection {
    @Override
    public ArrayList<SingleCommand> initCommands() {
        ArrayList<SingleCommand> singleCommands = new ArrayList<>();
        singleCommands.add(new SetTimeBlinkCommand(3));
        singleCommands.add(new StartBlinkCommand());
        return singleCommands;
    }
}

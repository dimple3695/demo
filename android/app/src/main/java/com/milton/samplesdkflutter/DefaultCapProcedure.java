package com.milton.samplesdkflutter;

import com.water.water_io_sdk.ble.connection.command.SingleCommand;
import com.water.water_io_sdk.ble.connection.procedures.abstractions.ProcedureAbstractDefaultConnection;

import java.util.ArrayList;

// These commands would be sent to cap every connections

// # Hydration cap commands support #
// - SetDailyUsageTimeCommand
// - SetTimeBlinkCommand
// - StartBlinkCommand
//----------------------------------
// # Vitamins cap commands support #
// - SetDailyUsageTimeCommand
// - SetTimeBlinkCommand
// - StartBlinkCommand
// - SetVitaminsReminderCommand
// - PlaySoundCommand


public class DefaultCapProcedure extends ProcedureAbstractDefaultConnection {

    //optional to add commands to arrays
    @Override
    public ArrayList<SingleCommand> initCommands() {
        ArrayList<SingleCommand> listCommands = new ArrayList<>();

        //## Important this reminder command support only on vitamins cap, no on the hydration cap, so at hydration cap please no use it##
        // This line add reminder command only if has changed,
        // This be true every time update new reminder when we use CapConfig.getInstance().reminderConfig().setReminders(...);

//        if (CapConfig.getInstance().isRemindersChange())
//            listCommands.add(new SetVitaminsReminderCommand());


//        Start play sound from cap if supported
//        listCommands.add(new PlaySoundCommand(true));

//         Start blinking cap
//        listCommands.add(new StartBlinkCommand());
        return listCommands;
    }

}

package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.registers.Flags;
import com.pretosmind.emu.z80.registers.RegisterName;

public class SCF extends AbstractOpCode {

    public SCF(State state) {
        super(state);
    }

    @Override
    public int execute() {

        incrementPC();

        final int a = state.getRegister(RegisterName.A).read();

        Flags.setFlag(flag, Flags.CARRY_FLAG, true);
        Flags.setFlag(flag, Flags.HALF_CARRY_FLAG, false);
        Flags.setFlag(flag, Flags.NEGATIVE_FLAG, false);
        Flags.copyFrom(flag, Flags.Y_FLAG | Flags.X_FLAG, a);

        return 4;
    }

}

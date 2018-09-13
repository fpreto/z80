package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.registers.Flags;

public class DAA extends AbstractOpCode {

    private final OpcodeReference target;

    public DAA(State state, OpcodeReference target) {
        super(state);
        this.target = target;
    }

    @Override
    public int execute() {

        incrementPC();

        final int a = target.read();
        final int result = ~a;

        Flags.copyFrom(flag, Flags.Y_FLAG | Flags.X_FLAG, result);
        Flags.setFlag(flag, Flags.HALF_CARRY_FLAG, true);
        Flags.setFlag(flag, Flags.NEGATIVE_FLAG, true);

        target.write(result);

        return 4 + target.cyclesCost() + target.cyclesCost();
    }

}

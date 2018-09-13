package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.registers.Flags;

public class RL extends AbstractOpCode {

    private final OpcodeReference target;

    public RL(State state, OpcodeReference target) {
        super(state);
        this.target = target;
    }

    @Override
    public int execute() {

        incrementPC();

        final int value = target.read();
        final int bit = Flags.getFlag(flag, Flags.CARRY_FLAG) ? 0x01 : 0x00;
        final int result = Z80Utils.mask8bit(value << 1 | bit);
        target.write(result);

        Flags.setFlag(flag, Flags.CARRY_FLAG, ((value & 0x80) == 0x80));
        Flags.setFlag(flag, Flags.HALF_CARRY_FLAG, false);
        Flags.setFlag(flag, Flags.NEGATIVE_FLAG, false);
        Flags.copyFrom(flag, Flags.Y_FLAG | Flags.X_FLAG, result);

        return 7 + target.cyclesCost();
    }

}

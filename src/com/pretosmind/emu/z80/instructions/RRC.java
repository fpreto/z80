package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.registers.Flags;

public class RRC extends AbstractOpCode {

    private final OpcodeReference target;
    private final OpcodeReference source;

    public RRC(State state, OpcodeReference target, OpcodeReference source) {
        super(state);
        this.target = target;
        this.source = source;
    }

    @Override
    public int execute() {

        incrementPC();

        final int value = source.read();
        final int bit = (value & 0x01) << 7;
        final int result = Z80Utils.mask8bit(value >>> 1 | bit);
        target.write(result);

        Flags.setFlag(flag, Flags.HALF_CARRY_FLAG, false);
        Flags.setFlag(flag, Flags.NEGATIVE_FLAG, false);
        Flags.copyFrom(flag, Flags.CARRY_FLAG | Flags.Y_FLAG | Flags.X_FLAG, result);

        return 7 + source.cyclesCost() + target.cyclesCost();
    }

}

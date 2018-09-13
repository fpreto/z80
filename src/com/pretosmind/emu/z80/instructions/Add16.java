package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.registers.Flags;

public class Add16 extends AbstractOpCode {

    private final OpcodeReference target;
    private final OpcodeReference source;

    public Add16(State state, OpcodeReference target, OpcodeReference source) {
        super(state);
        this.target = target;
        this.source = source;
    }

    @Override
    public int execute() {

        incrementPC();

        final int value1 = source.read();
        final int value2 = target.read();
        final int result = value1 + value2;
        final int carry = value1 ^ value2 ^ result;
        target.write(Z80Utils.mask16bit(result));

        Flags.setFlag(flag, Flags.CARRY_FLAG, ((carry & 0x10000) == 0x10000));
        Flags.setFlag(flag, Flags.HALF_CARRY_FLAG, ((carry & 0x1000) == 0x1000));
        Flags.copyFrom(flag, Flags.Y_FLAG | Flags.X_FLAG, (result >>> 8));
        Flags.setFlag(flag, Flags.NEGATIVE_FLAG, false);

        return 11 + source.cyclesCost() + target.cyclesCost();
    }

}

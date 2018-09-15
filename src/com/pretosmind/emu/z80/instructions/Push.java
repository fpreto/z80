package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.RegisterName;

public class Push extends AbstractOpCode {

    private final OpcodeReference target;
    private final Memory memory;

    public Push(State state, OpcodeReference target, Memory memory) {
        super(state);
        this.target = target;
        this.memory = memory;
    }

    @Override
    public int execute() {

        incrementPC();

        final int value = target.read();
        Z80Utils.push(sp, value, memory);

        return 5 + target.cyclesCost();
    }

    @Override
    public String toString() {
        return "PUSH " + target;
    }
}

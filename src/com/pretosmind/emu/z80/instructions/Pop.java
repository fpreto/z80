package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.RegisterName;

public class Pop extends AbstractOpCode {

    private final OpcodeReference target;
    private final Memory memory;

    public Pop(State state, OpcodeReference target, Memory memory) {
        super(state);
        this.target = target;
        this.memory = memory;
    }

    @Override
    public int execute() {

        incrementPC();

        final int value = Z80Utils.pop(sp, memory);
        target.write(value);

        return 5 + 3 + 3;
    }

    @Override
    public String toString() {
        return "POP " + target;
    }
}

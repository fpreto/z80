package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;

public class Ret extends AbstractOpCode {

    private final Condition condition;
    private final Memory memory;

    public Ret(State state, Condition condition, Memory memory) {
        super(state);
        this.condition = condition;
        this.memory = memory;
    }

    @Override
    public int execute() {

        incrementPC();

        if (condition.conditionMet()) {
            final int address = Z80Utils.pop(sp, memory);
            setPC(address);

            return 11;
        } else {
            return 5;
        }

    }

}

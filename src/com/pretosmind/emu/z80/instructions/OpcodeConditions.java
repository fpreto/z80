package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.RegisterName;

public class OpcodeConditions {

    private final State state;

    public OpcodeConditions(State state, Memory memory) {
        this.state = state;
    }

    /**
     * Always true condition
     */
    public Condition t() {
        return new ConditionAlwaysTrue();
    }

    public Condition f(int flag) {
        return new ConditionFlag(state.getRegister(RegisterName.F), flag, false);
    }

    public Condition nf(int flag) {
        return new ConditionFlag(state.getRegister(RegisterName.F), flag, true);
    }
}

package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;

public class EI extends AbstractOpCode {

    public EI(State state) {
        super(state);
    }

    @Override
    public int execute() {
        incrementPC();

        state.enableInterrupt();

        return 4;
    }

    @Override
    public String toString() {
        return "EI";
    }
}

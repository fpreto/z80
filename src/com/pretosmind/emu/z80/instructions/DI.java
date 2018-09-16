package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;

public class DI extends AbstractOpCode {

    public DI(State state) {
        super(state);
    }

    @Override
    public int execute() {
        incrementPC();

        state.resetInterrupt();

        return 4;
    }

    @Override
    public String toString() {
        return "DI";
    }
}

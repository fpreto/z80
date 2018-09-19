package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;

public class SET extends AbstractOpCode {

    private final OpcodeReference target;
    private final int n;

    public SET(State state, OpcodeReference target, int n) {
        super(state);
        this.target = target;
        this.n = n;
    }

    @Override
    public int execute() {

        incrementPC();

        final int value = target.read();
        final int bit = 1 << n;
        final int result = value | bit;
        target.write(result);

        return 4 + target.cyclesCost() + target.cyclesCost();
    }

    @Override
    public String toString() {
        return "SET " + n + ", " + target;
    }

}

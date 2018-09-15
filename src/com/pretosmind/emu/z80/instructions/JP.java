package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;

public class JP extends AbstractOpCode {

    private final Condition condition;
    private final OpcodeReference target;

    public JP(State state, Condition condition, OpcodeReference target) {
        super(state);
        this.target = target;
        this.condition = condition;
    }

    @Override
    public int execute() {

        incrementPC();

        final int position = target.read();

        if (condition.conditionMet()) {
            setPC(position);
        }

        return 4 + target.cyclesCost();

    }

    @Override
    public String toString() {
        return "JP " + target;
    }
}

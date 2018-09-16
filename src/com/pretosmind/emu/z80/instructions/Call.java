package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;

public class Call extends AbstractOpCode {

    private final Condition condition;
    private final OpcodeReference target;
    private final Memory memory;

    public Call(State state, Condition condition, OpcodeReference target, Memory memory) {
        super(state);
        this.target = target;
        this.condition = condition;
        this.memory = memory;
    }

    @Override
    public int execute() {

        incrementPC();

        final int position = target.read();

        if (condition.conditionMet()) {
            Z80Utils.push(sp, pc.read(), memory);
            setPC(position);
            return 4 + 3 + 4 + target.cyclesCost();
        } else {
            return 4 + target.cyclesCost();
        }


    }

    @Override
    public String toString() {
        final String conditionStr = condition.toString();
        return "CALL " + ((conditionStr.length() > 0) ? conditionStr + "," : "") + target;
    }
}

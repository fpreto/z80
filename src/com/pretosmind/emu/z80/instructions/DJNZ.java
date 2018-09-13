package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;

public class DJNZ extends AbstractOpCode {

    private final OpcodeReference target;
    private final OpcodeReference source;

    public DJNZ(State state, OpcodeReference target, OpcodeReference source) {
        super(state);
        this.target = target;
        this.source = source;
    }

    @Override
    public int execute() {

        incrementPC();

        int counter = target.read();
        counter = Z80Utils.mask8bit(counter - 1);
        target.write(counter);

        int jump = Z80Utils.twoCompliment8bit(source.read());

        if (counter != 0) {
            incrementPC(jump);
            return 5 + target.cyclesCost() + source.cyclesCost() + 5;
        }

        return 5 + target.cyclesCost() + source.cyclesCost();
    }

}

package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;

public class RST extends AbstractOpCode {

    private final Memory memory;
    private final int p;

    public RST(State state, int p, Memory memory) {
        super(state);
        this.memory = memory;
        this.p = p;
    }

    @Override
    public int execute() {

        incrementPC();

        final int position = Z80Utils.mask16bit(p);
        Z80Utils.push(sp, pc.read(), memory);
        setPC(position);

        return 5 + 3 + 3;


    }

    @Override
    public String toString() {
        return "RST " + String.format("%02X", p);
    }
}

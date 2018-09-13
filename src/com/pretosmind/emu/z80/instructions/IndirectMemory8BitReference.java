package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.mmu.Memory;

/**
 * Read 8-bit pointed by the 16-bit register address
 *
 * @author fpreto
 */
public final class IndirectMemory8BitReference implements OpcodeReference {

    private final OpcodeReference target;
    private final Memory memory;

    public IndirectMemory8BitReference(OpcodeReference target, Memory memory) {
        this.target = target;
        this.memory = memory;
    }

    @Override
    public int read() {
        final int value = memory.read(target.read());
        return value;
    }

    @Override
    public void write(int value) {
        memory.write(target.read(), value);
    }

    @Override
    public int cyclesCost() {
        return 3 + target.cyclesCost();
    }

}

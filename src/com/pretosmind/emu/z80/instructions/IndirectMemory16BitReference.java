package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;

/**
 * Read 8-bit pointed by the 16-bit register address
 *
 * @author fpreto
 */
public final class IndirectMemory16BitReference implements OpcodeReference {

    private final OpcodeReference target;
    private final Memory memory;

    public IndirectMemory16BitReference(OpcodeReference target, Memory memory) {
        this.target = target;
        this.memory = memory;
    }

    @Override
    public int read() {
        final int value = Z80Utils.read16FromMemory(target.read(), memory);
        return value;
    }

    @Override
    public void write(int value) {
        Z80Utils.write16ToMemory(target.read(), value, memory);
    }

    @Override
    public int cyclesCost() {
        return 6 + target.cyclesCost();
    }

}

package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterUtils;

/**
 * Read 16-bit from PC+1
 *
 * @author fpreto
 */
public final class Memory8BitReference implements OpcodeReference {

    private final Register pc;
    private final Memory memory;

    public Memory8BitReference(Register pc, Memory memory) {
        this.pc = pc;
        this.memory = memory;
    }

    @Override
    public int read() {
        int value = RegisterUtils.indirect(memory, pc);
        RegisterUtils.increment(pc);
        return value;
    }

    @Override
    public void write(int value) {
        memory.write(pc.read(), value);
        RegisterUtils.increment(pc);
    }

    @Override
    public int cyclesCost() {
        return 3;
    }

    @Override
    public String toString() {
        return "n";
    }
}

package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterUtils;

/**
 * Read 16-bit from PC+1
 *
 * @author fpreto
 */
public final class MemoryPlusRegister8BitReference implements OpcodeReference {

    private final Register pc;
    private final Memory memory;
    private final OpcodeReference target;
    private final boolean rewindPCBeforeWrite;

    public MemoryPlusRegister8BitReference(Register pc, OpcodeReference target, Memory memory, boolean rewindPCBeforeWrite) {
        this.pc = pc;
        this.target = target;
        this.memory = memory;
        this.rewindPCBeforeWrite = rewindPCBeforeWrite;
    }

    @Override
    public int read() {
        final int address = readAddress();
        final int value = memory.read(address);

        return value;
    }

    @Override
    public void write(int value) {
        if (rewindPCBeforeWrite) {
            RegisterUtils.decrement(pc, 1);
        }
        final int address = readAddress();
        memory.write(address, value);
    }

    private int readAddress() {
        final int dd = memory.read(pc.read());
        RegisterUtils.increment(pc, 1);
        final int address = Z80Utils.mask16bit(target.read() + dd);
        return address;
    }

    @Override
    public int cyclesCost() {
        return 3 + 5 + 4 + 3;
    }

    @Override
    public String toString() {
        return "nnnn";
    }
}

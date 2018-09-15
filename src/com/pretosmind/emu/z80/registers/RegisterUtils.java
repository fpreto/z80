package com.pretosmind.emu.z80.registers;

import com.pretosmind.emu.z80.mmu.Memory;

public final class RegisterUtils {

    public static final void increment(Register r) {

        int value = r.read();
        value++;
        r.write(value);

    }

    public static final void increment(Register r, int by) {
        int value = r.read();
        value += by;
        r.write(value);
    }

    public static final void decrement(Register r, int by) {
        int value = r.read();
        value -= by;
        r.write(value);
    }

    public static final int indirect(Memory memory, Register r) {
        return memory.read(r.read());
    }

}

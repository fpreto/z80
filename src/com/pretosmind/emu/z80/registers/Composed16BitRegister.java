package com.pretosmind.emu.z80.registers;

import com.pretosmind.emu.z80.Z80Utils;

public class Composed16BitRegister implements RegisterPair {

    private final Register high;
    private final Register low;

    public Composed16BitRegister(String h, String l) {
        this.high = new Plain8BitRegister(h);
        this.low = new Plain8BitRegister(l);
    }

    @Override
    public int read() {
        return Z80Utils.compose16bit(this.high.read(), this.low.read());
    }

    @Override
    public void write(int value) {
        this.high.write(Z80Utils.high8bits(value));
        this.low.write(Z80Utils.mask8bit(value));
    }

    @Override
    public Register getHigh() {
        return this.high;
    }

    @Override
    public Register getLow() {
        return this.low;
    }

    @Override
    public int cyclesCost() {
        return 0;
    }

}

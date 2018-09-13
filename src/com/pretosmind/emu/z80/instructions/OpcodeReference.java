package com.pretosmind.emu.z80.instructions;

public interface OpcodeReference {

    int read();

    void write(int value);

    int cyclesCost();

}

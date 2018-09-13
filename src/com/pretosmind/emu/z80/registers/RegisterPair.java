package com.pretosmind.emu.z80.registers;

public interface RegisterPair extends Register {

    Register getHigh();

    Register getLow();

}

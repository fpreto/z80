package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterBank;
import com.pretosmind.emu.z80.registers.RegisterName;

public class State {

    private final RegisterBank registers;
    private boolean halted;

    public State() {
        this.registers = new RegisterBank();
    }

    public Register getRegister(RegisterName name) {
        return this.registers.get(name);
    }

    public Register getRegisterAlternate(RegisterName name) {
        return this.registers.getAlternate(name);
    }

    public void setHalt(boolean halted) {
        this.halted = halted;
    }

    public boolean isHalted() {
        return this.halted;
    }

    @Override
    public String toString() {
        return "registers="+ registers +
                ", halted=" + halted;
    }
}

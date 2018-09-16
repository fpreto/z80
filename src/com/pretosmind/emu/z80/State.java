package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterBank;
import com.pretosmind.emu.z80.registers.RegisterName;

public class State {

    private final RegisterBank registers;
    private boolean halted;
    private boolean iff1;
    private boolean iff2;

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

    public void enableInterrupt() {
        iff1 = true;
        iff2 = true;
    }

    public void resetInterrupt() {
        iff1 = false;
        iff2 = false;
    }

    @Override
    public String toString() {
        return "registers="+ registers +
                ", halted=" + halted +
                ", iff1=" + iff1 +
                ", iff2=" + iff2;
    }
}

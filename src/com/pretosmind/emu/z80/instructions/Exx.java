package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterName;

import static com.pretosmind.emu.z80.registers.RegisterName.BC;
import static com.pretosmind.emu.z80.registers.RegisterName.DE;
import static com.pretosmind.emu.z80.registers.RegisterName.HL;

public class Exx extends AbstractOpCode {

    public Exx(State state) {
        super(state);
    }

    @Override
    public int execute() {

        incrementPC();

        flip(BC);
        flip(DE);
        flip(HL);

        return 4;
    }

    private void flip(RegisterName name) {

        final Register register = getRegister(name);
        final Register alternate = getRegisterAlternate(name);

        final int v1 = register.read();
        final int v2 = alternate.read();
        register.write(v2);
        alternate.write(v1);

    }

    @Override
    public String toString() {
        return "EXX";
    }
}

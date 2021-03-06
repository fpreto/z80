package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.RegisterName;

public final class OpcodeTargets {

    private final State state;
    private final Memory memory;

    public OpcodeTargets(State state, Memory memory) {
        this.state = state;
        this.memory = memory;
    }

    public OpcodeReference r(RegisterName name) {
        return state.getRegister(name);
    }

    public OpcodeReference _r(RegisterName name) {
        return state.getRegisterAlternate(name);
    }

    public OpcodeReference iRR(RegisterName name) {
        return new IndirectMemory8BitReference(state.getRegister(name), memory);
    }

    public OpcodeReference iRRn(RegisterName name, boolean rewindOnWrite) {
        return new MemoryPlusRegister8BitReference(state.getRegister(RegisterName.PC), state.getRegister(name), memory, rewindOnWrite);
    }

    public OpcodeReference iiRR(RegisterName name) {
        return new IndirectMemory16BitReference(state.getRegister(name), memory);
    }

    public OpcodeReference n() {
        return new Memory8BitReference(state.getRegister(RegisterName.PC), memory);
    }

    public OpcodeReference nn() {
        return new Memory16BitReference(state.getRegister(RegisterName.PC), memory);
    }

    public OpcodeReference iinn() {
        return new IndirectMemory16BitReference(nn(), memory);
    }

    public OpcodeReference inn() {
        return new IndirectMemory8BitReference(nn(), memory);
    }

}

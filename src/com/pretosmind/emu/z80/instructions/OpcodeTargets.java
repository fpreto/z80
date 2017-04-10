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
	
	public OpCodeDataTarget r(RegisterName name) {
		return state.getRegister(name);
	}
	
	public OpCodeDataTarget iRR(RegisterName name) {
		return new IndirectMemory8BitTarget(state.getRegister(RegisterName.BC), memory);
	}
	
	
}

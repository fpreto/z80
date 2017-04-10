package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.RegisterName;

public class OpcodeSources {

	private final State state;
	private final Memory memory;

	public OpcodeSources(State state, Memory memory) {
		this.state = state;
		this.memory = memory;
	}
	
	public OpCodeDataSource r(RegisterName name) {
		return state.getRegister(name);
	}
	
	public OpCodeDataSource nn() {
		return new Memory16BitSource(state.getRegister(RegisterName.PC), memory);
	}
	
	public OpCodeDataSource n() {
		return new Memory8BitSource(state.getRegister(RegisterName.PC), memory);
	}

}

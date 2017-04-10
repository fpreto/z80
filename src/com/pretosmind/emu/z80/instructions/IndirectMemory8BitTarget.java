package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.Register;

public final class IndirectMemory8BitTarget implements OpCodeDataTarget {
	
	private final Register register;
	private final Memory memory;
	
	public IndirectMemory8BitTarget(Register register, Memory memory) {
		this.register = register;
		this.memory = memory;
	}

	@Override
	public void write(int value) {
		memory.write(register.read(), value);
	}

	@Override
	public int cyclesCost() {
		return 3;
	}

}

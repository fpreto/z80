package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.Register;

/**
 * Read 8-bit pointed by the 16-bit register address 
 * 
 * @author fpreto
 *
 */
public final class IndirectMemory8BitSource implements OpCodeDataSource {
	
	private final Register register;
	private final Memory memory;
	
	public IndirectMemory8BitSource(Register register, Memory memory) {
		this.register = register;
		this.memory = memory;
	}

	@Override
	public int read() {
		final int value = memory.read(register.read());
		return value;
	}

	@Override
	public int cyclesCost() {
		return 3;
	}

}

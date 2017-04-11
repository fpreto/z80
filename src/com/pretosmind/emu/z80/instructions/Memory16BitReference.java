package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterUtils;

/**
 * Read 16-bit from PC+1 
 * 
 * @author fpreto
 *
 */
public final class Memory16BitReference implements OpcodeReference {
	
	private final Register pc;
	private final Memory memory;
	
	public Memory16BitReference(Register pc, Memory memory) {
		this.pc = pc;
		this.memory = memory;
	}

	@Override
	public int read() {
		int value = Z80Utils.read16FromMemory(pc.read(), memory);
		RegisterUtils.increment(pc, 2);
		return value;
	}

	@Override
	public void write(int value) {
		Z80Utils.write16ToMemory(pc.read(), value, memory);
		RegisterUtils.increment(pc, 2);
	}

	@Override
	public int cyclesCost() {
		return 3 + 3;
	}

}

package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;

public class Inc16 extends AbstractOpCode {
	
	private final OpCodeDataTarget target;
	private final OpCodeDataSource source;

	public Inc16(State state, OpCodeDataTarget target, OpCodeDataSource source) {
		super(state);
		this.target = target;
		this.source = source;
	}

	@Override
	public int execute() {
		
		incrementPC();
		
		int value = source.read();
		value = Z80Utils.mask16bit(value + 1);
		target.write(value);
		
		return 6 + source.cyclesCost() + target.cyclesCost();
	}

}

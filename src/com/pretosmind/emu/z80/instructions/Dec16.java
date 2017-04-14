package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;

public class Dec16 extends AbstractOpCode {
	
	private final OpcodeReference target;

	public Dec16(State state, OpcodeReference target) {
		super(state);
		this.target = target;
	}

	@Override
	public int execute() {
		
		incrementPC();
		
		final int value = target.read();
		final int result = Z80Utils.mask16bit(value - 1);
		target.write(result);
		
		return 6 + target.cyclesCost() + target.cyclesCost();
	}

}

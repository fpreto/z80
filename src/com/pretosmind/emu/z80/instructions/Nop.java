package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;

public class Nop extends AbstractOpCode {

	public Nop(State state) {
		super(state);
	}

	@Override
	public int execute() {
		incrementPC();
		
		return 4;
	}

}

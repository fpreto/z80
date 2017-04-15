package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;

public class Halt extends AbstractOpCode {

	public Halt(State state) {
		super(state);
	}

	@Override
	public int execute() {
		
		if (!state.isHalted()) {
			incrementPC();
			
			state.setHalt(true);
		}
		
		return 4;
	}

}

package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;

public class Ex extends AbstractOpCode {
	
	private final OpcodeReference register;
	private final OpcodeReference alternate;

	public Ex(State state, OpcodeReference register, OpcodeReference alternate) {
		super(state);
		this.register = register;
		this.alternate = alternate;
	}

	@Override
	public int execute() {
		
		incrementPC();
		
		final int v1 = register.read();
		final int v2 = alternate.read();
		
		register.write(v2);
		alternate.write(v1);
		
		return 4 + register.cyclesCost() + alternate.cyclesCost();
	}

}

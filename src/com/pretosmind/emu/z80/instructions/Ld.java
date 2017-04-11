package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;

public class Ld extends AbstractOpCode {
	
	private final OpcodeReference target;
	private final OpcodeReference source;

	public Ld(State state, OpcodeReference target, OpcodeReference source) {
		super(state);
		this.target = target;
		this.source = source;
	}

	@Override
	public int execute() {
		
		incrementPC();
		
		int value = source.read();
		target.write(value);
		
		return 4 + target.cyclesCost() + source.cyclesCost();
	}

}

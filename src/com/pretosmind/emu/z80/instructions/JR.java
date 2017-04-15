package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;

public class JR extends AbstractOpCode {
	
	private final Condition condition;
	private final OpcodeReference target;

	public JR(State state, Condition condition, OpcodeReference target) {
		super(state);
		this.target = target;
		this.condition = condition;
	}

	@Override
	public int execute() {
		
		incrementPC();
		
		final int relative = Z80Utils.twoCompliment8bit(target.read());
		
		if (condition.conditionMet()) {
			incrementPC(relative);
			return 4 + 5 + target.cyclesCost();
		} else {
			return 4 + target.cyclesCost();
		}
		
	}

}

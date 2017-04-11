package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.Z80Utils;
import com.pretosmind.emu.z80.registers.Flags;

public class Dec extends AbstractOpCode {
	
	private final OpcodeReference target;
	private final OpcodeReference source;

	public Dec(State state, OpcodeReference target, OpcodeReference source) {
		super(state);
		this.target = target;
		this.source = source;
	}

	@Override
	public int execute() {
		
		incrementPC();
		
		final int value = source.read();
		final int result = Z80Utils.mask8bit(value - 1);
		target.write(result);
		
		Flags.setFlag(flag, Flags.ZERO_FLAG, (result == 0));
		Flags.setFlag(flag, Flags.HALF_CARRY_FLAG, ((result & 0x0F) == 0x0F));
		Flags.copyFrom(flag, Flags.SIGNIFICANT_FLAG | Flags.Y_FLAG | Flags.X_FLAG, result);
		Flags.setFlag(flag, Flags.PARITY_FLAG, (result == 0x7F));
		Flags.setFlag(flag, Flags.NEGATIVE_FLAG, true);
		
		return 4 + source.cyclesCost() + target.cyclesCost();
	}

}

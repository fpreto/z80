package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.State;
import com.pretosmind.emu.z80.registers.Register;
import static com.pretosmind.emu.z80.registers.RegisterName.*;
import com.pretosmind.emu.z80.registers.RegisterUtils;

public abstract class AbstractOpCode implements OpCode {
	
	protected final State state;
	protected final Register pc;
	protected final Register flag;

	protected AbstractOpCode(State state) {
		this.state = state;
		this.pc = this.state.getRegister(PC);
		this.flag = this.state.getRegister(F);
	}
	
	/**
	 * Increment PC by 1
	 */
	protected void incrementPC() {
		incrementPC(1);
	}
	
	/**
	 * Increment the PC register
	 * @param by ammount of bytes to increment PC
	 */
	protected void incrementPC(int by) {
		RegisterUtils.increment(pc, by);
	}

}

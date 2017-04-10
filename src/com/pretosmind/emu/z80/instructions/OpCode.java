package com.pretosmind.emu.z80.instructions;

public interface OpCode {

	/**
	 * Execute OpCode from current PC position and return the number of cycles 
	 * @return
	 */
	int execute();
	
}

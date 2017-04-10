package com.pretosmind.emu.z80.registers;

import com.pretosmind.emu.z80.instructions.OpCodeDataSource;
import com.pretosmind.emu.z80.instructions.OpCodeDataTarget;

/**
 * 8-bit or 16-bit register that can be redden and written.
 * @author fpreto
 *
 */
public interface Register extends OpCodeDataSource, OpCodeDataTarget {
	
	/**
	 * Read the data from register
	 */
	int read();
	
	/**
	 * Write data to register
	 */
	void write(int value);

}

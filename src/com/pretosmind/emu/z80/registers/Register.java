package com.pretosmind.emu.z80.registers;

import com.pretosmind.emu.z80.instructions.OpcodeReference;

/**
 * 8-bit or 16-bit register that can be redden and written.
 * @author fpreto
 *
 */
public interface Register extends OpcodeReference {
	
	/**
	 * Read the data from register
	 */
	int read();
	
	/**
	 * Write data to register
	 */
	void write(int value);

}

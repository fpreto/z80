package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.mmu.Memory;

public final class Z80Utils {

	/**
	 * Return only the 8 least significant bits of an integer
	 * 
	 * @param value input data
	 * @return masked data
	 */
	public final static int mask8bit(int value) {
		return value & 0xFF;
	}
	
	/**
	 * Return only the 16 least significant bits of an integer
	 * 
	 * @param value input data
	 * @return masked data
	 */
	public final static int mask16bit(int value) {
		return value & 0xFFFF;
	}
	
	/**
	 * Return the higher 8-bits from a 16-bit data
	 * @param value 16-bit data
	 * @return 8-bit data
	 */
	public final static int high8bits(int value) {
		return (value >> 8);
	}
	
	/**
	 * Join two 8-bit data into a 16-bit data
	 * @param high 8-bit data
	 * @param low 8-bite data
	 * @return 16-bit data
	 */
	public final static int compose16bit(int high, int low) {
		return (high << 8) | low;
	}
	
	/**
	 * Read 16-bit from memory. The memory display is LH (little indian)
	 * 
	 * @param address 16-bit address
	 * @param memory 8-bit memory adapter
	 * @return
	 */
	public final static int read16FromMemory(int address, Memory memory) {
		return memory.read(address) | (memory.read(address + 1) << 8);
	}
	/**
	 * Write 16 bytes of data to memory
	 * @param address pointer
	 * @param value 16-bit value
	 * @param memory memory adapter
	 */
	public final static void write16ToMemory(int address, int value, Memory memory) {
		memory.write(address, Z80Utils.mask8bit(value));
		memory.write(address + 1, Z80Utils.high8bits(value));
	}
	
	
	
}

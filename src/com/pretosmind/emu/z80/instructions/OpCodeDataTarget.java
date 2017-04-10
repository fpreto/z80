package com.pretosmind.emu.z80.instructions;

public interface OpCodeDataTarget {

	void write(int value);
	
	int cyclesCost();
	
}

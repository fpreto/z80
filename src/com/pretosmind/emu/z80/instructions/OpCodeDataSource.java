package com.pretosmind.emu.z80.instructions;

public interface OpCodeDataSource {

	int read();
	
	int cyclesCost();
	
}

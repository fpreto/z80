package com.pretosmind.emu.z80.mmu;

public interface Memory {

	int read(int address);
	void write(int address, int value);
	
}

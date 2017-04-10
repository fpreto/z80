package com.pretosmind.emu.z80.registers;

import com.pretosmind.emu.z80.Z80Utils;

public final class Plain8BitRegister implements Register {

	private int data;
	
	@Override
	public int read() {
		return data;
	}

	@Override
	public void write(int value) {
		this.data = Z80Utils.mask8bit(value);
	}
	
	@Override
	public int cyclesCost() {
		return 0;
	}

}

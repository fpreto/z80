package com.pretosmind.emu.z80.registers;

public final class RegisterUtils {

	public static final void increment(Register r) {
		
		int value = r.read();
		value++;
		r.write(value);
		
	}
	


	public static final void increment(Register r, int by) {
		
		int value = r.read();
		value+=by;
		r.write(value);
		
	}
	
}

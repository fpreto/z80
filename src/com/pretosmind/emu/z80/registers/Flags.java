package com.pretosmind.emu.z80.registers;

public class Flags {
	
	public final static int CARRY_FLAG = 0x01;
	public final static int NEGATIVE_FLAG = 0x02;
	public final static int PARITY_FLAG = 0x04;
	public final static int X_FLAG = 0x08;
	public final static int HALF_CARRY_FLAG = 0x10;
	public final static int Y_FLAG = 0x20;
	public final static int ZERO_FLAG = 0x40;
	public final static int SIGNIFICANT_FLAG = 0x80;

	public final static void setFlag(Register r, int flag, boolean set) {
		final int currentFlag = r.read();
		
		if (set) {
			r.write(currentFlag | flag);
		} else {
			r.write(currentFlag & ~(flag));
		}
	}

	public final static void copyFrom(Register r, int flag, int value) {
		final int currentFlag = r.read() & ~(flag);
		r.write(currentFlag | (value & flag));
	}
	
}

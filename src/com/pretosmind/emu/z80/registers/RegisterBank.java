package com.pretosmind.emu.z80.registers;

public class RegisterBank {
	
	private final RegisterPair af;
	private final RegisterPair bc;
	private final RegisterPair de;
	private final RegisterPair hl;
	private final Register pc;
	private final Register sp;
	private final Register ix;
	private final Register iy;
	
	public RegisterBank() {
		this.af = new Composed16BitRegister();
		this.bc = new Composed16BitRegister();
		this.de = new Composed16BitRegister();
		this.hl = new Composed16BitRegister();
		this.pc = new Plain16BitRegister();
		this.sp = new Plain16BitRegister();
		this.ix = new Plain16BitRegister();
		this.iy = new Plain16BitRegister();
	}
	
	public Register get(RegisterName name) {
		switch (name) {
		case A:
			return this.af.getHigh();
		case F:
			return this.af.getLow();
		case B:
			return this.bc.getHigh();
		case C:
			return this.bc.getLow();
		case D:
			return this.de.getHigh();
		case E:
			return this.de.getLow();
		case H:
			return this.hl.getHigh();
		case L:
			return this.hl.getLow();
		case AF:
			return this.af;
		case BC:
			return this.bc;
		case DE:
			return this.de;
		case HL:
			return this.hl;
		case PC:
			return this.pc;
		case SP:
			return this.sp;
		case IX:
			return this.ix;
		case IY:
			return this.iy;
		default:
			return null;
		}
	}
	
	
}

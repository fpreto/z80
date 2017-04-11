package com.pretosmind.emu.z80.registers;

public class RegisterBank {
	
	private final RegisterPair af;
	private final RegisterPair bc;
	private final RegisterPair de;
	private final RegisterPair hl;
	private final RegisterPair _af;
	private final RegisterPair _bc;
	private final RegisterPair _de;
	private final RegisterPair _hl;
	private final Register pc;
	private final Register sp;
	private final Register ix;
	private final Register iy;
	
	public RegisterBank() {
		this.af = new Composed16BitRegister();
		this.bc = new Composed16BitRegister();
		this.de = new Composed16BitRegister();
		this.hl = new Composed16BitRegister();
		this._af = new Composed16BitRegister();
		this._bc = new Composed16BitRegister();
		this._de = new Composed16BitRegister();
		this._hl = new Composed16BitRegister();
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
	
	public Register getAlternate(RegisterName name) {
		switch (name) {
		case AF:
			return this._af;
		case BC:
			return this._bc;
		case DE:
			return this._de;
		case HL:
			return this._hl;
		default:
			return null;
		}
	}
	
	
}

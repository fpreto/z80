package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterBank;
import com.pretosmind.emu.z80.registers.RegisterName;

public class State {

	private final RegisterBank registers;
	
	public State() {
		this.registers = new RegisterBank();
	}
	
	public Register getRegister(RegisterName name) {
		return this.registers.get(name);
	}
	
	public Register getRegisterAlternate(RegisterName name) {
		return this.registers.getAlternate(name);
	}
	
}

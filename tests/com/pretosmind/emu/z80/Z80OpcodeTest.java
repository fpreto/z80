package com.pretosmind.emu.z80;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.pretosmind.emu.z80.registers.Flags.*;
import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterName;

import static com.pretosmind.emu.z80.registers.RegisterName.*;

public class Z80OpcodeTest {

	private State state;
	private MemoryTestHelper memory;
	private Z80 z80;
	
	private int previousFlag;
	
	@Before
	public void setUp() throws Exception {
		this.memory = new MemoryTestHelper();
		this.state = new State();
		this.z80 = new Z80(memory, state);
		
		previousFlag = state.getRegister(F).read();
	}

	@After
	public void tearDown() throws Exception {
		this.z80 = null;
		this.state = null;
		this.memory = null;
	}
	
	@Test
	public void nopTest() {
		
		memory.put(0x00); /* NOP */
		memory.put(0x00); /* NOP */
		memory.put(0x00); /* NOP */
		
		z80.execute(4);
		assertPC(0x01);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(2);
		assertPC(0x02);
		assertFlagUnchanged();
		
		z80.execute(2);
		assertPC(0x02);
		assertFlagUnchanged();
		assertZeroCycleBalance();

		z80.execute(1);
		assertPC(0x03);
		assertFlagUnchanged();
	}
	
	@Test
	public void ldBCTest() {
		memory.put(0x01, 0x34, 0x12); /* LD BC, 0x1234 */
		
		z80.execute(10);
		assertRegister(BC, 0x1234);
		assertPC(0x03);
		assertFlagUnchanged();
		assertZeroCycleBalance();
	}
	
	@Test
	public void ld_iBC_A() {
		memory.put(0x01, 0x34, 0x12); /* LD BC, 0x1234 */
		memory.put(0x3E, 0x7F); /* LD A, 7F */
		memory.put(0x02); /* LD (BC), A */
		
		z80.execute(17);
		assertRegister(BC, 0x1234);
		assertRegister(A, 0x7F);
		assertPC(0x05);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(7);
		assertRegister(BC, 0x1234);
		assertRegister(A, 0x7F);
		assertPC(0x06);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		assertEquals(0x7F, memory.read(0x1234));
	}
	
	@Test
	public void inc_hl() {
		memory.put(0x21, 0x34, 0x12); /* LD HL, 0x1234 */
		memory.put(0x03); /* INC HL */
		memory.put(0x21, 0xFF, 0xFF); /* LD HL, 0xFFFF */
		memory.put(0x03); /* INC HL */
		
		z80.execute(10);
		assertRegister(HL, 0x1234);
		assertPC(0x03);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(6);
		assertRegister(HL, 0x1235);
		assertPC(0x04);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(10);
		assertRegister(HL, 0xFFFF);
		assertPC(0x07);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(6);
		assertRegister(HL, 0x0000);
		assertPC(0x08);
		assertFlagUnchanged();
		assertZeroCycleBalance();
	}
	
	@Test
	public void inc_b() {
		memory.put(0x06, 0x00); /* LD B, 00h */
		memory.put(0x04); /* INC B */
		memory.put(0x06, 0x0F); /* LD B, 0Fh */
		memory.put(0x04); /* INC B */
		memory.put(0x06, 0x7F); /* LD B, 7fh */
		memory.put(0x04); /* INC B */
		memory.put(0x06, 0xFF); /* LD B, ffh */
		memory.put(0x04); /* INC B */
		memory.put(0x06, 0xFE); /* LD B, feh */
		memory.put(0x04); /* INC B */
		
		z80.execute(7);
		assertRegister(B, 0x00);
		assertPC(0x02);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(4);
		assertRegister(B, 0x01);
		assertPC(0x03);
		assertFlag(0, HALF_CARRY_FLAG | NEGATIVE_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG | ZERO_FLAG, CARRY_FLAG, 0x01 );
		assertZeroCycleBalance();
		
		// Test: 0fh + 01h = 10h
		z80.execute(11);
		assertRegister(B, 0x10);
		assertPC(0x06);
		assertFlag(HALF_CARRY_FLAG, NEGATIVE_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG | ZERO_FLAG, CARRY_FLAG, 0x10 );
		assertZeroCycleBalance();
		
		// Test: 7fh + 01h = 80h
		z80.execute(11);
		assertRegister(B, 0x80);
		assertPC(0x09);
		assertFlag(HALF_CARRY_FLAG | SIGNIFICANT_FLAG | PARITY_FLAG, NEGATIVE_FLAG | ZERO_FLAG, CARRY_FLAG, 0x80 );
		assertZeroCycleBalance();
		
		// Test: ffh + 01h = 00h
		z80.execute(11);
		assertRegister(B, 0x00);
		assertPC(0x0C);
		assertFlag(HALF_CARRY_FLAG | ZERO_FLAG, PARITY_FLAG |SIGNIFICANT_FLAG | NEGATIVE_FLAG, CARRY_FLAG, 0x00 );
		assertZeroCycleBalance();
		
		// Test: feh + 01h = ffh
		z80.execute(11);
		assertRegister(B, 0xff);
		assertPC(0x0F);
		assertFlag(SIGNIFICANT_FLAG, HALF_CARRY_FLAG | PARITY_FLAG | NEGATIVE_FLAG| ZERO_FLAG, CARRY_FLAG, 0xFF );
		assertZeroCycleBalance();
		
	}
	
	@Test
	public void dec_b() {
		memory.put(0x06, 0x01); /* LD B, 01h */
		memory.put(0x05); /* DEC B */
		memory.put(0x06, 0x00); /* LD B, 00h */
		memory.put(0x05); /* DEC B */
		memory.put(0x06, 0x10); /* LD B, 10h */
		memory.put(0x05); /* DEC B */
		
		// 01h - 01h = 00h
		z80.execute(7);
		assertRegister(B, 0x01);
		assertPC(0x02);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(4);
		assertRegister(B, 0x00);
		assertPC(0x03);
		assertFlag(NEGATIVE_FLAG | ZERO_FLAG, HALF_CARRY_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG, CARRY_FLAG, 0x00 );
		assertZeroCycleBalance();
		
		// Test: 00h - 01h = FFh
		z80.execute(11);
		assertRegister(B, 0xFF);
		assertPC(0x06);
		assertFlag(SIGNIFICANT_FLAG | NEGATIVE_FLAG | HALF_CARRY_FLAG, PARITY_FLAG | ZERO_FLAG, CARRY_FLAG, 0xFF );
		assertZeroCycleBalance();
		
		// Test: 10h - 01h = 0Fh
		z80.execute(11);
		assertRegister(B, 0x0F);
		assertPC(0x09);
		assertFlag(NEGATIVE_FLAG | HALF_CARRY_FLAG, ZERO_FLAG | SIGNIFICANT_FLAG | PARITY_FLAG, CARRY_FLAG, 0x0F );
		assertZeroCycleBalance();
	}
	
	@Test
	public void ld_b_n() {
		memory.put(0x06, 0x01); /* LD B, 01h */
		memory.put(0x06, 0xFF); /* LD B, ffh */
		memory.put(0x06, 0x00); /* LD B, 00h */
		
		z80.execute(7);
		assertRegister(B, 0x01);
		assertPC(0x02);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(7);
		assertRegister(B, 0xFF);
		assertPC(0x04);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(7);
		assertRegister(B, 0x00);
		assertPC(0x06);
		assertFlagUnchanged();
		assertZeroCycleBalance();
	
	}
	
	@Test
	public void rlca() {
		memory.put(0x3E, 0x01); /* LD A, 01h */
		memory.put(0x07); /* RLCA */
		memory.put(0x3E, 0x80); /* LD A, 80h */
		memory.put(0x07); /* RLCA */
		
		// 01h  -> 02h
		z80.execute(7);
		assertRegister(A, 0x01);
		assertPC(0x02);
		assertFlagUnchanged();
		assertZeroCycleBalance();
		
		z80.execute(7);
		assertRegister(A, 0x02);
		assertPC(0x03);
		assertFlag(0, CARRY_FLAG | NEGATIVE_FLAG | HALF_CARRY_FLAG,  NEGATIVE_FLAG | ZERO_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG , 0x02 );
		assertZeroCycleBalance();
		
		// 80h -> 01h
		z80.execute(14);
		assertRegister(A, 0x01);
		assertPC(0x06);
		assertFlag(CARRY_FLAG, NEGATIVE_FLAG | HALF_CARRY_FLAG,  NEGATIVE_FLAG | ZERO_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG , 0x01 );
		assertZeroCycleBalance();
		
		
	}
	
	@Test
	public void ex_af_af() {
		memory.put(0x06, 0x00); /* LD B, 00h */
		memory.put(0x05); /* DEC B */
		memory.put(0x3E, 0x80); /* LD A, 80h */
		memory.put(0x08); /* EX AF, AF' */
		memory.put(0x3E, 0xAB); /* LD A, ABh */
		memory.put(0x08); /* EX AF, AF' */
		memory.put(0x08); /* EX AF, AF' */
		
		z80.execute(18);
		assertRegister(B, 0xFF);
		assertRegister(F, 0xBA);
		assertRegister(A, 0x80);
		assertPC(0x05);
		assertZeroCycleBalance();

		z80.execute(4);
		assertPC(0x06);
		assertZeroCycleBalance();
		
		z80.execute(7);
		assertRegister(A, 0xAB);
		assertRegister(F, 0x00);
		assertRegister(B, 0xFF);
		assertPC(0x08);
		assertZeroCycleBalance();
		
		z80.execute(4);
		assertRegister(A, 0x80);
		assertRegister(F, 0xBA);
		assertRegister(B, 0xFF);
		assertPC(0x09);
		assertZeroCycleBalance();
		
		z80.execute(4);
		assertRegister(A, 0xAB);
		assertRegister(F, 0x00);
		assertRegister(B, 0xFF);
		assertPC(0x0A);
		assertZeroCycleBalance();
		
	}
	
	private void assertPC(int value) {
		assertRegister(PC, value);
	}
	
	private void assertZeroCycleBalance() {
		assertEquals(0, z80.getCyclesBalance());
	}
	
	private void assertRegister(RegisterName name, int value) {
		Register register = state.getRegister(name);
		assertEquals(value, register.read());
	}
	
	private void assertFlagUnchanged() {
		int currentFlag = state.getRegister(F).read();
		assertEquals(previousFlag, currentFlag);
		previousFlag = currentFlag;
	}
	
	private void assertFlag(int flagsSet, int flagsUnset, int flagsUntouched, int xy) {
		int currentFlag = state.getRegister(F).read();
		
		assertTrue("Invalid flags. Missing required set flags. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & flagsSet) == flagsSet));
		assertTrue("Invalid flags. Flags set while required to not be. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & flagsUnset) == 0));
		assertTrue("Invalid flags. Flags change while required to be preserved. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & flagsUntouched) == (previousFlag & flagsUntouched)));
		assertTrue("Invalid flags. XY not properly set. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & (X_FLAG | Y_FLAG)) == (xy & (X_FLAG | Y_FLAG))));
		
		
		previousFlag = currentFlag;
	}

}

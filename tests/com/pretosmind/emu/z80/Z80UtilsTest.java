package com.pretosmind.emu.z80;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Z80UtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTwoCompliment8bit() {
		assertEquals(-1, Z80Utils.twoCompliment8bit(0xFF));
		assertEquals(-2, Z80Utils.twoCompliment8bit(0xFE));
		assertEquals(-128, Z80Utils.twoCompliment8bit(0x80));
		assertEquals(-127, Z80Utils.twoCompliment8bit(0x81));
		assertEquals(0, Z80Utils.twoCompliment8bit(0x00));
		assertEquals(127, Z80Utils.twoCompliment8bit(0x7F));
	}

}

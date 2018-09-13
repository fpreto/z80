package com.pretosmind.emu.z80;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testEvenParity8bits() {
        assertEquals(true, Z80Utils.isEvenParity8bit(0b00000000));
        assertEquals(false, Z80Utils.isEvenParity8bit(0b00000001));
        assertEquals(true, Z80Utils.isEvenParity8bit(0b10000001));
        assertEquals(true, Z80Utils.isEvenParity8bit(0b11111111));
        assertEquals(false, Z80Utils.isEvenParity8bit(0b10111111));
        assertEquals(false, Z80Utils.isEvenParity8bit(0b10101011));
        assertEquals(true, Z80Utils.isEvenParity8bit(0b10101001));
        assertEquals(true, Z80Utils.isEvenParity8bit(0b10101010));
        assertEquals(true, Z80Utils.isEvenParity8bit(0b11001100));
        assertEquals(true, Z80Utils.isEvenParity8bit(0b00110011));
    }

}

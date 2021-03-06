package com.pretosmind.emu.z80;

import static org.junit.Assert.assertEquals;


public class Z80OpcodeTest extends AbstractZ80Test {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        io.printPort(0);
    }

    @org.junit.Test
    public void nopTest() {
        assemble("nop.z80");
        runUntilHalted();
        assertPC(0x04);
        assertFlagUnchanged();
    }

    @org.junit.Test
    public void ldTest() {
        assemble("ld.z80");
        runUntilHalted();
        assertPC(0x0F);
        assertAllRegisters(0x01, 0x23, 0x45, 0x67, 0x89, 0xAB, 0xCD);
        assertFlagUnchanged();
    }

    @org.junit.Test
    public void ldRRTest() {
        assemble("ldRR.z80");
        runUntilHalted();
        assertAllRegisters(0x01, 0x23, 0x45, 0x67, 0x89, 0xAB, 0xCD);
        assertFlagUnchanged();
    }



    @org.junit.Test
    public void zexallTest() {
        assemble("zexall.z80");
        runUntilHalted();
    }

//    @Test
//    public void ldBCTest() {
//
//        assemble(
//                " ld bc, 1234h"
//        );
//
//        z80.execute(10);
//        assertRegister(BC, 0x1234);
//        assertPC(0x03);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//    }

//    @Test
//    public void ld_iBC_A() {
//
//        assemble(
//                " ld bc, 1234h",
//                " ld a, 7fh",
//                " ld (bc), a"
//        );
//
//        z80.execute(17);
//        assertRegister(BC, 0x1234);
//        assertRegister(A, 0x7F);
//        assertPC(0x05);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(7);
//        assertRegister(BC, 0x1234);
//        assertRegister(A, 0x7F);
//        assertPC(0x06);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        assertEquals(0x7F, memory.read(0x1234));
//    }
//
//    @Test
//    public void inc_hl() {
//
//        assemble(
//                " ld hl, 01234h",
//                " inc hl",
//                " ld hl, 0FFFFh",
//                " inc hl"
//        );
//
//        z80.execute(10);
//        assertRegister(HL, 0x1234);
//        assertPC(0x03);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(6);
//        assertRegister(HL, 0x1235);
//        assertPC(0x04);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(10);
//        assertRegister(HL, 0xFFFF);
//        assertPC(0x07);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(6);
//        assertRegister(HL, 0x0000);
//        assertPC(0x08);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//    }
//
//    @Test
//    public void inc_b() {
//
//        assemble(
//                " ld b, 00h",
//                " inc b",
//                " ld b, 0fh",
//                " inc b",
//                " ld b, 7fh",
//                " inc b",
//                " ld b, 0ffh",
//                " inc b",
//                " ld b, 0feh",
//                " inc b"
//        );
//
//        z80.execute(7);
//        assertRegister(B, 0x00);
//        assertPC(0x02);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(4);
//        assertRegister(B, 0x01);
//        assertPC(0x03);
//        assertFlag(0, HALF_CARRY_FLAG | NEGATIVE_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG | ZERO_FLAG, CARRY_FLAG, 0x01);
//        assertZeroCycleBalance();
//
//        // Test: 0fh + 01h = 10h
//        z80.execute(11);
//        assertRegister(B, 0x10);
//        assertPC(0x06);
//        assertFlag(HALF_CARRY_FLAG, NEGATIVE_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG | ZERO_FLAG, CARRY_FLAG, 0x10);
//        assertZeroCycleBalance();
//
//        // Test: 7fh + 01h = 80h
//        z80.execute(11);
//        assertRegister(B, 0x80);
//        assertPC(0x09);
//        assertFlag(HALF_CARRY_FLAG | SIGNIFICANT_FLAG | PARITY_FLAG, NEGATIVE_FLAG | ZERO_FLAG, CARRY_FLAG, 0x80);
//        assertZeroCycleBalance();
//
//        // Test: ffh + 01h = 00h
//        z80.execute(11);
//        assertRegister(B, 0x00);
//        assertPC(0x0C);
//        assertFlag(HALF_CARRY_FLAG | ZERO_FLAG, PARITY_FLAG | SIGNIFICANT_FLAG | NEGATIVE_FLAG, CARRY_FLAG, 0x00);
//        assertZeroCycleBalance();
//
//        // Test: feh + 01h = ffh
//        z80.execute(11);
//        assertRegister(B, 0xff);
//        assertPC(0x0F);
//        assertFlag(SIGNIFICANT_FLAG, HALF_CARRY_FLAG | PARITY_FLAG | NEGATIVE_FLAG | ZERO_FLAG, CARRY_FLAG, 0xFF);
//        assertZeroCycleBalance();
//
//    }
//
//    @Test
//    public void dec_b() {
//        memory.put(0x06, 0x01); /* LD B, 01h */
//        memory.put(0x05); /* DEC B */
//        memory.put(0x06, 0x00); /* LD B, 00h */
//        memory.put(0x05); /* DEC B */
//        memory.put(0x06, 0x10); /* LD B, 10h */
//        memory.put(0x05); /* DEC B */
//
//        // 01h - 01h = 00h
//        z80.execute(7);
//        assertRegister(B, 0x01);
//        assertPC(0x02);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(4);
//        assertRegister(B, 0x00);
//        assertPC(0x03);
//        assertFlag(NEGATIVE_FLAG | ZERO_FLAG, HALF_CARRY_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG, CARRY_FLAG, 0x00);
//        assertZeroCycleBalance();
//
//        // Test: 00h - 01h = FFh
//        z80.execute(11);
//        assertRegister(B, 0xFF);
//        assertPC(0x06);
//        assertFlag(SIGNIFICANT_FLAG | NEGATIVE_FLAG | HALF_CARRY_FLAG, PARITY_FLAG | ZERO_FLAG, CARRY_FLAG, 0xFF);
//        assertZeroCycleBalance();
//
//        // Test: 10h - 01h = 0Fh
//        z80.execute(11);
//        assertRegister(B, 0x0F);
//        assertPC(0x09);
//        assertFlag(NEGATIVE_FLAG | HALF_CARRY_FLAG, ZERO_FLAG | SIGNIFICANT_FLAG | PARITY_FLAG, CARRY_FLAG, 0x0F);
//        assertZeroCycleBalance();
//    }
//
//    @Test
//    public void ld_b_n() {
//        memory.put(0x06, 0x01); /* LD B, 01h */
//        memory.put(0x06, 0xFF); /* LD B, ffh */
//        memory.put(0x06, 0x00); /* LD B, 00h */
//
//        z80.execute(7);
//        assertRegister(B, 0x01);
//        assertPC(0x02);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(7);
//        assertRegister(B, 0xFF);
//        assertPC(0x04);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(7);
//        assertRegister(B, 0x00);
//        assertPC(0x06);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//    }
//
//    @Test
//    public void rlca() {
//        memory.put(0x3E, 0x01); /* LD A, 01h */
//        memory.put(0x07); /* RLCA */
//        memory.put(0x3E, 0x80); /* LD A, 80h */
//        memory.put(0x07); /* RLCA */
//
//        // 01h  -> 02h
//        z80.execute(7);
//        assertRegister(A, 0x01);
//        assertPC(0x02);
//        assertFlagUnchanged();
//        assertZeroCycleBalance();
//
//        z80.execute(7);
//        assertRegister(A, 0x02);
//        assertPC(0x03);
//        assertFlag(0, CARRY_FLAG | NEGATIVE_FLAG | HALF_CARRY_FLAG, NEGATIVE_FLAG | ZERO_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG, 0x02);
//        assertZeroCycleBalance();
//
//        // 80h -> 01h
//        z80.execute(14);
//        assertRegister(A, 0x01);
//        assertPC(0x06);
//        assertFlag(CARRY_FLAG, NEGATIVE_FLAG | HALF_CARRY_FLAG, NEGATIVE_FLAG | ZERO_FLAG | PARITY_FLAG | SIGNIFICANT_FLAG, 0x01);
//        assertZeroCycleBalance();
//
//
//    }
//
//    @Test
//    public void ex_af_af() {
//        memory.put(0x06, 0x00); /* LD B, 00h */
//        memory.put(0x05); /* DEC B */
//        memory.put(0x3E, 0x80); /* LD A, 80h */
//        memory.put(0x08); /* EX AF, AF' */
//        memory.put(0x3E, 0xAB); /* LD A, ABh */
//        memory.put(0x08); /* EX AF, AF' */
//        memory.put(0x08); /* EX AF, AF' */
//
//        z80.execute(18);
//        assertRegister(B, 0xFF);
//        assertRegister(F, 0xBA);
//        assertRegister(A, 0x80);
//        assertPC(0x05);
//        assertZeroCycleBalance();
//
//        z80.execute(4);
//        assertPC(0x06);
//        assertZeroCycleBalance();
//
//        z80.execute(7);
//        assertRegister(A, 0xAB);
//        assertRegister(F, 0x00);
//        assertRegister(B, 0xFF);
//        assertPC(0x08);
//        assertZeroCycleBalance();
//
//        z80.execute(4);
//        assertRegister(A, 0x80);
//        assertRegister(F, 0xBA);
//        assertRegister(B, 0xFF);
//        assertPC(0x09);
//        assertZeroCycleBalance();
//
//        z80.execute(4);
//        assertRegister(A, 0xAB);
//        assertRegister(F, 0x00);
//        assertRegister(B, 0xFF);
//        assertPC(0x0A);
//        assertZeroCycleBalance();
//
//    }

}

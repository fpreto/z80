package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.instructions.*;
import com.pretosmind.emu.z80.mmu.IO;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.Flags;
import com.pretosmind.emu.z80.registers.Register;

import static com.pretosmind.emu.z80.registers.RegisterName.*;

public class Z80 {

    private final Memory memory;
    private final IO io;

    private final OpCode[] opcodeLookupTable;
    private final OpCode[] opcodeCBLookupTable;
    private final OpCode[] opcodeDDLookupTable;
    private final OpCode[] opcodeEDLookupTable;
    private final OpCode[] opcodeFDLookupTable;

    private OpCode[] currentLookupTable;
    private final State state;

    //
    // Fast references
    //
    private final Register pc;
    private final OpcodeTargets opt;
    private final OpcodeConditions opc;
    private int cyclesBalance;
    private int previosInstruction;

    public Z80(Memory memory, IO io, State state) {
        opcodeLookupTable = new OpCode[0x100];
        opcodeCBLookupTable = new OpCode[0x100];
        opcodeDDLookupTable = new OpCode[0x100];
        opcodeEDLookupTable = new OpCode[0x100];
        opcodeFDLookupTable = new OpCode[0x100];
        this.memory = memory;
        this.io = io;
        this.state = state;

        this.pc = state.getRegister(PC);

        this.opt = new OpcodeTargets(this.state, this.memory);
        this.opc = new OpcodeConditions(this.state, this.memory);

        fillOpcodeLookupTable();
        currentLookupTable = opcodeLookupTable;
    }

    public void reset() {

    }

    public void execute(int cycles) {

        cyclesBalance += cycles;

        while (cyclesBalance > 0) {
            OpCode opcode = readNextOpcode();
            //System.out.println(opcode);
            cyclesBalance -= opcode.execute();
            //System.out.println(state);
        }

    }

    public int getCyclesBalance() {
        return cyclesBalance;
    }

    private OpCode readNextOpcode() {
        final int opcodeAddress = pc.read();

        if (!state.isHalted()) {
            final int opcode = memory.read(opcodeAddress);
            OpCode instruction = currentLookupTable[opcode];

            if (instruction == null && currentLookupTable != opcodeLookupTable) {
                System.out.println("Missing opcode. " + String.format("%02X %02X", previosInstruction, opcode));
                currentLookupTable = opcodeLookupTable;
                return opcodeLookupTable[opcode];
            }

            currentLookupTable = opcodeLookupTable;
            previosInstruction = opcode;
            return instruction;
        } else {
            currentLookupTable = opcodeLookupTable;
            return opcodeLookupTable[0x76]; // Keep executing halted instruction
        }
    }


    private void fillOpcodeLookupTable() {
        opcodeLookupTable[0x00] = new Nop(state);
        opcodeLookupTable[0x01] = new Ld(state, opt.r(BC), opt.nn());
        opcodeLookupTable[0x02] = new Ld(state, opt.iRR(BC), opt.r(A));
        opcodeLookupTable[0x03] = new Inc16(state, opt.r(HL));
        opcodeLookupTable[0x04] = new Inc(state, opt.r(B));
        opcodeLookupTable[0x05] = new Dec(state, opt.r(B));
        opcodeLookupTable[0x06] = new Ld(state, opt.r(B), opt.n());
        opcodeLookupTable[0x07] = new RLCA(state, opt.r(A));
        opcodeLookupTable[0x08] = new Ex(state, opt.r(AF), opt._r(AF));
        opcodeLookupTable[0x09] = new Add16(state, opt.r(HL), opt.r(BC));
        opcodeLookupTable[0x0A] = new Ld(state, opt.r(A), opt.iRR(BC));
        opcodeLookupTable[0x0B] = new Dec16(state, opt.r(BC));
        opcodeLookupTable[0x0C] = new Inc(state, opt.r(C));
        opcodeLookupTable[0x0D] = new Dec(state, opt.r(C));
        opcodeLookupTable[0x0E] = new Ld(state, opt.r(C), opt.n());
        opcodeLookupTable[0x0F] = new RRCA(state, opt.r(A));
        opcodeLookupTable[0x10] = new DJNZ(state, opt.r(B), opt.n());
        opcodeLookupTable[0x11] = new Ld(state, opt.r(DE), opt.nn());
        opcodeLookupTable[0x12] = new Ld(state, opt.iRR(DE), opt.r(A));
        opcodeLookupTable[0x13] = new Inc16(state, opt.r(DE));
        opcodeLookupTable[0x14] = new Inc(state, opt.r(D));
        opcodeLookupTable[0x15] = new Dec(state, opt.r(D));
        opcodeLookupTable[0x16] = new Ld(state, opt.r(D), opt.n());
        opcodeLookupTable[0x17] = new RLA(state, opt.r(A));
        opcodeLookupTable[0x18] = new JR(state, opc.t(), opt.n());
        opcodeLookupTable[0x19] = new Add16(state, opt.r(HL), opt.r(DE));
        opcodeLookupTable[0x1A] = new Ld(state, opt.r(A), opt.iRR(DE));
        opcodeLookupTable[0x1B] = new Dec16(state, opt.r(DE));
        opcodeLookupTable[0x1C] = new Inc(state, opt.r(E));
        opcodeLookupTable[0x1D] = new Dec(state, opt.r(E));
        opcodeLookupTable[0x1E] = new Ld(state, opt.r(E), opt.n());
        opcodeLookupTable[0x1F] = new RR(state, opt.r(A));
        opcodeLookupTable[0x20] = new JR(state, opc.nf(Flags.ZERO_FLAG), opt.n());
        opcodeLookupTable[0x21] = new Ld(state, opt.r(HL), opt.nn());
        opcodeLookupTable[0x22] = new Ld(state, opt.iinn(), opt.r(HL));
        opcodeLookupTable[0x23] = new Inc16(state, opt.r(HL));
        opcodeLookupTable[0x24] = new Inc(state, opt.r(H));
        opcodeLookupTable[0x25] = new Dec(state, opt.r(H));
        opcodeLookupTable[0x26] = new Ld(state, opt.r(H), opt.n());
        opcodeLookupTable[0x27] = new DAA(state, opt.r(A));
        opcodeLookupTable[0x28] = new JR(state, opc.f(Flags.ZERO_FLAG), opt.n());
        opcodeLookupTable[0x29] = new Add16(state, opt.r(HL), opt.r(HL));
        opcodeLookupTable[0x2A] = new Ld(state, opt.r(HL), opt.iinn());
        opcodeLookupTable[0x2B] = new Dec16(state, opt.r(HL));
        opcodeLookupTable[0x2C] = new Inc(state, opt.r(L));
        opcodeLookupTable[0x2D] = new Dec(state, opt.r(L));
        opcodeLookupTable[0x2E] = new Ld(state, opt.r(L), opt.n());
        opcodeLookupTable[0x2F] = new CPL(state, opt.r(A));
        opcodeLookupTable[0x30] = new JR(state, opc.nf(Flags.CARRY_FLAG), opt.n());
        opcodeLookupTable[0x31] = new Ld(state, opt.r(SP), opt.nn());
        opcodeLookupTable[0x32] = new Ld(state, opt.inn(), opt.r(A));
        opcodeLookupTable[0x33] = new Inc16(state, opt.r(SP));
        opcodeLookupTable[0x34] = new Inc(state, opt.iRR(HL));
        opcodeLookupTable[0x35] = new Dec(state, opt.iRR(HL));
        opcodeLookupTable[0x36] = new Ld(state, opt.iRR(HL), opt.n());
        opcodeLookupTable[0x37] = new SCF(state);
        opcodeLookupTable[0x38] = new JR(state, opc.f(Flags.CARRY_FLAG), opt.n());
        opcodeLookupTable[0x39] = new Add16(state, opt.r(HL), opt.r(SP));
        opcodeLookupTable[0x3A] = new Ld(state, opt.r(A), opt.inn());
        opcodeLookupTable[0x3B] = new Dec16(state, opt.r(SP));
        opcodeLookupTable[0x3C] = new Inc(state, opt.r(A));
        opcodeLookupTable[0x3D] = new Dec(state, opt.r(A));
        opcodeLookupTable[0x3E] = new Ld(state, opt.r(A), opt.n());
        opcodeLookupTable[0x3F] = new CCF(state);
        opcodeLookupTable[0x40] = new Ld(state, opt.r(B), opt.r(B));
        opcodeLookupTable[0x41] = new Ld(state, opt.r(B), opt.r(C));
        opcodeLookupTable[0x42] = new Ld(state, opt.r(B), opt.r(D));
        opcodeLookupTable[0x43] = new Ld(state, opt.r(B), opt.r(E));
        opcodeLookupTable[0x44] = new Ld(state, opt.r(B), opt.r(H));
        opcodeLookupTable[0x45] = new Ld(state, opt.r(B), opt.r(L));
        opcodeLookupTable[0x46] = new Ld(state, opt.r(B), opt.iRR(HL));
        opcodeLookupTable[0x47] = new Ld(state, opt.r(B), opt.r(A));
        opcodeLookupTable[0x48] = new Ld(state, opt.r(C), opt.r(B));
        opcodeLookupTable[0x49] = new Ld(state, opt.r(C), opt.r(C));
        opcodeLookupTable[0x4A] = new Ld(state, opt.r(C), opt.r(D));
        opcodeLookupTable[0x4B] = new Ld(state, opt.r(C), opt.r(E));
        opcodeLookupTable[0x4C] = new Ld(state, opt.r(C), opt.r(H));
        opcodeLookupTable[0x4D] = new Ld(state, opt.r(C), opt.r(L));
        opcodeLookupTable[0x4E] = new Ld(state, opt.r(C), opt.iRR(HL));
        opcodeLookupTable[0x4F] = new Ld(state, opt.r(C), opt.r(A));
        opcodeLookupTable[0x50] = new Ld(state, opt.r(D), opt.r(B));
        opcodeLookupTable[0x51] = new Ld(state, opt.r(D), opt.r(C));
        opcodeLookupTable[0x52] = new Ld(state, opt.r(D), opt.r(D));
        opcodeLookupTable[0x53] = new Ld(state, opt.r(D), opt.r(E));
        opcodeLookupTable[0x54] = new Ld(state, opt.r(D), opt.r(H));
        opcodeLookupTable[0x55] = new Ld(state, opt.r(D), opt.r(L));
        opcodeLookupTable[0x56] = new Ld(state, opt.r(D), opt.iRR(HL));
        opcodeLookupTable[0x57] = new Ld(state, opt.r(D), opt.r(A));
        opcodeLookupTable[0x58] = new Ld(state, opt.r(E), opt.r(B));
        opcodeLookupTable[0x59] = new Ld(state, opt.r(E), opt.r(C));
        opcodeLookupTable[0x5A] = new Ld(state, opt.r(E), opt.r(D));
        opcodeLookupTable[0x5B] = new Ld(state, opt.r(E), opt.r(E));
        opcodeLookupTable[0x5C] = new Ld(state, opt.r(E), opt.r(H));
        opcodeLookupTable[0x5D] = new Ld(state, opt.r(E), opt.r(L));
        opcodeLookupTable[0x5E] = new Ld(state, opt.r(E), opt.iRR(HL));
        opcodeLookupTable[0x5F] = new Ld(state, opt.r(E), opt.r(A));
        opcodeLookupTable[0x60] = new Ld(state, opt.r(H), opt.r(B));
        opcodeLookupTable[0x61] = new Ld(state, opt.r(H), opt.r(C));
        opcodeLookupTable[0x62] = new Ld(state, opt.r(H), opt.r(D));
        opcodeLookupTable[0x63] = new Ld(state, opt.r(H), opt.r(E));
        opcodeLookupTable[0x64] = new Ld(state, opt.r(H), opt.r(H));
        opcodeLookupTable[0x65] = new Ld(state, opt.r(H), opt.r(L));
        opcodeLookupTable[0x66] = new Ld(state, opt.r(H), opt.iRR(HL));
        opcodeLookupTable[0x67] = new Ld(state, opt.r(H), opt.r(A));
        opcodeLookupTable[0x68] = new Ld(state, opt.r(L), opt.r(B));
        opcodeLookupTable[0x69] = new Ld(state, opt.r(L), opt.r(C));
        opcodeLookupTable[0x6A] = new Ld(state, opt.r(L), opt.r(D));
        opcodeLookupTable[0x6B] = new Ld(state, opt.r(L), opt.r(E));
        opcodeLookupTable[0x6C] = new Ld(state, opt.r(L), opt.r(H));
        opcodeLookupTable[0x6D] = new Ld(state, opt.r(L), opt.r(L));
        opcodeLookupTable[0x6E] = new Ld(state, opt.r(L), opt.iRR(HL));
        opcodeLookupTable[0x6F] = new Ld(state, opt.r(L), opt.r(A));
        opcodeLookupTable[0x70] = new Ld(state, opt.iRR(HL), opt.r(B));
        opcodeLookupTable[0x71] = new Ld(state, opt.iRR(HL), opt.r(C));
        opcodeLookupTable[0x72] = new Ld(state, opt.iRR(HL), opt.r(D));
        opcodeLookupTable[0x73] = new Ld(state, opt.iRR(HL), opt.r(E));
        opcodeLookupTable[0x74] = new Ld(state, opt.iRR(HL), opt.r(H));
        opcodeLookupTable[0x75] = new Ld(state, opt.iRR(HL), opt.r(L));
        opcodeLookupTable[0x76] = new Halt(state);
        opcodeLookupTable[0x77] = new Ld(state, opt.iRR(HL), opt.r(A));
        opcodeLookupTable[0x78] = new Ld(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0x79] = new Ld(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0x7A] = new Ld(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0x7B] = new Ld(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0x7C] = new Ld(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0x7D] = new Ld(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0x7E] = new Ld(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0x7F] = new Ld(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0x80] = new Add(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0x81] = new Add(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0x82] = new Add(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0x83] = new Add(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0x84] = new Add(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0x85] = new Add(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0x86] = new Add(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0x87] = new Add(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0x88] = new Adc(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0x89] = new Adc(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0x8A] = new Adc(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0x8B] = new Adc(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0x8C] = new Adc(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0x8D] = new Adc(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0x8E] = new Adc(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0x8F] = new Adc(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0x90] = new Sub(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0x91] = new Sub(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0x92] = new Sub(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0x93] = new Sub(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0x94] = new Sub(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0x95] = new Sub(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0x96] = new Sub(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0x97] = new Sub(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0x98] = new Sbc(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0x99] = new Sbc(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0x9A] = new Sbc(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0x9B] = new Sbc(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0x9C] = new Sbc(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0x9D] = new Sbc(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0x9E] = new Sbc(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0x9F] = new Sbc(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0xA0] = new And(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0xA1] = new And(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0xA2] = new And(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0xA3] = new And(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0xA4] = new And(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0xA5] = new And(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0xA6] = new And(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0xA7] = new And(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0xA8] = new Xor(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0xA9] = new Xor(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0xAA] = new Xor(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0xAB] = new Xor(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0xAC] = new Xor(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0xAD] = new Xor(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0xAE] = new Xor(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0xAF] = new Xor(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0xB0] = new Or(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0xB1] = new Or(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0xB2] = new Or(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0xB3] = new Or(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0xB4] = new Or(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0xB5] = new Or(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0xB6] = new Or(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0xB7] = new Or(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0xB8] = new Cp(state, opt.r(A), opt.r(B));
        opcodeLookupTable[0xB9] = new Cp(state, opt.r(A), opt.r(C));
        opcodeLookupTable[0xBA] = new Cp(state, opt.r(A), opt.r(D));
        opcodeLookupTable[0xBB] = new Cp(state, opt.r(A), opt.r(E));
        opcodeLookupTable[0xBC] = new Cp(state, opt.r(A), opt.r(H));
        opcodeLookupTable[0xBD] = new Cp(state, opt.r(A), opt.r(L));
        opcodeLookupTable[0xBE] = new Cp(state, opt.r(A), opt.iRR(HL));
        opcodeLookupTable[0xBF] = new Cp(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0xC0] = new Ret(state, opc.nf(Flags.ZERO_FLAG), memory);
        opcodeLookupTable[0xC1] = new Pop(state, opt.r(BC), memory);
        opcodeLookupTable[0xC2] = new JP(state, opc.nf(Flags.ZERO_FLAG), opt.nn());
        opcodeLookupTable[0xC3] = new JP(state, opc.t(), opt.nn());
        opcodeLookupTable[0xC4] = new Call(state, opc.nf(Flags.ZERO_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xC5] = new Push(state, opt.r(BC), memory);
        opcodeLookupTable[0xC6] = new Add(state, opt.r(A), opt.n());
        opcodeLookupTable[0xC7] = new RST(state, 0x00, memory);
        opcodeLookupTable[0xC8] = new Ret(state, opc.f(Flags.ZERO_FLAG), memory);
        opcodeLookupTable[0xC9] = new Ret(state, opc.t(), memory);
        opcodeLookupTable[0xCA] = new JP(state, opc.f(Flags.ZERO_FLAG), opt.nn());
        opcodeLookupTable[0xCB] = new FlipOpcode(state, this.opcodeCBLookupTable);
        opcodeLookupTable[0xCC] = new Call(state, opc.f(Flags.ZERO_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xCD] = new Call(state, opc.t(), opt.nn(), memory);
        opcodeLookupTable[0xCE] = new Adc(state, opt.r(A), opt.n());
        opcodeLookupTable[0xCF] = new RST(state, 0x08, memory);
        opcodeLookupTable[0xD0] = new Ret(state, opc.nf(Flags.CARRY_FLAG), memory);
        opcodeLookupTable[0xD1] = new Pop(state, opt.r(DE), memory);
        opcodeLookupTable[0xD2] = new JP(state, opc.nf(Flags.CARRY_FLAG), opt.nn());
        opcodeLookupTable[0xD3] = new Out(state, opt.n(), opt.r(A), io);
        opcodeLookupTable[0xD4] = new Call(state, opc.nf(Flags.CARRY_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xD5] = new Push(state, opt.r(DE), memory);
        opcodeLookupTable[0xD6] = new Sub(state, opt.r(A), opt.n());
        opcodeLookupTable[0xD7] = new RST(state, 0x10, memory);
        opcodeLookupTable[0xD8] = new Ret(state, opc.f(Flags.CARRY_FLAG), memory);
        opcodeLookupTable[0xD9] = new Exx(state);
        opcodeLookupTable[0xDA] = new JP(state, opc.f(Flags.CARRY_FLAG), opt.nn());
        opcodeLookupTable[0xDB] = new In(state, opt.r(A), opt.n(), io);
        opcodeLookupTable[0xDC] = new Call(state, opc.f(Flags.CARRY_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xDD] = new FlipOpcode(state, this.opcodeDDLookupTable);
        opcodeLookupTable[0xDE] = new Sbc(state, opt.r(A), opt.n());
        opcodeLookupTable[0xDF] = new RST(state, 0x18, memory);
        opcodeLookupTable[0xE0] = new Ret(state, opc.nf(Flags.PARITY_FLAG), memory);
        opcodeLookupTable[0xE1] = new Pop(state, opt.r(HL), memory);
        opcodeLookupTable[0xE2] = new JP(state, opc.nf(Flags.PARITY_FLAG), opt.nn());
        opcodeLookupTable[0xE3] = new Ex(state, opt.iRR(SP), opt.r(HL));
        opcodeLookupTable[0xE4] = new Call(state, opc.nf(Flags.PARITY_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xE5] = new Push(state, opt.r(HL), memory);
        opcodeLookupTable[0xE6] = new And(state, opt.r(A), opt.n());
        opcodeLookupTable[0xE7] = new RST(state, 0x20, memory);
        opcodeLookupTable[0xE8] = new Ret(state, opc.f(Flags.PARITY_FLAG), memory);
        opcodeLookupTable[0xE9] = new JP(state, opc.t(), opt.iRR(HL));
        opcodeLookupTable[0xEA] = new JP(state, opc.f(Flags.PARITY_FLAG), opt.nn());
        opcodeLookupTable[0xEB] = new Ex(state, opt.r(DE), opt.r(HL));
        opcodeLookupTable[0xEC] = new Call(state, opc.f(Flags.PARITY_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xED] = new FlipOpcode(state, this.opcodeEDLookupTable);
        opcodeLookupTable[0xEE] = new Xor(state, opt.r(A), opt.n());
        opcodeLookupTable[0xEF] = new RST(state, 0x28, memory);
        opcodeLookupTable[0xF0] = new Ret(state, opc.nf(Flags.NEGATIVE_FLAG), memory);
        opcodeLookupTable[0xF1] = new Pop(state, opt.r(AF), memory);
        opcodeLookupTable[0xF2] = new JP(state, opc.nf(Flags.NEGATIVE_FLAG), opt.nn());
        opcodeLookupTable[0xF3] = new DI(state);
        opcodeLookupTable[0xF4] = new Call(state, opc.nf(Flags.NEGATIVE_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xF5] = new Push(state, opt.r(AF), memory);
        opcodeLookupTable[0xF6] = new Or(state, opt.r(A), opt.n());
        opcodeLookupTable[0xF7] = new RST(state, 0x30, memory);
        opcodeLookupTable[0xF8] = new Ret(state, opc.f(Flags.NEGATIVE_FLAG), memory);
        opcodeLookupTable[0xF9] = new Ld(state, opt.r(SP), opt.r(HL));
        opcodeLookupTable[0xFA] = new JP(state, opc.f(Flags.NEGATIVE_FLAG), opt.nn());
        opcodeLookupTable[0xFB] = new EI(state);
        opcodeLookupTable[0xFC] = new Call(state, opc.f(Flags.NEGATIVE_FLAG), opt.nn(), memory);
        opcodeLookupTable[0xFD] = new FlipOpcode(state, this.opcodeFDLookupTable);
        opcodeLookupTable[0xFE] = new Cp(state, opt.r(A), opt.n());
        opcodeLookupTable[0xFF] = new RST(state, 0x38, memory);


        //
        // CB Instructions
        //
        opcodeCBLookupTable[0x00] = new RLC(state, opt.r(B));
        opcodeCBLookupTable[0x01] = new RLC(state, opt.r(C));
        opcodeCBLookupTable[0x02] = new RLC(state, opt.r(D));
        opcodeCBLookupTable[0x03] = new RLC(state, opt.r(E));
        opcodeCBLookupTable[0x04] = new RLC(state, opt.r(H));
        opcodeCBLookupTable[0x05] = new RLC(state, opt.r(L));
        opcodeCBLookupTable[0x06] = new RLC(state, opt.iRR(HL));
        opcodeCBLookupTable[0x07] = new RLC(state, opt.r(A));
        opcodeCBLookupTable[0x08] = new RRC(state, opt.r(B));
        opcodeCBLookupTable[0x09] = new RRC(state, opt.r(C));
        opcodeCBLookupTable[0x0A] = new RRC(state, opt.r(D));
        opcodeCBLookupTable[0x0B] = new RRC(state, opt.r(E));
        opcodeCBLookupTable[0x0C] = new RRC(state, opt.r(H));
        opcodeCBLookupTable[0x0D] = new RRC(state, opt.r(L));
        opcodeCBLookupTable[0x0E] = new RRC(state, opt.iRR(HL));
        opcodeCBLookupTable[0x0F] = new RRC(state, opt.r(A));
		/*

		CB:
10	RL	B
11	RL	C
12	RL	D
13	RL	E
14	RL	H
15	RL	L
16	RL	(HL)
17	RL	A
18	RR	B
19	RR	C
1A	RR	D
1B	RR	E
1C	RR	H
1D	RR	L
1E	RR	(HL)
1F	RR	A
20	SLA	B
21	SLA	C
22	SLA	D
23	SLA	E
24	SLA	H
25	SLA	L
26	SLA	(HL)
27	SLA	A
28	SRA	B
29	SRA	C
2A	SRA	D
2B	SRA	E
2C	SRA	H
2D	SRA	L
2E	SRA	(HL)
2F	SRA	A
30
31
32
33
34
35
36
37
38	SRL	B
39	SRL	C
3A	SRL	D
3B	SRL	E
3C	SRL	H
3D	SRL	L
3E	SRL	(HL)
3F	SRL	A
40	BIT	0,B
41	BIT	0,C
42	BIT	0,D
43	BIT	0,E
44	BIT	0,H
45	BIT	0,L
46	BIT	0,(HL)
47	BIT	0,A
48	BIT	1,B
49	BIT	1,C
4A	BIT	1,D
4B	BIT	1,E
4C	BIT	1,H
4D	BIT	1,L
4E	BIT	1,(HL)
4F	BIT	1,A
50	BIT	2,B
51	BIT	2,C
52	BIT	2,D
53	BIT	2,E
54	BIT	2,H
55	BIT	2,L
56	BIT	2,(HL)
57	BIT	2,A
58	BIT	3,B
59	BIT	3,C
5A	BIT	3,D
5B	BIT	3,E
5C	BIT	3,H
5D	BIT	3,L
5E	BIT	3,(HL)
5F	BIT	3,A
60	BIT	4,B
61	BIT	4,C
62	BIT	4,D
63	BIT	4,E
64	BIT	4,H
65	BIT	4,L
66	BIT	4,(HL)
67	BIT	4,A
68	BIT	5,B
69	BIT	5,C
6A	BIT	5,D
6B	BIT	5,E
6C	BIT	5,H
6D	BIT	5,L
6E	BIT	5,(HL)
6F	BIT	5,A
70	BIT	6,B
71	BIT	6,C
72	BIT	6,D
73	BIT	6,E
74	BIT	6,H
75	BIT	6,L
76	BIT	6,(HL)
77	BIT	6,A
78	BIT	7,B
79	BIT	7,C
7A	BIT	7,D
7B	BIT	7,E
7C	BIT	7,H
7D	BIT	7,L
7E	BIT	7,(HL)
7F	BIT	7,A
80	RES	0,B
81	RES	0,C
82	RES	0,D
83	RES	0,E
84	RES	0,H
85	RES	0,L
86	RES	0,(HL)
87	RES	0,A
88	RES	1,B
89	RES	1,C
8A	RES	1,D
8B	RES	1,E
8C	RES	1,H
8D	RES	1,L
8E	RES	1,(HL)
8F	RES	1,A
90	RES	2,B
91	RES	2,C
92	RES	2,D
93	RES	2,E
94	RES	2,H
95	RES	2,L
96	RES	2,(HL)
97	RES	2,A
98	RES	3,B
99	RES	3,C
9A	RES	3,D
9B	RES	3,E
9C	RES	3,H
9D	RES	3,L
9E	RES	3,(HL)
9F	RES	3,A
A0	RES	4,B
A1	RES	4,C
A2	RES	4,D
A3	RES	4,E
A4	RES	4,H
A5	RES	4,L
A6	RES	4,(HL)
A7	RES	4,A
A8	RES	5,B
A9	RES	5,C
AA	RES	5,D
AB	RES	5,E
AC	RES	5,H
AD	RES	5,L
AE	RES	5,(HL)
AF	RES	5,A
B0	RES	6,B
B1	RES	6,C
B2	RES	6,D
B3	RES	6,E
B4	RES	6,H
B5	RES	6,L
B6	RES	6,(HL)
B7	RES	6,A
B8	RES	7,B
B9	RES	7,C
BA	RES	7,D
BB	RES	7,E
BC	RES	7,H
BD	RES	7,L
BE	RES	7,(HL)
BF	RES	7,A
C0	SET	0,B
C1	SET	0,C
C2	SET	0,D
C3	SET	0,E
C4	SET	0,H
C5	SET	0,L
C6	SET	0,(HL)
C7	SET	0,A
C8	SET	1,B
C9	SET	1,C
CA	SET	1,D
CB	SET	1,E
CC	SET	1,H
CD	SET	1,L
CE	SET	1,(HL)
CF	SET	1,A
D0	SET	2,B
D1	SET	2,C
D2	SET	2,D
D3	SET	2,E
D4	SET	2,H
D5	SET	2,L
D6	SET	2,(HL)
D7	SET	2,A
D8	SET	3,B
D9	SET	3,C
DA	SET	3,D
DB	SET	3,E
DC	SET	3,H
DD	SET	3,L
DE	SET	3,(HL)
DF	SET	3,A
E0	SET	4,B
E1	SET	4,C
E2	SET	4,D
E3	SET	4,E
E4	SET	4,H
E5	SET	4,L
E6	SET	4,(HL)
E7	SET	4,A
E8	SET	5,B
E9	SET	5,C
EA	SET	5,D
EB	SET	5,E
EC	SET	5,H
ED	SET	5,L
EE	SET	5,(HL)
EF	SET	5,A
F0	SET	6,B
F1	SET	6,C
F2	SET	6,D
F3	SET	6,E
F4	SET	6,H
F5	SET	6,L
F6	SET	6,(HL)
F7	SET	6,A
F8	SET	7,B
F9	SET	7,C
FA	SET	7,D
FB	SET	7,E
FC	SET	7,H
FD	SET	7,L
FE	SET	7,(HL)
FF	SET	7,A
		 */
    }

    private class FlipOpcode extends AbstractOpCode {

        private final OpCode[] table;

        public FlipOpcode(State state, final OpCode[] table) {
            super(state);
            this.table = table;
        }

        @Override
        public int execute() {
            incrementPC();
            Z80.this.currentLookupTable = table;
            return 4;
        }
    }


}

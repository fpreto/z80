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
            System.out.println(opcode);
            cyclesBalance -= opcode.execute();
            System.out.println(state);
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
                currentLookupTable = opcodeLookupTable;
                return opcodeLookupTable[opcode];
            }

            currentLookupTable = opcodeLookupTable;
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
        opcodeLookupTable[0x07] = new RLCA(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0x08] = new Ex(state, opt.r(AF), opt._r(AF));
        opcodeLookupTable[0x09] = new Add16(state, opt.r(HL), opt.r(BC));
        opcodeLookupTable[0x0A] = new Ld(state, opt.r(A), opt.iRR(BC));
        opcodeLookupTable[0x0B] = new Dec16(state, opt.r(BC));
        opcodeLookupTable[0x0C] = new Inc(state, opt.r(C));
        opcodeLookupTable[0x0D] = new Dec(state, opt.r(C));
        opcodeLookupTable[0x0E] = new Ld(state, opt.r(C), opt.n());
        opcodeLookupTable[0x0F] = new RRC(state, opt.r(A), opt.r(A));
        opcodeLookupTable[0x10] = new DJNZ(state, opt.r(B), opt.n());
        opcodeLookupTable[0x11] = new Ld(state, opt.r(DE), opt.nn());
        opcodeLookupTable[0x12] = new Ld(state, opt.iRR(DE), opt.r(A));
        opcodeLookupTable[0x13] = new Inc16(state, opt.r(DE));
        opcodeLookupTable[0x14] = new Inc(state, opt.r(D));
        opcodeLookupTable[0x15] = new Dec(state, opt.r(D));
        opcodeLookupTable[0x16] = new Ld(state, opt.r(D), opt.n());
        opcodeLookupTable[0x17] = new RL(state, opt.r(A));
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


		/*
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

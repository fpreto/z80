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
        opcodeLookupTable[0x1F] = new RRA(state, opt.r(A));
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
        opcodeCBLookupTable[0x10] = new RL(state, opt.r(B));
        opcodeCBLookupTable[0x11] = new RL(state, opt.r(C));
        opcodeCBLookupTable[0x12] = new RL(state, opt.r(D));
        opcodeCBLookupTable[0x13] = new RL(state, opt.r(E));
        opcodeCBLookupTable[0x14] = new RL(state, opt.r(H));
        opcodeCBLookupTable[0x15] = new RL(state, opt.r(L));
        opcodeCBLookupTable[0x16] = new RL(state, opt.iRR(HL));
        opcodeCBLookupTable[0x17] = new RL(state, opt.r(A));
        opcodeCBLookupTable[0x18] = new RR(state, opt.r(B));
        opcodeCBLookupTable[0x19] = new RR(state, opt.r(C));
        opcodeCBLookupTable[0x1A] = new RR(state, opt.r(D));
        opcodeCBLookupTable[0x1B] = new RR(state, opt.r(E));
        opcodeCBLookupTable[0x1C] = new RR(state, opt.r(H));
        opcodeCBLookupTable[0x1D] = new RR(state, opt.r(L));
        opcodeCBLookupTable[0x1E] = new RR(state, opt.iRR(HL));
        opcodeCBLookupTable[0x1F] = new RR(state, opt.r(A));
        opcodeCBLookupTable[0x20] = new SLA(state, opt.r(B));
        opcodeCBLookupTable[0x21] = new SLA(state, opt.r(C));
        opcodeCBLookupTable[0x22] = new SLA(state, opt.r(D));
        opcodeCBLookupTable[0x23] = new SLA(state, opt.r(E));
        opcodeCBLookupTable[0x24] = new SLA(state, opt.r(H));
        opcodeCBLookupTable[0x25] = new SLA(state, opt.r(L));
        opcodeCBLookupTable[0x26] = new SLA(state, opt.iRR(HL));
        opcodeCBLookupTable[0x27] = new SLA(state, opt.r(A));
        opcodeCBLookupTable[0x28] = new SRA(state, opt.r(B));
        opcodeCBLookupTable[0x29] = new SRA(state, opt.r(C));
        opcodeCBLookupTable[0x2A] = new SRA(state, opt.r(D));
        opcodeCBLookupTable[0x2B] = new SRA(state, opt.r(E));
        opcodeCBLookupTable[0x2C] = new SRA(state, opt.r(H));
        opcodeCBLookupTable[0x2D] = new SRA(state, opt.r(L));
        opcodeCBLookupTable[0x2E] = new SRA(state, opt.iRR(HL));
        opcodeCBLookupTable[0x2F] = new SRA(state, opt.r(A));
        opcodeCBLookupTable[0x30] = new SLL(state, opt.r(B));
        opcodeCBLookupTable[0x31] = new SLL(state, opt.r(C));
        opcodeCBLookupTable[0x32] = new SLL(state, opt.r(D));
        opcodeCBLookupTable[0x33] = new SLL(state, opt.r(E));
        opcodeCBLookupTable[0x34] = new SLL(state, opt.r(H));
        opcodeCBLookupTable[0x35] = new SLL(state, opt.r(L));
        opcodeCBLookupTable[0x36] = new SLL(state, opt.iRR(HL));
        opcodeCBLookupTable[0x37] = new SLL(state, opt.r(A));
        opcodeCBLookupTable[0x38] = new SRL(state, opt.r(B));
        opcodeCBLookupTable[0x39] = new SRL(state, opt.r(C));
        opcodeCBLookupTable[0x3A] = new SRL(state, opt.r(D));
        opcodeCBLookupTable[0x3B] = new SRL(state, opt.r(E));
        opcodeCBLookupTable[0x3C] = new SRL(state, opt.r(H));
        opcodeCBLookupTable[0x3D] = new SRL(state, opt.r(L));
        opcodeCBLookupTable[0x3E] = new SRL(state, opt.iRR(HL));
        opcodeCBLookupTable[0x3F] = new SRL(state, opt.r(A));
        opcodeCBLookupTable[0x40] = new BIT(state, opt.r(B), 0);
        opcodeCBLookupTable[0x41] = new BIT(state, opt.r(C), 0);
        opcodeCBLookupTable[0x42] = new BIT(state, opt.r(D), 0);
        opcodeCBLookupTable[0x43] = new BIT(state, opt.r(E), 0);
        opcodeCBLookupTable[0x44] = new BIT(state, opt.r(H), 0);
        opcodeCBLookupTable[0x45] = new BIT(state, opt.r(L), 0);
        opcodeCBLookupTable[0x46] = new BIT(state, opt.iRR(HL), 0);
        opcodeCBLookupTable[0x47] = new BIT(state, opt.r(A), 0);
        opcodeCBLookupTable[0x48] = new BIT(state, opt.r(B), 1);
        opcodeCBLookupTable[0x49] = new BIT(state, opt.r(C), 1);
        opcodeCBLookupTable[0x4A] = new BIT(state, opt.r(D), 1);
        opcodeCBLookupTable[0x4B] = new BIT(state, opt.r(E), 1);
        opcodeCBLookupTable[0x4C] = new BIT(state, opt.r(H), 1);
        opcodeCBLookupTable[0x4D] = new BIT(state, opt.r(L), 1);
        opcodeCBLookupTable[0x4E] = new BIT(state, opt.iRR(HL), 1);
        opcodeCBLookupTable[0x4F] = new BIT(state, opt.r(A), 1);
        opcodeCBLookupTable[0x50] = new BIT(state, opt.r(B), 2);
        opcodeCBLookupTable[0x51] = new BIT(state, opt.r(C), 2);
        opcodeCBLookupTable[0x52] = new BIT(state, opt.r(D), 2);
        opcodeCBLookupTable[0x53] = new BIT(state, opt.r(E), 2);
        opcodeCBLookupTable[0x54] = new BIT(state, opt.r(H), 2);
        opcodeCBLookupTable[0x55] = new BIT(state, opt.r(L), 2);
        opcodeCBLookupTable[0x56] = new BIT(state, opt.iRR(HL), 2);
        opcodeCBLookupTable[0x57] = new BIT(state, opt.r(A), 2);
        opcodeCBLookupTable[0x58] = new BIT(state, opt.r(B), 3);
        opcodeCBLookupTable[0x59] = new BIT(state, opt.r(C), 3);
        opcodeCBLookupTable[0x5A] = new BIT(state, opt.r(D), 3);
        opcodeCBLookupTable[0x5B] = new BIT(state, opt.r(E), 3);
        opcodeCBLookupTable[0x5C] = new BIT(state, opt.r(H), 3);
        opcodeCBLookupTable[0x5D] = new BIT(state, opt.r(L), 3);
        opcodeCBLookupTable[0x5E] = new BIT(state, opt.iRR(HL), 3);
        opcodeCBLookupTable[0x5F] = new BIT(state, opt.r(A), 3);
        opcodeCBLookupTable[0x60] = new BIT(state, opt.r(B), 4);
        opcodeCBLookupTable[0x61] = new BIT(state, opt.r(C), 4);
        opcodeCBLookupTable[0x62] = new BIT(state, opt.r(D), 4);
        opcodeCBLookupTable[0x63] = new BIT(state, opt.r(E), 4);
        opcodeCBLookupTable[0x64] = new BIT(state, opt.r(H), 4);
        opcodeCBLookupTable[0x65] = new BIT(state, opt.r(L), 4);
        opcodeCBLookupTable[0x66] = new BIT(state, opt.iRR(HL), 4);
        opcodeCBLookupTable[0x67] = new BIT(state, opt.r(A), 4);
        opcodeCBLookupTable[0x68] = new BIT(state, opt.r(B), 5);
        opcodeCBLookupTable[0x69] = new BIT(state, opt.r(C), 5);
        opcodeCBLookupTable[0x6A] = new BIT(state, opt.r(D), 5);
        opcodeCBLookupTable[0x6B] = new BIT(state, opt.r(E), 5);
        opcodeCBLookupTable[0x6C] = new BIT(state, opt.r(H), 5);
        opcodeCBLookupTable[0x6D] = new BIT(state, opt.r(L), 5);
        opcodeCBLookupTable[0x6E] = new BIT(state, opt.iRR(HL), 5);
        opcodeCBLookupTable[0x6F] = new BIT(state, opt.r(A), 5);
        opcodeCBLookupTable[0x70] = new BIT(state, opt.r(B), 6);
        opcodeCBLookupTable[0x71] = new BIT(state, opt.r(C), 6);
        opcodeCBLookupTable[0x72] = new BIT(state, opt.r(D), 6);
        opcodeCBLookupTable[0x73] = new BIT(state, opt.r(E), 6);
        opcodeCBLookupTable[0x74] = new BIT(state, opt.r(H), 6);
        opcodeCBLookupTable[0x75] = new BIT(state, opt.r(L), 6);
        opcodeCBLookupTable[0x76] = new BIT(state, opt.iRR(HL), 6);
        opcodeCBLookupTable[0x77] = new BIT(state, opt.r(A), 6);
        opcodeCBLookupTable[0x78] = new BIT(state, opt.r(B), 7);
        opcodeCBLookupTable[0x79] = new BIT(state, opt.r(C), 7);
        opcodeCBLookupTable[0x7A] = new BIT(state, opt.r(D), 7);
        opcodeCBLookupTable[0x7B] = new BIT(state, opt.r(E), 7);
        opcodeCBLookupTable[0x7C] = new BIT(state, opt.r(H), 7);
        opcodeCBLookupTable[0x7D] = new BIT(state, opt.r(L), 7);
        opcodeCBLookupTable[0x7E] = new BIT(state, opt.iRR(HL), 7);
        opcodeCBLookupTable[0x7F] = new BIT(state, opt.r(A), 7);
        opcodeCBLookupTable[0x80] = new RES(state, opt.r(B), 0);
        opcodeCBLookupTable[0x81] = new RES(state, opt.r(C), 0);
        opcodeCBLookupTable[0x82] = new RES(state, opt.r(D), 0);
        opcodeCBLookupTable[0x83] = new RES(state, opt.r(E), 0);
        opcodeCBLookupTable[0x84] = new RES(state, opt.r(H), 0);
        opcodeCBLookupTable[0x85] = new RES(state, opt.r(L), 0);
        opcodeCBLookupTable[0x86] = new RES(state, opt.iRR(HL), 0);
        opcodeCBLookupTable[0x87] = new RES(state, opt.r(A), 0);
        opcodeCBLookupTable[0x88] = new RES(state, opt.r(B), 1);
        opcodeCBLookupTable[0x89] = new RES(state, opt.r(C), 1);
        opcodeCBLookupTable[0x8A] = new RES(state, opt.r(D), 1);
        opcodeCBLookupTable[0x8B] = new RES(state, opt.r(E), 1);
        opcodeCBLookupTable[0x8C] = new RES(state, opt.r(H), 1);
        opcodeCBLookupTable[0x8D] = new RES(state, opt.r(L), 1);
        opcodeCBLookupTable[0x8E] = new RES(state, opt.iRR(HL), 1);
        opcodeCBLookupTable[0x8F] = new RES(state, opt.r(A), 1);
        opcodeCBLookupTable[0x90] = new RES(state, opt.r(B), 2);
        opcodeCBLookupTable[0x91] = new RES(state, opt.r(C), 2);
        opcodeCBLookupTable[0x92] = new RES(state, opt.r(D), 2);
        opcodeCBLookupTable[0x93] = new RES(state, opt.r(E), 2);
        opcodeCBLookupTable[0x94] = new RES(state, opt.r(H), 2);
        opcodeCBLookupTable[0x95] = new RES(state, opt.r(L), 2);
        opcodeCBLookupTable[0x96] = new RES(state, opt.iRR(HL), 2);
        opcodeCBLookupTable[0x97] = new RES(state, opt.r(A), 2);
        opcodeCBLookupTable[0x98] = new RES(state, opt.r(B), 3);
        opcodeCBLookupTable[0x99] = new RES(state, opt.r(C), 3);
        opcodeCBLookupTable[0x9A] = new RES(state, opt.r(D), 3);
        opcodeCBLookupTable[0x9B] = new RES(state, opt.r(E), 3);
        opcodeCBLookupTable[0x9C] = new RES(state, opt.r(H), 3);
        opcodeCBLookupTable[0x9D] = new RES(state, opt.r(L), 3);
        opcodeCBLookupTable[0x9E] = new RES(state, opt.iRR(HL), 3);
        opcodeCBLookupTable[0x9F] = new RES(state, opt.r(A), 3);
        opcodeCBLookupTable[0xA0] = new RES(state, opt.r(B), 4);
        opcodeCBLookupTable[0xA1] = new RES(state, opt.r(C), 4);
        opcodeCBLookupTable[0xA2] = new RES(state, opt.r(D), 4);
        opcodeCBLookupTable[0xA3] = new RES(state, opt.r(E), 4);
        opcodeCBLookupTable[0xA4] = new RES(state, opt.r(H), 4);
        opcodeCBLookupTable[0xA5] = new RES(state, opt.r(L), 4);
        opcodeCBLookupTable[0xA6] = new RES(state, opt.iRR(HL), 4);
        opcodeCBLookupTable[0xA7] = new RES(state, opt.r(A), 4);
        opcodeCBLookupTable[0xA8] = new RES(state, opt.r(B), 5);
        opcodeCBLookupTable[0xA9] = new RES(state, opt.r(C), 5);
        opcodeCBLookupTable[0xAA] = new RES(state, opt.r(D), 5);
        opcodeCBLookupTable[0xAB] = new RES(state, opt.r(E), 5);
        opcodeCBLookupTable[0xAC] = new RES(state, opt.r(H), 5);
        opcodeCBLookupTable[0xAD] = new RES(state, opt.r(L), 5);
        opcodeCBLookupTable[0xAE] = new RES(state, opt.iRR(HL), 5);
        opcodeCBLookupTable[0xAF] = new RES(state, opt.r(A), 5);
        opcodeCBLookupTable[0xB0] = new RES(state, opt.r(B), 6);
        opcodeCBLookupTable[0xB1] = new RES(state, opt.r(C), 6);
        opcodeCBLookupTable[0xB2] = new RES(state, opt.r(D), 6);
        opcodeCBLookupTable[0xB3] = new RES(state, opt.r(E), 6);
        opcodeCBLookupTable[0xB4] = new RES(state, opt.r(H), 6);
        opcodeCBLookupTable[0xB5] = new RES(state, opt.r(L), 6);
        opcodeCBLookupTable[0xB6] = new RES(state, opt.iRR(HL), 6);
        opcodeCBLookupTable[0xB7] = new RES(state, opt.r(A), 6);
        opcodeCBLookupTable[0xB8] = new RES(state, opt.r(B), 7);
        opcodeCBLookupTable[0xB9] = new RES(state, opt.r(C), 7);
        opcodeCBLookupTable[0xBA] = new RES(state, opt.r(D), 7);
        opcodeCBLookupTable[0xBB] = new RES(state, opt.r(E), 7);
        opcodeCBLookupTable[0xBC] = new RES(state, opt.r(H), 7);
        opcodeCBLookupTable[0xBD] = new RES(state, opt.r(L), 7);
        opcodeCBLookupTable[0xBE] = new RES(state, opt.iRR(HL), 7);
        opcodeCBLookupTable[0xBF] = new RES(state, opt.r(A), 7);


        opcodeCBLookupTable[0xC0] = new SET(state, opt.r(B), 0);
        opcodeCBLookupTable[0xC1] = new SET(state, opt.r(C), 0);
        opcodeCBLookupTable[0xC2] = new SET(state, opt.r(D), 0);
        opcodeCBLookupTable[0xC3] = new SET(state, opt.r(E), 0);
        opcodeCBLookupTable[0xC4] = new SET(state, opt.r(H), 0);
        opcodeCBLookupTable[0xC5] = new SET(state, opt.r(L), 0);
        opcodeCBLookupTable[0xC6] = new SET(state, opt.iRR(HL), 0);
        opcodeCBLookupTable[0xC7] = new SET(state, opt.r(A), 0);
        opcodeCBLookupTable[0xC8] = new SET(state, opt.r(B), 1);
        opcodeCBLookupTable[0xC9] = new SET(state, opt.r(C), 1);
        opcodeCBLookupTable[0xCA] = new SET(state, opt.r(D), 1);
        opcodeCBLookupTable[0xCB] = new SET(state, opt.r(E), 1);
        opcodeCBLookupTable[0xCC] = new SET(state, opt.r(H), 1);
        opcodeCBLookupTable[0xCD] = new SET(state, opt.r(L), 1);
        opcodeCBLookupTable[0xCE] = new SET(state, opt.iRR(HL), 1);
        opcodeCBLookupTable[0xCF] = new SET(state, opt.r(A), 1);
        opcodeCBLookupTable[0xD0] = new SET(state, opt.r(B), 2);
        opcodeCBLookupTable[0xD1] = new SET(state, opt.r(C), 2);
        opcodeCBLookupTable[0xD2] = new SET(state, opt.r(D), 2);
        opcodeCBLookupTable[0xD3] = new SET(state, opt.r(E), 2);
        opcodeCBLookupTable[0xD4] = new SET(state, opt.r(H), 2);
        opcodeCBLookupTable[0xD5] = new SET(state, opt.r(L), 2);
        opcodeCBLookupTable[0xD6] = new SET(state, opt.iRR(HL), 2);
        opcodeCBLookupTable[0xD7] = new SET(state, opt.r(A), 2);
        opcodeCBLookupTable[0xD8] = new SET(state, opt.r(B), 3);
        opcodeCBLookupTable[0xD9] = new SET(state, opt.r(C), 3);
        opcodeCBLookupTable[0xDA] = new SET(state, opt.r(D), 3);
        opcodeCBLookupTable[0xDB] = new SET(state, opt.r(E), 3);
        opcodeCBLookupTable[0xDC] = new SET(state, opt.r(H), 3);
        opcodeCBLookupTable[0xDD] = new SET(state, opt.r(L), 3);
        opcodeCBLookupTable[0xDE] = new SET(state, opt.iRR(HL), 3);
        opcodeCBLookupTable[0xDF] = new SET(state, opt.r(A), 3);
        opcodeCBLookupTable[0xE0] = new SET(state, opt.r(B), 4);
        opcodeCBLookupTable[0xE1] = new SET(state, opt.r(C), 4);
        opcodeCBLookupTable[0xE2] = new SET(state, opt.r(D), 4);
        opcodeCBLookupTable[0xE3] = new SET(state, opt.r(E), 4);
        opcodeCBLookupTable[0xE4] = new SET(state, opt.r(H), 4);
        opcodeCBLookupTable[0xE5] = new SET(state, opt.r(L), 4);
        opcodeCBLookupTable[0xE6] = new SET(state, opt.iRR(HL), 4);
        opcodeCBLookupTable[0xE7] = new SET(state, opt.r(A), 4);
        opcodeCBLookupTable[0xE8] = new SET(state, opt.r(B), 5);
        opcodeCBLookupTable[0xE9] = new SET(state, opt.r(C), 5);
        opcodeCBLookupTable[0xEA] = new SET(state, opt.r(D), 5);
        opcodeCBLookupTable[0xEB] = new SET(state, opt.r(E), 5);
        opcodeCBLookupTable[0xEC] = new SET(state, opt.r(H), 5);
        opcodeCBLookupTable[0xED] = new SET(state, opt.r(L), 5);
        opcodeCBLookupTable[0xEE] = new SET(state, opt.iRR(HL), 5);
        opcodeCBLookupTable[0xEF] = new SET(state, opt.r(A), 5);
        opcodeCBLookupTable[0xF0] = new SET(state, opt.r(B), 6);
        opcodeCBLookupTable[0xF1] = new SET(state, opt.r(C), 6);
        opcodeCBLookupTable[0xF2] = new SET(state, opt.r(D), 6);
        opcodeCBLookupTable[0xF3] = new SET(state, opt.r(E), 6);
        opcodeCBLookupTable[0xF4] = new SET(state, opt.r(H), 6);
        opcodeCBLookupTable[0xF5] = new SET(state, opt.r(L), 6);
        opcodeCBLookupTable[0xF6] = new SET(state, opt.iRR(HL), 6);
        opcodeCBLookupTable[0xF7] = new SET(state, opt.r(A), 6);
        opcodeCBLookupTable[0xF8] = new SET(state, opt.r(B), 7);
        opcodeCBLookupTable[0xF9] = new SET(state, opt.r(C), 7);
        opcodeCBLookupTable[0xFA] = new SET(state, opt.r(D), 7);
        opcodeCBLookupTable[0xFB] = new SET(state, opt.r(E), 7);
        opcodeCBLookupTable[0xFC] = new SET(state, opt.r(H), 7);
        opcodeCBLookupTable[0xFD] = new SET(state, opt.r(L), 7);
        opcodeCBLookupTable[0xFE] = new SET(state, opt.iRR(HL), 7);
        opcodeCBLookupTable[0xFF] = new SET(state, opt.r(A), 7);

        opcodeDDLookupTable[0x09] = new Add16(state, opt.r(IX), opt.r(BC));
        opcodeDDLookupTable[0x19] = new Add16(state, opt.r(IX), opt.r(DE));
        opcodeDDLookupTable[0x21] = new Ld(state, opt.r(IX), opt.nn());
        opcodeDDLookupTable[0x22] = new Ld(state, opt.iinn(), opt.r(IX));
        opcodeDDLookupTable[0x23] = new Inc16(state, opt.r(IX));
        opcodeDDLookupTable[0x24] = new Inc(state, opt.r(IXH));
        opcodeDDLookupTable[0x25] = new Dec(state, opt.r(IXH));
        opcodeDDLookupTable[0x26] = new Ld(state, opt.r(IXH), opt.n());
        opcodeDDLookupTable[0x29] = new Add16(state, opt.r(IX), opt.r(IX));
        opcodeDDLookupTable[0x2A] = new Ld(state, opt.r(IX), opt.iinn());
        opcodeDDLookupTable[0x2B] = new Dec16(state, opt.r(IX));
        opcodeDDLookupTable[0x2C] = new Inc(state, opt.r(IXL));
        opcodeDDLookupTable[0x2D] = new Dec(state, opt.r(IXL));
        opcodeDDLookupTable[0x2E] = new Ld(state, opt.r(IXL), opt.n());
        opcodeDDLookupTable[0x34] = new Inc(state, opt.iRRn(IX, true));
        opcodeDDLookupTable[0x35] = new Dec(state, opt.iRRn(IX, true));
        opcodeDDLookupTable[0x36] = new Ld(state, opt.iRRn(IX, false), opt.n());
        opcodeDDLookupTable[0x39] = new Add16(state, opt.r(IX), opt.r(SP));
        opcodeDDLookupTable[0x44] = new Ld(state, opt.r(B), opt.r(IXH));
        opcodeDDLookupTable[0x45] = new Ld(state, opt.r(B), opt.r(IXL));
        opcodeDDLookupTable[0x46] = new Ld(state, opt.r(B), opt.iRRn(IX, false));
        opcodeDDLookupTable[0x4C] = new Ld(state, opt.r(C), opt.r(IXH));
        opcodeDDLookupTable[0x4D] = new Ld(state, opt.r(C), opt.r(IXL));
        opcodeDDLookupTable[0x4E] = new Ld(state, opt.r(C), opt.iRRn(IX, false));
        opcodeDDLookupTable[0x54] = new Ld(state, opt.r(D), opt.r(IXH));
        opcodeDDLookupTable[0x55] = new Ld(state, opt.r(D), opt.r(IXL));
        opcodeDDLookupTable[0x56] = new Ld(state, opt.r(D), opt.iRRn(IX, false));
        opcodeDDLookupTable[0x5C] = new Ld(state, opt.r(E), opt.r(IXH));
        opcodeDDLookupTable[0x5D] = new Ld(state, opt.r(E), opt.r(IXL));
        opcodeDDLookupTable[0x5E] = new Ld(state, opt.r(E), opt.iRRn(IX, false));
        opcodeDDLookupTable[0x60] = new Ld(state, opt.r(IXH), opt.r(B));
        opcodeDDLookupTable[0x61] = new Ld(state, opt.r(IXH), opt.r(C));
        opcodeDDLookupTable[0x62] = new Ld(state, opt.r(IXH), opt.r(D));
        opcodeDDLookupTable[0x63] = new Ld(state, opt.r(IXH), opt.r(E));
        opcodeDDLookupTable[0x64] = new Ld(state, opt.r(IXH), opt.r(H));
        opcodeDDLookupTable[0x65] = new Ld(state, opt.r(IXH), opt.r(L));
        opcodeDDLookupTable[0x66] = new Ld(state, opt.r(IXH), opt.iRRn(IX, false));
        opcodeDDLookupTable[0x67] = new Ld(state, opt.r(IXH), opt.r(A));
        opcodeDDLookupTable[0x68] = new Ld(state, opt.r(IXL), opt.r(B));
        opcodeDDLookupTable[0x69] = new Ld(state, opt.r(IXL), opt.r(C));
        opcodeDDLookupTable[0x6A] = new Ld(state, opt.r(IXL), opt.r(D));
        opcodeDDLookupTable[0x6B] = new Ld(state, opt.r(IXL), opt.r(E));
        opcodeDDLookupTable[0x6C] = new Ld(state, opt.r(IXL), opt.r(H));
        opcodeDDLookupTable[0x6D] = new Ld(state, opt.r(IXL), opt.r(L));
        opcodeDDLookupTable[0x6E] = new Ld(state, opt.r(IXL), opt.iRRn(IX, false));
        opcodeDDLookupTable[0x6F] = new Ld(state, opt.r(IXL), opt.r(A));

        /*
#DD (Be aware of missing opcodes in this list)
70	LD	(IX+d),B
71	LD	(IX+d),C
72	LD	(IX+d),D
73	LD	(IX+d),E
74	LD	(IX+d),H
75	LD	(IX+d),L
76
77	LD	(IX+d),A
78
79
7A
7B
7C
7D
7E	LD	A,(IX+d)
7F
80
81
82
83
84
85
86	ADD	A,(IX+d)
87
88
89
8A
8B
8C
8D
8E	ADC	A,(IX+d)
8F
90
91
92
93
94
95
96	SUB	(IX+d)
97
98
99
9A
9B
9C
9D
9E	SBC	(IX+d)
9F
A0
A1
A2
A3
A4
A5
A6	AND	(IX+d)
A7
A8
A9
AA
AB
AC
AD
AE	XOR	(IX+d)
AF
B0
B1
B2
B3
B4
B5
B6	OR	(IX+d)
B7
B8
B9
BA
BB
BC
BD
BE	CP	(IX+d)
BF
C0
C1
C2
C3
C4
C5
C6
C7
C8
C9
CA
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

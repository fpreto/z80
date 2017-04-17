package com.pretosmind.emu.z80;

import static com.pretosmind.emu.z80.registers.RegisterName.*;

import com.pretosmind.emu.z80.instructions.Add;
import com.pretosmind.emu.z80.instructions.Add16;
import com.pretosmind.emu.z80.instructions.CCF;
import com.pretosmind.emu.z80.instructions.CPL;
import com.pretosmind.emu.z80.instructions.DAA;
import com.pretosmind.emu.z80.instructions.DJNZ;
import com.pretosmind.emu.z80.instructions.Dec;
import com.pretosmind.emu.z80.instructions.Dec16;
import com.pretosmind.emu.z80.instructions.Ex;
import com.pretosmind.emu.z80.instructions.Halt;
import com.pretosmind.emu.z80.instructions.Inc;
import com.pretosmind.emu.z80.instructions.Inc16;
import com.pretosmind.emu.z80.instructions.JR;
import com.pretosmind.emu.z80.instructions.Ld;
import com.pretosmind.emu.z80.instructions.Nop;
import com.pretosmind.emu.z80.instructions.OpCode;
import com.pretosmind.emu.z80.instructions.OpcodeConditions;
import com.pretosmind.emu.z80.instructions.OpcodeTargets;
import com.pretosmind.emu.z80.instructions.RL;
import com.pretosmind.emu.z80.instructions.RLCA;
import com.pretosmind.emu.z80.instructions.RR;
import com.pretosmind.emu.z80.instructions.RRC;
import com.pretosmind.emu.z80.instructions.SCF;
import com.pretosmind.emu.z80.mmu.Memory;
import com.pretosmind.emu.z80.registers.Flags;
import com.pretosmind.emu.z80.registers.Register;

public class Z80 {
	
	private final Memory memory;
	private int cyclesBalance;
	
	private final OpCode[] opcodeLookupTable;
	
	private final State state;
	
	//
	// Fast references
	//
	private final Register pc;
	
	private final OpcodeTargets opt;
	private final OpcodeConditions opc;
	
	public Z80(Memory memory, State state) {
		opcodeLookupTable = new OpCode[0x100];
		this.memory = memory;
		this.state = state;
		
		this.pc = state.getRegister(PC);
		
		this.opt = new OpcodeTargets(this.state, this.memory);
		this.opc = new OpcodeConditions(this.state, this.memory);
		
		fillOpcodeLookupTable();
	}

	public void execute(int cycles) {
		
		cyclesBalance += cycles;
		
		while (cyclesBalance > 0) {
			OpCode opcode = readNextOpcode();
			cyclesBalance -= opcode.execute();
		}
		
	}
	
	public int getCyclesBalance() {
		return cyclesBalance;
	}
	
	private OpCode readNextOpcode() {
		final int opcodeAddress = pc.read();
		
		if (!state.isHalted()) {
			final int opcode = memory.read(opcodeAddress);
			return opcodeLookupTable[opcode];
		} else {
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
		opcodeLookupTable[0x0E] = new RRC(state, opt.r(A), opt.r(A));
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
		
		/*	
88	ADC	A,B	
89	ADC	A,C	
8A	ADC	A,D	
8B	ADC	A,E	
8C	ADC	A,H	
8D	ADC	A,L	
8E	ADC	A,(HL)	
8F	ADC	A,A	
90	SUB	B	
91	SUB	C	
92	SUB	D	
93	SUB	E	
94	SUB	H	
95	SUB	L	
96	SUB	(HL)	
97	SUB	A	
98	SBC	B	
99	SBC	C	
9A	SBC	D	
9B	SBC	E	
9C	SBC	H	
9D	SBC	L	
9E	SBC	(HL)	
9F	SBC	A	
A0	AND	B	
A1	AND	C	
A2	AND	D	
A3	AND	E	
A4	AND	H	
A5	AND	L	
A6	AND	(HL)	
A7	AND	A	
A8	XOR	B	
A9	XOR	C	
AA	XOR	D	
AB	XOR	E	
AC	XOR	H	
AD	XOR	L	
AE	XOR	(HL)	
AF	XOR	A	
B0	OR	B	
B1	OR	C	
B2	OR	D	
B3	OR	E	
B4	OR	H	
B5	OR	L	
B6	OR	(HL)	
B7	OR	A	
B8	CP	B	
B9	CP	C	
BA	CP	D	
BB	CP	E	
BC	CP	H	
BD	CP	L	
BE	CP	(HL)	
BF	CP	A	
C0	RET	NZ	
C1	POP	BC	
C2	JP	NZ,nn	
C3	JP	nn	
C4	CALL	NZ,nn	
C5	PUSH	BC	
C6	ADD	A,n	
C7	RST	00H	
C8	RET	Z	
C9	RET		
CA	JP	Z,nn	
CB	#CB		
CC	CALL	Z,nn	
CD	CALL	nn	
CE	ADC	A,n	
CF	RST	08H	
D0	RET	NC	
D1	POP	DE	
D2	JP	NC,nn	
D3	OUT	(n),A	
D4	CALL	NC,nn	
D5	PUSH	DE	
D6	SUB	n	
D7	RST	10H	
D8	RET	C	
D9	EXX		
DA	JP	C,nn	
DB	IN	A,(n)	
DC	CALL	C,nn	
DD	#DD		
DE	SBC	A,n	
DF	RST	18H	
E0	RET	PO	
E1	POP	HL	
E2	JP	PO,nn	
E3	EX	(SP),HL	
E4	CALL	PO,nn	
E5	PUSH	HL	
E6	AND	n	
E7	RST	20H	
E8	RET	PE	
E9	JP	(HL)	
EA	JP	PE,nn	
EB	EX	DE,HL	
EC	CALL	PE,nn	
ED	#ED		
EE	XOR	n	
EF	RST	28H	
F0	RET	P	
F1	POP	AF	
F2	JP	P,nn	
F3	DI		
F4	CALL	P,nn	
F5	PUSH	AF	
F6	OR	n	
F7	RST	30H	
F8	RET	M	
F9	LD	SP,HL	
FA	JP	M,nn	
FB	EI		
FC	CALL	M,nn	
FD	#FD		
FE	CP	n	
FF	RST	38H	

		
		 */
	}
	
	
	
}

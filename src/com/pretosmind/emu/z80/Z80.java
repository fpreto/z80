package com.pretosmind.emu.z80;

import static com.pretosmind.emu.z80.registers.RegisterName.*;

import com.pretosmind.emu.z80.instructions.Dec;
import com.pretosmind.emu.z80.instructions.Ex;
import com.pretosmind.emu.z80.instructions.Inc;
import com.pretosmind.emu.z80.instructions.Inc16;
import com.pretosmind.emu.z80.instructions.Ld;
import com.pretosmind.emu.z80.instructions.Nop;
import com.pretosmind.emu.z80.instructions.OpCode;
import com.pretosmind.emu.z80.instructions.OpcodeTargets;
import com.pretosmind.emu.z80.instructions.RLCA;
import com.pretosmind.emu.z80.mmu.Memory;
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
	
	public Z80(Memory memory, State state) {
		opcodeLookupTable = new OpCode[0x100];
		this.memory = memory;
		this.state = state;
		
		this.pc = state.getRegister(PC);
		
		this.opt = new OpcodeTargets(this.state, this.memory);
		
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
		final int opcode = memory.read(opcodeAddress);
		return opcodeLookupTable[opcode];
	}
	
	
	private void fillOpcodeLookupTable() {
		opcodeLookupTable[0x00] = new Nop(state);
		opcodeLookupTable[0x01] = new Ld(state, opt.r(BC), opt.nn());
		opcodeLookupTable[0x02] = new Ld(state, opt.iRR(BC), opt.r(A));
		opcodeLookupTable[0x03] = new Inc16(state, opt.r(HL), opt.r(HL));
		opcodeLookupTable[0x04] = new Inc(state, opt.r(B), opt.r(B));
		opcodeLookupTable[0x05] = new Dec(state, opt.r(B), opt.r(B));
		opcodeLookupTable[0x06] = new Ld(state, opt.r(B), opt.n());
		opcodeLookupTable[0x07] = new RLCA(state, opt.r(A), opt.r(A));
		opcodeLookupTable[0x08] = new Ex(state, opt.r(AF), opt._r(AF));

		opcodeLookupTable[0x21] = new Ld(state, opt.r(HL), opt.nn());
		opcodeLookupTable[0x3E] = new Ld(state, opt.r(A), opt.n());
		
		/*	
09	ADD	HL,BC	
0A	LD	A,(BC)	
0B	DEC	BC	
0C	INC	C	
0D	DEC	C	
0E	LD	C,n	
0F	RRCA		
10	DJNZ	e	
11	LD	DE,nn	
12	LD	(DE),A	
13	INC	DE	
14	INC	D	
15	DEC	D	
16	LD	D,n	
17	RLA		
18	JR	e	
19	ADD	HL,DE	
1A	LD	A,(DE)	
1B	DEC	DE	
1C	INC	E	
1D	DEC	E	
1E	LD	E,n	
1F	RRA		
20	JR	NZ,e	
21	LD	HL,nn	
22	LD	(nn),HL	
23	INC	HL	
24	INC	H	
25	DEC	H	
26	LD	H,n	
27	DAA		
28	JR	Z,e	
29	ADD	HL,HL	
2A	LD	HL,(nn)	
2B	DEC	HL	
2C	INC	L	
2D	DEC	L	
2E	LD	L,n	
2F	CPL		
30	JR	NC,e	
31	LD	SP,nn	
32	LD	(nn),A	
33	INC	SP	
34	INC	(HL)	
35	DEC	(HL)	
36	LD	(HL),n	
37	SCF		
38	JR	C,e	
39	ADD	HL,SP	
3A	LD	A,(nn)	
3B	DEC	SP	
3C	INC	A	
3D	DEC	A	
3E	LD	A,n	
3F	CCF		
40	LD	B,B	
41	LD	B,C	
42	LD	B,D	
43	LD	B,E	
44	LD	B,H	
45	LD	B,L	
46	LD	B,(HL)	
47	LD	B,A	
48	LD	C,B	
49	LD	C,C	
4A	LD	C,D	
4B	LD	C,E	
4C	LD	C,H	
4D	LD	C,L	
4E	LD	C,(HL)	
4F	LD	C,A	
50	LD	D,B	
51	LD	D,C	
52	LD	D,D	
53	LD	D,E	
54	LD	D,H	
55	LD	D,L	
56	LD	D,(HL)	
57	LD	D,A	
58	LD	E,B	
59	LD	E,C	
5A	LD	E,D	
5B	LD	E,E	
5C	LD	E,H	
5D	LD	E,L	
5E	LD	E,(HL)	
5F	LD	E,A	
60	LD	H,B	
61	LD	H,C	
62	LD	H,D	
63	LD	H,E	
64	LD	H,H	
65	LD	H,L	
66	LD	H,(HL)	
67	LD	H,A	
68	LD	L,B	
69	LD	L,C	
6A	LD	L,D	
6B	LD	L,E	
6C	LD	L,H	
6D	LD	L,L	
6E	LD	L,(HL)	
6F	LD	L,A	
70	LD	(HL),B	
71	LD	(HL),C	
72	LD	(HL),D	
73	LD	(HL),E	
74	LD	(HL),H	
75	LD	(HL),L	
76	HALT		
77	LD	(HL),A	
78	LD	A,B	
79	LD	A,C	
7A	LD	A,D	
7B	LD	A,E	
7C	LD	A,H	
7D	LD	A,L	
7E	LD	A,(HL)	
7F	LD	A,A	
80	ADD	A,B	
81	ADD	A,C	
82	ADD	A,D	
83	ADD	A,E	
84	ADD	A,H	
85	ADD	A,L	
86	ADD	A,(HL)	
87	ADD	A,A	
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

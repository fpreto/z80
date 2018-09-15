package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.registers.Register;
import com.pretosmind.emu.z80.registers.RegisterName;
import nl.grauw.glass.Source;
import nl.grauw.glass.SourceBuilder;
import org.junit.After;
import org.junit.Before;

import java.io.*;
import java.util.ArrayList;

import static com.pretosmind.emu.z80.registers.Flags.X_FLAG;
import static com.pretosmind.emu.z80.registers.Flags.Y_FLAG;
import static com.pretosmind.emu.z80.registers.RegisterName.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractZ80Test {

    protected State state;
    protected MemoryTestHelper memory;
    protected Z80 z80;

    private int previousFlag;
    private int cycles;

    @Before
    public void setUp() throws Exception {
        this.memory = new MemoryTestHelper();
        this.state = new State();
        this.z80 = new Z80(memory, state);
        this.cycles = 0;

        previousFlag = state.getRegister(F).read();
    }

    @After
    public void tearDown() throws Exception {
        this.z80 = null;
        this.state = null;
        this.memory = null;
    }

    protected void runUntilHalted() {
        while (!this.state.isHalted()) {
            z80.execute(1);
        }
    }

    protected void assertPC(int value) {
        assertRegister(PC, value);
    }

    protected void assertZeroCycleBalance() {
        assertEquals(0, z80.getCyclesBalance());
    }

    protected void assertAllRegisters(int a, int b, int c, int d, int e, int h, int l) {
        assertRegister(A, a);
        assertRegister(B, b);
        assertRegister(C, c);
        assertRegister(D, d);
        assertRegister(E, e);
        assertRegister(H, h);
        assertRegister(L, l);
    }

    protected void assertRegister(RegisterName name, int value) {
        Register register = state.getRegister(name);
        assertEquals(value, register.read());
    }

    protected void assertFlagUnchanged() {
        int currentFlag = state.getRegister(F).read();
        assertEquals(previousFlag, currentFlag);
        previousFlag = currentFlag;
    }

    protected void assertFlag(int flagsSet, int flagsUnset, int flagsUntouched, int xy) {
        int currentFlag = state.getRegister(F).read();

        assertTrue("Invalid flags. Missing required set flags. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & flagsSet) == flagsSet));
        assertTrue("Invalid flags. Flags set while required to not be. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & flagsUnset) == 0));
        assertTrue("Invalid flags. Flags change while required to be preserved. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & flagsUntouched) == (previousFlag & flagsUntouched)));
        assertTrue("Invalid flags. XY not properly set. F=" + Integer.toBinaryString(currentFlag), ((currentFlag & (X_FLAG | Y_FLAG)) == (xy & (X_FLAG | Y_FLAG))));


        previousFlag = currentFlag;
    }


    protected void assembleStr(String... sourceLines) {
        StringBuilder builder = new StringBuilder();
        for (String lineText : sourceLines)
            builder.append(lineText).append("\n");
        SourceBuilder sourceBuilder = new SourceBuilder(new ArrayList<File>());
        Source source = sourceBuilder.parse(new StringReader(builder.toString()), null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            source.assemble(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.memory.load(output.toByteArray());
    }

    protected void assemble(File file) {
        SourceBuilder sourceBuilder = new SourceBuilder(new ArrayList<File>());
        Source source = null;
        try {
            source = sourceBuilder.parse(new FileInputStream(file), file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            source.assemble(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.memory.load(output.toByteArray());

    }

    protected String getAssemblePath() {
        return "resources/tests";
    }

    protected void assemble(String file) {
        assemble(new File(getAssemblePath(), file));
    }

}

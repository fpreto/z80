package com.pretosmind.emu.z80;

import com.pretosmind.emu.z80.mmu.Memory;

import static org.junit.Assert.fail;


public final class MemoryTestHelper implements Memory {

    private final int[] data;
    private int putAt;

    public MemoryTestHelper() {

        this.data = new int[0x10000];
        this.putAt = 0;

    }

    @Override
    public int read(int address) {
        return data[address];
    }

    @Override
    public void write(int address, int value) {

        if (value != Z80Utils.mask8bit(value)) {
            fail("Trying to write invalid data in memory. Address=" + Integer.toHexString(address) + ", value=" + Integer.toHexString(value));
        }

        data[address] = value;
    }

    public void put(int... data) {
        for (int d : data) {
            write(putAt++, d);
        }
    }

    public void load(byte[] data) {
        for (byte b : data) {
            put(((int) b) & 0xFF);
        }
    }

}

package com.pretosmind.emu.z80.registers;

public class Flags {

    public final static int CARRY_FLAG = 0x01;
    public final static int NEGATIVE_FLAG = 0x02;
    public final static int PARITY_FLAG = 0x04;
    public final static int X_FLAG = 0x08;
    public final static int HALF_CARRY_FLAG = 0x10;
    public final static int Y_FLAG = 0x20;
    public final static int ZERO_FLAG = 0x40;
    public final static int SIGNIFICANT_FLAG = 0x80;

    /**
     * Set/Unset a flag (or a set of flags) in the flag register
     *
     * @param r    reference to register flag
     * @param flag flags to set
     * @param set  if true will set, unset otherwise
     */
    public final static void setFlag(Register r, int flag, boolean set) {
        final int currentFlags = r.read();

        if (set) {
            r.write(currentFlags | flag);
        } else {
            r.write(currentFlags & ~(flag));
        }
    }

    /**
     * Get if a flag or set of flags are set in the flag register
     *
     * @param r    reference to the flag register
     * @param flag flags to check
     * @return true if all flags passed in flag param are set
     */
    public final static boolean getFlag(Register r, int flag) {

        final int currentFlags = r.read();

        return ((currentFlags & flag) == flag);

    }

    /**
     * Copy all flags from a value. This will apply the flag as mask on the
     * value and set on the flag register. After the operation, for each flag
     * passed on the flag param the bit will match the value and for the other
     * values will remain untouched on the flag register.
     *
     * @param r     reference to the flag register
     * @param flag  flags to copy from value
     * @param value a value that will be used as reference for the flags
     */
    public final static void copyFrom(Register r, int flag, int value) {
        final int currentFlag = r.read() & ~(flag);
        r.write(currentFlag | (value & flag));
    }

}

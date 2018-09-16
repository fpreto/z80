package com.pretosmind.emu.z80.instructions;

import com.pretosmind.emu.z80.registers.Flags;
import com.pretosmind.emu.z80.registers.Register;

/**
 * This condition is always true
 *
 * @author fpreto
 */
public class ConditionFlag implements Condition {

    private final Register r;
    private final int flag;
    private final boolean negate;

    public ConditionFlag(Register r, int flag, boolean negate) {
        this.r = r;
        this.flag = flag;
        this.negate = negate;
    }

    @Override
    public boolean conditionMet() {
        if (!negate) {
            return Flags.getFlag(r, flag);
        } else {
            return !Flags.getFlag(r, flag);
        }
    }

    @Override
    public String toString() {

        return ((negate) ?  "N" : "") + Flags.toString(flag);

    }
}

package boq.utils.misc;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;

public enum PlayerOrientation {
    SOUTH, WEST, NORTH, EAST;

    public final static PlayerOrientation[] values = values();

    public static PlayerOrientation getEntityOrientation(Entity entity) {
        int rotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        return values[rotation];
    }

    public ForgeDirection toDirection() {
        switch (this) {
            case EAST:
                return ForgeDirection.EAST;
            case WEST:
                return ForgeDirection.WEST;
            case NORTH:
                return ForgeDirection.NORTH;
            case SOUTH:
                return ForgeDirection.SOUTH;
        }

        throw new IllegalArgumentException("Unhandler enum value");
    }
}

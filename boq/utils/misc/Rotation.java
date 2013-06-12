package boq.utils.misc;

public enum Rotation {
    R0, R90, R180, R270;

    public final static Rotation[] values = values();

    public static Rotation playerToSwitchOrientation(PlayerOrientation player) {
        switch (player) {
            case NORTH:
                return R0;
            case EAST:
                return R90;
            case SOUTH:
                return R180;
            case WEST:
                return R270;
        }

        return null;
    }
}

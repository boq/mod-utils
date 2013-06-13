package boq.utils.misc;

public enum Rotation {
    R0, R90, R180, R270;

    public final static Rotation[] values = values();

    public static Rotation playerToSwitchOrientation(PlayerOrientation player, boolean clockwise) {
        switch (player) {
            case NORTH:
                return R0;
            case EAST:
                return clockwise ? R90 : R270;
            case SOUTH:
                return R180;
            case WEST:
                return clockwise ? R270 : R90;
        }

        return null;
    }

    public Rotation opposite() {
        switch (this) {
            case R0:
                return R180;
            case R90:
                return R270;
            case R180:
                return R0;
            case R270:
                return R90;
            default:
                throw new RuntimeException("Invalid enum value");
        }
    }
}

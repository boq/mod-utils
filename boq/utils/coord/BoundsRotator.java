package boq.utils.coord;

import net.minecraftforge.common.ForgeDirection;
import boq.utils.misc.Rotation;

public class BoundsRotator {

    private BoundsRotator() {}

    private static void rotX(Bounds bounds, double minY, double minZ, double maxY, double maxZ) {
        bounds.minY = minY;
        bounds.minZ = minZ;

        bounds.maxY = maxY;
        bounds.maxZ = maxZ;
        bounds.correct();
    }

    private static void rotY(Bounds bounds, double minX, double minZ, double maxX, double maxZ) {
        bounds.minX = minX;
        bounds.minZ = minZ;

        bounds.maxX = maxX;
        bounds.maxZ = maxZ;
        bounds.correct();
    }

    private static void rotZ(Bounds bounds, double minX, double minY, double maxX, double maxY) {
        bounds.minX = minX;
        bounds.minY = minY;

        bounds.maxX = maxX;
        bounds.maxY = maxY;
        bounds.correct();
    }

    // rotate clockwise around Z axis, R0 - +X, +Y
    public static void rotate(Bounds b, Rotation rotation) {
        switch (rotation) {
            case R0:
                break;
            case R90:
                rotZ(b, b.minY, 1 - b.minX, b.maxY, 1 - b.maxX);
                break;
            case R180:
                rotZ(b, 1 - b.minX, 1 - b.minY, 1 - b.maxX, 1 - b.maxY);
                break;
            case R270:
                rotZ(b, 1 - b.minY, b.minX, 1 - b.maxY, b.maxX);
                break;
        }
    }

    public static void rotate(Bounds b, ForgeDirection direction) {
        switch (direction) {
            case SOUTH:
                break;
            case WEST:
                rotY(b, 1 - b.minZ, b.minX, 1 - b.maxZ, b.maxX);
                break;
            case NORTH:
                rotY(b, 1 - b.minX, 1 - b.minZ, 1 - b.maxX, 1 - b.maxZ);
                break;
            case EAST:
                rotY(b, b.minZ, 1 - b.minX, b.maxZ, 1 - b.maxX);
                break;
            case UP:
                rotX(b, b.minZ, 1 - b.minY, b.maxZ, 1 - b.maxY);
                break;
            case DOWN:
                rotX(b, 1 - b.minZ, b.minY, 1 - b.maxZ, b.maxY);
                break;
            case UNKNOWN:
                throw new IllegalArgumentException();
        }
    }

}

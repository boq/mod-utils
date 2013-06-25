package boq.utils.render;

import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import boq.utils.log.Log;
import boq.utils.misc.Rotation;

public class CenterRotationHelper {

    public static void setupSide(ForgeDirection side) {
        switch (side) {
            case UP:
                GL11.glRotatef(-90, 1, 0, 0);
                break;
            case DOWN:
                GL11.glRotatef(90, 1, 0, 0);
                break;
            case NORTH:
                GL11.glRotatef(180, 0, 1, 0);
                break;
            case SOUTH:
                break;
            case EAST:
                GL11.glRotatef(90, 0, 1, 0);
                break;
            case WEST:
                GL11.glRotatef(-90, 0, 1, 0);
                break;
            default:
                Log.warning("Invalid side while rendering @ RenderHelper.CenterRotationHelper");
                break;
        }
    }

    public static void setupRotation(Rotation rotation) {
        switch (rotation) {
            case R0:
                break;
            case R90:
                GL11.glRotatef(90, 0, 0, 1);
                break;
            case R180:
                GL11.glRotatef(180, 0, 0, 1);
                break;
            case R270:
                GL11.glRotatef(270, 0, 0, 1);
                break;
        }
    }
}

package boq.utils.render;

import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import boq.utils.log.Log;
import boq.utils.misc.Rotation;

public class RotationHelper {

    public static void setupSide(ForgeDirection side) {
        switch (side) {
            case DOWN:
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glTranslatef(0, 0, -1);
                break;
            case UP:
                GL11.glRotatef(-90, 1, 0, 0);
                GL11.glTranslatef(0, -1, 0);
                break;
            case NORTH:
                GL11.glRotatef(180, 0, 1, 0);
                GL11.glTranslatef(-1, 0, -1);
                break;
            case SOUTH:
                break;
            case EAST:
                GL11.glRotatef(90, 0, 1, 0);
                GL11.glTranslatef(-1, 0, 0);
                break;
            case WEST:
                GL11.glRotatef(-90, 0, 1, 0);
                GL11.glTranslatef(0, 0, -1);
                break;
            default:
                Log.warning("Invalid side while rendering @ RenderHelper.setupSide");
                break;
        }
    }

    public static void setupRotation(Rotation rotation) {
        switch (rotation) {
            case R0:
                break;
            case R90:
                GL11.glRotatef(-90, 0, 0, 1);
                GL11.glTranslatef(-1, 0, 0);
                break;
            case R180:
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glTranslatef(-1, -1, 0);
                break;
            case R270:
                GL11.glRotatef(90, 0, 0, 1);
                GL11.glTranslatef(0, -1, 0);
                break;
        }
    }

}

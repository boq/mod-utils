package boq.utils.render;

import java.util.*;

import net.minecraft.client.renderer.GLAllocation;

import org.lwjgl.opengl.GL11;

public abstract class ParameterModel<T> {
    private Map<T, Integer> lists = new HashMap<T, Integer>();

    public abstract void compile(T param);

    public void render(T param) {
        Integer displayList = lists.get(param);
        if (displayList == null) {
            displayList = GLAllocation.generateDisplayLists(1);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glNewList(displayList, GL11.GL_COMPILE);
            compile(param);
            GL11.glEndList();
            GL11.glPopMatrix();
            put(param, displayList);
        }
        GL11.glCallList(displayList);
    }

    @Override
    protected void finalize() throws Throwable {
        for (Integer list : lists.values())
            GL11.glDeleteLists(list, 1);
    }

    private synchronized void put(T param, int displayList) {
        lists.put(param, displayList);
    }

    public synchronized void flush() {
        Iterator<Integer> it = lists.values().iterator();
        while (it.hasNext()) {
            GL11.glDeleteLists(it.next(), 1);
            it.remove();
        }
    }

}

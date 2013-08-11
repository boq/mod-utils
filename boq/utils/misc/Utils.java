package boq.utils.misc;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

public class Utils {
    public static final Random RANDOM = new Random();

    public static void dropItem(World world, Vec3 vec, ItemStack is) {
        dropItem(world, vec.xCoord, vec.yCoord, vec.zCoord, is);
    }

    public static EntityItem createDrop(Entity dropper, ItemStack is) {
        return createEntityItem(dropper.worldObj, dropper.posX, dropper.posY, dropper.posZ, is);
    }

    public static EntityItem createEntityItem(World world, double x, double y, double z, ItemStack is) {
        EntityItem item = new EntityItem(world, x, y, z, is.copy());
        double f3 = 0.05;
        item.motionX = RANDOM.nextGaussian() * f3;
        item.motionY = RANDOM.nextGaussian() * f3 + 0.2;
        item.motionZ = RANDOM.nextGaussian() * f3;
        return item;
    }

    public static void dropItem(World world, double x, double y, double z, ItemStack is) {
        EntityItem item = createEntityItem(world, x, y, z, is);
        world.spawnEntityInWorld(item);
    }

    public static Object[] wrap(Object... args) {
        return args;
    }

    public static int toInt(Object obj) {
        Preconditions.checkNotNull(obj, "Expected number but got null");

        if (obj instanceof Number)
            return ((Number)obj).intValue();

        return Integer.parseInt(obj.toString());
    }

    public final static Object[] FALSE = wrap(false);
    public final static Object[] TRUE = wrap(true);

    public static boolean checkArg(Object[] args, int pos) {
        return args.length > pos && args[pos] != null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends NBTBase> T getTag(NBTTagCompound tag, String key) {
        NBTBase t = tag.getTag(key);
        return (T)t;
    }

    public static NBTTagCompound getItemTag(ItemStack stack) {
        NBTTagCompound result = stack.getTagCompound();

        if (result == null) {
            result = new NBTTagCompound("tag");
            stack.setTagCompound(result);
        }

        return result;
    }
}

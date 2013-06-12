package boq.utils.net;

import static boq.utils.net.BlockHelper.*;

import java.io.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.liquids.LiquidStack;
import boq.utils.net.BlockHelper.DataBlockReader;
import boq.utils.net.BlockHelper.DataBlockWriter;

public final class StreamHelper {

    private StreamHelper() {}

    public static NBTBase readNBTTag(DataInput input, boolean decompress) throws IOException {
        return readBlock(input, decompress, new DataBlockReader<NBTBase>() {

            @Override
            public NBTBase read(DataInput input) throws IOException {
                return NBTBase.readNamedTag(input);
            }
        });
    }

    public static void writeNBTTag(final NBTBase tag, DataOutput output, boolean compress) throws IOException {
        if (tag != null)
            writeBlock(output, compress, new DataBlockWriter() {
                @Override
                public void write(DataOutput data) throws IOException {
                    NBTBase.writeNamedTag(tag, data);
                }
            });
        else
            writeNullBlock(output);
    }

    public static ItemStack readItemStack(DataInput input, boolean compressed) throws IOException {
        return readBlock(input, compressed, new DataBlockReader<ItemStack>() {

            @Override
            public ItemStack read(DataInput input) throws IOException {
                int itemId = input.readShort();
                byte count = input.readByte();
                int damage = input.readShort();
                ItemStack stack = new ItemStack(itemId, count, damage);
                boolean hasTag = input.readBoolean();
                if (hasTag)
                    stack.stackTagCompound = (NBTTagCompound)NBTBase.readNamedTag(input);
                return stack;
            }
        });
    }

    public static void writeItemStack(final ItemStack stack, DataOutput output, boolean compress) throws IOException {
        if (stack != null)
            writeBlock(output, compress, new DataBlockWriter() {
                @Override
                public void write(DataOutput output) throws IOException {
                    output.writeShort(stack.itemID);
                    output.writeByte(stack.stackSize);
                    output.writeShort(stack.getItemDamage());
                    boolean hasTag = stack.hasTagCompound();
                    output.writeBoolean(hasTag);
                    if (hasTag)
                        NBTBase.writeNamedTag(stack.stackTagCompound, output);
                }
            });
        else
            writeNullBlock(output);
    }

    public static LiquidStack readLiquidStack(DataInput input) throws IOException {
        int itemId = input.readShort();
        int amount = input.readInt();
        int itemMeta = input.readShort();
        return new LiquidStack(itemId, amount, itemMeta);
    }

    public static void writeLiquidStack(LiquidStack stack, DataOutput output) throws IOException {
        output.writeShort(stack.itemID);
        output.writeInt(stack.amount);
        output.writeShort(stack.itemMeta);
    }

    public static Vec3 readVec3(DataInput input) throws IOException {
        double x = input.readDouble();
        double y = input.readDouble();
        double z = input.readDouble();
        return Vec3.createVectorHelper(x, y, z);
    }

    public static void writeVec3(Vec3 o, DataOutput output) throws IOException {
        output.writeDouble(o.xCoord);
        output.writeDouble(o.yCoord);
        output.writeDouble(o.zCoord);
    }

    public static void writeVLI(DataOutput output, int value) throws IOException {
        // I'm not touching signed integers.
        if (value < 0)
            throw new IllegalArgumentException("Value cannot be negative");

        while (true) {
            int b = value & 0x7F;
            int next = value >> 7;
            if (next > 0) {
                b |= 0x80;
                output.writeByte(b);
                value = next;
            } else {
                output.writeByte(b);
                break;
            }
        }
    }

    public static int readVLI(DataInput input) throws IOException {
        int result = 0;
        int shift = 0;
        int b;
        do {
            b = input.readByte();
            result = result | ((b & 0x7F) << shift);
            shift += 7;
        } while (b < 0);

        return result;
    }

    public static class VLIWrap {
        public int value;

        public VLIWrap(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "(wrapped)" + value;
        }

    }

    public static Object wrap(int value) {
        return new VLIWrap(value);
    }
}

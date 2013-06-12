package boq.utils.serializable;

import java.io.*;
import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;

public interface IFieldSerializer {
    public void writeToNBT(Object data, Field field, NBTTagCompound tag, boolean isNullable);

    public void readFromNBT(Object data, Field field, NBTTagCompound tag, boolean isNullable);

    public void writeToStream(Object data, Field field, DataOutput output, boolean isNullable) throws IOException;

    public void readFromStream(Object data, Field field, DataInput input, boolean isNullable) throws IOException;
}

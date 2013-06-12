package boq.utils.serializable;

import java.io.DataInput;
import java.io.DataOutput;

import net.minecraft.nbt.NBTTagCompound;

public interface ISerializableData {

    public void writeToNBT(NBTTagCompound tag);

    public void readFromNBT(NBTTagCompound tag);

    public void writeToStream(DataOutput output);

    public void readFromStream(DataInput input);
}

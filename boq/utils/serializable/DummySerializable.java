package boq.utils.serializable;

import java.io.DataInput;
import java.io.DataOutput;

import net.minecraft.nbt.NBTTagCompound;

public class DummySerializable implements ISerializableData {

    @Override
    public void writeToNBT(NBTTagCompound tag) {}

    @Override
    public void readFromNBT(NBTTagCompound tag) {}

    @Override
    public void writeToStream(DataOutput output) {}

    @Override
    public void readFromStream(DataInput input) {}

}

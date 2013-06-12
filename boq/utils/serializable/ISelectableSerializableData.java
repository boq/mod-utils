package boq.utils.serializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;

public interface ISelectableSerializableData {
    public interface IFieldSelector {
        public boolean canVisit(Field field, int flags);
    }

    public void writeToNBT(NBTTagCompound tag, IFieldSelector selector);

    public void readFromNBT(NBTTagCompound tag, IFieldSelector selector);

    public void writeToStream(DataOutput output, IFieldSelector selector);

    public void readFromStream(DataInput input, IFieldSelector selector);
}

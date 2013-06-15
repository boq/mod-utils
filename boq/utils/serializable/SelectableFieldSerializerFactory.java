package boq.utils.serializable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import boq.utils.serializable.StandardFieldSerializerFactory.ValueSerializer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class SelectableFieldSerializerFactory implements IFieldSerializerFactory {

    private static class Serializer extends ValueSerializer<ISelectableSerializableData> {

        private final Class<? extends ISelectableSerializableData> cls;

        private Serializer(Class<? extends ISelectableSerializableData> cls) {
            this.cls = cls;
        }

        @Override
        public ISelectableSerializableData getDefaultValue() {
            return null;
        }

        @Override
        public void writeToStream(ISelectableSerializableData value, DataOutput output) throws IOException {
            value.writeToStream(output, SerializableData.streamSelector);
        }

        @Override
        public ISelectableSerializableData readFromStream(DataInput input) throws IOException {
            try {
                ISelectableSerializableData data = cls.newInstance();
                data.readFromStream(input, SerializableData.streamSelector);
                return data;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public void writeToNBT(String fieldName, ISelectableSerializableData value, NBTTagCompound tag) {
            NBTTagCompound data = new NBTTagCompound();
            value.writeToNBT(data, SerializableData.nbtSelector);
            tag.setTag(fieldName, data);
        }

        @Override
        public ISelectableSerializableData readFromNBT(String fieldName, NBTTagCompound tag) {
            NBTTagCompound data = tag.getCompoundTag(fieldName);

            try {
                ISelectableSerializableData inst = cls.newInstance();
                inst.readFromNBT(data, SerializableData.nbtSelector);
                return inst;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    private static Map<Class<?>, IFieldSerializer> clsSerializers = Maps.newHashMap();

    @Override
    public IFieldSerializer getSerializer(Field field, boolean isNullable) {
        Class<?> fieldCls = field.getType();
        Preconditions.checkArgument(ISelectableSerializableData.class.isAssignableFrom(fieldCls), "Invalid field type");

        IFieldSerializer result = clsSerializers.get(fieldCls);

        if (result == null) {
            @SuppressWarnings("unchecked")
            Class<? extends ISelectableSerializableData> serializableCls = (Class<? extends ISelectableSerializableData>)fieldCls;
            result = new Serializer(serializableCls);
            clsSerializers.put(fieldCls, result);
        }

        return result;
    }
}

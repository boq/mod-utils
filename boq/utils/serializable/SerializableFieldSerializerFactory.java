package boq.utils.serializable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import boq.utils.serializable.StandardFieldSerializerFactory.ValueSerializer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class SerializableFieldSerializerFactory implements IFieldSerializerFactory {

    private static class Serializer extends ValueSerializer<ISerializableData> {

        private final Class<? extends ISerializableData> cls;

        private Serializer(Class<? extends ISerializableData> cls) {
            this.cls = cls;
        }

        @Override
        public ISerializableData getDefaultValue() {
            return null;
        }

        @Override
        public void writeToStream(ISerializableData value, DataOutput output) throws IOException {
            value.writeToStream(output);
        }

        @Override
        public ISerializableData readFromStream(DataInput input) throws IOException {
            try {
                ISerializableData data = cls.newInstance();
                data.readFromStream(input);
                return data;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public void writeToNBT(String fieldName, ISerializableData value, NBTTagCompound tag) {
            NBTTagCompound data = new NBTTagCompound();
            value.writeToNBT(data);
            tag.setTag(fieldName, data);
        }

        @Override
        public ISerializableData readFromNBT(String fieldName, NBTTagCompound tag) {
            NBTTagCompound data = tag.getCompoundTag(fieldName);

            try {
                ISerializableData inst = cls.newInstance();
                inst.readFromNBT(data);
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
        Preconditions.checkArgument(ISerializableData.class.isAssignableFrom(fieldCls), "Invalid field type");

        IFieldSerializer result = clsSerializers.get(fieldCls);

        if (result == null) {
            @SuppressWarnings("unchecked")
            Class<? extends ISerializableData> serializableCls = (Class<? extends ISerializableData>)fieldCls;
            result = new Serializer(serializableCls);
            clsSerializers.put(fieldCls, result);
        }

        return result;
    }

}

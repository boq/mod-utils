package boq.utils.serializable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import boq.utils.log.Log;
import boq.utils.net.StreamHelper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class StandardFieldSerializerFactory implements IFieldSerializerFactory {

    private static Object getValue(Object data, Field f) {
        try {
            return f.get(data);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void setValue(Object data, Field f, Object value) {
        try {
            f.set(data, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static abstract class ValueSerializer<T> implements IFieldSerializer {
        public abstract T getDefaultValue();

        @Override
        public void writeToStream(Object data, Field field, DataOutput output, boolean isNullable) throws IOException {
            @SuppressWarnings("unchecked")
            T value = (T)getValue(data, field);

            boolean isNull = value == null;

            if (isNullable)
                output.writeBoolean(isNull);

            if (isNull)
                Preconditions.checkArgument(isNullable, "Field %s from class %s is null, but not marked as nullable", field.getName(), field.getDeclaringClass());
            else
                writeToStream(value, output);
        }

        public abstract void writeToStream(T value, DataOutput output) throws IOException;

        @Override
        public void readFromStream(Object data, Field field, DataInput input, boolean isNullable) throws IOException {
            if (isNullable) {
                boolean isNull = input.readBoolean();
                if (isNull) {
                    setValue(data, field, null);
                    return;
                }
            }

            T value = readFromStream(input);
            setValue(data, field, value);
        }

        public abstract T readFromStream(DataInput input) throws IOException;

        @Override
        public void writeToNBT(Object data, Field field, NBTTagCompound tag, boolean isNullable) {
            @SuppressWarnings("unchecked")
            T value = (T)getValue(data, field);

            String fieldName = field.getName();
            if (value == null) {
                Preconditions.checkArgument(isNullable, "Field %s from class %s is null, but not marked as nullable", fieldName, field.getDeclaringClass());
                return;
            }

            writeToNBT(fieldName, value, tag);
        }

        public abstract void writeToNBT(String fieldName, T value, NBTTagCompound tag);

        @Override
        public void readFromNBT(Object data, Field field, NBTTagCompound tag, boolean isNullable) {
            String fieldName = field.getName();

            if (tag.hasKey(fieldName)) {
                T value = readFromNBT(fieldName, tag);
                setValue(data, field, value);
            } else if (isNullable)
                setValue(data, field, null);
            else {
                T value = getDefaultValue();
                Log.warning("Not-nullable field %s is not present in NBT, using default value %s", fieldName, value);
                setValue(data, field, value);
            }
        }

        public abstract T readFromNBT(String fieldName, NBTTagCompound tag);
    }

    private final static IFieldSerializer intSerializer = new ValueSerializer<Integer>() {

        @Override
        public void writeToStream(Integer value, DataOutput output) throws IOException {
            output.writeInt(value);
        }

        @Override
        public Integer readFromStream(DataInput input) throws IOException {
            return input.readInt();
        }

        @Override
        public void writeToNBT(String fieldName, Integer value, NBTTagCompound tag) {
            tag.setInteger(fieldName, value);
        }

        @Override
        public Integer readFromNBT(String fieldName, NBTTagCompound tag) {
            return tag.getInteger(fieldName);
        }

        @Override
        public Integer getDefaultValue() {
            return 0;
        }
    };

    private final static IFieldSerializer boolSerializer = new ValueSerializer<Boolean>() {

        @Override
        public void writeToStream(Boolean value, DataOutput output) throws IOException {
            output.writeBoolean(value);
        }

        @Override
        public Boolean readFromStream(DataInput input) throws IOException {
            return input.readBoolean();
        }

        @Override
        public void writeToNBT(String fieldName, Boolean value, NBTTagCompound tag) {
            tag.setBoolean(fieldName, value);
        }

        @Override
        public Boolean readFromNBT(String fieldName, NBTTagCompound tag) {
            return tag.getBoolean(fieldName);
        }

        @Override
        public Boolean getDefaultValue() {
            return false;
        }

    };

    private final static IFieldSerializer floatSerializer = new ValueSerializer<Float>() {

        @Override
        public void writeToStream(Float value, DataOutput output) throws IOException {
            output.writeFloat(value);
        }

        @Override
        public Float readFromStream(DataInput input) throws IOException {
            return input.readFloat();
        }

        @Override
        public void writeToNBT(String fieldName, Float value, NBTTagCompound tag) {
            tag.setFloat(fieldName, value);
        }

        @Override
        public Float readFromNBT(String fieldName, NBTTagCompound tag) {
            return tag.getFloat(fieldName);
        }

        @Override
        public Float getDefaultValue() {
            return 0.0f;
        }
    };

    private final static IFieldSerializer doubleSerializer = new ValueSerializer<Double>() {

        @Override
        public void writeToStream(Double value, DataOutput output) throws IOException {
            output.writeDouble(value);
        }

        @Override
        public Double readFromStream(DataInput input) throws IOException {
            return input.readDouble();
        }

        @Override
        public void writeToNBT(String fieldName, Double value, NBTTagCompound tag) {
            tag.setDouble(fieldName, value);
        }

        @Override
        public Double readFromNBT(String fieldName, NBTTagCompound tag) {
            return tag.getDouble(fieldName);
        }

        @Override
        public Double getDefaultValue() {
            return 0.0;
        }

    };

    private final static IFieldSerializer stringSerializer = new ValueSerializer<String>() {

        @Override
        public void writeToStream(String value, DataOutput output) throws IOException {
            output.writeUTF(value);
        }

        @Override
        public String readFromStream(DataInput input) throws IOException {
            return input.readUTF();
        }

        @Override
        public void writeToNBT(String fieldName, String value, NBTTagCompound tag) {
            tag.setString(fieldName, value);
        }

        @Override
        public String readFromNBT(String fieldName, NBTTagCompound tag) {
            return tag.getString(fieldName);
        }

        @Override
        public String getDefaultValue() {
            return "";
        }

    };

    private final static IFieldSerializer itemStackSerializer = new ValueSerializer<ItemStack>() {

        @Override
        public ItemStack getDefaultValue() {
            return null;
        }

        @Override
        public void writeToStream(final ItemStack stack, DataOutput output) throws IOException {
            StreamHelper.writeItemStack(stack, output, true);
        }

        @Override
        public ItemStack readFromStream(DataInput input) throws IOException {
            return StreamHelper.readItemStack(input, true);
        }

        @Override
        public void writeToNBT(String fieldName, ItemStack value, NBTTagCompound tag) {
            NBTTagCompound item = new NBTTagCompound();
            value.writeToNBT(item);
            tag.setTag(fieldName, item);
        }

        @Override
        public ItemStack readFromNBT(String fieldName, NBTTagCompound tag) {
            NBTTagCompound item = tag.getCompoundTag(fieldName);
            return ItemStack.loadItemStackFromNBT(item);
        }
    };

    private static class EnumSerializer extends ValueSerializer<Enum<?>> {

        private Enum<?>[] values;

        public EnumSerializer(Class<Enum<?>> enumCls) {
            values = enumCls.getEnumConstants();
        }

        @Override
        public void writeToStream(Enum<?> value, DataOutput output) throws IOException {
            output.writeInt(value.ordinal());
        }

        @Override
        public Enum<?> readFromStream(DataInput input) throws IOException {
            int value = input.readInt();
            return values[value];
        }

        @Override
        public void writeToNBT(String fieldName, Enum<?> value, NBTTagCompound tag) {
            tag.setInteger(fieldName, value.ordinal());
        }

        @Override
        public Enum<?> readFromNBT(String fieldName, NBTTagCompound tag) {
            int value = tag.getInteger(fieldName);
            return values[value];
        }

        @Override
        public Enum<?> getDefaultValue() {
            return values[0];
        }
    }

    private static Map<Class<Enum<?>>, EnumSerializer> enumSerializers = Maps.newHashMap();

    @Override
    public IFieldSerializer getSerializer(Field field, boolean isNullable) {
        Class<?> fieldCls = field.getType();
        Preconditions.checkArgument(!isNullable || !fieldCls.isPrimitive(), "Primitive field cannot be nullable");

        if (fieldCls == Integer.class || fieldCls == int.class)
            return intSerializer;
        else if (fieldCls == String.class)
            return stringSerializer;
        else if (fieldCls == Boolean.class || fieldCls == boolean.class)
            return boolSerializer;
        else if (fieldCls == Float.class || fieldCls == float.class)
            return floatSerializer;
        else if (fieldCls == Double.class || fieldCls == double.class)
            return doubleSerializer;
        else if (fieldCls == ItemStack.class)
            return itemStackSerializer;
        else if (fieldCls.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<Enum<?>> enumCls = (Class<Enum<?>>)fieldCls;

            EnumSerializer serializer = enumSerializers.get(enumCls);

            if (serializer == null) {
                serializer = new EnumSerializer(enumCls);
                enumSerializers.put(enumCls, serializer);
            }

            return serializer;
        }
        else
            throw new IllegalArgumentException(String.format("Class %s of field %s from %s is not serializable", fieldCls, field.getName(), field.getDeclaringClass()));
    }

}

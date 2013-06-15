package boq.utils.serializable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.*;

public class SerializableData implements ISerializableData, ISelectableSerializableData {

    private static ClassToInstanceMap<IFieldSerializerFactory> factories = MutableClassToInstanceMap.create();

    private static Map<Class<?>, FieldEntry[]> serializers = Maps.newIdentityHashMap();

    private interface IFieldVisitor {
        public void visitField(Field field, IFieldSerializer serializer, boolean isNullable);
    }

    private static class FieldEntry {
        public final Field field;
        public final IFieldSerializer serializer;
        public final boolean isNullable;
        public final int flags;

        public FieldEntry(Field field, IFieldSerializer serializer, boolean isNullable, int flags) {
            this.field = field;
            this.serializer = serializer;
            this.isNullable = isNullable;
            this.flags = flags;
        }
    }

    private static IFieldSerializerFactory getFactory(Class<? extends IFieldSerializerFactory> factoryCls) {
        IFieldSerializerFactory factory = factories.getInstance(factoryCls);

        if (factory == null) {
            try {
                factory = factoryCls.newInstance();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }

            @SuppressWarnings("unchecked")
            Class<IFieldSerializerFactory> JAAAAVAAAA = (Class<IFieldSerializerFactory>)factoryCls;
            factories.putInstance(JAAAAVAAAA, factory);
        }

        return factory;
    }

    private static FieldEntry[] getEntryList(Class<?> clazz) {
        FieldEntry[] result = serializers.get(clazz);

        if (result != null)
            return result;

        List<FieldEntry> tmp = Lists.newArrayList();

        Class<?> superCls = clazz.getSuperclass();

        if (superCls != SerializableData.class)
            for (FieldEntry e : getEntryList(superCls))
                tmp.add(e);

        for (Field field : clazz.getDeclaredFields()) {
            SerializableField ann = field.getAnnotation(SerializableField.class);

            if (ann != null) {
                boolean isNullable = ann.nullable();
                int flags = ann.flags();

                Class<? extends IFieldSerializerFactory> factoryCls = ann.serializer();
                IFieldSerializerFactory factory = getFactory(factoryCls);
                IFieldSerializer serializer = factory.getSerializer(field, isNullable);
                field.setAccessible(true);
                FieldEntry entry = new FieldEntry(field, serializer, isNullable, flags);
                tmp.add(entry);
            }
        }

        result = tmp.toArray(new FieldEntry[0]);
        serializers.put(clazz, result);
        return result;
    }

    private void visitFields(IFieldSelector selector, IFieldVisitor visitor) {
        for (FieldEntry e : getEntryList(getClass()))
            if (selector.canVisit(e.field, e.flags))
                visitor.visitField(e.field, e.serializer, e.isNullable);
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag, IFieldSelector selector) {
        visitFields(selector, new IFieldVisitor() {
            @Override
            public void visitField(Field field, IFieldSerializer serializer, boolean isNullable) {
                serializer.writeToNBT(SerializableData.this, field, tag, isNullable);
            }
        });
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag, IFieldSelector selector) {
        visitFields(selector, new IFieldVisitor() {
            @Override
            public void visitField(Field field, IFieldSerializer serializer, boolean isNullable) {
                serializer.readFromNBT(SerializableData.this, field, tag, isNullable);
            }
        });
    }

    @Override
    public void writeToStream(final DataOutput output, IFieldSelector selector) {
        visitFields(selector, new IFieldVisitor() {
            @Override
            public void visitField(Field field, IFieldSerializer serializer, boolean isNullable) {
                try {
                    serializer.writeToStream(SerializableData.this, field, output, isNullable);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void readFromStream(final DataInput input, IFieldSelector selector) {
        visitFields(selector, new IFieldVisitor() {
            @Override
            public void visitField(Field field, IFieldSerializer serializer, boolean isNullable) {
                try {
                    serializer.readFromStream(SerializableData.this, field, input, isNullable);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    final static IFieldSelector nbtSelector = new IFieldSelector() {
        @Override
        public boolean canVisit(Field field, int mask) {
            return (mask & SerializableField.NBT_SERIALIZABLE) != 0;
        }
    };

    final static IFieldSelector streamSelector = new IFieldSelector() {
        @Override
        public boolean canVisit(Field field, int mask) {
            return (mask & SerializableField.STREAM_SERIALIZABLE) != 0;
        }
    };

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        writeToNBT(tag, nbtSelector);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        readFromNBT(tag, nbtSelector);
    }

    @Override
    public void writeToStream(DataOutput output) {
        writeToStream(output, streamSelector);
    }

    @Override
    public void readFromStream(DataInput input) {
        readFromStream(input, streamSelector);
    }

}

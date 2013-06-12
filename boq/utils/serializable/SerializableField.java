package boq.utils.serializable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializableField {

    public final static int NBT_SERIALIZABLE = 1 << 0;
    public final static int STREAM_SERIALIZABLE = 1 << 1;

    public final static int USER_DEFINED_0 = 1 << 8;
    public final static int USER_DEFINED_1 = 1 << 9;

    public final static int SERIALIZABLE = 3;

    public Class<? extends IFieldSerializerFactory> serializer() default StandardFieldSerializerFactory.class;

    public boolean nullable() default false;

    public int flags() default SERIALIZABLE;
}

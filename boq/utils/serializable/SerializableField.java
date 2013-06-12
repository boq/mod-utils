package boq.utils.serializable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerializableField {

    public Class<? extends IFieldSerializerFactory> serializer() default StandardFieldSerializerFactory.class;

    public boolean nullable() default false;
}

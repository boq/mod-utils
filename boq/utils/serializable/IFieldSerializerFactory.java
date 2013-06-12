package boq.utils.serializable;

import java.lang.reflect.Field;

public interface IFieldSerializerFactory {
    public IFieldSerializer getSerializer(Field field, boolean isNullable);
}

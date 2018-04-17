package com.effektif.workflow.mongo;

import com.effektif.workflow.api.model.TaskId;
import com.effektif.workflow.impl.json.*;
import com.effektif.workflow.impl.json.types.AbstractTypeMapper;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

/**
 * zhenghaibo 2018/4/8
 */
public class TaskIdMongoMapper extends AbstractTypeMapper<TaskId> implements JsonTypeMapperFactory {

    @Override
    public JsonTypeMapper createTypeMapper(Type type, Class<?> clazz, Mappings mappings) {
        if (clazz == TaskId.class) {
            return this;
        }
        return null;
    }

    @Override
    public void write(TaskId objectValue, JsonWriter jsonWriter) {
        JsonObjectWriter jsonObjectWriter = (JsonObjectWriter) jsonWriter;
        jsonObjectWriter.writeValue(new ObjectId(objectValue.getInternal()));
    }

    @Override
    public TaskId read(Object jsonValue, JsonReader jsonReader) {
        return new TaskId(jsonValue.toString());
    }
}

package com.effektif.workflow.impl.json.types;

import com.alibaba.fastjson.JSON;
import com.effektif.workflow.api.activities.ExecutionItem;
import com.effektif.workflow.impl.json.*;

import java.lang.reflect.Type;

/**
 * zhenghaibo
 * 2018/4/8 18:02
 */
public class ExecutionItemStreamMapper extends AbstractTypeMapper<ExecutionItem> implements JsonTypeMapperFactory {
    @Override public JsonTypeMapper createTypeMapper(Type type, Class<?> clazz, Mappings mappings) {
        if (clazz == ExecutionItem.class) {
            return this;
        }
        return null;
    }

    @Override public void write(ExecutionItem objectValue, JsonWriter jsonWriter) {
        JsonObjectWriter jsonObjectWriter = (JsonObjectWriter) jsonWriter;
        jsonObjectWriter.writeValue(JSON.toJSON(objectValue));
    }

    @Override public ExecutionItem read(Object jsonValue, JsonReader jsonReader) {
        return JSON.parseObject(jsonValue.toString(), ExecutionItem.class);
    }
}

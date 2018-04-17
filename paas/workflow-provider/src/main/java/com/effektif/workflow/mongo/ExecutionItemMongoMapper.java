package com.effektif.workflow.mongo;

import com.alibaba.fastjson.JSON;
import com.effektif.workflow.api.activities.ExecutionItem;
import com.effektif.workflow.impl.json.*;
import com.effektif.workflow.impl.json.types.AbstractTypeMapper;

import java.lang.reflect.Type;

/**
 * zhenghaibo
 * 2018/4/8 18:27
 */
public class ExecutionItemMongoMapper extends AbstractTypeMapper<ExecutionItem> implements JsonTypeMapperFactory {

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

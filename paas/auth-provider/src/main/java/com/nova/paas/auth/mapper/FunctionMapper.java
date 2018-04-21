package com.nova.paas.auth.mapper;

import com.nova.paas.auth.entity.Function;
import org.apache.ibatis.annotations.Mapper;

/**
 * zhenghaibo
 * 18/4/10 16:00
 */
@Mapper
public interface FunctionMapper {
    void insert(Function function);

    /*************************** separate *********************************/


}

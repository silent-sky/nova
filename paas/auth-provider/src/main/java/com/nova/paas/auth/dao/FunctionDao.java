package com.nova.paas.auth.dao;

import com.nova.paas.auth.entity.Function;
import com.nova.paas.auth.mapper.FunctionMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * zhenghaibo
 * 2018/4/21 17:34
 */
@Repository
public class FunctionDao {
    @Inject
    private FunctionMapper functionMapper;

    public void insert(Function function) {
        functionMapper.insert(function);
    }
}

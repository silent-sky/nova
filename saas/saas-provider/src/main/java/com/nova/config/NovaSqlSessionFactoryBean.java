package com.nova.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class NovaSqlSessionFactoryBean extends SqlSessionFactoryBean {

    protected String valuedEnumBasePackages;

    private Resource[] mapperLocations;

    public void setValuedEnumBasePackages(String valuedEnumBasePackages) {
        this.valuedEnumBasePackages = valuedEnumBasePackages;
    }

    @Override
    public void setMapperLocations(Resource[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    @Override
    protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
        SqlSessionFactory superSqlSessionFactory = super.buildSqlSessionFactory();
        Configuration configuration = superSqlSessionFactory.getConfiguration();
        if (StringUtils.hasLength(valuedEnumBasePackages)) {
            String[] enumPackages = valuedEnumBasePackages.split(",");

            TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
            Set<Class<? extends Valued>> enumClasses = doScanEnumClass(enumPackages);
            for (Class<? extends Valued> cls : enumClasses) {
                registry.register(cls, ValuedEnumTypeHandler.class);
                log.info("ValuedEnumTypeHandler is registered for type " + cls.getName());
            }

            if (!ObjectUtils.isEmpty(mapperLocations)) {
                for (Resource mapperLocation : mapperLocations) {
                    if (mapperLocation == null) {
                        continue;
                    }

                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
                                configuration,
                                mapperLocation.toString(),
                                configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (Exception e) {
                        throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                    } finally {
                        ErrorContext.instance().reset();
                    }

                    log.debug("Parsed mapper file: '" + mapperLocation + "'");
                }
            }
        }
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    protected Set<Class<? extends Valued>> doScanEnumClass(String... enumBasePackages) {
        Set<Class<? extends Valued>> filterdClasses = new HashSet<>();
        ResolverUtil<Valued> resolverUtil = new ResolverUtil<>();
        resolverUtil.findImplementations(Valued.class, enumBasePackages);
        Set<Class<? extends Valued>> handlerSet = resolverUtil.getClasses();
        for (Class<? extends Valued> type : handlerSet) {
            if (type.isEnum()) {
                filterdClasses.add(type);
            }
        }
        return filterdClasses;
    }
}

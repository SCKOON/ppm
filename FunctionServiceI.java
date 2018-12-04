package org.jeecgframework.web.system.service;

import org.jeecgframework.web.system.pojo.base.TSFunction;

public interface FunctionServiceI {

    public void delete(TSFunction function);

    public void save(TSFunction function) throws Exception;

    public void update(TSFunction function)throws Exception;
}

package com.ppms.counterManagement.service.imp;

import com.constants.SiteStatus;
import com.ppms.counterManagement.dao.CounterDao;
import com.ppms.counterManagement.service.CounterServiceI;
import com.ppms.entity.CounterEntity;
import com.ppms.entity.TerminalEntity;
import com.ppms.utils.DataReturn;
import com.ppms.utils.DataSourceValue;
import org.jeecgframework.core.common.exception.BusinessException;
import org.jeecgframework.core.common.hibernate.qbc.CriteriaQuery;
import org.jeecgframework.core.common.model.json.DataGrid;
import org.jeecgframework.core.common.service.impl.CommonServiceImpl;
import org.jeecgframework.core.extend.datasource.DataSourceType;
import org.jeecgframework.core.util.MyBeanUtils;
import org.jeecgframework.core.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("counterService")
@Transactional
@DataSourceValue(DataSourceType.dataSource_ppms)
public class CounterServiceImpl extends CommonServiceImpl implements CounterServiceI {

    @Autowired
    private CounterDao counterDao;


    @Override
    public void batchDelete(String ids) {
        for (String id : ids.split(",")) {
            CounterEntity counterEntity = counterDao.getEntity(CounterEntity.class, Integer.parseInt(id));
            counterDao.delete(counterEntity);
        }
    }

    @Override
    public DataReturn getAllEntities(CounterEntity counterEntity, int page, int rows, HttpServletRequest request) {
        return counterDao.getAllEntities(counterEntity, page, rows, request);
    }

    @Override
    @DataSourceValue(DataSourceType.dataSource_ppms)
    public CounterEntity getEntity(Serializable id) {
        CounterEntity counterEntity = (CounterEntity) counterDao.getSession().get(CounterEntity.class, id);
        return counterEntity;
    }

    @Override
    public void queryListForView(DataGrid grid, HttpServletRequest request) {
        counterDao.queryListForView(grid, request);
    }

    @Override
    public <T> T getEntity(Class entityName, Serializable id) {
        return counterDao.getEntity(entityName, id);
    }

    @Override
    public <T> List<T> getListByCriteriaQuery(CriteriaQuery cq, Boolean ispage) {
        return counterDao.getListByCriteriaQuery(cq, ispage);
    }

    @Override
    public Serializable save(CounterEntity entity) {
        TerminalEntity terminalEntity = getEntity(TerminalEntity.class, entity.getTerminalEntity().getId());
        if (terminalEntity == null) {
            throw new BusinessException("The terminal should relate to a center which exist in the system!");
        }
        if (!SiteStatus.ACTIVE.getStatus().equals(terminalEntity.getStatus())) {
            throw new BusinessException("The center that relate to this terminal should be Actived status!");
        }
        if (oConvertUtils.isEmpty(entity.getCode())) {
            throw new BusinessException("Terminal code should not be empty!");
        }
        if (oConvertUtils.isEmpty(entity.getName())) {
            throw new BusinessException("Terminal name should not be empty!");
        }
        if (oConvertUtils.isEmpty(entity.getcIndex())) {
            throw new BusinessException("Terminal index should not be empty!");
        }
        String ip = entity.getIp();
        if (oConvertUtils.isEmpty(ip)) {
            throw new BusinessException("Terminal ip should not be empty!");
        } else {
            if (oConvertUtils.isNotEmpty(ip)) {
                String ipv4Reg = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$";
                String ipv6Reg = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
                if (!match(ipv4Reg, ip) && !match(ipv6Reg, ip)) {
                    throw new BusinessException("Terminal ip should be valid!");
                }
            }
        }
        String mac = entity.getMacAddr();
        if (oConvertUtils.isNotEmpty(mac)) {
            String macreg = "^[A-F0-9]{2}(-[A-F0-9]{2}){5}$|^[A-F0-9]{2}(:[A-F0-9]{2}){5}$";
            if (!match(macreg, mac)) {
                throw new BusinessException("Terminal Mac Address  should be valid!");
            }
        }
        entity.setTerminalEntity(terminalEntity);
        return counterDao.save(entity);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    @Override
    public void saveOrUpdate(CounterEntity entity) throws Exception {
        TerminalEntity terminalEntity = getEntity(TerminalEntity.class, entity.getTerminalEntity().getId());
        if (terminalEntity == null) {
            throw new BusinessException("The terminal should relate to a center which exist in the system!");
        }
        if (!SiteStatus.ACTIVE.getStatus().equals(terminalEntity.getStatus())) {
            throw new BusinessException("The center that relate to this terminal should be Actived status!");
        }
        if (oConvertUtils.isEmpty(entity.getCode())) {
            throw new BusinessException("Terminal code should not be empty!");
        }
        if (oConvertUtils.isEmpty(entity.getName())) {
            throw new BusinessException("Terminal name should not be empty!");
        }
        if (oConvertUtils.isEmpty(entity.getcIndex())) {
            throw new BusinessException("Terminal index should not be empty!");
        }
        String ip = entity.getIp();
        if (oConvertUtils.isEmpty(ip)) {
            throw new BusinessException("Terminal ip should not be empty!");
        } else {
            if (oConvertUtils.isNotEmpty(ip)) {
                String ipv4Reg = "^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$";
                String ipv6Reg = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
                if (!match(ipv4Reg, ip) && !match(ipv6Reg, ip)) {
                    throw new BusinessException("Terminal ip should be valid!");
                }
            }
        }
        String mac = entity.getMacAddr();
        if (oConvertUtils.isNotEmpty(mac)) {
            String macreg = "^[A-F0-9]{2}(-[A-F0-9]{2}){5}$|^[A-F0-9]{2}(:[A-F0-9]{2}){5}$";
            if (!match(macreg, mac)) {
                throw new BusinessException("Terminal Mac Address  should be valid!");
            }
        }
        CounterEntity t = getEntity(CounterEntity.class, entity.getId());
        try {
            MyBeanUtils.copyBeanNotNull2Bean(entity, t);
        } catch (Exception e) {
            throw new Exception(e);
        }
        t.setTerminalEntity(terminalEntity);
        counterDao.saveOrUpdate(t);
    }

    @Override
    public <T> void delete(T entity) {
        counterDao.delete(entity);
    }
}
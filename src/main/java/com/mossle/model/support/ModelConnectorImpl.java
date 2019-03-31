package com.mossle.model.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;

import com.mossle.model.persistence.domain.ModelBase;
import com.mossle.model.persistence.domain.ModelItem;
import com.mossle.model.persistence.manager.ModelBaseManager;
import com.mossle.model.persistence.manager.ModelItemManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.transaction.annotation.Transactional;

public class ModelConnectorImpl implements ModelConnector {
    private static Logger logger = LoggerFactory
            .getLogger(ModelConnectorImpl.class);
    private ModelBaseManager modelBaseManager;
    private ModelItemManager modelItemManager;
    private JdbcTemplate jdbcTemplate;
    private BeanMapper beanMapper = new BeanMapper();

    public ModelInfoDTO findByCode(String code) {
        ModelBase modelBase = modelBaseManager.findUniqueBy("code", code);

        if (modelBase == null) {
            return null;
        }

        ModelInfoDTO modelInfoDto = new ModelInfoDTO();
        beanMapper.copy(modelBase, modelInfoDto);

        List<ModelItem> modelItems = modelItemManager.find(
                "from ModelItem where modelBase=?", modelBase);

        for (ModelItem modelItem : modelItems) {
            ModelItemDTO modelItemDto = new ModelItemDTO();
            beanMapper.copy(modelItem, modelItemDto);
            modelInfoDto.addItem(modelItemDto);
        }

        List<ModelBase> children = modelBaseManager.find(
                "from ModelBase where modelBase=?", modelBase);

        for (ModelBase child : children) {
            ModelInfoDTO childModelInfoDto = new ModelInfoDTO();
            beanMapper.copy(child, childModelInfoDto);
            modelInfoDto.getInfos().add(childModelInfoDto);

            List<ModelItem> childModelItems = modelItemManager.find(
                    "from ModelItem where modelBase=?", child);

            for (ModelItem childModelItem : childModelItems) {
                ModelItemDTO childModelItemDto = new ModelItemDTO();
                beanMapper.copy(childModelItem, childModelItemDto);
                childModelInfoDto.addItem(childModelItemDto);
            }
        }

        return modelInfoDto;
    }

    /**
     * 保存.
     */
    @Transactional
    public ModelInfoDTO save(ModelInfoDTO modelInfoDto) {
        return this.saveInfo(modelInfoDto, null);
    }

    /**
     * 保存info.
     */
    public ModelInfoDTO saveInfo(ModelInfoDTO modelInfoDto, ModelBase parent) {
        ModelBase modelBase = modelBaseManager.findUniqueBy("code",
                modelInfoDto.getCode());

        if (modelBase == null) {
            modelBase = new ModelBase();
        }

        beanMapper.copy(modelInfoDto, modelBase);

        if (parent != null) {
            modelBase.setModelBase(parent);
        }

        modelBaseManager.save(modelBase);
        modelInfoDto.setId(modelBase.getId());

        if (StringUtils.isBlank(modelBase.getCode())) {
            String code = Long.toString(modelBase.getId());
            modelBase.setCode(code);
            modelBaseManager.save(modelBase);
            modelInfoDto.setCode(code);
        }

        this.saveItems(modelInfoDto.getItems(), modelBase);
        this.saveChildInfos(modelInfoDto.getInfos(), modelBase);

        return modelInfoDto;
    }

    /**
     * 保存items.
     */
    public void saveItems(List<ModelItemDTO> modelItemDtos, ModelBase parent) {
        for (ModelItemDTO modelItemDto : modelItemDtos) {
            String code = modelItemDto.getCode();
            ModelItem modelItem = modelItemManager
                    .findUnique("from ModelItem where modelBase=? and code=?",
                            parent, code);

            if (modelItem == null) {
                modelItem = new ModelItem();
                modelItem.setModelBase(parent);
            }

            beanMapper.copy(modelItemDto, modelItem);
            modelItemManager.save(modelItem);
        }
    }

    /**
     * 保存child info.
     */
    public void saveChildInfos(List<ModelInfoDTO> children, ModelBase parent) {
        if (children.isEmpty()) {
            return;
        }

        List<ModelBase> removed = this.findRemovedInfos(children, parent);

        List<String> removedIds = new ArrayList<String>();

        for (ModelBase modelBase : removed) {
            modelItemManager.removeAll(modelBase.getModelItems());
            modelBaseManager.remove(modelBase);
            removedIds.add(modelBase.getCode());
        }

        for (ModelInfoDTO child : children) {
            String rowId = this.findRowId(child);

            if (removedIds.contains(rowId)) {
                logger.info("skip : {}", rowId);

                continue;
            }

            // insert or update
            child.setCode(rowId);
            this.saveInfo(child, parent);
        }
    }

    public String findRowId(ModelInfoDTO modelInfoDto) {
        String rowId = modelInfoDto.getCode();

        if (StringUtils.isNotBlank(rowId)) {
            return rowId;
        }

        rowId = modelInfoDto.findItem("_rowId").getValue();

        if (StringUtils.isNotBlank(rowId)) {
            return rowId;
        }

        return null;
    }

    public List<ModelBase> findRemovedInfos(List<ModelInfoDTO> children,
            ModelBase parent) {
        List<String> codes = new ArrayList<String>();

        for (ModelInfoDTO child : children) {
            String rowId = this.findRowId(child);
            codes.add(rowId);
        }

        List<ModelBase> childModelBases = modelBaseManager.findBy("modelBase",
                parent);

        List<ModelBase> removedInfos = new ArrayList<ModelBase>();

        for (ModelBase modelBase : childModelBases) {
            if (!codes.contains(modelBase.getCode())) {
                removedInfos.add(modelBase);
            }
        }

        return removedInfos;
    }

    public Page findDraft(int pageNo, int pageSize, String userId) {
        String hql = "from ModelBase where initiator=? and status=? order by id desc";
        Page page = modelBaseManager.pagedQuery(hql, pageNo, pageSize, userId,
                "draft");

        return page;
    }

    public long findDraftCount(String userId) {
        String hql = "select count(*) from ModelBase where initiator=? and status=?";

        return modelBaseManager.getCount(hql, userId, "draft");
    }

    public void removeDraft(String code) {
        ModelBase modelBase = modelBaseManager.findUniqueBy("code", code);

        if (modelBase == null) {
            return;
        }

        modelItemManager.removeAll(modelBase.getModelItems());
        modelBaseManager.remove(modelBase);
    }

    /**
     * 复制数据.
     */
    public ModelInfoDTO copyModel(ModelInfoDTO original, List<String> fields) {
        ModelInfoDTO dest = new ModelInfoDTO();
        beanMapper.copy(original, dest);
        dest.setCreateTime(new Date());
        dest.setStatus("draft");
        dest.getItemMap().clear();

        for (ModelItemDTO item : original.getItems()) {
            if (fields.contains(item.getCode())) {
                dest.addItem(item);
            }
        }

        return this.save(dest);
    }

    public long findTotalCount(String category, String tenantId, String q) {
        List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();

        if (StringUtils.isNotBlank(q)) {
            for (String text : q.split("\\|")) {
                String name = text.split("=")[0];
                String value = text.split("=")[1];
                propertyFilters.add(new PropertyFilter("LIKES_" + name, value));
            }
        }

        return this.findTotalCount(category, tenantId, propertyFilters);
    }

    public List<Map<String, Object>> findResult(Page page, String category,
            String tenantId, Map<String, String> headers, String q) {
        List<PropertyFilter> propertyFilters = new ArrayList<PropertyFilter>();

        if (StringUtils.isNotBlank(q)) {
            for (String text : q.split("\\|")) {
                String name = text.split("=")[0];
                String value = text.split("=")[1];
                propertyFilters.add(new PropertyFilter("LIKES_" + name, value));
            }
        }

        return this.findResult(page, category, tenantId, headers,
                propertyFilters);
    }

    public long findTotalCount(String category, String tenantId,
            List<PropertyFilter> propertyFilters) {
        String sqlPrefix = null;
        List<Object> params = new ArrayList<Object>();

        if (propertyFilters.isEmpty()) {
            sqlPrefix = "select count(*) from MODEL_BASE r where r.PROCESS_ID=? and r.TENANT_ID=?";
        } else {
            sqlPrefix = "select count(distinct r.ID) from MODEL_BASE r";

            int index = 0;

            for (PropertyFilter propertyFilter : propertyFilters) {
                String propName = "p" + (index++);
                sqlPrefix += (" join MODEL_ITEM " + propName + " on r.ID="
                        + propName + ".BASE_ID and " + propName
                        + ".NAME=? and " + propName + ".VALUE like ?");
                params.add(propertyFilter.getPropertyName());
                params.add("%" + propertyFilter.getMatchValue() + "%");
            }

            sqlPrefix += " where r.PROCESS_ID=? and r.TENANT_ID=?";
        }

        params.add(category);
        params.add(tenantId);

        long totalCount = jdbcTemplate.queryForObject(sqlPrefix, Long.class,
                params.toArray(new Object[0]));

        return totalCount;
    }

    public List<Map<String, Object>> findResult(Page page, String category,
            String tenantId, Map<String, String> headers,
            List<PropertyFilter> propertyFilters) {
        String sqlPrefix = null;
        List<Object> params = new ArrayList<Object>();
        Map<String, String> usedFieldMap = new HashMap<String, String>();

        if (propertyFilters.isEmpty()) {
            sqlPrefix = "select r.ID from MODEL_BASE r";
        } else {
            sqlPrefix = "select r.ID from MODEL_BASE r";

            int index = 0;

            for (PropertyFilter propertyFilter : propertyFilters) {
                String propName = "p" + index;
                sqlPrefix += (" join MODEL_ITEM " + propName + " on r.ID="
                        + propName + ".BASE_ID and " + propName
                        + ".NAME=? and " + propName + ".VALUE like ?");
                params.add(propertyFilter.getPropertyName());
                params.add("%" + propertyFilter.getMatchValue() + "%");
                usedFieldMap.put(propertyFilter.getPropertyName(), propName);
                index++;
            }
        }

        String sqlOrder = null;

        if (page.isOrderEnabled()) {
            String orderBy = page.getOrderBy();
            String order = page.getOrder();

            if (usedFieldMap.containsKey(orderBy)) {
                String propName = usedFieldMap.get(orderBy);
                sqlOrder = " order by " + propName + ".VALUE " + order;
            } else {
                String propName = "p";
                sqlPrefix += (" join MODEL_ITEM " + propName + " on r.ID="
                        + propName + ".BASE_ID and " + propName + ".NAME='"
                        + orderBy + "'");
                sqlOrder = " order by " + propName + ".VALUE " + order;
            }
        }

        sqlPrefix += " where r.PROCESS_ID=? and r.TENANT_ID=?";
        params.add(category);
        params.add(tenantId);

        if (sqlOrder != null) {
            sqlPrefix += sqlOrder;
        }

        String sql = sqlPrefix + " limit " + page.getStart() + ","
                + page.getPageSize();
        logger.debug("sql : {}", sql);

        List<Map<String, Object>> records = jdbcTemplate.queryForList(sql,
                params.toArray(new Object[0]));
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> record : records) {
            Map<String, Object> map = new HashMap<String, Object>();
            list.add(map);

            Long recordId = (Long) record.get("id");
            List<Map<String, Object>> props = jdbcTemplate.queryForList(
                    "select * from MODEL_ITEM where BASE_ID=?", recordId);

            for (Map<String, Object> prop : props) {
                if (headers.containsKey(prop.get("code"))) {
                    map.put((String) prop.get("code"), prop.get("value"));
                }
            }
        }

        return list;
    }

    @Resource
    public void setModelBaseManager(ModelBaseManager modelBaseManager) {
        this.modelBaseManager = modelBaseManager;
    }

    @Resource
    public void setModelItemManager(ModelItemManager modelItemManager) {
        this.modelItemManager = modelItemManager;
    }

    @Resource
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

package com.mossle.model.support;

import java.util.List;

import javax.annotation.Resource;

import com.mossle.api.model.ModelConnector;
import com.mossle.api.model.ModelInfoDTO;
import com.mossle.api.model.ModelItemDTO;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

import com.mossle.model.persistence.domain.ModelBase;
import com.mossle.model.persistence.domain.ModelItem;
import com.mossle.model.persistence.manager.ModelBaseManager;
import com.mossle.model.persistence.manager.ModelItemManager;

import org.apache.commons.lang3.StringUtils;

public class ModelConnectorImpl implements ModelConnector {
    private ModelBaseManager modelBaseManager;
    private ModelItemManager modelItemManager;
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
            modelInfoDto.getItems().add(modelItemDto);
        }

        return modelInfoDto;
    }

    public ModelInfoDTO save(ModelInfoDTO modelInfoDto) {
        ModelBase modelBase = modelBaseManager.findUniqueBy("code",
                modelInfoDto.getCode());

        if (modelBase == null) {
            modelBase = new ModelBase();
        }

        beanMapper.copy(modelInfoDto, modelBase);
        modelBaseManager.save(modelBase);
        modelInfoDto.setId(modelBase.getId());

        if (StringUtils.isBlank(modelBase.getCode())) {
            String code = Long.toString(modelBase.getId());
            modelBase.setCode(code);
            modelBaseManager.save(modelBase);
            modelInfoDto.setCode(code);
        }

        for (ModelItemDTO modelItemDto : modelInfoDto.getItems()) {
            String code = modelItemDto.getCode();
            ModelItem modelItem = modelItemManager.findUnique(
                    "from ModelItem where modelBase=? and code=?", modelBase,
                    code);

            if (modelItem == null) {
                modelItem = new ModelItem();
                modelItem.setModelBase(modelBase);
            }

            beanMapper.copy(modelItemDto, modelItem);
            modelItemManager.save(modelItem);
        }

        return modelInfoDto;
    }

    public Page findDraft(int pageNo, int pageSize, String userId) {
        String hql = "from ModelBase where initiator=? and status=? order by id desc";
        Page page = modelBaseManager.pagedQuery(hql, pageNo, pageSize, userId,
                "draft");

        return page;
    }

    public void removeDraft(String code) {
        ModelBase modelBase = modelBaseManager.findUniqueBy("code", code);

        if (modelBase == null) {
            return;
        }

        modelItemManager.removeAll(modelBase.getModelItems());
        modelBaseManager.remove(modelBase);
    }

    @Resource
    public void setModelBaseManager(ModelBaseManager modelBaseManager) {
        this.modelBaseManager = modelBaseManager;
    }

    @Resource
    public void setModelItemManager(ModelItemManager modelItemManager) {
        this.modelItemManager = modelItemManager;
    }
}

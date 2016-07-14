package com.mossle.portal.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.spring.MessageHelper;

import com.mossle.portal.persistence.domain.PortalInfo;
import com.mossle.portal.persistence.domain.PortalItem;
import com.mossle.portal.persistence.domain.PortalRef;
import com.mossle.portal.persistence.domain.PortalWidget;
import com.mossle.portal.persistence.manager.PortalInfoManager;
import com.mossle.portal.persistence.manager.PortalItemManager;
import com.mossle.portal.persistence.manager.PortalRefManager;
import com.mossle.portal.persistence.manager.PortalWidgetManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("portal")
public class PortalController {
    private static Logger logger = LoggerFactory
            .getLogger(PortalController.class);
    private PortalWidgetManager portalWidgetManager;
    private PortalInfoManager portalInfoManager;
    private PortalItemManager portalItemManager;
    private PortalRefManager portalRefManager;
    private BeanMapper beanMapper = new BeanMapper();
    private MessageHelper messageHelper;
    private CurrentUserHolder currentUserHolder;
    private TenantHolder tenantHolder;

    @RequestMapping("index")
    public String index(Model model) {
        String userId = currentUserHolder.getUserId();
        PortalRef portalRef = this.createOrGetPortalRef(userId);

        if (portalRef == null) {
            return "portal/index";
        }

        PortalInfo portalInfo = portalRef.getPortalInfo();

        List<Integer> columnIndexes = portalItemManager
                .find("select distinct columnIndex from PortalItem where portalInfo=? order by columnIndex",
                        portalInfo);
        logger.debug("columnIndexes : {}", columnIndexes);

        if (!columnIndexes.contains(Integer.valueOf(1))) {
            columnIndexes.add(Integer.valueOf(1));
        }

        if (!columnIndexes.contains(Integer.valueOf(2))) {
            columnIndexes.add(Integer.valueOf(2));
        }

        if (!columnIndexes.contains(Integer.valueOf(3))) {
            columnIndexes.add(Integer.valueOf(3));
        }

        Collections.sort(columnIndexes);

        Map<Integer, List<PortalItem>> map = new LinkedHashMap<Integer, List<PortalItem>>();

        for (Integer columnIndex : columnIndexes) {
            List<PortalItem> portalItems = portalItemManager
                    .find("from PortalItem where portalInfo=? and columnIndex=? order by rowIndex",
                            portalInfo, columnIndex);
            map.put(columnIndex, portalItems);
        }

        model.addAttribute("map", map);

        List<PortalWidget> portalWidgets = portalWidgetManager.getAll();
        model.addAttribute("portalWidgets", portalWidgets);

        return "portal/index";
    }

    @RequestMapping("save")
    public String save(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("portalWidgetId") Long portalWidgetId,
            @RequestParam("portalItemName") String portalItemName) {
        String userId = currentUserHolder.getUserId();
        PortalInfo portalInfo = this.copyOrGetPortalInfo(userId);

        PortalWidget portalWidget = portalWidgetManager.get(portalWidgetId);
        PortalItem portalItem = null;

        if (id == null) {
            portalItem = new PortalItem();

            Integer columnIndex = (Integer) portalItemManager
                    .findUnique(
                            "select min(columnIndex) from PortalItem where portalInfo=?",
                            portalInfo);

            if (columnIndex == null) {
                columnIndex = 0;
            }

            Long rowIndexLong = (Long) portalItemManager
                    .findUnique(
                            "select count(*) from PortalItem where portalInfo=? and columnIndex=?",
                            portalInfo, columnIndex);

            if (rowIndexLong == null) {
                rowIndexLong = 0L;
            }

            int rowIndex = rowIndexLong.intValue();
            portalItem.setColumnIndex(columnIndex);
            portalItem.setRowIndex(rowIndex);
            portalItem.setPortalInfo(portalInfo);
        } else {
            portalItem = this.createOrGetPortalItem(portalInfo, id);
        }

        portalItem.setName(portalItemName);
        portalItem.setPortalWidget(portalWidget);
        portalItemManager.save(portalItem);

        return "redirect:/portal/index.do";
    }

    @RequestMapping("remove")
    public String remove(@RequestParam("id") Long id) {
        String userId = currentUserHolder.getUserId();
        PortalInfo portalInfo = this.copyOrGetPortalInfo(userId);
        PortalItem portalItem = this.createOrGetPortalItem(portalInfo, id);
        portalItemManager.remove(portalItem);

        return "redirect:/portal/index.do";
    }

    @RequestMapping("updateOrder")
    public String updateOrder(@RequestParam("ids") List<Long> ids,
            @RequestParam("priorities") List<String> priorities) {
        String userId = currentUserHolder.getUserId();
        PortalInfo portalInfo = this.copyOrGetPortalInfo(userId);
        int index = 0;

        for (Long id : ids) {
            PortalItem portalItem = this.createOrGetPortalItem(portalInfo, id);
            String[] array = priorities.get(index).split(":");
            int columnIndex = Integer.parseInt(array[0]);
            int rowIndex = Integer.parseInt(array[1]);
            portalItem.setColumnIndex(columnIndex);
            portalItem.setRowIndex(rowIndex);
            portalItemManager.save(portalItem);
            index++;
        }

        return "redirect:/portal/index.do";
    }

    public PortalRef createOrGetPortalRef(String userId) {
        PortalRef portalRef = portalRefManager.findUniqueBy("userId", userId);

        if (portalRef == null) {
            PortalInfo portalInfo = portalInfoManager.findUniqueBy(
                    "globalStatus", "true");

            if (portalInfo == null) {
                return null;
            }

            portalRef = new PortalRef();
            portalRef.setPortalInfo(portalInfo);
            portalRef.setUserId(userId);
            portalRefManager.save(portalRef);
        }

        return portalRef;
    }

    public PortalInfo copyOrGetPortalInfo(String userId) {
        PortalRef portalRef = this.createOrGetPortalRef(userId);
        PortalInfo portalInfo = null;

        if (portalRef != null) {
            portalInfo = portalRef.getPortalInfo();

            if (userId.equals(portalInfo.getUserId())) {
                return portalInfo;
            }
        }

        PortalInfo targetPortalInfo = new PortalInfo();

        if (portalInfo != null) {
            beanMapper.copy(portalInfo, targetPortalInfo);
        }

        targetPortalInfo.setUserId(userId);
        targetPortalInfo.setId(null);
        targetPortalInfo.setPortalItems(new HashSet<PortalItem>());
        targetPortalInfo.setPortalRefs(new HashSet<PortalRef>());
        portalInfoManager.save(targetPortalInfo);

        PortalRef targetPortalRef = new PortalRef();
        targetPortalRef.setPortalInfo(targetPortalInfo);
        targetPortalRef.setUserId(userId);
        portalRefManager.save(targetPortalRef);
        portalRefManager.remove(portalRef);

        if (portalInfo != null) {
            for (PortalItem portalItem : portalInfo.getPortalItems()) {
                PortalItem targetPortalItem = new PortalItem();
                beanMapper.copy(portalItem, targetPortalItem);
                targetPortalItem.setPortalInfo(targetPortalInfo);
                targetPortalItem.setId(null);

                portalItemManager.save(targetPortalItem);
            }
        }

        return targetPortalInfo;
    }

    public PortalItem createOrGetPortalItem(PortalInfo portalInfo,
            Long portalItemId) {
        PortalItem portalItem = portalItemManager.get(portalItemId);
        String hql = "from PortalItem where portalInfo=? and columnIndex=? and rowIndex=?";
        PortalItem targetPortalItem = portalItemManager.findUnique(hql,
                portalInfo, portalItem.getColumnIndex(),
                portalItem.getRowIndex());

        return targetPortalItem;
    }

    // ~ ======================================================================
    @Resource
    public void setPortalWidgetManager(PortalWidgetManager portalWidgetManager) {
        this.portalWidgetManager = portalWidgetManager;
    }

    @Resource
    public void setPortalInfoManager(PortalInfoManager portalInfoManager) {
        this.portalInfoManager = portalInfoManager;
    }

    @Resource
    public void setPortalItemManager(PortalItemManager portalItemManager) {
        this.portalItemManager = portalItemManager;
    }

    @Resource
    public void setPortalRefManager(PortalRefManager portalRefManager) {
        this.portalRefManager = portalRefManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}

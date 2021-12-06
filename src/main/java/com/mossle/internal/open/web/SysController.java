package com.mossle.internal.open.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.internal.open.persistence.domain.SysCategory;
import com.mossle.internal.open.persistence.domain.SysEntry;
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.SysCategoryManager;
import com.mossle.internal.open.persistence.manager.SysEntryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("sys")
public class SysController {
    private SysInfoManager sysInfoManager;
    private SysCategoryManager sysCategoryManager;
    private SysEntryManager sysEntryManager;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("index")
    public String index(Model model) {
        List<SysCategory> sysCategories = sysCategoryManager.getAll("priority",
                true);
        model.addAttribute("sysCategories", sysCategories);

        return "sys/index";
    }

    @RequestMapping("view")
    public String view(@RequestParam("id") Long id, Model model) {
        SysInfo sysInfo = this.sysInfoManager.get(id);
        model.addAttribute("sysInfo", sysInfo);

        return "sys/view";
    }

    @RequestMapping("detail")
    public String detail(@RequestParam("id") Long id, Model model) {
        SysInfo sysInfo = this.sysInfoManager.get(id);
        model.addAttribute("sysInfo", sysInfo);

        return "sys/detail";
    }

    @RequestMapping("view-user")
    public String viewUser(Model model) {
        List<SysCategory> sysCategories = sysCategoryManager.getAll("priority",
                true);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (SysCategory sysCategory : sysCategories) {
            Map<String, Object> map = new HashMap<String, Object>();
            String hql = "from SysEntry where sysInfo.sysCategory.id=? and status='active' and type=? order by priority";
            List<SysEntry> sysEntries = this.sysEntryManager.find(hql,
                    sysCategory.getId(), "user");

            if (sysEntries.isEmpty()) {
                continue;
            }

            map.put("sysCategory", sysCategory);
            map.put("sysEntries", sysEntries);
            list.add(map);
        }

        model.addAttribute("list", list);

        return "sys/view-user";
    }

    @RequestMapping("view-admin")
    public String viewAdmin(Model model) {
        List<SysCategory> sysCategories = sysCategoryManager.getAll("priority",
                true);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (SysCategory sysCategory : sysCategories) {
            Map<String, Object> map = new HashMap<String, Object>();
            String hql = "from SysEntry where sysInfo.sysCategory.id=? and status='active' and type=? order by priority";
            List<SysEntry> sysEntries = this.sysEntryManager.find(hql,
                    sysCategory.getId(), "admin");

            if (sysEntries.isEmpty()) {
                continue;
            }

            map.put("sysCategory", sysCategory);
            map.put("sysEntries", sysEntries);
            list.add(map);
        }

        model.addAttribute("list", list);

        return "sys/view-admin";
    }

    @RequestMapping("entry-list")
    public String entryList(@RequestParam("infoId") Long infoId, Model model) {
        SysInfo sysInfo = this.sysInfoManager.get(infoId);
        model.addAttribute("sysInfo", sysInfo);
        model.addAttribute("sysEntries", this.sysEntryManager.find(
                "from SysEntry where sysInfo.id=?", infoId));

        return "sys/entry-list";
    }

    @RequestMapping("entry-input")
    public String entryInput(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("infoId") Long infoId, Model model) {
        SysInfo sysInfo = this.sysInfoManager.get(infoId);
        model.addAttribute("sysInfo", sysInfo);

        if (id != null) {
            SysEntry sysEntry = this.sysEntryManager.get(id);
            model.addAttribute("model", sysEntry);
        }

        return "sys/entry-input";
    }

    @RequestMapping("entry-save")
    public String entryList(SysEntry sysEntry,
            @RequestParam("infoId") Long infoId, Model model) {
        SysInfo sysInfo = this.sysInfoManager.get(infoId);
        model.addAttribute("sysInfo", sysInfo);

        SysEntry dest = null;

        if (sysEntry.getId() != null) {
            dest = new SysEntry();
            beanMapper.copy(sysEntry, dest);
        } else {
            dest = sysEntry;
        }

        dest.setSysInfo(this.sysInfoManager.get(infoId));

        sysEntryManager.save(dest);

        return "redirect:/sys/entry-list.do?infoId=" + infoId;
    }

    // ~ ======================================================================
    @Resource
    public void setSysInfoManager(SysInfoManager sysInfoManager) {
        this.sysInfoManager = sysInfoManager;
    }

    @Resource
    public void setSysCategoryManager(SysCategoryManager sysCategoryManager) {
        this.sysCategoryManager = sysCategoryManager;
    }

    @Resource
    public void setSysEntryManager(SysEntryManager sysEntryManager) {
        this.sysEntryManager = sysEntryManager;
    }
}

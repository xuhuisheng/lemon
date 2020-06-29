package com.mossle.internal.open.web.rs;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.sys.SysCategoryDTO;
import com.mossle.api.sys.SysInfoDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.util.BaseDTO;
import com.mossle.core.util.Select2Info;

import com.mossle.internal.open.persistence.domain.SysCategory;
import com.mossle.internal.open.persistence.domain.SysInfo;
import com.mossle.internal.open.persistence.manager.SysCategoryManager;
import com.mossle.internal.open.persistence.manager.SysInfoManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.util.Assert;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("sys/rs/remote")
public class SysRemoteController {
    private static Logger logger = LoggerFactory
            .getLogger(SysRemoteController.class);
    private SysInfoManager sysInfoManager;
    private SysCategoryManager sysCategoryManager;

    @RequestMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findAll() {
        List<SysCategory> sysCategories = sysCategoryManager.getAll("priority",
                true);
        List<SysCategoryDTO> sysCategoryDtos = this
                .convertCategories(sysCategories);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(sysCategoryDtos);

        return baseDto;
    }

    @RequestMapping(value = "findFavorites", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findFavorites() {
        List<SysInfo> sysInfos = sysInfoManager.getAll("priority", true);
        List<SysInfoDTO> sysInfoDtos = this.convertInfos(sysInfos);
        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(sysInfoDtos);

        return baseDto;
    }

    public List<SysCategoryDTO> convertCategories(
            List<SysCategory> sysCategories) {
        List<SysCategoryDTO> sysCategoryDtos = new ArrayList<SysCategoryDTO>();

        for (SysCategory sysCategory : sysCategories) {
            sysCategoryDtos.add(this.convertCategory(sysCategory));
        }

        return sysCategoryDtos;
    }

    public SysCategoryDTO convertCategory(SysCategory sysCategory) {
        SysCategoryDTO sysCategoryDto = new SysCategoryDTO();
        sysCategoryDto.setCode(sysCategory.getCode());
        sysCategoryDto.setName(sysCategory.getName());

        String hql = "from SysInfo where sysCategory=? order by priority";
        List<SysInfo> sysInfos = this.sysInfoManager.find(hql, sysCategory);
        List<SysInfoDTO> sysInfoDtos = this.convertInfos(sysInfos);
        sysCategoryDto.setChildren(sysInfoDtos);

        return sysCategoryDto;
    }

    public List<SysInfoDTO> convertInfos(List<SysInfo> sysInfos) {
        List<SysInfoDTO> sysInfoDtos = new ArrayList<SysInfoDTO>();

        for (SysInfo sysInfo : sysInfos) {
            sysInfoDtos.add(this.convertInfo(sysInfo));
        }

        return sysInfoDtos;
    }

    public SysInfoDTO convertInfo(SysInfo sysInfo) {
        SysInfoDTO sysInfoDto = new SysInfoDTO();
        sysInfoDto.setCode(sysInfo.getCode());
        sysInfoDto.setName(sysInfo.getName());
        sysInfoDto.setLogo(sysInfo.getLogo());
        sysInfoDto.setUrl(sysInfo.getUrl());

        return sysInfoDto;
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
}

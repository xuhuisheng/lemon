package com.mossle.disk.web;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserConnector;
import com.mossle.api.user.UserDTO;

import com.mossle.core.page.Page;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.domain.DiskShare;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.persistence.manager.DiskShareManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("disk")
public class DiskController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskController.class);
    private DiskShareManager diskShareManager;
    private DiskInfoManager diskInfoManager;
    private StoreConnector storeConnector;
    private TenantHolder tenantHolder;
    private UserConnector userConnector;

    /**
     * 首页.
     */
    @RequestMapping("disk-home")
    public String home(
            @RequestParam(value = "username", required = false) String username,
            Model model) {
        if (username == null) {
            Page page = diskInfoManager.pagedQuery("from DiskInfo", 1, 10);
            List<DiskInfo> diskInfos = (List<DiskInfo>) page.getResult();
            List<String> userIds = new ArrayList<String>();
            List<UserDTO> userDtos = new ArrayList<UserDTO>();

            for (DiskInfo diskInfo : diskInfos) {
                String userId = diskInfo.getCreator();

                if (userIds.contains(userId)) {
                    continue;
                }

                UserDTO userDto = userConnector.findById(userId);
                userDtos.add(userDto);
            }

            model.addAttribute("userDtos", userDtos);
        } else {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("filter_LIKES_username", username);

            Page page = userConnector.pagedQuery(tenantHolder.getTenantId(),
                    new Page(), parameters);
            model.addAttribute("userDtos", page.getResult());
        }

        return "disk/disk-home";
    }

    /**
     * 列表.
     */
    @RequestMapping("disk-list")
    public String list(@RequestParam("u") String u,
            @RequestParam(value = "path", required = false) String path,
            Model model) {
        if (path == null) {
            path = "";
        }

        String userId = u;

        List<DiskShare> diskShares = diskShareManager.findBy("creator", userId);
        model.addAttribute("diskShares", diskShares);
        model.addAttribute("path", path);

        return "disk/disk-list";
    }

    /**
     * 详情.
     */
    @RequestMapping("disk-view")
    public String view(@RequestParam("id") Long id, Model model) {
        DiskShare diskShare = diskShareManager.get(id);
        model.addAttribute("diskShare", diskShare);

        return "disk/disk-view";
    }

    /**
     * 下载.
     */
    @RequestMapping("disk-download")
    public void download(@RequestParam("id") Long id,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String tenantId = tenantHolder.getTenantId();
        DiskShare diskShare = diskShareManager.get(id);
        DiskInfo diskInfo = diskShare.getDiskInfo();
        InputStream is = null;

        try {
            ServletUtils.setFileDownloadHeader(request, response,
                    diskInfo.getName());
            is = storeConnector
                    .getStore("default/user/" + diskInfo.getCreator(),
                            diskInfo.getRef(), tenantId).getDataSource()
                    .getInputStream();
            IoUtils.copyStream(is, response.getOutputStream());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // ~ ======================================================================
    @Resource
    public void setDiskShareManager(DiskShareManager diskShareManager) {
        this.diskShareManager = diskShareManager;
    }

    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }
}

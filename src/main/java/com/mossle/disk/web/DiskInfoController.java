package com.mossle.disk.web;

import java.io.InputStream;

import java.util.List;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.store.StoreConnector;
import com.mossle.api.tenant.TenantHolder;

import com.mossle.core.auth.CurrentUserHolder;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.IoUtils;
import com.mossle.core.util.ServletUtils;

import com.mossle.disk.persistence.domain.DiskInfo;
import com.mossle.disk.persistence.manager.DiskInfoManager;
import com.mossle.disk.service.DiskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("disk")
public class DiskInfoController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskInfoController.class);
    private DiskInfoManager diskInfoManager;
    private CurrentUserHolder currentUserHolder;
    private StoreConnector storeConnector;
    private DiskService diskService;
    private TenantHolder tenantHolder;

    /**
     * 列表显示.
     */
    @RequestMapping("disk-info-list")
    public String list(
            @RequestParam(value = "path", required = false) String path,
            Model model) {
        if (path == null) {
            path = "";
        }

        String userId = currentUserHolder.getUserId();
        List<DiskInfo> diskInfos = diskService.listFiles(userId, path);
        model.addAttribute("diskInfos", diskInfos);
        model.addAttribute("path", path);

        return "disk/disk-info-list";
    }

    /**
     * 平铺显示.
     */
    @RequestMapping("disk-info-grid")
    public String grid(
            @RequestParam(value = "path", required = false) String path,
            Model model) {
        if (path == null) {
            path = "";
        }

        String userId = currentUserHolder.getUserId();
        List<DiskInfo> diskInfos = diskService.listFiles(userId, path);
        model.addAttribute("diskInfos", diskInfos);
        model.addAttribute("path", path);

        return "disk/disk-info-grid";
    }

    /**
     * 上传文件.
     */
    @RequestMapping("disk-info-upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,
            @RequestParam("path") String path) throws Exception {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        diskService.createFile(userId, new MultipartFileDataSource(file),
                file.getOriginalFilename(), file.getSize(), path, tenantId);

        return "{\"success\":true}";
    }

    /**
     * 创建目录.
     */
    @RequestMapping("disk-info-createDir")
    public String createDir(@RequestParam("path") String path,
            @RequestParam("name") String name) {
        String userId = currentUserHolder.getUserId();
        diskService.createDir(userId, name, path);

        return "redirect:/disk/disk-info-list.do?path=" + path;
    }

    /**
     * 删除文件.
     */
    @RequestMapping("disk-info-remove")
    public String remove(@RequestParam("id") Long id) {
        String parentPath = diskService.remove(id);

        return "redirect:/disk/disk-info-list.do?path=" + parentPath;
    }

    /**
     * 上一级目录.
     */
    @RequestMapping("disk-info-parentDir")
    public String parentDir(@RequestParam("path") String path) throws Exception {
        if (path == null) {
            return "redirect:/disk/disk-info-list.do";
        }

        if ("".equals(path)) {
            return "redirect:/disk/disk-info-list.do";
        }

        String parentPath = path.substring(0, path.lastIndexOf("/"));

        return "redirect:/disk/disk-info-list.do?path=" + parentPath;
    }

    /**
     * 下载.
     */
    @RequestMapping("disk-info-download")
    public void download(@RequestParam("id") Long id,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String userId = currentUserHolder.getUserId();
        String tenantId = tenantHolder.getTenantId();
        DiskInfo diskInfo = diskInfoManager.get(id);
        InputStream is = null;

        try {
            ServletUtils.setFileDownloadHeader(request, response,
                    diskInfo.getName());
            is = storeConnector
                    .getStore("default/user/" + userId, diskInfo.getRef(),
                            tenantId).getDataSource().getInputStream();
            IoUtils.copyStream(is, response.getOutputStream());
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 详情.
     */
    @RequestMapping("disk-info-view")
    public String view(@RequestParam("id") Long id, Model model) {
        DiskInfo diskInfo = diskInfoManager.get(id);
        model.addAttribute("diskInfo", diskInfo);

        return "disk/disk-info-view";
    }

    /**
     * 重命名.
     */
    @RequestMapping("disk-info-rename")
    public String rename(@RequestParam("id") Long id,
            @RequestParam("name") String name) {
        String userId = currentUserHolder.getUserId();
        String parentPath = diskService.rename(userId, id, name);

        return "redirect:/disk/disk-info-list.do?path=" + parentPath;
    }

    /**
     * 移动.
     */
    @RequestMapping("disk-info-move")
    public String move(@RequestParam("id") Long id,
            @RequestParam("parentId") Long parentId) {
        String userId = currentUserHolder.getUserId();
        String parentPath = diskService.move(userId, id, parentId);

        return "redirect:/disk/disk-info-list.do?path=" + parentPath;
    }

    /**
     * 判断名称是否重复.
     */
    @RequestMapping("disk-info-checkName")
    @ResponseBody
    public String checkName(@RequestParam("name") String name,
            @RequestParam("path") String path) throws Exception {
        String userId = currentUserHolder.getUserId();
        boolean dumplicated = diskService.isDumplicated(userId, name, path);

        return "{\"success\":" + dumplicated + "}";
    }

    /**
     * 目录树.
     */
    @RequestMapping("disk-info-tree")
    @ResponseBody
    public String tree() throws Exception {
        String userId = currentUserHolder.getUserId();
        StringBuilder buff = new StringBuilder();
        buff.append("[{\"id\":0,\"name\":\"根目录\",\"open\":true,\"iconSkin\":\"ico_open\",\"children\":");
        buff.append(this.convertJson(diskService.listFiles(userId, ""), userId));
        buff.append("}]");

        return buff.toString();
    }

    public String convertJson(List<DiskInfo> diskInfos, String userId) {
        if (diskInfos.isEmpty()) {
            return null;
        }

        StringBuilder buff = new StringBuilder();
        buff.append("[");

        for (DiskInfo diskInfo : diskInfos) {
            if (!"dir".equals(diskInfo.getType())) {
                continue;
            }

            buff.append(this.convertJson(diskInfo, userId)).append(",");
        }

        if (buff.length() > 1) {
            buff.deleteCharAt(buff.length() - 1);
        }

        buff.append("]");

        return buff.toString();
    }

    public String convertJson(DiskInfo diskInfo, String userId) {
        if (!"dir".equals(diskInfo.getType())) {
            return null;
        }

        StringBuilder buff = new StringBuilder();
        buff.append("{\"id\":").append(diskInfo.getId()).append(",\"name\":\"")
                .append(diskInfo.getName())
                .append("\",\"iconSkin\":\"ico_open\"");

        String hql = "from DiskInfo where creator=? and type='dir' and parentPath=?";
        String parentPath = diskInfo.getParentPath() + "/" + diskInfo.getName();
        String children = this.convertJson(
                diskInfoManager.find(hql, userId, parentPath), userId);

        if (children != null) {
            buff.append(",\"open\":true,\"children\":").append(children);
        } else {
            buff.append(",\"open\":false");
        }

        buff.append("}");

        return buff.toString();
    }

    // ~ ======================================================================
    @Resource
    public void setDiskInfoManager(DiskInfoManager diskInfoManager) {
        this.diskInfoManager = diskInfoManager;
    }

    @Resource
    public void setCurrentUserHolder(CurrentUserHolder currentUserHolder) {
        this.currentUserHolder = currentUserHolder;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setDiskService(DiskService diskService) {
        this.diskService = diskService;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}

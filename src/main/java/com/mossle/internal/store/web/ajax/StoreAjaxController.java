package com.mossle.internal.store.web.ajax;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.store.StoreConnector;
import com.mossle.api.store.StoreDTO;
import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.store.MultipartFileDataSource;
import com.mossle.core.util.BaseDTO;

import com.mossle.internal.store.persistence.domain.StoreBatch;
import com.mossle.internal.store.persistence.domain.StoreInfo;
import com.mossle.internal.store.persistence.manager.StoreBatchManager;
import com.mossle.internal.store.persistence.manager.StoreInfoManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("store/ajax")
public class StoreAjaxController {
    private static Logger logger = LoggerFactory
            .getLogger(StoreAjaxController.class);
    private StoreConnector storeConnector;
    private JsonMapper jsonMapper = new JsonMapper();
    private StoreInfoManager storeInfoManager;
    private StoreBatchManager storeBatchManager;

    @RequestMapping("upload")
    public BaseDTO upload(@RequestParam("file") MultipartFile multipartFile,
            @RequestParam(value = "batchId", required = false) String batchId)
            throws Exception {
        logger.info("upload");

        try {
            StoreBatch storeBatch = this.generateOrFindBatch(batchId);
            String model = "attachment";

            DataSource dataSource = new MultipartFileDataSource(multipartFile);
            String tenantId = "1";
            StoreDTO storeDto = storeConnector.saveStore(model, dataSource,
                    tenantId);
            String path = storeDto.getKey();

            String hql = "from StoreInfo where model=? and path=?";
            StoreInfo storeInfo = storeInfoManager.findUnique(hql, model, path);
            storeInfo.setStoreBatch(storeBatch);
            // storeInfo.setType();
            // storeInfo.setSize();
            storeInfoManager.save(storeInfo);

            batchId = Long.toString(storeBatch.getId());

            BaseDTO baseDto = this.list(batchId);

            return baseDto;
        } catch (Exception ex) {
            BaseDTO baseDto = new BaseDTO();
            baseDto.setCode(500);
            baseDto.setMessage(ex.getMessage());

            return baseDto;
        }
    }

    @RequestMapping("list")
    public BaseDTO list(@RequestParam("batchId") String batchId) {
        logger.info("list {}", batchId);

        Long id = Long.parseLong(batchId);
        String hql = "from StoreInfo where storeBatch.id=? order by id desc";
        List<StoreInfo> storeInfos = storeInfoManager.find(hql, id);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (StoreInfo storeInfo : storeInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            list.add(map);
            map.put("name", storeInfo.getName());
            map.put("model", storeInfo.getModel());
            map.put("path", storeInfo.getPath());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("batchId", batchId);
        data.put("list", list);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(data);

        return baseDto;
    }

    @RequestMapping("remove")
    public BaseDTO remove(@RequestParam("id") Long id) throws Exception {
        StoreInfo storeInfo = storeInfoManager.get(id);
        this.storeInfoManager.remove(storeInfo);
        this.storeConnector.removeStore(storeInfo.getModel(),
                storeInfo.getPath(), "1");

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);

        return baseDto;
    }

    @RequestMapping(value = "download/**", method = RequestMethod.GET)
    public void getObject(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.info("get");

        String objectName = this.findObjectName(request);
        String model = "attachment";
        String key = objectName;
        String tenantId = "1";
        StoreDTO storeDto = storeConnector.getStore(model, key, tenantId);

        IOUtils.copy(storeDto.getDataSource().getInputStream(),
                response.getOutputStream());
    }

    // ~
    public StoreBatch generateOrFindBatch(String batchIdText) {
        StoreBatch storeBatch = null;

        try {
            if (StringUtils.isNotBlank(batchIdText)) {
                Long id = Long.parseLong(batchIdText);
                storeBatch = storeBatchManager.get(id);
                storeBatch.setUpdateTime(new Date());

                return storeBatch;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        storeBatch = new StoreBatch();
        storeBatch.setCreateTime(new Date());
        storeBatch.setUpdateTime(new Date());
        storeBatchManager.save(storeBatch);

        return storeBatch;
    }

    public String findObjectName(HttpServletRequest request) {
        // logger.info("{}", request.getRequestURL());
        logger.info("{}", request.getRequestURI());

        String prefix = request.getContextPath()
                + "/store/ajax/download/attachment";
        String objectName = request.getRequestURI().substring(prefix.length());

        logger.info("prefix : {}", prefix);
        logger.info("objectName : {}", objectName);

        return objectName;
    }

    @Resource
    public void setStoreConnector(StoreConnector storeConnector) {
        this.storeConnector = storeConnector;
    }

    @Resource
    public void setStoreInfoManager(StoreInfoManager storeInfoManager) {
        this.storeInfoManager = storeInfoManager;
    }

    @Resource
    public void setStoreBatchManager(StoreBatchManager storeBatchManager) {
        this.storeBatchManager = storeBatchManager;
    }
}

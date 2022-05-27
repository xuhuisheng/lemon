package com.mossle.internal.sendmail.web.sys;

import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.tenant.TenantHolder;
import com.mossle.api.user.UserCache;
import com.mossle.api.user.UserDTO;

import com.mossle.client.open.OpenClient;
import com.mossle.client.open.SysDTO;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.Exportor;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.spring.MessageHelper;
import com.mossle.core.store.InputStreamDataSource;
import com.mossle.core.util.BaseDTO;

import com.mossle.spi.rpc.RpcAuthHelper;
import com.mossle.spi.rpc.RpcAuthResult;

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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("sendmail/sys")
public class SendmailSysController {
    private static Logger logger = LoggerFactory
            .getLogger(SendmailSysController.class);
    private JsonMapper jsonMapper = new JsonMapper();
    private OpenClient openClient;
    private TenantHolder tenantHolder;

    @RequestMapping("{sysCode}/index")
    public String index(@PathVariable("sysCode") String sysCode,
            @ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        // open sys
        SysDTO sysDto = openClient.findSys(sysCode);
        model.addAttribute("sysCode", sysCode);
        model.addAttribute("sysDto", sysDto);

        String tenantId = sysCode;
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);

        // page = ossBucketManager.pagedQuery(page, propertyFilters);

        // model.addAttribute("page", page);
        return "sendmail/sys/index";
    }

    // ~
    @Resource
    public void setOpenClient(OpenClient openClient) {
        this.openClient = openClient;
    }

    @Resource
    public void setTenantHolder(TenantHolder tenantHolder) {
        this.tenantHolder = tenantHolder;
    }
}

package com.mossle.disk.web.api;

import javax.servlet.http.HttpServletRequest;

import com.mossle.disk.support.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("disk/api/test")
public class DiskTestApiController {
    private static Logger logger = LoggerFactory
            .getLogger(DiskTestApiController.class);

    // TODO: switch by properties
    @RequestMapping("auto-login")
    public Result autoLogin(@RequestParam("username") String username,
            HttpServletRequest request) {
        logger.info("auto login : {}", username);
        request.getSession().setAttribute("username", username);

        return Result.success();
    }
}

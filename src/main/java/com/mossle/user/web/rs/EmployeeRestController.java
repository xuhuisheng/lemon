package com.mossle.user.web.rs;


import javax.annotation.Resource;

import com.mossle.api.employee.EmployeeDTO;



import com.mossle.core.util.BaseDTO;


import com.mossle.user.service.UserEmployeeService;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/rs/employee")
public class EmployeeRestController {
    private static Logger logger = LoggerFactory
            .getLogger(EmployeeRestController.class);
    private UserEmployeeService userEmployeeService;

    @RequestMapping(value = "findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseDTO findById(@RequestParam("userId") String userId)
            throws Exception {
        logger.debug("find by id {}", userId);

        EmployeeDTO employeeDto = userEmployeeService.findById(userId);

        BaseDTO baseDto = new BaseDTO();
        baseDto.setCode(200);
        baseDto.setData(employeeDto);

        return baseDto;
    }

    // ~ ======================================================================
    @Resource
    public void setUserEmployeeService(UserEmployeeService userEmployeeService) {
        this.userEmployeeService = userEmployeeService;
    }
}

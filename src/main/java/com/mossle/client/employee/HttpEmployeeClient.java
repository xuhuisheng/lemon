package com.mossle.client.employee;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Map;

import com.mossle.api.employee.EmployeeDTO;

import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.BaseDTO;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

public class HttpEmployeeClient implements EmployeeClient {
    private static Logger logger = LoggerFactory
            .getLogger(HttpEmployeeClient.class);
    private BeanMapper beanMapper = new BeanMapper();
    private JsonMapper jsonMapper = new JsonMapper();
    private String baseUrl;

    public EmployeeDTO findById(String userId, String tenantId) {
        try {
            String url = baseUrl + "/user/rs/employee/findById.do";
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "userId=" + userId;
            conn.getOutputStream().write(payload.getBytes("utf-8"));

            String text = IOUtils.toString(conn.getInputStream(), "UTF-8");
            BaseDTO baseDto = jsonMapper.fromJson(text, BaseDTO.class);

            Map<String, Object> data = (Map<String, Object>) baseDto.getData();
            EmployeeDTO employeeDto = new EmployeeDTO();
            beanMapper.copy(data, employeeDto);

            return employeeDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    // ~
    @Value("${client.employee.url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

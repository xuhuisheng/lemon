package com.mossle.expense.service;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mossle.api.model.ModelInfoDTO;

import com.mossle.core.mapper.JsonMapper;

import com.mossle.expense.persistence.domain.ExpenseRequest;
import com.mossle.expense.persistence.manager.ExpenseRequestManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class ExpenseBpmService {
    private static Logger logger = LoggerFactory
            .getLogger(ExpenseBpmService.class);
    private ExpenseRequestManager expenseRequestManager;
    private JsonMapper jsonMapper = new JsonMapper();

    public void save(int eventCode, ModelInfoDTO modelInfo, String userId,
            String activityId, String activityName) throws Exception {
        logger.debug("{} {} {} {}", eventCode, userId, modelInfo.getCode(),
                activityId);

        // logger.info("{}", jsonMapper.toJson(modelInfo));
        String businessKey = modelInfo.getCode();
        ExpenseRequest expenseRequest = expenseRequestManager.findUniqueBy(
                "code", businessKey);

        if (expenseRequest != null) {
            this.update(expenseRequest, modelInfo, eventCode, activityId,
                    activityName);
        } else {
            this.insert(userId, modelInfo, eventCode, activityId, activityName);
        }
    }

    public void update(ExpenseRequest expenseRequest, ModelInfoDTO modelInfo,
            int eventCode, String activityId, String activityName) {
        String type = this.processString(modelInfo, "type");
        Double price = this.processDouble(modelInfo, "price");
        Date startTime = this.processDate(modelInfo, "startTime");
        Date endTime = this.processDate(modelInfo, "endTime");
        Integer headCount = this.processInt(modelInfo, "headCount");
        String person = this.processString(modelInfo, "person");
        String traffic = this.processString(modelInfo, "traffic");
        String country = this.processString(modelInfo, "country");
        String address = this.processString(modelInfo, "address");
        String thing = this.processString(modelInfo, "thing");

        expenseRequest.setType(type);
        expenseRequest.setPrice(price);
        // TODO
        expenseRequest.setStartTime(startTime);
        expenseRequest.setEndTime(endTime);
        expenseRequest.setHeadCount(headCount);
        expenseRequest.setPerson(person);
        expenseRequest.setTraffic(traffic);
        expenseRequest.setCountry(country);
        expenseRequest.setAddress(address);
        expenseRequest.setThing(thing);

        expenseRequestManager.save(expenseRequest);

        // TODO: update status
        if (eventCode == 12) {
            expenseRequest.setStatus("驳回");
        } else if (eventCode == 21) {
            if (!"驳回".equals(expenseRequest.getStatus())) {
                expenseRequest.setStatus("草稿");
            }
        } else if (eventCode == 23) {
            expenseRequest.setStatus("作废");
        } else if (eventCode == 24) {
            expenseRequest.setStatus("完成");
        } else {
            expenseRequest.setStatus(activityName);
        }

        expenseRequestManager.save(expenseRequest);
    }

    public void insert(String userId, ModelInfoDTO modelInfo, int eventCode,
            String activityId, String activityName) {
        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.setCode(modelInfo.getCode());
        expenseRequest.setUserId(userId);
        expenseRequest.setDeptCode(modelInfo.getApplicantDept());
        expenseRequest.setDeptName(modelInfo.getApplicantDept());
        expenseRequest.setCreateTime(modelInfo.getCreateTime());
        expenseRequest.setStatus(activityId);
        expenseRequest.setDescription("");
        expenseRequest.setTenantId("1");
        expenseRequestManager.save(expenseRequest);

        this.update(expenseRequest, modelInfo, eventCode, activityId,
                activityName);
    }

    public String processString(ModelInfoDTO modelInfo, String name) {
        Object value = modelInfo.findItemValue(name);

        if (value == null) {
            return "";
        }

        if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    public Integer processInt(ModelInfoDTO modelInfo, String name) {
        Object value = modelInfo.findItemValue(name);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        if ("".equals(value)) {
            return null;
        }

        if (value instanceof String) {
            String text = (String) value;

            return Integer.parseInt(text);
        }

        return null;
    }

    public Double processDouble(ModelInfoDTO modelInfo, String name) {
        Object value = modelInfo.findItemValue(name);

        if (value == null) {
            return null;
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if ("".equals(value)) {
            return null;
        }

        if (value instanceof String) {
            String text = (String) value;

            return Double.parseDouble(text);
        }

        return null;
    }

    public Date processDate(ModelInfoDTO modelInfo, String name) {
        try {
            Object value = modelInfo.findItemValue(name);

            if (value == null) {
                return null;
            }

            if (value instanceof Date) {
                return (Date) value;
            }

            if (value instanceof String) {
                String text = (String) value;

                return new SimpleDateFormat("yyyy-MM-dd").parse(text);
            }

            return new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);

            return null;
        }
    }

    @Resource
    public void setExpenseRequestManager(
            ExpenseRequestManager expenseRequestManager) {
        this.expenseRequestManager = expenseRequestManager;
    }
}

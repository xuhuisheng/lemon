package com.mossle.user.data;

import java.util.List;

import com.mossle.core.csv.CsvCallback;

import com.mossle.user.persistence.domain.AccountDept;
import com.mossle.user.persistence.domain.PersonInfo;
import com.mossle.user.persistence.manager.AccountDeptManager;
import com.mossle.user.persistence.manager.PersonInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPersonCallback implements CsvCallback {
    private static Logger logger = LoggerFactory
            .getLogger(UserPersonCallback.class);
    private PersonInfoManager personInfoManager;
    private AccountDeptManager accountDeptManager;
    private String defaultTenantId = "1";

    public void process(List<String> list, int lineNo) throws Exception {
        logger.debug("default tenant id : {}", defaultTenantId);

        String username = list.get(0);
        String deptCode = list.get(1);

        if (StringUtils.isBlank(username)) {
            logger.warn("username cannot be blank {} {}", lineNo, list);

            return;
        }

        username = username.toLowerCase();

        this.createOrUpdateDept(username, deptCode, lineNo);
    }

    public void createOrUpdateDept(String username, String deptCode, int lineNo) {
        PersonInfo personInfo = personInfoManager.findUniqueBy("username",
                username);

        if (personInfo == null) {
            logger.info("cannot find person info : {}", username);

            return;
        }

        AccountDept accountDept = accountDeptManager.findUniqueBy("code",
                deptCode);

        if (accountDept == null) {
            logger.info("cannot find account dept : {}", deptCode);

            return;
        }

        personInfo.setDepartmentCode(deptCode);
        personInfo.setDepartmentName(accountDept.getName());
        personInfo.setDepartmentPath(this.processDeptPath(accountDept));
        personInfoManager.save(personInfo);
    }

    public String processDeptPath(AccountDept accountDept) {
        StringBuilder buff = new StringBuilder();

        while (accountDept != null) {
            accountDept = accountDeptManager.get(accountDept.getId());
            buff.insert(0, "/" + accountDept.getCode());
            accountDept = accountDept.getAccountDept();
        }

        return buff.toString();
    }

    // ~
    public void setPersonInfoManager(PersonInfoManager personInfoManager) {
        this.personInfoManager = personInfoManager;
    }

    public void setAccountDeptManager(AccountDeptManager accountDeptManager) {
        this.accountDeptManager = accountDeptManager;
    }
}

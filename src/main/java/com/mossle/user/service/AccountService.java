package com.mossle.user.service;

import java.util.Set;

import javax.annotation.Resource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.mossle.core.mapper.BeanMapper;

import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountInfoManager;
import com.mossle.user.support.AccountDTO;

// import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AccountService {
    private static Logger logger = LoggerFactory
            .getLogger(AccountService.class);
    private AccountInfoManager accountInfoManager;
    private Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
    private BeanMapper beanMapper = new BeanMapper();

    // private static Validator validator =
    // Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();
    public void insertAccount(AccountDTO accountDto) {
        Set<ConstraintViolation<AccountDTO>> violationSet = validator
                .validate(accountDto);

        for (ConstraintViolation<AccountDTO> violation : violationSet) {
            logger.info("{} {}", violation.getPropertyPath().toString(),
                    violation.getMessage());
        }

        if (!violationSet.isEmpty()) {
            ConstraintViolation<AccountDTO> violation = violationSet.iterator()
                    .next();
            throw new RuntimeException(violation.getPropertyPath().toString()
                    + " " + violation.getMessage());
        }

        AccountInfo accountInfo = new AccountInfo();
        beanMapper.copy(accountDto, accountInfo);
        accountInfoManager.save(accountInfo);
    }

    // ~
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }
}

package com.mossle.user.client;

import javax.annotation.Resource;

import com.mossle.api.auth.CustomPasswordEncoder;
import com.mossle.api.user.AccountStatus;

import com.mossle.client.authn.AuthnClient;

import com.mossle.spi.device.DeviceDTO;

import com.mossle.user.persistence.domain.AccountCredential;
import com.mossle.user.persistence.domain.AccountDevice;
import com.mossle.user.persistence.domain.AccountInfo;
import com.mossle.user.persistence.manager.AccountCredentialManager;
import com.mossle.user.persistence.manager.AccountDeviceManager;
import com.mossle.user.persistence.manager.AccountInfoManager;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalAuthnClient implements AuthnClient {
    private static Logger logger = LoggerFactory
            .getLogger(LocalAuthnClient.class);
    private AccountInfoManager accountInfoManager;
    private AccountCredentialManager accountCredentialManager;
    private AccountDeviceManager accountDeviceManager;
    private CustomPasswordEncoder customPasswordEncoder;

    public String authenticate(String username, String password, String tenantId) {
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);
        String hql = "from AccountCredential where accountInfo=? and catalog='default'";
        AccountCredential accountCredential = accountCredentialManager
                .findUnique(hql, accountInfo);

        if (accountCredential == null) {
            logger.info("cannot find credential : {} {}", username, "xxx");

            return AccountStatus.FAILURE;
        }

        String encodedPassword = accountCredential.getPassword();

        boolean result = customPasswordEncoder.matches(password,
                encodedPassword);

        if (!result) {
            return AccountStatus.FAILURE;
        }

        return AccountStatus.SUCCESS;
    }

    public DeviceDTO findDevice(String code) {
        AccountDevice accountDevice = this.accountDeviceManager.findUniqueBy(
                "code", code);

        if (accountDevice == null) {
            return null;
        }

        DeviceDTO deviceDto = new DeviceDTO();
        deviceDto.setCode(code);
        deviceDto.setType(accountDevice.getType());
        deviceDto.setOs(accountDevice.getOs());
        deviceDto.setClient(accountDevice.getClient());

        return deviceDto;
    }

    public void saveDevice(DeviceDTO deviceDto) {
        logger.debug("save device : {}", deviceDto.getCode());

        if (deviceDto == null) {
            return;
        }

        String code = deviceDto.getCode();

        if (StringUtils.isBlank(code)) {
            logger.info("code cannot blank");

            return;
        }

        AccountDevice accountDevice = this.accountDeviceManager.findUniqueBy(
                "code", code);

        if (accountDevice == null) {
            accountDevice = new AccountDevice();
            accountDevice.setCode(code);
        }

        accountDevice.setOs(deviceDto.getOs());
        accountDevice.setClient(deviceDto.getClient());

        String username = deviceDto.getUsername();
        AccountInfo accountInfo = accountInfoManager.findUniqueBy("username",
                username);
        accountDevice.setAccountInfo(accountInfo);
        this.accountDeviceManager.save(accountDevice);
    }

    // ~
    @Resource
    public void setAccountInfoManager(AccountInfoManager accountInfoManager) {
        this.accountInfoManager = accountInfoManager;
    }

    @Resource
    public void setAccountCredentialManager(
            AccountCredentialManager accountCredentialManager) {
        this.accountCredentialManager = accountCredentialManager;
    }

    @Resource
    public void setAccountDeviceManager(
            AccountDeviceManager accountDeviceManager) {
        this.accountDeviceManager = accountDeviceManager;
    }

    @Resource
    public void setCustomPasswordEncoder(
            CustomPasswordEncoder customPasswordEncoder) {
        this.customPasswordEncoder = customPasswordEncoder;
    }
}

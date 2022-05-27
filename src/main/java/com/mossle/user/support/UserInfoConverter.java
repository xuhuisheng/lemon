package com.mossle.user.support;

import com.mossle.user.persistence.domain.AccountInfo;

public class UserInfoConverter {
    public UserInfoDTO convertOne(AccountInfo accountInfo) {
        UserInfoDTO userInfoDto = new UserInfoDTO();
        userInfoDto.setCode(accountInfo.getCode());

        return userInfoDto;
    }
}

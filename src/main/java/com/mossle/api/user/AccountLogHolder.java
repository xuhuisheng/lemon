package com.mossle.api.user;

public class AccountLogHolder {
    private static ThreadLocal<AccountLogDTO> threadLocal = new ThreadLocal<AccountLogDTO>();

    public static void init(String application, String username,
            String clientIp, String description) {
        AccountLogDTO accountLogDto = new AccountLogDTO();
        accountLogDto.setApplication(application);
        accountLogDto.setUsername(username);
        accountLogDto.setClient(clientIp);
        accountLogDto.setDescription(description);
        threadLocal.set(accountLogDto);
    }

    public static AccountLogDTO getAccountLogDto() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}

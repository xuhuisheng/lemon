package com.mossle.api.menu;

import java.util.List;

public class MockMenuConnector implements MenuConnector {
    public List<MenuDTO> findMenus(String systemCode, String userId) {
        return null;
    }

    public List<MenuDTO> findSystemMenus(String userId) {
        return null;
    }
}

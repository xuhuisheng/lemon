package com.mossle.auth.support;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.cache.Cache;
import javax.cache.CacheManager;

import com.mossle.api.menu.MenuDTO;

public class MenuCache {
    private CacheManager cacheManager;
    private Cache<String, List<MenuDTO>> cache;

    @PostConstruct
    public void init() {
        this.cache = this.cacheManager.getCache("menu");
    }

    public List<MenuDTO> findByCode(String code) {
        return cache.get(code);
    }

    public void updateByCode(String code, List<MenuDTO> menuDtos) {
        cache.put(code, menuDtos);
    }

    public List<MenuDTO> findEntries() {
        return cache.get("entry");
    }

    public void updateEntries(List<MenuDTO> menuDtos) {
        cache.put("entry", menuDtos);
    }

    @Resource
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}

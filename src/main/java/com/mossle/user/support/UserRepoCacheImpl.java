package com.mossle.user.support;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import javax.cache.Cache;
import javax.cache.CacheManager;

import com.mossle.api.userrepo.UserRepoCache;
import com.mossle.api.userrepo.UserRepoDTO;

public class UserRepoCacheImpl implements UserRepoCache {
    private CacheManager cacheManager;
    private Cache<String, UserRepoDTO> cache;

    @PostConstruct
    public void init() {
        this.cache = this.cacheManager.getCache("userrepo");
    }

    public UserRepoDTO findById(String id) {
        String key = "userRepoId:" + id;

        return cache.get(key);
    }

    public UserRepoDTO findByCode(String code) {
        String key = "userRepoCode:" + code;

        return cache.get(key);
    }

    public void updateUserRepo(UserRepoDTO userRepoDto) {
        cache.put("userRepoId:" + userRepoDto.getId(), userRepoDto);
        cache.put("userRepoCode:" + userRepoDto.getCode(), userRepoDto);
    }

    public void removeUserRepo(UserRepoDTO userRepoDto) {
        cache.remove("userRepoId:" + userRepoDto.getId());
        cache.remove("userRepoCode:" + userRepoDto.getCode());
    }

    @Resource
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}

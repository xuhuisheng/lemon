package com.mossle.internal.store.service;

import java.util.Date;

import com.mossle.internal.store.persistence.domain.StoreAttach;

import org.springframework.web.multipart.MultipartFile;

public class AttachService {
    public String upload(MultipartFile multipartFile, String catalog,
            String businessKey) {
        StoreAttach storeAttach = new StoreAttach();
        storeAttach.setCreateTime(new Date());
        storeAttach.setStatus("init");
        storeAttach.setCatalog(catalog);
        storeAttach.setBusinessKey(businessKey);

        return null;
    }
}

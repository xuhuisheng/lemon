package com.mossle.internal.store.client;

import java.io.*;

import java.net.*;

import java.text.SimpleDateFormat;

import java.util.*;

import com.mossle.api.store.StoreDTO;

import com.mossle.core.mapper.JsonMapper;
import com.mossle.core.util.IoUtils;

import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.InputStreamResource;

import org.springframework.stereotype.Component;

import org.springframework.util.FileCopyUtils;

public interface StoreClient {
    StoreDTO saveStore(InputStream inputStream, String fileName,
            String contentType, String tenantId) throws Exception;

    StoreDTO getStore(String key, String tenantId) throws Exception;
}

package com.mossle.disk.scheduler;

import java.util.Date;

import javax.annotation.Resource;

import com.mossle.disk.service.DiskFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

@Component
public class DeleteFileJob {
    private static Logger logger = LoggerFactory.getLogger(DeleteFileJob.class);
    private DiskFileService diskFileService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void markDeletedFiles() {
        logger.debug("mark deleted files start {}", new Date());

        Long processedDiskInfoId = Long.valueOf(0L);

        while (processedDiskInfoId != null) {
            processedDiskInfoId = diskFileService
                    .markDeleted(processedDiskInfoId);
        }

        logger.debug("mark deleted files end {}", new Date());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void realDeleteFiles() {
        logger.debug("real delete files start {}", new Date());

        // TODO: 需要再有一个后悔期，彻底删除之后还能通过后台找回
        Long processedDiskInfoId = Long.valueOf(0L);

        while (processedDiskInfoId != null) {
            processedDiskInfoId = diskFileService.markDone(processedDiskInfoId);
        }

        logger.debug("real delete files end {}", new Date());
    }

    @Resource
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }
}

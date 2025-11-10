package com.utm.what2do.service.impl;

import com.utm.what2do.service.DataMaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataMaintenanceServiceImpl implements DataMaintenanceService {

    @Override
    public void syncFromSource(String source) {
        log.info("Triggering data synchronization from source: {}", source);
        // TODO implement synchronization logic
    }

    @Override
    public void cleanupObsoleteData() {
        log.info("Triggering data cleanup job");
        // TODO implement cleanup logic
    }
}

package com.utm.what2do.controller;

import com.utm.what2do.common.R;
import com.utm.what2do.service.DataMaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataMaintenanceController {

    private final DataMaintenanceService dataMaintenanceService;

    @PostMapping("/sync")
    public R<Void> sync(@RequestParam(defaultValue = "external_feed") String source) {
        dataMaintenanceService.syncFromSource(source);
        return R.ok();
    }

    @PostMapping("/cleanup")
    public R<Void> cleanup() {
        dataMaintenanceService.cleanupObsoleteData();
        return R.ok();
    }
}

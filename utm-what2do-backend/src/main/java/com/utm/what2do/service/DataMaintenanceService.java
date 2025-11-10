package com.utm.what2do.service;

public interface DataMaintenanceService {

    void syncFromSource(String source);

    void cleanupObsoleteData();
}

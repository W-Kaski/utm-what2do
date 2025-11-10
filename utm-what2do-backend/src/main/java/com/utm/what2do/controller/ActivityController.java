package com.utm.what2do.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.utm.what2do.common.R;
import com.utm.what2do.model.dto.ActivityQueryDTO;
import com.utm.what2do.model.dto.ActivityRequestDTO;
import com.utm.what2do.model.entity.Activity;
import com.utm.what2do.model.vo.ActivityVO;
import com.utm.what2do.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public R<IPage<ActivityVO>> listActivities(ActivityQueryDTO queryDTO) {
        return R.ok(activityService.pageActivities(queryDTO));
    }

    @PostMapping
    public R<Activity> createActivity(@Valid @RequestBody ActivityRequestDTO dto) {
        return R.ok(activityService.createActivity(dto));
    }

    @PutMapping("/{id}")
    public R<Activity> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityRequestDTO dto) {
        return R.ok(activityService.updateActivity(id, dto));
    }

    @GetMapping("/{id}")
    public R<Activity> getActivity(@PathVariable Long id) {
        return R.ok(activityService.getById(id));
    }
}

package com.utm.what2do.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.utm.what2do.common.R;
import com.utm.what2do.model.dto.OrganizationRequestDTO;
import com.utm.what2do.model.entity.Organization;
import com.utm.what2do.model.vo.OrganizationVO;
import com.utm.what2do.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public R<IPage<OrganizationVO>> listOrganizations(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return R.ok(organizationService.pageOrganizations(page, size));
    }

    @PostMapping
    public R<Organization> createOrganization(@Valid @RequestBody OrganizationRequestDTO dto) {
        return R.ok(organizationService.createOrganization(dto));
    }

    @PutMapping("/{id}")
    public R<Organization> updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationRequestDTO dto) {
        return R.ok(organizationService.updateOrganization(id, dto));
    }

    @GetMapping("/{id}")
    public R<Organization> getOrganization(@PathVariable Long id) {
        return R.ok(organizationService.getById(id));
    }
}

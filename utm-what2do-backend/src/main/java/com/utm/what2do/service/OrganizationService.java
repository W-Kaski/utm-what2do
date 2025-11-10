package com.utm.what2do.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.utm.what2do.model.dto.OrganizationRequestDTO;
import com.utm.what2do.model.entity.Organization;
import com.utm.what2do.model.vo.OrganizationVO;

public interface OrganizationService extends IService<Organization> {

    IPage<OrganizationVO> pageOrganizations(int page, int size);

    Organization createOrganization(OrganizationRequestDTO dto);

    Organization updateOrganization(Long id, OrganizationRequestDTO dto);
}

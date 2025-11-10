package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.mapper.OrganizationMapper;
import com.utm.what2do.model.dto.OrganizationRequestDTO;
import com.utm.what2do.model.entity.Organization;
import com.utm.what2do.model.vo.OrganizationVO;
import com.utm.what2do.service.OrganizationService;
import com.utm.what2do.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    @Override
    public IPage<OrganizationVO> pageOrganizations(int page, int size) {
        Page<Organization> pager = Page.of(page, size);
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        return this.baseMapper.selectPage(pager, queryWrapper)
                .convert(entity -> BeanCopyUtils.copy(entity, OrganizationVO.class));
    }

    @Override
    public Organization createOrganization(OrganizationRequestDTO dto) {
        Organization organization = BeanCopyUtils.copy(dto, new Organization());
        this.save(organization);
        return organization;
    }

    @Override
    public Organization updateOrganization(Long id, OrganizationRequestDTO dto) {
        Organization organization = this.getById(id);
        if (organization == null) {
            throw new IllegalArgumentException("Organization not found: " + id);
        }
        BeanCopyUtils.copy(dto, organization);
        this.updateById(organization);
        return organization;
    }
}

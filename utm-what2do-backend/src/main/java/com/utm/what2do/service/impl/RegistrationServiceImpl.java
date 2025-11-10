package com.utm.what2do.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utm.what2do.mapper.RegistrationMapper;
import com.utm.what2do.model.dto.RegistrationQueryDTO;
import com.utm.what2do.model.dto.RegistrationRequestDTO;
import com.utm.what2do.model.entity.Registration;
import com.utm.what2do.model.vo.RegistrationVO;
import com.utm.what2do.service.RegistrationService;
import com.utm.what2do.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {

    @Override
    public IPage<RegistrationVO> pageRegistrations(RegistrationQueryDTO queryDTO) {
        Page<RegistrationVO> page = Page.of(queryDTO.getPage(), queryDTO.getSize());
        return this.baseMapper.selectRegistrations(page, queryDTO);
    }

    @Override
    public Registration createRegistration(RegistrationRequestDTO dto) {
        Registration registration = BeanCopyUtils.copy(dto, new Registration());
        this.save(registration);
        return registration;
    }

    @Override
    public Registration updateRegistration(Long id, RegistrationRequestDTO dto) {
        Registration registration = this.getById(id);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found: " + id);
        }
        BeanCopyUtils.copy(dto, registration);
        this.updateById(registration);
        return registration;
    }
}

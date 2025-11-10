package com.utm.what2do.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.dto.RegistrationQueryDTO;
import com.utm.what2do.model.dto.RegistrationRequestDTO;
import com.utm.what2do.model.entity.Registration;
import com.utm.what2do.model.vo.RegistrationVO;

public interface RegistrationService extends IService<Registration> {

    IPage<RegistrationVO> pageRegistrations(RegistrationQueryDTO queryDTO);

    Registration createRegistration(RegistrationRequestDTO dto);

    Registration updateRegistration(Long id, RegistrationRequestDTO dto);
}

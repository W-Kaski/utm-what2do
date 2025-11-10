package com.utm.what2do.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.utm.what2do.common.R;
import com.utm.what2do.model.dto.RegistrationQueryDTO;
import com.utm.what2do.model.dto.RegistrationRequestDTO;
import com.utm.what2do.model.entity.Registration;
import com.utm.what2do.model.vo.RegistrationVO;
import com.utm.what2do.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping
    public R<IPage<RegistrationVO>> listRegistrations(RegistrationQueryDTO queryDTO) {
        return R.ok(registrationService.pageRegistrations(queryDTO));
    }

    @PostMapping
    public R<Registration> createRegistration(@Valid @RequestBody RegistrationRequestDTO dto) {
        return R.ok(registrationService.createRegistration(dto));
    }

    @PutMapping("/{id}")
    public R<Registration> updateRegistration(@PathVariable Long id, @Valid @RequestBody RegistrationRequestDTO dto) {
        return R.ok(registrationService.updateRegistration(id, dto));
    }

    @GetMapping("/{id}")
    public R<Registration> getRegistration(@PathVariable Long id) {
        return R.ok(registrationService.getById(id));
    }
}

package com.utm.what2do.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.utm.what2do.common.R;
import com.utm.what2do.model.dto.UserQueryDTO;
import com.utm.what2do.model.dto.UserRequestDTO;
import com.utm.what2do.model.entity.User;
import com.utm.what2do.model.vo.UserVO;
import com.utm.what2do.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public R<IPage<UserVO>> listUsers(UserQueryDTO queryDTO) {
        return R.ok(userService.pageUsers(queryDTO));
    }

    @PostMapping
    public R<User> createUser(@Valid @RequestBody UserRequestDTO dto) {
        return R.ok(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    public R<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        return R.ok(userService.updateUser(id, dto));
    }

    @GetMapping("/{id}")
    public R<User> getUser(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }
}

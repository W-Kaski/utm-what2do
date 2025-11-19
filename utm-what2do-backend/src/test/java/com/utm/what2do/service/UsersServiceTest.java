package com.utm.what2do.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.utm.what2do.common.exception.BusinessException;
import com.utm.what2do.constant.RoleConstants;
import com.utm.what2do.mapper.UsersMapper;
import com.utm.what2do.model.dto.UserLoginDTO;
import com.utm.what2do.model.dto.UserRegisterDTO;
import com.utm.what2do.model.entity.Users;
import com.utm.what2do.model.vo.UserInfoVO;
import com.utm.what2do.service.impl.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import cn.dev33.satoken.secure.BCrypt;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UsersService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UsersServiceTest {

    @Mock
    private UsersMapper usersMapper;

    @InjectMocks
    private UsersServiceImpl usersService;

    private UserRegisterDTO registerDTO;
    private UserLoginDTO loginDTO;
    private Users testUser;

    @BeforeEach
    void setUp() {
        // 准备注册DTO
        registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setEmail("test@utm.utoronto.ca");
        registerDTO.setPassword("password123");
        registerDTO.setRole(RoleConstants.USER);

        // 准备登录DTO
        loginDTO = new UserLoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        // 准备测试用户
        testUser = new Users();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@utm.utoronto.ca");
        testUser.setPassword_hash(BCrypt.hashpw("password123", BCrypt.gensalt()));
        testUser.setRole(RoleConstants.USER);
        testUser.setCreated_at(new Date());
        testUser.setUpdated_at(new Date());
        testUser.setDeleted(0);
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void register_Success() {
        // Given
        when(usersMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(usersMapper.insert(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        // Mock StpUtil (需要在实际测试环境中配置Sa-Token)
        // 这里只验证基本逻辑

        // When
        UserInfoVO result = usersService.register(registerDTO);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@utm.utoronto.ca", result.getEmail());
        assertEquals(RoleConstants.USER, result.getRole());

        verify(usersMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(usersMapper).insert(argThat(user ->
            user.getUsername().equals("testuser") &&
            user.getPassword_hash() != null &&
            !user.getPassword_hash().equals("password123") // 密码应该被加密
        ));
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void register_UsernameExists() {
        // Given
        when(usersMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            usersService.register(registerDTO);
        });

        verify(usersMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(usersMapper, never()).insert(any());
    }

    @Test
    @DisplayName("用户注册 - 社团管理员角色")
    void register_ClubManagerRole() {
        // Given
        registerDTO.setRole(RoleConstants.CLUB_MANAGER);
        when(usersMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(usersMapper.insert(any(Users.class))).thenAnswer(invocation -> {
            Users user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        // When
        UserInfoVO result = usersService.register(registerDTO);

        // Then
        assertNotNull(result);
        assertEquals(RoleConstants.CLUB_MANAGER, result.getRole());

        verify(usersMapper).insert(argThat(user ->
            user.getRole().equals(RoleConstants.CLUB_MANAGER)
        ));
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() {
        // Given
        when(usersMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // When
        UserInfoVO result = usersService.login(loginDTO);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(RoleConstants.USER, result.getRole());

        verify(usersMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户登录 - 用户不存在")
    void login_UserNotFound() {
        // Given
        when(usersMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            usersService.login(loginDTO);
        });

        verify(usersMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void login_WrongPassword() {
        // Given
        loginDTO.setPassword("wrongpassword");
        when(usersMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            usersService.login(loginDTO);
        });

        verify(usersMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("获取用户信息 - 成功")
    void getUserInfo_Success() {
        // Given
        when(usersMapper.selectById(1L)).thenReturn(testUser);

        // When
        UserInfoVO result = usersService.getUserInfo(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@utm.utoronto.ca", result.getEmail());
        assertNull(result.getPasswordHash()); // 密码不应该返回

        verify(usersMapper).selectById(1L);
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void getUserInfo_UserNotFound() {
        // Given
        when(usersMapper.selectById(999L)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            usersService.getUserInfo(999L);
        });

        verify(usersMapper).selectById(999L);
    }

    @Test
    @DisplayName("更新用户资料 - 成功")
    void updateProfile_Success() {
        // Given
        when(usersMapper.selectById(1L)).thenReturn(testUser);
        when(usersMapper.updateById(any(Users.class))).thenReturn(1);

        Users updateData = new Users();
        updateData.setProfile_image_url("https://example.com/avatar.jpg");
        updateData.setBio("Updated bio");

        // When
        UserInfoVO result = usersService.updateProfile(1L, updateData);

        // Then
        assertNotNull(result);
        verify(usersMapper).selectById(1L);
        verify(usersMapper).updateById(argThat(user ->
            user.getProfile_image_url() != null &&
            user.getBio() != null
        ));
    }

    @Test
    @DisplayName("更新用户资料 - 用户不存在")
    void updateProfile_UserNotFound() {
        // Given
        when(usersMapper.selectById(999L)).thenReturn(null);

        Users updateData = new Users();
        updateData.setBio("Bio");

        // When & Then
        assertThrows(BusinessException.class, () -> {
            usersService.updateProfile(999L, updateData);
        });

        verify(usersMapper).selectById(999L);
        verify(usersMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("密码加密验证")
    void passwordEncryption_Test() {
        // Given
        String plainPassword = "password123";

        // When - BCrypt加密
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // Then
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(BCrypt.checkpw(plainPassword, hashedPassword));
        assertFalse(BCrypt.checkpw("wrongpassword", hashedPassword));
    }

    @Test
    @DisplayName("用户实体转换为VO - 密码字段不应包含")
    void convertToVO_NoPasswordField() {
        // When
        UserInfoVO vo = usersService.getUserInfo(1L);

        // Mock the mapper
        when(usersMapper.selectById(1L)).thenReturn(testUser);
        vo = usersService.getUserInfo(1L);

        // Then
        assertNotNull(vo);
        assertNull(vo.getPasswordHash()); // VO中不应该包含密码hash
    }
}

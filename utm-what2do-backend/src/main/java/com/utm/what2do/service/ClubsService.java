package com.utm.what2do.service;

import com.utm.what2do.model.entity.Clubs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.utm.what2do.model.vo.ClubDetailVO;

import java.util.List;

/**
* @author PC
* @description 针对表【clubs】的数据库操作Service
* @createDate 2025-11-11 02:14:33
*/
public interface ClubsService extends IService<Clubs> {

    /**
     * 获取所有社团列表
     * @return 社团列表
     */
    List<Clubs> getAllClubs();

    /**
     * 获取社团详情（包含即将举办的活动）
     * @param clubId 社团ID
     * @return 社团详情
     */
    ClubDetailVO getClubDetail(Long clubId);

    /**
     * 根据slug获取社团详情
     * @param slug 社团slug
     * @return 社团详情
     */
    ClubDetailVO getClubDetailBySlug(String slug);
}

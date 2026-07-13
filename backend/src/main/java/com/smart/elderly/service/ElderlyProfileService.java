package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.ElderlyProfile;

public interface ElderlyProfileService extends IService<ElderlyProfile> {

    Result<PageResult<ElderlyProfile>> pageList(int pageNum, int pageSize, String keyword);

    Result<?> addElderly(ElderlyProfile profile);

    Result<?> updateElderly(ElderlyProfile profile);

    Result<?> deleteElderly(Long id);
}

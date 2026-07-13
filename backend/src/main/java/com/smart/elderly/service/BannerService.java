package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Banner;

import java.util.List;

public interface BannerService extends IService<Banner> {

    Result<List<Banner>> listActive();
}

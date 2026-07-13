package com.smart.elderly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.elderly.common.PageResult;
import com.smart.elderly.common.Result;
import com.smart.elderly.entity.Notice;

public interface NoticeService extends IService<Notice> {

    Result<PageResult<Notice>> pageList(int pageNum, int pageSize, Integer type);

    Result<?> addNotice(Notice notice);

    Result<?> updateNotice(Notice notice);

    Result<?> deleteNotice(Long id);
}

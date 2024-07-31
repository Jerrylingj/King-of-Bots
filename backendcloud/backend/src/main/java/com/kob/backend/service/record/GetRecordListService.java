package com.kob.backend.service.record;

import com.alibaba.fastjson2.JSONObject;

public interface GetRecordListService {
    //  传回页面编号
    JSONObject getList(Integer page);

}

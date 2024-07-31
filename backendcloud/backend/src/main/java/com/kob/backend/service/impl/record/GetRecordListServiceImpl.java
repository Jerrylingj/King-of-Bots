package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.SymbolTable;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.kob.backend.consumer.WebSocketServer.recordMapper;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        System.out.println("page2: " + page);
        //  一页展示10条记录
        IPage<Record> recordIPage = new Page<>(page, 10);

        //  对时间排序
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");//   Desc降序 Asc升序

        List<Record> records = recordMapper.selectPage(recordIPage, queryWrapper).getRecords();

        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Record record : records) {
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());

            JSONObject item = new JSONObject();
            item.put("a_photo",userA.getPhoto());
            item.put("a_username",userA.getUsername());
            item.put("b_photo",userB.getPhoto());
            item.put("b_username",userB.getUsername());
            //  为方便直接在后端获取结果返回前端
            String result = "平局";
            if ("A".equals(record.getLoser())) {
                result = "B胜";
            } else if ("B".equals(record.getLoser())) {
                result = "A胜";
            }
            item.put("result",result);
            item.put("record", record);
            items.add(item);
        }
        resp.put("records", items);
        //  再传回页面总数
        resp.put("records_count",recordMapper.selectCount(null));
        return resp;
    }
}

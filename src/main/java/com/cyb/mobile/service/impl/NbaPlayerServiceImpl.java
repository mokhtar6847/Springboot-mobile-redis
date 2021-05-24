package com.cyb.mobile.service.impl;

import com.cyb.mobile.domain.NbaPlayer;
import com.cyb.mobile.mapper.NbaPlayerMapper;
import com.cyb.mobile.service.NbaPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName：NbaPlayerServiceImpl
 * @Description：TODO
 * @Author：chenyb
 * @Date：2020/8/17 11:57 上午
 * @Versiion：1.0
 */
@Service
public class NbaPlayerServiceImpl implements NbaPlayerService {
    @Autowired
    private NbaPlayerMapper nbaPlayerMapper;
    @Override
    public List<NbaPlayer> ListNbaPlayer() {
        return nbaPlayerMapper.ListNbaPlayer();
    }
}

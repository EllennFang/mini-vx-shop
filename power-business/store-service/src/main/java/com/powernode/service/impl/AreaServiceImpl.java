package com.powernode.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.mapper.AreaMapper;
import com.powernode.domain.Area;
import com.powernode.service.AreaService;
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService{

}

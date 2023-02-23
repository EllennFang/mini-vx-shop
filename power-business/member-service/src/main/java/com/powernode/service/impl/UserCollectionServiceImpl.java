package com.powernode.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.powernode.domain.UserCollection;
import com.powernode.mapper.UserCollectionMapper;
import com.powernode.service.UserCollectionService;
@Service
public class UserCollectionServiceImpl extends ServiceImpl<UserCollectionMapper, UserCollection> implements UserCollectionService{

}

package org.tcloud.jsr.mbp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Author Will.tuo
 * @Date 2023/10/31 10:14
 * @Description BaseDao
 **/
public abstract class BaseDao<E> extends ServiceImpl<BaseMapper<E>, E> implements IService<E> {

}

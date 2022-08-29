package com.minjiang.deliveryman.mapper;

import com.minjiang.deliveryman.pojo.Deliveryman;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minjiang.deliveryman.status.DeliverymanStatus;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【deliveryman】的数据库操作Mapper
* @createDate 2022-08-19 14:07:08
* @Entity com.minjiang.deliveryman.pojo.Deliveryman
*/
public interface DeliverymanMapper extends BaseMapper<Deliveryman> {
    @Select("SELECT id,name,status,date FROM deliveryman WHERE id = #{id}")
    Deliveryman selectDeliveryman(String id);

    @Select("SELECT id,name,status,date FROM deliveryman WHERE status = #{status}")
    List<Deliveryman> selectAvaliableDeliveryman(DeliverymanStatus status);

}





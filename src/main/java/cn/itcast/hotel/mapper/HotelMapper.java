package cn.itcast.hotel.mapper;

import cn.itcast.hotel.pojo.Hotel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {

    @Select("select * from tb_hotel where name like concat('%',#{keyword},'%') " +
            "or city like concat('%',#{keyword},'%') " +
            "or address like concat('%',#{keyword},'%')")
    List<Hotel> selectList(String keyword);

    @Select("select count(*) from tb_hotel where name like concat('%',#{keyword},'%')"+
            "or city like concat('%',#{keyword},'%') "+
            "or address like concat('%',#{keyword},'%')")
    Long getTotal(String keyword);

    @Select("select * from tb_hotel")
    List<Hotel> selectAll();
}

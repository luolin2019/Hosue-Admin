package cn.itcast.hotel.mapper;

import cn.itcast.hotel.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {
    @Select("select * from admin")
    List<Admin> findAll();

    @Select("select name from admin where name=#{userName}")
    String queryName(String userName);
    @Select("select password from admin where password=#{password}")
    String queryPassword(String password);
    @Select("select id from admin where name=#{userName}")
    int queryId(String userName);
}

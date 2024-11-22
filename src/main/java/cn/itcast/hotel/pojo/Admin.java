package cn.itcast.hotel.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin")
public class Admin {
    private Long id;
    private String name;
    private String password;

}

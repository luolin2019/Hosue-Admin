package cn.itcast.hotel.web;

import cn.itcast.hotel.mapper.AdminMapper;
import cn.itcast.hotel.pojo.Admin;
import cn.itcast.hotel.pojo.LoginDTO;
import cn.itcast.hotel.pojo.LoginVO;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;


@CrossOrigin(origins = "*")
@Controller
@RequestMapping("admin")
@Slf4j
public class LoginController {
    @Autowired
    private AdminMapper adminMapper;

    @PostMapping ("/login")
    @ResponseBody
    public Boolean login(@RequestBody LoginDTO loginDTO){

        boolean flag=false;
        String userName=loginDTO.getUserName();
        String password=loginDTO.getPassWord();

        String name=adminMapper.queryName(userName);
        String psw=adminMapper.queryPassword(password);
//        int id=0;

        if (!StringUtils.isEmpty(name)){
            int id=adminMapper.queryId(userName);
            if (name.equals(userName)&&psw.equals(password)){
                LoginVO loginVO=LoginVO.builder()
                        .id(id)
                        .userName(name)
                        .password(psw)
                        .build();

                flag=true;
                return flag;
            }
        }


        if (StringUtils.isEmpty(name)||StringUtils.isEmpty(psw)){
            return flag;
        }



        return flag;
//        //1.管理员校验
//        if (StringUtils.isEmpty(userName)||StringUtils.isEmpty(password)){
//            return false;
//        }
//        String name=adminMapper.queryName(userName);
//        String psd=adminMapper.queryPassword(password);
//        if (name==null||psd==null){
//            log.info("账户或密码错误");
//            return false;
//        }
//        if (name.equals(userName)&&psd.equals(password)){
//            return true;
//        }
//
//        return false;
    }

}


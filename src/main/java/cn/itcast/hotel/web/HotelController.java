package cn.itcast.hotel.web;

import cn.itcast.hotel.constants.HotelMqConstants;
import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.enums.HotelQueryParams;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.service.IHotelService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hotel")
@Slf4j
public class HotelController {
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private IHotelService hotelService;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @GetMapping("/{id}")
    public Hotel queryById(@PathVariable("id") Long id){
        return hotelService.getById(id);
    }



    @GetMapping("/list")
    public PageResult hotelList(
            @RequestParam(value = "keyword",required = false) String keyword,
            @RequestParam(value = "brand",required = false) String brand,
            @RequestParam(value = "city",required = false) String city,
            @RequestParam(value = "star",required = false) String star,
            @RequestParam(value = "price",required = false) String price,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "1") Integer size
    ){
        PageResult result=new PageResult();
        if (!StringUtils.isEmpty(keyword) || !StringUtils.isEmpty(brand) ||
                !StringUtils.isEmpty(city) || !StringUtils.isEmpty(star) ||
                !StringUtils.isEmpty(price)){

            result=search(keyword, brand, city, star, price, page, size);
        }else {
            Page<Hotel> result1 = hotelService.page(new Page<>(page, size));

            result=new PageResult(result1.getTotal(), result1.getRecords());
        }
        return result;

//        Page<Hotel> result = hotelService.page(new Page<>(page, size));
//
//        return new PageResult(result.getTotal(), result.getRecords());
    }


    @GetMapping("/searchKey")
    public PageResult search(@RequestParam(value = "keyword",required = false) String keyword,
                             @RequestParam(value = "brand",required = false) String brand,
                             @RequestParam(value = "city",required = false) String city,
                             @RequestParam(value = "star",required = false) String star,
                             @RequestParam(value = "price",required = false) String price,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "size", defaultValue = "1") Integer size
    ){
        List<Hotel> results=new ArrayList<>();
        results=hotelMapper.selectAll();
        log.info("酒店总数: " + results.stream().count());


/*        List<Hotel> filteredHotels = results.stream()
                .filter(hotel -> hotel.getBrand().equals(brand))
                .filter(hotel -> hotel.getStarName().equals(star))
                .filter(hotel -> hotel.getCity().equals(city))
                .filter(hotel -> hotel.getPrice() <= price)
                .filter(hotel -> (hotel.getName().contains(keyword)
                        ||hotel.getAddress().contains(keyword)||hotel.getCity().equals(keyword)))
                .collect(Collectors.toList());*/

        List<Hotel> filteredHotels = results;

        if (!brand.isEmpty()) {
            filteredHotels = filteredHotels.stream()
                    .filter(hotel -> hotel.getBrand().equals(brand))
                    .collect(Collectors.toList());
        }

        if (!star .isEmpty()) {
            filteredHotels = filteredHotels.stream()
                    .filter(hotel -> hotel.getStarName().equals(star))
                    .collect(Collectors.toList());
        }

        if (!city .isEmpty()) {
            filteredHotels = filteredHotels.stream()
                    .filter(hotel -> hotel.getCity().equals(city))
                    .collect(Collectors.toList());
        }

        if (!price.isEmpty()) {
            Integer Price= Integer.parseInt(price);
            if (Price==1){
                filteredHotels = filteredHotels.stream()
                        .filter(hotel -> hotel.getPrice() <= 100)
                        .collect(Collectors.toList());
            } else if (Price==2) {
                filteredHotels = filteredHotels.stream()
                        .filter(hotel -> hotel.getPrice() > 100&&hotel.getPrice()<=300)
                        .collect(Collectors.toList());
            } else if (Price==3) {
                filteredHotels = filteredHotels.stream()
                        .filter(hotel -> hotel.getPrice() > 300&&hotel.getPrice()<=600)
                        .collect(Collectors.toList());
            } else if (Price==4) {
                filteredHotels = filteredHotels.stream()
                        .filter(hotel -> hotel.getPrice() > 600&&hotel.getPrice()<=1500)
                        .collect(Collectors.toList());
            }else {
                filteredHotels = filteredHotels.stream()
                        .filter(hotel -> hotel.getPrice() >1500)
                        .collect(Collectors.toList());
            }

        }

        if (!keyword.isEmpty()) {
            filteredHotels = filteredHotels.stream()
                    .filter(hotel -> (hotel.getName().contains(keyword)
                            ||hotel.getAddress().contains(keyword)||hotel.getCity().equals(keyword)))
                    .collect(Collectors.toList());
        }


        Long p=filteredHotels.stream().count();

        Page<Hotel> result1=hotelService.page(new Page<>(page,size,p));

        result1.setRecords(filteredHotels);
        result1.setCurrent(page);
        result1.setSize(size);

        int startIndex = (page - 1) * size;
        int endIndex = (int) Math.min(startIndex + size, p);

        PageResult result3 = new PageResult(p,result1.getRecords()
                .subList(startIndex,endIndex));
        log.info("总数p= "+p);

        if (filteredHotels.isEmpty()){
            return new PageResult();
        }

        return result3;


/*        List<Hotel> result=hotelMapper.selectList(keyword);
        Long p=hotelMapper.getTotal(keyword);
        Page<Hotel> result1=hotelService.page(new Page<>(page,size,p));
        result1.setRecords(result);

        result1.setCurrent(page);
        result1.setSize(size);
        int startIndex = (page - 1) * size;
        int endIndex = (int) Math.min(startIndex + size, p);

        PageResult result3 = new PageResult(p,result1.getRecords()
                .subList(startIndex,endIndex));
        log.info("总数p= "+p);
        if (result.isEmpty()){
            return new PageResult();
        }
        return result3;*/
    }

    @PostMapping
    public void saveHotel(@RequestBody Hotel hotel){
        // 新增酒店
        hotelService.save(hotel);
        // 发送MQ消息
        rabbitTemplate.convertAndSend(HotelMqConstants.EXCHANGE_NAME, HotelMqConstants.INSERT_KEY, hotel.getId());
    }

    @PutMapping()
    public void updateById(@RequestBody Hotel hotel){
        if (hotel.getId() == null) {
            throw new InvalidParameterException("id不能为空");
        }
        hotelService.updateById(hotel);

        // 发送MQ消息
        rabbitTemplate.convertAndSend(HotelMqConstants.EXCHANGE_NAME, HotelMqConstants.INSERT_KEY, hotel.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        hotelService.removeById(id);

        // 发送MQ消息
        rabbitTemplate.convertAndSend(HotelMqConstants.EXCHANGE_NAME, HotelMqConstants.DELETE_KEY, id);
    }
}

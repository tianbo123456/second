package com.example.project.service.impl;

import com.example.project.entity.Orders;
import com.example.project.mapper.OrdersMapper;
import com.example.project.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-03-04
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

}

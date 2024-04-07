package com.example.project.service.impl;

import com.example.project.entity.Messages;
import com.example.project.mapper.MessagesMapper;
import com.example.project.service.IMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息通知 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-03-04
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesMapper, Messages> implements IMessagesService {

}

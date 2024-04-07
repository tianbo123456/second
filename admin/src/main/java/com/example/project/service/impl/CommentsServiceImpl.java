package com.example.project.service.impl;

import com.example.project.entity.Comments;
import com.example.project.mapper.CommentsMapper;
import com.example.project.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-03-04
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {

}

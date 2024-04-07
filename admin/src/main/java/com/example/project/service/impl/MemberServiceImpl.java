package com.example.project.service.impl;

import com.example.project.entity.Member;
import com.example.project.mapper.MemberMapper;
import com.example.project.service.IMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-03-04
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

}

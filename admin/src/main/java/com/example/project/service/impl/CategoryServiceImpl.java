package com.example.project.service.impl;

import com.example.project.entity.Category;
import com.example.project.mapper.CategoryMapper;
import com.example.project.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 维修类型 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-03-04
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}

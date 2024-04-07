package com.example.project.service.impl;

import com.example.project.entity.Material;
import com.example.project.mapper.MaterialMapper;
import com.example.project.service.IMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 维修材料 服务实现类
 * </p>
 *
 * @author 
 * @since 2024-03-04
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {

}

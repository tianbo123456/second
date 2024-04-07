package com.example.project.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import cn.hutool.core.annotation.Alias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.project.common.LDTConfig;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
* <p>
* 待接单记录
* </p>
*
* @author 
* @since 2024-03-04
*/
@Getter
@Setter
@ApiModel(value = "Preorders对象", description = "待接单记录")
public class Preorders implements Serializable {

private static final long serialVersionUID = 1L;

    // 编号
    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 接单维修工
    @ApiModelProperty("接单维修工")
    @Alias("接单维修工")
    private Integer userId;


    // 维修单
    @ApiModelProperty("维修单")
    @Alias("维修单")
    private String name;


    // 图片
    @ApiModelProperty("图片")
    @Alias("图片")
    private String img;


    // 发单用户
    @ApiModelProperty("发单用户")
    @Alias("发单用户")
    private Integer bizUserId;


    // 维修单编号
    @ApiModelProperty("维修单编号")
    @Alias("维修单编号")
    private Integer goodid;



}
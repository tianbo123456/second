package com.example.project.entity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
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
* 维修单
* </p>
*
* @author 
* @since 2024-03-04
*/
@Getter
@Setter
@ApiModel(value = "Repairs对象", description = "维修单")
public class Repairs implements Serializable {

private static final long serialVersionUID = 1L;

    // 编号
    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 维修类型
    @ApiModelProperty("维修类型")
    @Alias("维修类型")
    private Integer categoryId;


    // 发单用户
    @ApiModelProperty("发单用户")
    @Alias("发单用户")
    private Integer userId;


    // 维修标题
    @ApiModelProperty("维修标题")
    @Alias("维修标题")
    private String name;


    // 详细内容
    @ApiModelProperty("详细内容")
    @Alias("详细内容")
    private String content;


    // 图片
    @ApiModelProperty("图片")
    @Alias("图片")
    private String img;


    // 发布时间
    @ApiModelProperty("发布时间")
    @Alias("发布时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    // 状态,待接单|已接单
    @ApiModelProperty("状态,待接单|已接单")
    @Alias("状态,待接单|已接单")
    private String stateRadio;



}
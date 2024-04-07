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
* 维修材料
* </p>
*
* @author 
* @since 2024-03-04
*/
@Getter
@Setter
@ApiModel(value = "Material对象", description = "维修材料")
public class Material implements Serializable {

private static final long serialVersionUID = 1L;

    // 编号
    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 记录人
    @ApiModelProperty("记录人")
    @Alias("记录人")
    private Integer userId;


    // 材料名
    @ApiModelProperty("材料名")
    @Alias("材料名")
    private String name;


    // 库存量
    @ApiModelProperty("库存量")
    @Alias("库存量")
    private String stock;


    // 备注
    @ApiModelProperty("备注")
    @Alias("备注")
    private String content;


    // 记录时间
    @ApiModelProperty("记录时间")
    @Alias("记录时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;



}
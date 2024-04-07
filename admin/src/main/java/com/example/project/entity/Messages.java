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
* 消息通知
* </p>
*
* @author 
* @since 2024-03-04
*/
@Getter
@Setter
@ApiModel(value = "Messages对象", description = "消息通知")
public class Messages implements Serializable {

private static final long serialVersionUID = 1L;

    // 编号
    @ApiModelProperty("编号")
    @Alias("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    // 通知内容
    @ApiModelProperty("通知内容")
    @Alias("通知内容")
    private String content;


    // 通知时间
    @ApiModelProperty("通知时间")
    @Alias("通知时间")
    private String time;


    // 通知人
    @ApiModelProperty("通知人")
    @Alias("通知人")
    private Integer userId;


    // 是否已读
    @ApiModelProperty("是否已读")
    @Alias("是否已读")
    private Boolean isread;



}
package com.huawei.ibooking.model;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import com.huawei.ibooking.validator.UpdateAction;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
@TableName("tbl_student")
public class StudentDO {
    @NotNull(groups= UpdateAction.class, message = "不能为空")
    private Integer id;
    @NotNull(message = "不能为空")
    @Length(max=16, min=1, message = "长度在16个字符以内")
    private String stuNum;
    @NotNull(message = "不能为空")
    @Length(max=16, min=1, message = "长度在16个字符以内")
    private String name;
    @NotNull(message = "不能为空")
    @Length(max=16, min=1, message = "长度在16个字符以内")
    private String password;
    @NotNull(message = "不能为空")
    private Integer credit;
    @NotNull(message = "不能为空")
    @Email(message = "格式不正确")
    private String email;
}

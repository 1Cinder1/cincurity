package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    private Integer phone;


    private String email;


    private String password;


    private String avatar;


    private Integer smokingStatus;


    private Integer age;


    private Integer gender;


    private Integer everMarried;


    private Integer workType;


    private Integer residenceType;


    private Integer asthma;


    private Integer kidneyDisease;


    private Integer skinCancer;


    private java.util.Date createTime;


    private java.util.Date createUser;


    private Integer isDeleted;


    private byte isBanned;


    private Integer heartDisease;


    private Integer alcoholDrinking;


    private Integer race;


    private Integer diabetic;


    private Integer genHealth;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
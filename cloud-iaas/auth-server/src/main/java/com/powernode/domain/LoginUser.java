package com.powernode.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
* ClassName:User
* Package:com.powernode.domain
* Description:
* Date:2022/11/5 14:51
* author:abc
*/
/**
    * 用户表
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`user`")
public class LoginUser implements Serializable, UserDetails {
    /**
     * 会员表主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 微信的openid
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 用户昵称
     */
    @TableField(value = "nick_name")
    private String nickName;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name")
    private String realName;

    /**
     * 用户邮箱
     */
    @TableField(value = "user_mail")
    private String userMail;

    /**
     * 登录密码
     */
    @TableField(value = "login_password")
    private String loginPassword;

    /**
     * 支付密码
     */
    @TableField(value = "pay_password")
    private String payPassword;

    /**
     * 手机号码
     */
    @TableField(value = "user_mobile")
    private String userMobile;

    /**
     * 修改时间
     */
    @TableField(value = "modify_time")
    private Date modifyTime;

    /**
     * 注册时间
     */
    @TableField(value = "user_regtime")
    private Date userRegtime;

    /**
     * 注册IP
     */
    @TableField(value = "user_regip")
    private String userRegip;

    /**
     * 最后登录时间
     */
    @TableField(value = "user_lasttime")
    private Date userLasttime;

    /**
     * 最后登录IP
     */
    @TableField(value = "user_lastip")
    private String userLastip;

    /**
     * 备注
     */
    @TableField(value = "user_memo")
    private String userMemo;

    /**
     * M(男) or F(女)
     */
    @TableField(value = "sex")
    private String sex;

    /**
     * 例如：2009-11-27
     */
    @TableField(value = "birth_date")
    private String birthDate;

    /**
     * 头像图片路径
     */
    @TableField(value = "pic")
    private String pic;

    /**
     * 状态 1 正常 0 无效
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 用户积分
     */
    @TableField(value = "score")
    private Integer score;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //因为小程序会员没有权限
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return "$2a$10$TQV09sgWZi5LlsdQ2UPXd.Zv/bTknotEW1Ea7yc.BQ7Npe9qimkza";
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }

    /*public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("WECHAT"));
    }*/
}
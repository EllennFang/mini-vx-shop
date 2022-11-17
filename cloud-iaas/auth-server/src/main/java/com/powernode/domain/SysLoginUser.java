package com.powernode.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ClassName:SysUser
 * Package:com.powernode.domain
 * Description:
 * Date:2022/10/27 9:50
 * author:abc
 */
/**
 * 系统用户
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class SysLoginUser implements Serializable , UserDetails {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;


    /**
     * 状态  0：禁用   1：正常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 后台管理员用户的权限集合
     */
    @TableField(exist = false)
    private List<String> auths;

    /**
     * 权限
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //创建权限集合
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        //循环遍历auths
        auths.forEach(auth -> {
            //处理权限中的空值和,逗号
            //判断权限是否有值
            if (StrUtil.isNotEmpty(auth)) {
                //判断是否有,号，有说明里面包含多个权限
                if (auth.contains(",")) {
                    //分隔
                    String[] authArray = auth.split(",");
                    //循环数组
                    for (String singleAuth : authArray) {
                        authorities.add(new SimpleGrantedAuthority(singleAuth));
                    }
                } else {
                    //没有
                    authorities.add(new SimpleGrantedAuthority(auth));
                }
            }
        });

        return authorities;
    }

    /**
     * 用户名一般我们给用户的id
     * @return
     */
    public String getUsername() {
        return String.valueOf(this.userId);
    }

    public String getPassword() {
        return this.password;
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
}
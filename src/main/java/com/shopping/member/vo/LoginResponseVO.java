package com.shopping.member.vo;

import com.shopping.comm.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseVO {
    private String token;
    private String name;
    private Role role;
}

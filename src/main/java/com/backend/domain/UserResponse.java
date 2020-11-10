package com.backend.domain;

import com.backend.object.request.UserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    private String username;
    private String password;

    public UserResponse(UserRequest u) {
        BeanUtils.copyProperties(u, this);
    }

    public void update(UserRequest condition) {
        BeanUtils.copyProperties(condition, this);
    }
}

package com.pivinadanang.blog.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 10, nullable = true)
    private String phoneNumber;

    // ALTER TABLE users ADD COLUMN email VARCHAR(255) DEFAULT '';
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    //ALTER TABLE users ADD COLUMN profile_image VARCHAR(255) DEFAULT '';
    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "password", length = 200, nullable = false)
    private String password;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "facebook_account_id")
    private String facebookAccountId;

    @Column(name = "google_account_id")
    private String googleAccountId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<CommentEntity> comments = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+getRole().getName().toUpperCase()));
//        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));


        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

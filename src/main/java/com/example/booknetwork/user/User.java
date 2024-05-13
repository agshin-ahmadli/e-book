package com.example.booknetwork.user;

import com.example.booknetwork.book.Book;
import com.example.booknetwork.history.BookTransactionHistory;
import com.example.booknetwork.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;

    private String lastName;

    private LocalDate dataOfBirth;

    @Column(unique = true)
    private String email;

    private String password;

    private boolean accountLocked;

    private boolean enabled;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate lastModifiedDate;


    @ManyToMany(fetch = FetchType.LAZY)
    private List<Role> roleList;

    @OneToMany(mappedBy = "owner")
    private List<Book> books = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory> transactionHistories = new ArrayList<>();

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roleList
                .stream()
                .map(r-> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true ;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String fullName(){
        return firstName + " " + lastName;
    }
}

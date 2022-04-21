package com.app.gestionInterventions.models.blackList;

import com.app.gestionInterventions.models.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "JwtBlackList")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtBlackList {
    @Id
    @JsonIgnore
    private final String token;
    @DBRef
    private  final User authentificator;
    private final Date logged_in;

    private final LocalDateTime logged_out;

    public JwtBlackList(String token,User user,Date logged_in,LocalDateTime logged_out) {
        this.token = token.substring(7,token.length());
        this.authentificator=user;
        this.logged_in=logged_in;
        this.logged_out= logged_out==null?LocalDateTime.now():logged_out;
    }

    public String getToken() {
        return token;
    }

    public User getAuthentificator() {
        return authentificator;
    }

    public Date getLogged_in() {
        return logged_in;
    }

    public LocalDateTime getLogged_out() {
        return logged_out;
    }
}

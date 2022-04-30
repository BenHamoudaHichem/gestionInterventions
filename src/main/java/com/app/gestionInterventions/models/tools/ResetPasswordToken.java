package com.app.gestionInterventions.models.tools;

import com.app.gestionInterventions.models.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
@Document(collection = "resetPasswords")
public class ResetPasswordToken {
    private static final int EXPIRATION = 10 * 60 * 1000;

    @Id
    private String id;
    @NotEmpty
    private String token;
    @DBRef
    @NotNull
    private User user;
    private Date expiryDate;


    public ResetPasswordToken() {
        super();
    }
    public ResetPasswordToken(String id, String token, User user) {
        super();
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);

    }
    public ResetPasswordToken(String token, User user) {
        this(null,token,user);


    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar date = Calendar.getInstance();
        long timeInSecs = date.getTimeInMillis();
        return new Date(timeInSecs + EXPIRATION);
    }
}

package com.example.itextpdf.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    /**
     * user id.
     */
    private Long id;

    /**
     * username.
     */
    private String userName;

    /**
     * email.
     */
    private String email;

    /**
     * phoneNumber.
     */
    private long phoneNumber;

    /**
     * description.
     */
    private String description;
}

package com.pubx.userservice.dto.request;

import com.pubx.userservice.enums.OrgType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProjectProfileRequest {

    @NotBlank(message = "Organization name is required")
    @Size(max = 255)
    private String orgName;

    @Size(max = 100)
    private String username;

    @Size(max = 2000)
    private String description;

    private OrgType orgType;
    private String city;
    private String state;
    private String websiteUrl;
    private String linkedinUrl;
}

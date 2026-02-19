package com.efsenkovski.reservasalas.infraadapters.in.api.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest(@NotBlank(message = "Name is mandatory!") String name,
                          @NotBlank(message = "Email is mandatory") @Email(message = "Enter a valid email address!", regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
                                  flags = Pattern.Flag.CASE_INSENSITIVE) String email) {
}

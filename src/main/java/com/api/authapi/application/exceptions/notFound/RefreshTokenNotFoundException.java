package com.api.authapi.application.exceptions.notFound;

public class RefreshTokenNotFoundException extends NotFoundException {
    public RefreshTokenNotFoundException() {
        super(
                "REFRESH_NOT_FOUND",
                "Refresh token not found."
        );
    }
}

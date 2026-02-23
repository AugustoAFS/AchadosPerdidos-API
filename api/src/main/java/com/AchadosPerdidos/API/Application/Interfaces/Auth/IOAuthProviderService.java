package com.AchadosPerdidos.API.Application.Interfaces.Auth;

import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.OAuthUserDTO;

public interface IOAuthProviderService {

    String generateAuthorizationUrl();

    OAuthUserDTO exchangeCodeForUserInfo(String authorizationCode);

    String getRedirectUrl();
}

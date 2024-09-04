package uk.co.dajohnston.houseworkapi.openapi;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.OAUTH2;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    security = {@SecurityRequirement(name = "OAuth2")},
    servers = {@Server(url = "/", description = "Default Server URL")})
@SecurityScheme(
    name = "OAuth2",
    type = OAUTH2,
    flows =
        @OAuthFlows(
            password =
                @OAuthFlow(
                    scopes = {
                      @OAuthScope(name = "create:users"),
                      @OAuthScope(name = "read:users"),
                      @OAuthScope(name = "offline_access")
                    },
                    tokenUrl = "https://dajohnston.eu.auth0.com/oauth/token",
                    authorizationUrl = "https://dajohnston.eu.auth0.com/oauth/authorize")))
public class OpenApiConfig {}
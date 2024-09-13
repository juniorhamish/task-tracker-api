# Auth0

There is a Machine to Machine application defined for getting tokens for Swagger and Postman. The
client ID is TIaYE5fDGYH6KYFWQHHyWOr933yI0agZ, get the client secret by logging into the Auth0
dashboard.

In order to get a token, you have to specify an audience, Swagger does not do this, so Auth0 is
configured to have a default audience of
`https://task-tracker-api-594170868558.europe-west9.run.app`. If the audience URL is ever changed,
remember to update the default one in Auth0 so that swagger will keep working.

There is a single page application defined in Auth0 too (JtOVDaDEeSOT7HAaBt0tLBFJvb5g7tyh). This is
only used by the UI for generating tokens.

For accessing the management API, e.g. to retrieve user info, there is another machine to machine
application defined (ftsF6gKFOfQTomApZdAfMBeqyZWWKhgM). Should probably learn more about this to
understand why I can't just use the same Machine to Machine application as above for this.

## Roles

Auth0 allows you to create roles and each role will have a set of scopes. The access token will
contain the scopes relevant to the role the user has and the scopes requested in the token request.
The role information is not included in the token. I previously configured Auth0 to add the user's
roles to a custom claim in the token as part of the post login flow, however I have now removed this
in favour of just using scopes. If it needs to be added back in, remember to update the Auth0
action.

```javascript
    if (event.authorization?.roles && event.authorization?.roles.length > 0) {
  api.accessToken.setCustomClaim(namespace + 'roles', event.authorization?.roles);
}
```

SpringBoot requires some extra processing to take the roles from the custom claim and add them into
the collection of granted authorities.

```java

@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
  var originalConverter = new JwtGrantedAuthoritiesConverter();
  var jwtAuthenticationConverter = new JwtAuthenticationConverter();
  jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
      (Jwt jwt) -> {
        Collection<GrantedAuthority> result = originalConverter.convert(jwt);
        var rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName(customClaimNamespace + "/roles");
        rolesConverter.setAuthorityPrefix("ROLE_");
        result.addAll(rolesConverter.convert(jwt));
        return result;
      });
  return jwtAuthenticationConverter;
}
```

## Scopes

Initially I created a few roles and had Auth0 assign the basic user role to everyone on first login.
This role then provided scopes like read:users and create:users, however this is overkill and I have
removed it. Auth0 no longer adds the role to the user, so the scopes won't be present and I have
removed the code that enforces them. If you ever want to look at scopes again, the controller
methods should be annotated with:

```
@PreAuthorize("hasAuthority('SCOPE_create:users')")
```

where `create:users` is the name of the scope. `SCOPE_` is a prefix that identifies the `authority`
as a scope.
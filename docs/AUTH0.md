# Auth0

There is a Machine to Machine application defined for getting tokens for Swagger and Postman. The client ID is TIaYE5fDGYH6KYFWQHHyWOr933yI0agZ, get the client secret by logging into the Auth0 dashboard.

In order to get a token, you have to specify an audience, Swagger does not do this, so Auth0 is configured to have a default audience of `https://task-tracker-api-594170868558.europe-west9.run.app`. If the audience URL is ever changed, remember to update the default one in Auth0 so that swagger will keep working.

There is a single page application defined in Auth0 too (JtOVDaDEeSOT7HAaBt0tLBFJvb5g7tyh). This is only used by the UI for generating tokens.

For accessing the management API, e.g. to retrieve user info, there is another machine to machine application defined (ftsF6gKFOfQTomApZdAfMBeqyZWWKhgM). Should probably learn more about this to understand why I can't just use the same Machine to Machine application as above for this.
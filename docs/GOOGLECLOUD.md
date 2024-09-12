# Google Cloud Run

The application is deployed to google cloud run using a GitHub action in the main branch. There was
a lot of messing around with service accounts to get it to work. Worth keeping in mind is that the
Workload Identity Federation that grants the access to deploy the application using the service
account contains a check that the `subject` is coming from the GitHub repository name (
juniorhamish/task-tracker-api). So if this ever changes again, make sure to update that access.

Workload Identity Federation -> GitHub Actions Pool -> Grant Access -> Grant access using service
account impersonation

Choose the service account, select the subject attribute from the dropdown and set that to repo:
`juniorhamish/task-tracker-api:ref:refs/heads/main`, replacing task-tracker-api with the name of the
repo.
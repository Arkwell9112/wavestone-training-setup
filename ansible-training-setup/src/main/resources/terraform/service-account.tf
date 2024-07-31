resource "google_service_account" "no_rights" {
  account_id   = "no-rights"
  display_name = "No rights SA for VMs."
}

resource "google_service_account" "read_rights" {
  account_id   = "read-rights"
  display_name = "Read infrastructure rights SA for VMs."
}

resource "google_project_iam_member" "read_rights_binding" {
  project = "{project}"
  role    = "roles/datastore.owner"
  member  = "serviceAccount:${google_service_account.read_rights.email}"
}

resource "google_service_account_key" "read_rights_key" {
  service_account_id = google_service_account.read_rights.name
}

output "read_rights_private_key" {
  value = google_service_account_key.read_rights_key.private_key
}

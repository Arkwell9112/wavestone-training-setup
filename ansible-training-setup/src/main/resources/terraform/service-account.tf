resource "google_service_account" "no_rights" {
  account_id   = "no-rights"
  display_name = "No rights SA for VMs."
}

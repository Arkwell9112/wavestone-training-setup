resource "google_project_service" "services_iam" {
  service = "iam.googleapis.com"

  disable_on_destroy = false
}

resource "google_project_service" "services_compute" {
  service = " compute.googleapis.com"

  disable_on_destroy = false
}

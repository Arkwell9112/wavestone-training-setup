resource "google_compute_firewall" "deny_all_ingress" {
  name     = "deny-all-ingress"
  network  = google_compute_network.vpc.name
  priority = 999

  direction = "INGRESS"

  source_ranges = ["0.0.0.0/0"]

  deny {
    protocol = "all"
    ports    = []
  }
}

resource "google_compute_firewall" "allow_all_egress" {
  name     = "allow-all-egress"
  network  = google_compute_network.vpc.name
  priority = 999

  direction = "EGRESS"

  destination_ranges = ["0.0.0.0/0"]

  allow {
    protocol = "all"
    ports    = []
  }
}

resource "google_compute_firewall" "user-x-ingress-internal-id" {
  name     = "{user-x-ingress-internal-name}"
  network  = google_compute_network.vpc.name
  priority = 998

  direction = "INGRESS"

  source_ranges      = ["10.1.1.0/24"]
  destination_ranges = ["{user-x-ingress-destination-range}"]

  allow {
    protocol = "tcp"
    ports    = [22]
  }
}

resource "google_compute_firewall" "user-x-ingress-external-id" {
  name     = "{user-x-ingress-external-name}"
  network  = google_compute_network.vpc.name
  priority = 998

  direction = "INGRESS"

  source_ranges      = ["0.0.0.0/0"]
  destination_ranges = ["{user-x-ingress-destination-range}"]

  allow {
    protocol = "tcp"
    ports    = [80]
  }
}

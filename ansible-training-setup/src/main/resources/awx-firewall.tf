resource "google_compute_firewall" "awx_ingress_external" {
  name     = "awx-ingress-external"
  network  = google_compute_network.vpc.name
  priority = 999

  direction = "INGRESS"

  source_ranges      = ["0.0.0.0/0"]
  destination_ranges = ["10.1.1.0/24"]

  allow {
    protocol = "tcp"
    ports    = [8013, 8043, 22]
  }
}

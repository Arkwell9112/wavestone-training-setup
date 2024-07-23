resource "google_compute_subnetwork" "awx_subnet" {
  name          = "awx-subnet"
  ip_cidr_range = "10.1.1.0/8"
  network       = google_compute_network.vpc.id
}

resource "google_compute_address" "awx_ip" {
  name = "awx-ip"
}

resource "google_compute_instance" "awx_vm" {
  name         = "awx-vm"
  machine_type = "{awx-machine-type}"

  labels = {
    category = "awx"
  }

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-11"
    }
  }

  scratch_disk {
    interface = "NVME"
  }

  network_interface {
    subnetwork = google_compute_subnetwork.awx_subnet.name
    access_config {
      nat_ip = google_compute_address.awx_ip.address
    }
  }

  service_account {
    email  = google_service_account.no_rights.email
    scopes = ["cloud-platform"]
  }
}

output "awx-vm-ip" {
  value = google_compute_address.awx_ip.address
}

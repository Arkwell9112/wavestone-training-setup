resource "google_compute_subnetwork" "user-x-subnet-id" {
  name          = "{user-x-subnet-name}"
  ip_cidr_range = "{user-x-subnet-range}"
  network       = google_compute_network.vpc.id
}

resource "google_compute_address" "user-x-ip-id" {
  name = "{user-x-ip-name}"
}

resource "google_compute_instance" "user-x-vm-id" {
  name         = "{user-x-vm-name}"
  machine_type = "{user-machine-type}"

  labels = {
    category = "user-workstation"
    owner    = "{user-x-owner}"
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
    subnetwork = google_compute_subnetwork.user-x-subnet-id.name
    access_config {
      nat_ip = google_compute_address.user-x-ip-id.address
    }
  }

  service_account {
    email  = google_service_account.no_rights.email
    scopes = ["cloud-platform"]
  }
}

output "user-x-vm-public-ip-output-id" {
  value = google_compute_address.user-x-ip-id.address
}

output "user-x-vm-private-ip-output-id" {
  value = google_compute_instance.user-x-vm-id.network_interface.0.network_ip
}

resource "google_compute_subnetwork" "user-x-subnet-id" {
  name          = "{user-x-subnet-name}"
  ip_cidr_range = "{user-x-subnet-range}"
  network       = google_compute_network.vpc.id
}

resource "google_compute_address" "user-x-ip-id_first" {
  name = "{user-x-ip-name}-first"
}

resource "google_compute_address" "user-x-ip-id_second" {
  name = "{user-x-ip-name}-second"
}

resource "google_compute_instance" "user-x-vm-id_first" {
  name         = "{user-x-vm-name}-first"
  machine_type = "{user-machine-type}"

  labels = {
    category = "user-workstation"
    owner    = "{user-x-owner}"
  }

  metadata = {
    "ssh-keys" = "ansible:{ssh-key} ansible"
  }

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-11"
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.user-x-subnet-id.name
    access_config {
      nat_ip = google_compute_address.user-x-ip-id_first.address
    }
  }

  service_account {
    email = google_service_account.no_rights.email
    scopes = ["cloud-platform"]
  }
}

resource "google_compute_instance" "user-x-vm-id_second" {
  name         = "{user-x-vm-name}-second"
  machine_type = "{user-machine-type}"

  labels = {
    category = "user-workstation"
    owner    = "{user-x-owner}"
  }

  metadata = {
    "ssh-keys" = "ansible:{ssh-key} ansible"
  }

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-11"
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.user-x-subnet-id.name
    access_config {
      nat_ip = google_compute_address.user-x-ip-id_second.address
    }
  }

  service_account {
    email = google_service_account.no_rights.email
    scopes = ["cloud-platform"]
  }
}

output "user-x-vm-private-ip-output-id_first" {
  value = google_compute_instance.user-x-vm-id_first.network_interface.0.network_ip
}

output "user-x-vm-public-ip-output-id_first" {
  value = google_compute_address.user-x-ip-id_first.address
}

output "user-x-vm-private-ip-output-id_second" {
  value = google_compute_instance.user-x-vm-id_second.network_interface.0.network_ip
}

output "user-x-vm-public-ip-output-id_second" {
  value = google_compute_address.user-x-ip-id_second.address
}

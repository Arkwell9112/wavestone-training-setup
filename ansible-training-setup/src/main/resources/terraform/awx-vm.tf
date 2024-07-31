resource "google_compute_subnetwork" "awx_subnet" {
  name          = "awx-subnet"
  ip_cidr_range = "10.1.1.0/24"
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

  metadata = {
    "ssh-keys" = "ansible:{ssh-key} ansible"
  }

  boot_disk {
    source = google_compute_disk.awx_vm_disk.name
  }

  network_interface {
    subnetwork = google_compute_subnetwork.awx_subnet.name
    access_config {
      nat_ip = google_compute_address.awx_ip.address
    }
  }

  service_account {
    email = google_service_account.read_rights.email
    scopes = ["cloud-platform"]
  }
}

resource "google_compute_disk" "awx_vm_disk" {
  name  = "awx-vm-disk"
  image = "debian-cloud/debian-11"
  size  = 100
}

output "awx_vm_ip" {
  value = google_compute_address.awx_ip.address
}

terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "3.69.0"
    }
  }
}

provider "google" {
  credentials = file(var.keyfile_location)
  region      = var.default_region
  project     = var.gcp_project_id
}

#################################################
##
## General
##

//resource "google_compute_network" "main" {
//  name = "${var.prefix}-network"
//
//  lifecycle {
//    create_before_destroy = true
//  }
//}
//
//resource "google_compute_subnetwork" "main" {
//  name          = "${var.prefix}-subnet"
//  network       = google_compute_network.main.name
//  ip_cidr_range = var.private_network_cidr
//  region        = var.region
//}

#################################################
##
## Local variables
##

locals {
  default_target_list = [
    for name, machine in google_compute_instance.default :
    "${machine.zone}/${machine.name}"
  ]

  default_disks = flatten([
    for machine_name, machine in var.machines : [
      for disk_name, disk in machine.additional_disks : {
        "${machine_name}-${disk_name}" = {
          "machine_name" : machine_name,
          "machine" : machine,
          "disk_size" : disk.size,
          "disk_name" : disk_name
        }
      }
    ]
  ])
}

#################################################
##
## default
##

resource "google_compute_address" "default" {
  for_each = {
    for name, machine in var.machines :
    name => machine
  }

  name         = "${each.key}-pip"
  address_type = "EXTERNAL"
  region       = each.value.region
}

resource "google_compute_disk" "default" {
  for_each = {
    for item in local.default_disks :
    keys(item)[0] => values(item)[0]
  }

  name = each.key
  type = "pd-ssd"
  zone = each.value.machine.zone
  size = each.value.disk_size

  physical_block_size_bytes = 4096
}

resource "google_compute_attached_disk" "default" {
  for_each = {
    for item in local.default_disks :
    keys(item)[0] => values(item)[0]
  }

  disk     = google_compute_disk.default[each.key].id
  instance = google_compute_instance.default[each.value.machine_name].id
}

resource "google_compute_instance" "default" {
  for_each = {
    for name, machine in var.machines :
    name => machine
  }

  name         = each.key
  machine_type = each.value.size
  zone         = each.value.zone

  tags = [each.value.node_type, each.value.type]

  boot_disk {
    initialize_params {
      image = each.value.boot_disk.image_name
      size  = each.value.boot_disk.size
    }
  }

  network_interface {
    subnetwork = var.network-name

    access_config {
      nat_ip = google_compute_address.default[each.key].address
    }
  }

  metadata = {
    ssh-keys = "ubuntu:${trimspace(file(pathexpand(var.ssh_pub_key)))}"
  }

  service_account {
    email  = var.default_sa_email
    scopes = var.default_sa_scopes
  }

  # Since we use google_compute_attached_disk we need to ignore this
  lifecycle {
    ignore_changes = ["attached_disk"]
  }
}

resource "google_compute_firewall" "default" {
  name    = "${var.network-name}-network-allow-6443"
  network = var.network-name
  allow {
    protocol = "tcp"
    ports    = ["6443"]
  }
}



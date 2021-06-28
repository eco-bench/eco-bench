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
  region      = var.region
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
  master_target_list = [
    for name, machine in google_compute_instance.master :
    "${machine.zone}/${machine.name}"
  ]

  worker_target_list = [
    for name, machine in google_compute_instance.worker :
    "${machine.zone}/${machine.name}"
  ]

  master_disks = flatten([
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
    if machine.node_type == "master"
  ])

  worker_disks = flatten([
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
    if machine.node_type == "worker"
  ])
}

#################################################
##
## Master
##

resource "google_compute_address" "master" {
  for_each = {
    for name, machine in var.machines :
    name => machine
    if machine.node_type == "master"
  }

  name         = "${var.prefix}-${each.key}-pip"
  address_type = "EXTERNAL"
  region       = var.cloud_region
}

resource "google_compute_disk" "master" {
  for_each = {
    for item in local.master_disks :
    keys(item)[0] => values(item)[0]
  }

  name = "${var.prefix}-${each.key}"
  type = "pd-ssd"
  zone = each.value.machine.zone
  size = each.value.disk_size

  physical_block_size_bytes = 4096
}

resource "google_compute_attached_disk" "master" {
  for_each = {
    for item in local.master_disks :
    keys(item)[0] => values(item)[0]
  }

  disk     = google_compute_disk.master[each.key].id
  instance = google_compute_instance.master[each.value.machine_name].id
}

resource "google_compute_instance" "master" {
  for_each = {
    for name, machine in var.machines :
    name => machine
    if machine.node_type == "master"
  }

  name         = "${var.prefix}-${each.key}"
  machine_type = each.value.size
  zone         = each.value.zone

  tags = ["master"]

  boot_disk {
    initialize_params {
      image = each.value.boot_disk.image_name
      size  = each.value.boot_disk.size
    }
  }

  network_interface {
    subnetwork = var.network-name

    access_config {
      nat_ip = google_compute_address.master[each.key].address
    }
  }

  metadata = {
    ssh-keys = "ubuntu:${trimspace(file(pathexpand(var.ssh_pub_key)))}"
  }

  service_account {
    email  = var.master_sa_email
    scopes = var.master_sa_scopes
  }

  # Since we use google_compute_attached_disk we need to ignore this
  lifecycle {
    ignore_changes = ["attached_disk"]
  }
}

#################################################
##
## Worker
##

resource "google_compute_disk" "worker" {
  for_each = {
    for item in local.worker_disks :
    keys(item)[0] => values(item)[0]
  }

  name = "${var.prefix}-${each.key}"
  type = "pd-ssd"
  zone = each.value.machine.zone
  size = each.value.disk_size

  physical_block_size_bytes = 4096
}

resource "google_compute_attached_disk" "worker" {
  for_each = {
    for item in local.worker_disks :
    keys(item)[0] => values(item)[0]
  }

  disk     = google_compute_disk.worker[each.key].id
  instance = google_compute_instance.worker[each.value.machine_name].id
}

resource "google_compute_address" "worker" {
  for_each = {
    for name, machine in var.machines :
    name => machine
    if machine.node_type == "worker"
  }

  name         = "${var.prefix}-${each.key}-pip"
  address_type = "EXTERNAL"
  region       = var.edge_region
}

resource "google_compute_instance" "worker" {
  for_each = {
    for name, machine in var.machines :
    name => machine
    if machine.node_type == "worker"
  }

  name         = "${var.prefix}-${each.key}"
  machine_type = each.value.size
  zone         = each.value.zone

  tags = ["worker"]

  boot_disk {
    initialize_params {
      image = each.value.boot_disk.image_name
      size  = each.value.boot_disk.size
    }
  }

  network_interface {
    subnetwork = var.network-name

    access_config {
      nat_ip = google_compute_address.worker[each.key].address
    }
  }

  metadata = {
    ssh-keys = "ubuntu:${trimspace(file(pathexpand(var.ssh_pub_key)))}"
  }

  service_account {
    email  = var.worker_sa_email
    scopes = var.worker_sa_scopes
  }

  # Since we use google_compute_attached_disk we need to ignore this
  lifecycle {
    ignore_changes = ["attached_disk"]
  }
}

resource "google_compute_address" "worker_lb" {
  name         = "${var.prefix}-worker-lb-address"
  address_type = "EXTERNAL"
  region       = var.edge_region
}





  terraform {
    required_providers {
      google = {
        source = "hashicorp/google"
        version = "~> 3.48"
      }
    }
  }

    provider "google" {

    credentials = file("~/eco-bench-96d0fa486a15.json")

    project = "eco-bench"
    region  = var.region
    zone    = var.zone
  }

  resource "google_compute_address" "worker" {
    name         = "${var.cluster_prefix}-edge-node-pip"
    address_type = "EXTERNAL"
    region       = var.region
  }

  resource "google_compute_instance_from_template" "tpl" {
    name = "${var.cluster_prefix}-edge-node"
    source_instance_template = "ubuntu-20-04-2cpu-2gb"

    network_interface {
      subnetwork = "${var.cluster_prefix}-subnet"

      access_config {
        nat_ip = google_compute_address.worker.address
      }
    }

    metadata = {
      ssh-keys = "ubuntu:${trimspace(file(pathexpand(var.ssh_pub_key)))}"
    }
  }

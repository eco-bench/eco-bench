variable "keyfile_location" {
  description = "Location of the json keyfile to use with the google provider"
  type        = string
}

variable "gcp_project_id" {
  description = "ID of the project"
  type        = string
}

variable "default_region" {
  type = string
}

variable "machines" {
  type = map(object({
    node_type = string
    type      = string
    size      = string
    region    = string
    zone      = string
    additional_disks = map(object({
      size = number
    }))
    boot_disk = object({
      image_name = string
      size       = number
    })
  }))
}

variable "default_sa_scopes" {
  type    = list(string)
  default = ["https://www.googleapis.com/auth/cloud-platform"]
}

variable "default_sa_email" {
  type    = string
  default = ""
}

variable "ssh_pub_key" {}

variable "ssh_whitelist" {
  type = list(string)
}

variable "api_server_whitelist" {
  type = list(string)
}

variable "nodeport_whitelist" {
  type = list(string)
}

variable "private_network_cidr" {
  default = "10.0.10.0/24"
}

variable "network-name" {
  default = "default"
}

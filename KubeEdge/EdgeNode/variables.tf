variable "ssh_pub_key" {
  description = "SSH key path"
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}

variable "region" {
  description = "Desired region"
  type        = string
  default     = "europe-central2"
}

variable "zone" {
  description = "Desired zone within the region"
  type        = string
  default     = "europe-central2-a"
}

variable "cluster_prefix" {
  description = "Prefix of the cluster the node will be deployed in"
  type        = string
  default     = "kubeedge-cluster"
}
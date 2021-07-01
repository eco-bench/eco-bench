output "default_ip_addresses" {
  value = {
    for key, instance in google_compute_instance.default :
    instance.name => {
      "private_ip" = instance.network_interface.0.network_ip
      "public_ip"  = instance.network_interface.0.access_config.0.nat_ip
    }
  }
}

//output "ingress_controller_lb_ip_address" {
//  value = google_compute_address.worker_lb.address
//}
//
//output "control_plane_lb_ip_address" {
//  value = google_compute_forwarding_rule.master_lb.ip_address
//}

resource "local_file" "AnsibleInventory" {
  content = templatefile("inventory.tmpl",
    {
      worker = {
        for key, instance in google_compute_instance.default :
        instance.name => instance.network_interface.0.access_config.0.nat_ip
        if contains(instance.tags, "worker")
      }
      master = {
        for key, instance in google_compute_instance.default :
        instance.name => instance.network_interface.0.access_config.0.nat_ip
        if contains(instance.tags, "master")
      }
      openyurt_instances = {
        for key, instance in google_compute_instance.default :
        instance.name => instance.network_interface.0.access_config.0.nat_ip
        if contains(instance.tags, "openyurt")
      }
      k3s_instances = {
        for key, instance in google_compute_instance.default :
        instance.name => instance.network_interface.0.access_config.0.nat_ip
        if contains(instance.tags, "k3s")
      }
      microk8s_instances = {
        for key, instance in google_compute_instance.default :
        instance.name => instance.network_interface.0.access_config.0.nat_ip
        if contains(instance.tags, "microk8s")
      }
      mongo_instances = {
        for key, instance in google_compute_instance.default :
        instance.name => instance.network_interface.0.access_config.0.nat_ip
        if contains(instance.tags, "mongodb")
      }
    }
  )
  filename = "./inventory.ini"
}

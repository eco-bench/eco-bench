### The Ansible inventory file
resource "local_file" "AnsibleInventory" {
  content = templatefile("inventory.tmpl",
    {
      worker1-name = google_compute_instance.worker1.name,
      worker1-ip   = google_compute_instance.worker1.network_interface.0.access_config.0.nat_ip,
      worker1-id   = google_compute_instance.worker1.instance_id,
      worker2-name = google_compute_instance.worker2.name,
      worker2-ip   = google_compute_instance.worker2.network_interface.0.access_config.0.nat_ip,
      worker2-id   = google_compute_instance.worker2.instance_id,
      master1-name = google_compute_instance.master1.name,
      master1-ip   = google_compute_instance.master1.network_interface.0.access_config.0.nat_ip,
      master1-id   = google_compute_instance.master1.instance_id,
  })
  filename = "inventory"
}
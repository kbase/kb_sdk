# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|

  # For a configuration reference, please see the online documentation at
  # https://docs.vagrantup.com.

  config.vm.box = "kbase/kb-sdk-test"

  # We create these directories in the VM:
  # /vagrant/kb_sdk -- this repo
  # /vagrant/jars -- jar repo
  # /vagrant/* can be any extra needed storage we want -- it goes in ./temp_vagrant

  config.vm.synced_folder "./", "/vagrant", disabled: true
  config.vm.synced_folder "./temp_vagrant", "/vagrant"
  config.vm.synced_folder "./", "/vagrant/kb_sdk"

  config.vm.provision "shell", inline: <<-SHELL
    # Pull the jars repo
    rm -rf /vagrant/jars
    git clone https://github.com/kbase/jars /vagrant/jars
  SHELL
end

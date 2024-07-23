#!/bin/bash

path="${PWD}"

if [[ ! -f "/ansible" ]]
then
  UBUNTU_CODENAME=jammy
  wget -O- "https://keyserver.ubuntu.com/pks/lookup?fingerprint=on&op=get&search=0x6125E2A8C77F2818FB7BD15B93C4A3FD7BB9C367" | sudo gpg --dearmour -o /usr/share/keyrings/ansible-archive-keyring.gpg
  echo "deb [signed-by=/usr/share/keyrings/ansible-archive-keyring.gpg] http://ppa.launchpad.net/ansible/ansible/ubuntu $UBUNTU_CODENAME main" | sudo tee /etc/apt/sources.list.d/ansible.list
  sudo apt update && sudo apt install ansible -y
  ssh-keygen -q -t rsa -b 4096 -N '' -f ansible/ssh-key
  touch /ansible
fi

cd ansible || exit

ansible-playbook setup-playbook.yml --extra-vars "path=${path} inputPath=${1}"

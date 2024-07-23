#!/bin/bash

path="$(dirname "$0")"

cd "${path}" || exit

if [[ ! -f "/ansible" ]]
then
  apt install wget -y
  apt install gnupg2 -y
  apt install coreutils -y
  UBUNTU_CODENAME=jammy
  wget -O- "https://keyserver.ubuntu.com/pks/lookup?fingerprint=on&op=get&search=0x6125E2A8C77F2818FB7BD15B93C4A3FD7BB9C367" | sudo gpg --dearmour -o /usr/share/keyrings/ansible-archive-keyring.gpg
  echo "deb [signed-by=/usr/share/keyrings/ansible-archive-keyring.gpg] http://ppa.launchpad.net/ansible/ansible/ubuntu $UBUNTU_CODENAME main" | sudo tee /etc/apt/sources.list.d/ansible.list
  sudo apt update && sudo apt install ansible -y
  touch /ansible
fi

ansible-playbook install-playbook.yml --extra-vars "path=${path} inputPath=${1}"

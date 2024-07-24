#!/bin/bash

path="${PWD}"

cd ansible || exit

ansible-playbook awx-setup-playbook.yml -i public-inventory-gcp.yml --extra-vars "path=${path}"

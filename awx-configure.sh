#!/bin/bash

path="${PWD}"

cd ansible || exit

ansible-playbook awx-configure-playbook.yml -i public-inventory-gcp.yml --extra-vars "path=${path} inputPath=${1}"

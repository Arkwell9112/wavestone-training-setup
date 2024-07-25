#!/bin/bash

path="${PWD}"

cd ansible || exit

ansible-playbook awx-check-playbook.yml -i public-inventory-gcp.yml --extra-vars "path=${path}"

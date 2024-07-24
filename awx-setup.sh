#!/bin/bash

cd ansible || exit

ansible-playbook awx-setup-playbook.yml -i public-inventory-gcp.yml

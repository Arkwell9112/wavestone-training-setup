#!/bin/bash

cd ansible || exit

ansible-playbook awx-check-playbook.yml -i public-inventory-gcp.yml

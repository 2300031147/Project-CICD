# Deploy to Kubernetes using Ansible

This document explains how to deploy the Project-CICD full-stack application (backend + frontend + MySQL) to a Kubernetes cluster using the included Ansible playbook and Kubernetes manifests.

Prerequisites
- `kubectl` configured to target your cluster.
- `docker` available locally (for building images).
- `ansible` installed (tested with Ansible 2.9+). The playbook uses local connection.

Quick steps (local cluster like `kind` or remote cluster):

1. (Optional) Edit variables in `ansible/vars.yml` or pass overrides with `-e`.
   - `registry` — container registry (leave empty to use local image names).
   - `tag` — image tag (default `latest`).
   - `push_images` — set to `true` if you want to `docker push` images to `registry`.
   - `jwt_secret` — set a secure JWT secret.

2. Build images and deploy with Ansible:

```powershell
cd "d:/Project CICD/Project-CICD"
ansible-playbook -i ansible/inventory.ini ansible/deploy.yml
```

Notes and tips
- For `kind`, after building images locally you can load them into kind with `kind load docker-image <image>` before applying manifests.
- For remote clusters you must push images to a registry and set `registry` and `push_images: true`.
- The playbook renders templates into `ansible/rendered` and runs `kubectl apply -f` against them.

What was added
- `ansible/` — playbook, variables, inventory, and templates.
- `DEPLOY_K8S_ANSIBLE.md` — deployment instructions.

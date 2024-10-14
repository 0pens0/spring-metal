#!/usr/bin/env python

import yaml
import os


workspaceDir = os.environ.get('TANZU_BUILD_WORKSPACE_DIR')

print("creating httproute")
with open(workspaceDir + '/output/containerapp.yml', 'r') as file:
    containerapp = yaml.safe_load(file)

with open(workspaceDir + '/httproute.yml', 'r') as file:
    httproute = yaml.safe_load(file)


httproute["metadata"]["name"] = containerapp["metadata"]["name"] + "-http-route"
httproute["metadata"]["annotations"]["apps.tanzu.vmware.com/promote-group"] = "ContainerApp/"+containerapp["metadata"]["name"]
httproute["metadata"]["annotations"]["apps.tanzu.vmware.com/promotable"] = ""

httproute["spec"]["rules"][0]["backendRefs"][0]["name"] = containerapp["metadata"]["name"]
httproute["spec"]["rules"][0]["backendRefs"][0]["port"] = containerapp["spec"]["ports"][0]["port"]

with open(workspaceDir + '/output/httproute.yml', 'w') as outfile:
    yaml.dump(httproute, outfile, default_flow_style=False)
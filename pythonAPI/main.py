from flask import Flask, request, jsonify
from kubernetes import client, config
from os import path
import uuid
import yaml

app = Flask(__name__)

# Load Kubernetes configuration from default location
config.load_kube_config()

with open(path.join(path.dirname(__file__), "mydeployment.yaml")) as f:
        deploymentYML = yaml.safe_load(f)

@app.route('/instances', methods=['POST'])
def create_instance():
    # Generate unique namespace name for the user
    user_namespace = str(uuid.uuid4())
    # Define Kubernetes Namespace object
    namespace = client.V1Namespace(metadata=client.V1ObjectMeta(name=user_namespace))
    try:
        coreAPI = client.CoreV1Api()
        appsAPI = client.AppsV1Api()

        # Create the namespace
        coreAPI.create_namespace(namespace)
        print("Namespace created. name='%s'" % namespace.metadata.name)
                
        # Create the deployment
        resp = appsAPI.create_namespaced_deployment(
            body=deploymentYML, namespace=user_namespace)
        print("Deployment created. status='%s'" % resp.metadata.name)
        
        # Return success response
        response = {'message': 'Instance created successfully', 'namespace': user_namespace}
        return jsonify(response), 201
    except Exception as e:
        # log the error
        print(e)
        # Return error response
        response = {'message': str(e)}
        return jsonify(response), 500

@app.route('/instances/<string:namespace>', methods=['DELETE'])
def delete_instance(namespace):
    coreAPI = client.CoreV1Api()
    appsAPI = client.AppsV1Api()
    # Check if the namespace exists and belongs to the user
    try:
        coreAPI.read_namespace(name=namespace)
        # Delete the namespace
        coreAPI.delete_namespace(name=namespace)
        # Return success response
        response = {'message': 'Instance deleted successfully'}
        return jsonify(response), 200
    except client.rest.ApiException as e:
        if e.status == 404:
            # Namespace not found
            response = {'message': 'Instance not found'}
            return jsonify(response), 404
        elif e.status == 403:
            # Forbidden - namespace does not belong to the user
            response = {'message': 'Instance does not belong to the user'}
            return jsonify(response), 403
        else:
            # Other error
            response = {'message': str(e)}
            return jsonify(response), 500

if __name__ == '__main__':
    app.run()

import psutil
import json

disk = psutil.disk_usage('/').percent
error = ""

data = json.dumps({"error": error, "value": disk})
print(data)

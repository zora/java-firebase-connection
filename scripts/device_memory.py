import psutil
import json
memory = psutil.virtual_memory().percent
error = ""
data = json.dumps({'error': error, 'value': memory})
print(data)

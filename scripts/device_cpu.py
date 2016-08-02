import psutil
import json
cpuUsage = psutil.cpu_percent(interval=1)
error = ""
data = json.dumps({'error': error, 'value': cpuUsage})
print(data)

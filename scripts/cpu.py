import subprocess
import json

script_path = "scripts/cpu.sh"
results = subprocess.check_output([script_path], stderr=subprocess.STDOUT)
results = results.decode('utf-8').strip()
error = ""
try:
    cpu_usage = float(results)
except ValueError:
    error = ("Unable to parse cpu_usage:" + results)

data = json.dumps({'error': error, 'value': cpu_usage})
print(data)

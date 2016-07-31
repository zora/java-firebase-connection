import json
from sense_hat import SenseHat
sense = SenseHat()

pressure = sense.get_pressure()
error = ""

data = json.dumps({'error':error, 'value': pressure})
print(data)

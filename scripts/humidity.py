import json
from sense_hat import SenseHat
sense = SenseHat()

temp = sense.get_temperature()
ppressure = sense.get_pressure()
humidity = sense.get_humidity()
calctemp = 0.0071*temp*temp + 0.86*temp -10.0
calchum = humidity * (2.5 - 0.029*temp)

calctemp = round(calctemp, 2)
calchum = round(calchum, 2)

error = ""

data = json.dumps({'error':error, 'value': humidity})
print(data)

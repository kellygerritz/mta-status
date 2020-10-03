Two exposed endpoints

GET '/status/{line}'
    Takes the single letter or digit of the subway line and returns whether or not it is delayed
    Non existing lines will return a 404

GET '/uptime/{line}'
    Takes the single letter or digit of the subway line and returns what percentage of time it has been up
    Non existing lines will return a 404

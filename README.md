# ZBirr
centralized money transferring and payment app using android, and django

Project done using:
	Google's Firebase for authentication,
	Django for backend and database,
	Android kotlin frontend,
                   
The backend is hosted on heroku. https://zbirr-rest.herokuapp.com/

The django project is located one up from this repository.
If you want to run the django server change the BASEURL constant in constants.kt in the android project to http://127.0.0.1:(some port).
Use chrome's portforwarding, forward port (some port) or any other port to http://127.0.0.1:8000.
Make sure to have both the computer and the android phone on the same network. Usb cable can also be used.

pip install -r requirements.txt -----> to run the django project, first install the requirements

Appetize link: https://appetize.io/embed/1p9hjhj8p9deqhxcm59dqvaa3r

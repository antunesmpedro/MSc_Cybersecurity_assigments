from flask import Flask, render_template, jsonify, request, make_response, send_file
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash
from flask_sslify import SSLify

from functools import wraps
from pki_helpers import generate_private_key, generate_public_key, generate_csr, sign_csr, create_client_certificates
from cryptography import x509
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes, serialization

import OpenSSL
import uuid
import werkzeug.serving
import jwt
import datetime
import ssl
import os
import socket
import math

#from werkzeug.middleware.proxy_fix import ProxyFix 		# this is for production server

'''
TODO: 

- download do client api e dos certificados

- fazer o client api

- criar servicos 1,2,3

'''

app = Flask(__name__)
#sslify = SSLify(app, permanent=True)         # this should redirect urls to https
#app.wsgi_app = ProxyFix(app.wsgi_app)		  # this is for production server
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///server_chat_message.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['MAIN_DIRECTORY'] = '~/Desktop/seg_software/api/teste/'
app.config['SECRET_KEY'] = '612510b887f93a9372ee146b0f956e28f0b7739272665b02849802df9c2b75c4'
#secret_key = sha256('grupo14')
#app.debug = True
db = SQLAlchemy(app)


'''
 this class is used for web server production
'''
class PeerCertWSGIRequestHandler( werkzeug.serving.WSGIRequestHandler ):
    """
    We subclass this class so that we can gain access to the connection
    property. self.connection is the underlying client socket. When a TLS
    connection is established, the underlying socket is an instance of
    SSLSocket, which in turn exposes the getpeercert() method.

    The output from that method is what we want to make available elsewhere
    in the application.
    """
    def make_environ(self):
        """
        The superclass method develops the environ hash that eventually
        forms part of the Flask request object.

        We allow the superclass method to run first, then we insert the
        peer certificate into the hash. That exposes it to us later in
        the request variable that Flask provides
        """
        environ = super(PeerCertWSGIRequestHandler, self).make_environ()
        x509_binary = self.connection.getpeercert(True)
        x509 = OpenSSL.crypto.load_certificate( OpenSSL.crypto.FILETYPE_ASN1, x509_binary )
        environ['peercert'] = x509
        return environ

'''
DATABASE MODELS
'''
class User(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	username = db.Column(db.String(50), unique=True)
	password = db.Column(db.String(80), nullable=False)
	name = db.Column(db.String(50), nullable=False)
	age = db.Column(db.Integer, nullable=False)
	cellphone = db.Column(db.Integer, nullable=False)
	cc_bi = db.Column(db.String(50), nullable=False)
	security_level = db.Column(db.Integer, nullable=False)


class Pre_User(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	one_time_id = db.Column(db.String(100), unique=True, nullable=False)
	name = db.Column(db.String(50), nullable=False)
	age = db.Column(db.Integer, nullable=False)
	cellphone = db.Column(db.Integer, nullable=False)
	cc_bi = db.Column(db.String(50), nullable=False)


'''
create ssl context and needed certificates and CAs
'''
def my_ssl_context():

	# generate CA keys pair

	if os.path.isfile("./ca_keys/ca-private-key.pem") == False and os.path.isfile("./ca_keys/ca-public-key.pem") == False:
		os.mkdir(app.config['MAIN_DIRECTORY'] + 'ca_keys')
		
		private_key = generate_private_key("./ca_keys/ca-private-key.pem", app.config['SECRET_KEY'])
		public_key = generate_public_key(
			private_key,
			filename="./ca_keys/ca-public-key.pem",
			country="PT",
			state="Porto",
			locality="Porto",
			org="SES_GRUPO_14",
			hostname="ses-grupo-14.com",
		)

	# generate SERVER key pairs and cert

	if os.path.isfile("./sign_keys_pair/server-private-key.pem") == False:
		os.mkdir(app.config['MAIN_DIRECTORY'] + 'sign_keys_pair')
		os.mkdir(app.config['MAIN_DIRECTORY'] + 'cert_requests')
		os.mkdir(app.config['MAIN_DIRECTORY'] + 'sign_keys_pair/clients')
		os.mkdir(app.config['MAIN_DIRECTORY'] + 'cert_requests/clients')
		
		server_private_key = generate_private_key("./sign_keys_pair/server-private-key.pem", app.config['SECRET_KEY'])
		generate_csr(
			server_private_key,
			filename="./cert_requests/server-csr.pem",
			country="PT",
			state="Port",
			locality="Porto",
			org="SERVER_GRUPO_14",
			alt_names=["localhost"],
			hostname="server-grupo-14.com",
		)

	# generate server_public_key
		csr_file = open("./cert_requests/server-csr.pem", "rb")
		csr = x509.load_pem_x509_csr(
			csr_file.read(), default_backend()
		)
		# get CA key pairs
		ca_public_key_file = open("./ca_keys/ca-public-key.pem", "rb")
		ca_public_key = x509.load_pem_x509_certificate(
			ca_public_key_file.read(), default_backend()
		)
		ca_private_key_file = open("./ca_keys/ca-private-key.pem", "rb")		
		ca_private_key = serialization.load_pem_private_key(
			ca_private_key_file.read(),
			app.config['SECRET_KEY'].encode("utf-8"),
			default_backend(),
		)
		server_public_key = sign_csr(csr, ca_public_key, ca_private_key, "./sign_keys_pair/server-public-key.pem")

	context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER) 
	context.load_cert_chain('./sign_keys_pair/server-public-key.pem', './sign_keys_pair/server-private-key.pem', app.config['SECRET_KEY'])
	
	# context to the web server production
	#context = ssl.create_default_context( purpose=ssl.Purpose.CLIENT_AUTH, cafile="./ca_keys/ca-public-key.pem" )
	#context.verify_mode = ssl.CERT_REQUIRED
	#context.load_verify_locations("./ca_keys/ca-public-key.pem")
	return context


def root_calc(x_root,n):
	x_root = float(x_root)
	n = float(n)
	if n > 0:
		res = n**(1./x_root)
	else:
		res = -((-n)**(1./x_root))

	return res

# decorated function to check access token
def token_required(f):
	@wraps(f)
	def decorated(*args, **kwargs):
		token = None

		if 'x-access-token' in request.headers:
			token = request.headers['x-access-token']
			#token = request.args.get('token') # http://127.0.0.1:/5000/route?token=lasjhfo8r4ujwe23ed

		if not token:
			return jsonify({'message': 'Token is missing!'}), 401

		try:
			data = jwt.decode(token, app.config['SECRET_KEY'])
			current_user = User.query.filter_by(id=data['public_id']).first()
		except:
			return jsonify({'message': 'Token is invalid!'}), 403

		return f(current_user, *args, **kwargs)

	return decorated


def create_server(app):

	@app.route('/')
	def index():
		#return render_template('helloworld.html', client_cert=request.environ['peercert'])
		return 'Hello World'

	@app.route('/pre-register', methods=['POST'])
	def add_pre_user():
		pre_user = request.get_json()
		
		try:
			new_pre_user = Pre_User(one_time_id = str(uuid.uuid4()), name = pre_user['name'], age = pre_user['age'], cellphone = pre_user['cellphone'], cc_bi = pre_user['cc_bi'])
			db.session.add(new_pre_user)
			db.session.commit()
		except:
			return jsonify({'message': 'Cannot create your pre_user!'}), 403

		#
		# TODO UPLOAD CLIENT API WITH REDIRECT URL?
		#

		return jsonify({'message': f"New Pre-User with the name {new_pre_user.name} created! You should register your account with ID = {new_pre_user.one_time_id}", "id": new_pre_user.one_time_id}), 200


	@app.route('/register/<pre_user_one_time_id>', methods=['POST'])
	def register(pre_user_one_time_id):

		pre_user = Pre_User.query.filter_by(one_time_id=pre_user_one_time_id).first()
		user = request.get_json()
		print(user)
		print(pre_user.name)

		hashed_password = generate_password_hash(user['password'], method='sha256')

		try:
			new_user = User(username=user['username'], password = hashed_password, name = pre_user.name, age = pre_user.age, 
				cellphone = pre_user.cellphone, cc_bi = pre_user.cc_bi, security_level=1)

			
			db.session.add(new_user)
			db.session.commit()

			create_client_certificates(new_user.id, user['password'],app.config['SECRET_KEY'])

			db.session.delete(pre_user)
			db.session.commit()
		except:
			return jsonify({'message': 'Cannot create your user!'}), 403
		

		#
		# TODO UPLOAD CLIENT CERTS WITH REDIRECT URL?
		#

		return jsonify({'message': f"New user {new_user.id} created!"}), 200



	@app.route('/login')
	def login():

		# buildind error response
		response_error =  make_response("Could not verify", 401)
		response_error.headers['WWW-Authenticate'] = 'Basic realm="Login Required!"'
		response_error.headers['Content-Type'] = 'application/json'

		auth = request.authorization

		if not auth or not auth.username or not auth.password:
			
			return response_error

		user = User.query.filter_by(name=auth.username).first()

		if not user:
			return response_error

		if check_password_hash(user.password, auth.password):
			token = jwt.encode({'public_id': user.id, 'exp' : datetime.datetime.utcnow() + datetime.timedelta(minutes=30)}, app.config['SECRET_KEY'])
			return jsonify({'token': token.decode('UTF-8'), 'user_id': user.id}), 200

		return response_error


	@app.route('/download', methods=['GET'])
	def download_api():
		path = app.config['MAIN_DIRECTORY'] + "download_teste.py"
		return send_file(path, as_attachment=True)

	@app.route('/users', methods=['GET'])
	@token_required
	def get_all_users(current_user):

		users = User.query.all()

		# this code serializes the data from users
		output= []
		for user in users:
			user_data = {}
			user_data['username'] = user.username
			user_data['name'] = user.name
			user_data['cc_bi'] = user.cc_bi
			user_data['security_level'] = user.security_level
			output.append(user_data) 

		return jsonify({"users": output}), 200


	@app.route('/user/<username>', methods=['GET'])
	@token_required
	def get_user(current_user, username):

		user = User.query.filter_by(username=username).first()

		if not user:
			return jsonify({'message' : 'No user found!'}), 403

		user_data = {}
		user_data['id'] = user.id
		user_data['username'] = user.username
		user_data['name'] = user.name
		user_data['cc_bi'] = user.cc_bi
		user_data['security_level'] = user.security_level

		return jsonify({'user': user_data})
	

	@app.route('/user/<username>', methods=['PUT'])
	@token_required
	def promote_user(current_user,username):

		if not current_user.username == "admin":
			return jsonify({'message' : 'Cannot perfom that function!'}), 401

		user = User.query.filter_by(username=username).first()

		if not user:
			return jsonify({'message' : 'No user found!'}), 403

		user.security_level = user.security_level + 1
		db.session.commit()

		return jsonify({'message': f"The user {user.id} has been promoted!"}), 200


	@app.route('/user/<username>', methods=['DELETE'])
	@token_required
	def delete_user(current_user, username):
		if not current_user.username == "admin":
			return jsonify({'message' : 'Cannot perfom that function!'}), 401

		user = User.query.filter_by(username=username).first()
		if not user:
			return jsonify({'message' : 'No user found!'}), 403
			
		db.session.delete(user)
		db.session.commit()
		return jsonify({'message': f"The user {user.id} has been deleted!"}), 200


	@app.route('/service_1', methods=['POST'])
	@token_required
	def service_1(current_user):
		if not current_user.security_level >= 1:
			return jsonify({'message' : 'Cannot perfom that function!'}), 401

		data = request.get_json()

		rt_res = root_calc(2, int(data['number']))
		return jsonify({'message': f"The result of square root({data['number']}) is {rt_res}"}), 200

	@app.route('/service_2', methods=['POST'])
	@token_required
	def service_2(current_user):
		if not current_user.security_level >= 2:
			return jsonify({'message' : 'Cannot perfom that function!'}), 401

		data = request.get_json()

		rt_res = root_calc(3, int(data['number']))
		return jsonify({'message': f"The result of cube root({data['number']}) is {rt_res}"}), 200

	@app.route('/service_3', methods=['POST'])
	@token_required
	def service_3(current_user):
		if not current_user.security_level >= 3:
			return jsonify({'message' : 'Cannot perfom that function!'}), 401

		data = request.get_json()

		rt_res = root_calc(int(data['root']), int(data['number']))
		return jsonify({'message': f"The result of {data['root']}th root({data['number']}) is {rt_res}"}), 200


if __name__ == '__main__':

	if os.path.isfile("./server_chat_message.db") == False:
		db.create_all()
		hashed_password = generate_password_hash("admin", method='sha256')
		db.session.add(User(username="admin", password=hashed_password, name="admin", age=0, cellphone=0, cc_bi="0000-0000", security_level=3))
		db.session.commit()

	create_server(app)

	context = my_ssl_context()
	#app.run(host='localhost', ssl_context = context, request_handler=PeerCertWSGIRequestHandler, port = 5000)	# request_handler is needed in production server to get client certs
	app.run(host='localhost', ssl_context = context, port = 5000)

	
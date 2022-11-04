from flask import Flask, jsonify, request, make_response, send_file
import jwt
import datetime
from flask_sqlalchemy import SQLAlchemy
from functools import wraps
import uuid
from werkzeug.security import generate_password_hash, check_password_hash
from gevent.pywsgi import WSGIServer

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
			current_user = User.query.filter_by(public_id=data['public_id']).first()
		except:
			return jsonify({'message': 'Token is invalid!'}), 403

		return f(current_user, *args, **kwargs)

	return decorated


def create_server(app):



	@app.route('/')
	def index():
		return 'Hello!'


	@app.route('/pre-register', methods=['POST'])
	def add_pre_user():
		pre_user = request.get_json()


		new_user = User(public_id = str(uuid.uuid4()), name = data['name'], password = hashed_password, admin = False)
		db.session.add(new_user)
		db.session.commit()

		return jsonify({'message': f"New user {new_user.id} created!"})


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
			token = jwt.encode({'public_id': user.public_id, 'exp' : datetime.datetime.utcnow() + datetime.timedelta(minutes=30)}, app.config['SECRET_KEY'])
			return jsonify({'token': token.decode('UTF-8')})

		return response_error


	@app.route('/download', methods=['GET'])
	def download_api():
		path = "/home/pedroantunes/Desktop/seg_software"
		return send_file(path, as_attachment=True)

	@app.route('/user', methods=['GET'])
	@token_required
	def get_all_users(current_user):

		if not current_user.admin:
			return jsonify({'message' : 'Cannot perfom that function!'}), 403

		users = User.query.all()

		# this code serializes the data from users
		output= []
		for user in users:
			user_data = {}
			user_data['public_id'] = user.public_id
			user_data['name'] = user.name
			user_data['password'] = user.password
			user_data['admin'] = user.admin
			output.append(user_data) 

		return jsonify({"users": output})


	@app.route('/user/<user_id>', methods=['GET'])
	@token_required
	def get_user(current_user, user_id):

		if not current_user.admin:
			return jsonify({'message' : 'Cannot perfom that function!'}), 403

		user = User.query.filter_by(public_id=user_id).first()

		if not user:
			return jsonify({'message' : 'No user found!'}), 401

		user_data = {}
		user_data['public_id'] = user.public_id
		user_data['name'] = user.name
		user_data['password'] = user.password
		user_data['admin'] = user.admin

		return jsonify({'user': user_data})


	@app.route('/user', methods=['POST'])
	@token_required
	def add_user(current_user):

		if not current_user.admin:
			return jsonify({'message' : 'Cannot perfom that function!'}), 403

		data = request.get_json()

		hashed_password = generate_password_hash(data['password'], method='sha256')


		new_user = User(public_id = str(uuid.uuid4()), name = data['name'], password = hashed_password, admin = False)
		db.session.add(new_user)
		db.session.commit()

		return jsonify({'message': f"New user {new_user.id} created!"})


	@app.route('/user/<user_id>', methods=['PUT'])
	@token_required
	def promote_user(current_user,user_id):

		if not current_user.admin:
			return jsonify({'message' : 'Cannot perfom that function!'}), 403

		user = User.query.filter_by(public_id=user_id).first()

		if not user:
			return jsonify({'message' : 'No user found!'}), 401

		user.admin = True
		db.session.commit()

		return jsonify({'message': f"The user {user.id} has been promoted!"})


	@app.route('/user/<user_id>', methods=['DELETE'])
	@token_required
	def delete_user(current_user, user_id):
		if not current_user.admin:
			return jsonify({'message' : 'Cannot perfom that function!'}), 403

		user = User.query.filter_by(public_id=user_id).first()
		if not user:
			return jsonify({'message' : 'No user found!'}), 401
			
		db.session.delete(user)
		db.session.commit()
		return jsonify({'message': f"The user {user.id} has been deleted!"})



if __name__ == '__main__':
	app = Flask(__name__)

	app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///todo.db'
	app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False 
	app.config['SECRET_KEY'] = 'thisisthesecretkey'
	# app.debug = True
	db = SQLAlchemy(app)

	class User(db.Model):
		id = db.Column(db.Integer, primary_key=True)
		username = db.Column(db.String(50))
		password = db.Column(db.String(80))
		admin = db.Column(db.Boolean)
		security_level = db.Column(db.Integer, nullable=False)
		#pre_user = db.Column(db.String(50), db.ForeignKey('pre_user.id'), unique=True, nullable=False)


	class Pre_User(db.Model):
		one_time_id = db.Column(db.String(50), unique=True, nullable=False, primary_key=True)
		name = db.Column(db.String(50), nullable=False)
		age = db.Column(db.Integer, nullable=False)
		cellphone = db.Column(db.Integer, nullable=False)
		cc_bi = db.Column(db.String(50), nullable=False)
		#user = db.relationship('User', backref='pre_user', useList=False)


	create_server(app)


	app.run(host='localhost', port = 5000)

	

from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer

app = Flask(__name__)

# Load the SRBedding model
model = SentenceTransformer("smartcat/SRBedding-base-v1")

@app.route('/embed', methods=['POST'])
def embed():
    data = request.get_json()
    embeddings = model.encode(data.get("content"))
    return jsonify(embeddings.tolist())

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
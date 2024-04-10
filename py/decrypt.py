from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Util.Padding import unpad
from enmoji import emoji_to_bytes
import hashlib

def generate_key(token):
    hash_object = hashlib.sha256()
    hash_object.update(token.encode('utf-8'))
    token = hash_object.digest()
    return token

def decrypt(enc_data, token):
    enc_data_bytes = emoji_to_bytes(enc_data)
    # use half len iv
    hiv = enc_data_bytes[:8]  
    ciphertext = enc_data_bytes[8:]
    key = generate_key(token)
    cipher = AES.new(key, AES.MODE_CBC, iv=hiv + hiv)
    padded_data = cipher.decrypt(ciphertext)
    data = unpad(padded_data, AES.block_size)
    return data.decode('utf-8')

# user inputs
token = input("Please enter token: ")
encrypted_data = input("Please enter enmoji: ")

# decrypt the data
decrypted_data = decrypt(encrypted_data, token)
print(f"Original text: {decrypted_data}")
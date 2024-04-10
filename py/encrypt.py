from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Util.Padding import pad
from enmoji import bytes_to_emoji
import hashlib

def generate_key(token):
    hash_object = hashlib.sha256()
    hash_object.update(token.encode('utf-8'))
    token = hash_object.digest()
    return token

def encrypt(data, token):
    key = generate_key(token)
    # use half len iv
    hiv = get_random_bytes(8)
    cipher = AES.new(key, AES.MODE_CBC, iv=hiv+hiv)
    padded_data = pad(data.encode(), 16)
    ciphertext = cipher.encrypt(padded_data)
    return bytes_to_emoji(hiv + ciphertext)

def pkcs7padding(data, block_size=16):
    if type(data) != bytearray and type(data) != bytes:
        raise TypeError("Only support bytearray/bytes !")
    pl = block_size - (len(data) % block_size)
    return data + bytearray([pl for i in range(pl)])

# user inputs
token = input("Please enter token: ")
data_to_encrypt = input("Please enter text: ")

# Encrypt the data
encrypted_data = encrypt(data_to_encrypt, token)
print(f"Enmoji: {encrypted_data}")
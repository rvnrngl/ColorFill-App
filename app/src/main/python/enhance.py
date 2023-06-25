import cv2
from PIL import Image
import numpy as np
import base64
import io

def correct_color_blindness(image, type):
    # Convert image to RGB array
    rgb = np.array(image.convert('RGB'))

    # Define color conversion matrices for each type of color blindness
    deuteranopia = np.array([[1.8, -0.8, 0], [-0.03, 1.03, 0], [0, 0, 1]])
    protanopia = np.array([[0.99, 0.01, 0], [0.94, 0.06, 0], [0, 0, 1]])
    tritanopia = np.array([[1, 0, 0], [0, 1, 0], [-0.77, 0.77, 1]])

    # Apply color conversion matrix based on the type of color blindness
    if type == 'deuteranopia':
        matrix = deuteranopia
    elif type == 'protanopia':
        matrix = protanopia
    elif type == 'tritanopia':
        matrix = tritanopia
    else:
        return "Invalid type of color blindness"

    # Reshape RGB array into a 2D matrix for matrix multiplication
    flat_rgb = rgb.reshape(-1, 3)

     # Apply color conversion matrix to each pixel in the image
    new_rgb = np.dot(flat_rgb, np.linalg.inv(matrix).T).astype(np.uint8)

    # Reshape new RGB array back into a 3D array
    new_rgb = new_rgb.reshape(rgb.shape)

    # Create new image from RGB array
    new_image = Image.fromarray(new_rgb, mode='RGB')

    buff = io.BytesIO()
    new_image.save(buff,format="JPEG")
    corrected_image = base64.b64encode(buff.getvalue())

    return ""+str(corrected_image, 'utf-8')

def main(data):
    # Decode Deuteranopia
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data,np.uint8)
    img = cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = Image.fromarray(img)

    tritanopia_corrected = correct_color_blindness(img, 'tritanopia')


    return tritanopia_corrected
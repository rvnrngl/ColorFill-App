import cv2
from PIL import Image
import numpy as np
import base64
import io

def simulate_dalto(image):
    # LMS Algorithm
    image = image.convert('RGB')
    RGB = np.asarray(image, dtype=np.float32)

    # convert image from rgb to lms
    rgb2lms = np.array([[17.8824, 43.5161, 4.11935],
                        [3.45565, 27.1554, 3.86714],
                        [0.0299566, 0.184309, 1.46709]])

    LMS = np.dot(RGB, rgb2lms.T)

    # transformation to deuteranopia 
    deuteranopia = np.array([[1,0,0],
                            [0.494207,0,1.24827],
                            [0,0,1]])
    
    # transformation to protanopia
    protanopia = np.array([[0,2.02344,-2.52581],
                       [0,1,0],
                       [0,0,1]])
    
    # transformation to tritanopia
    #tritanopia = np.array([[1,0,0],
    #                    [0,1,0],
    #                    [-0.395913,0.801109,0]])

    types = [deuteranopia,protanopia]
    
    img_str = ['','']

    for x in range(len(types)):
        # simulate colorblind
        _LMS = np.dot(LMS, types[x].T)

        _RGB = np.dot(_LMS, np.linalg.inv(rgb2lms).T)

        # Clip the RGB values to ensure they are in the valid range of [0, 255]
        _RGB = np.clip(_RGB, 0, 255).astype('uint8')
        
        simulated_image = Image.fromarray(_RGB, mode='RGB')
        buff = io.BytesIO()
        simulated_image.save(buff,format="JPEG")
        img_str[x] = base64.b64encode(buff.getvalue())

    return ""+str(img_str[0], 'utf-8'), ""+str(img_str[1], 'utf-8')

"""
def simulate_deu_and_pro(image):
    # LMS Algorithm
    image = image.convert('RGB')
    RGB = np.asarray(image, dtype=float)

    # convert image from rgb to lms
    rgb2lms = np.array([[17.8824, 43.5161, 4.11935],
                        [3.45565, 27.1554, 3.86714],
                        [0.0299566, 0.184309, 1.46709]])

    # convert image from lms to rgb
    lms2rgb = np.linalg.inv(rgb2lms)

    # transformation to deuteranopia 
    deuteranopia = np.array([[1,0,0],
                            [0.494207,0,1.24827],
                            [0,0,1]])
    
    # transformation to protanopia
    protanopia = np.array([[0,2.02344,-2.52581],
                       [0,1,0],
                       [0,0,1]])
    
    # transform to LMS space
    LMS = np.zeros_like(RGB)               
    for i in range(RGB.shape[0]):
        for j in range(RGB.shape[1]):
            rgb = RGB[i,j,:3]
            LMS[i,j,:3] = np.dot(rgb2lms, rgb)

    types = [deuteranopia,protanopia]
    
    img_str = ['','']

    for x in range(len(types)):
        # simulate colorblind
        _LMS = np.zeros_like(RGB)  
        for i in range(RGB.shape[0]):
            for j in range(RGB.shape[1]):
                lms = LMS[i,j,:3]
                _LMS[i,j,:3] = np.dot(types[x], lms)

        _RGB = np.zeros_like(RGB) 
        for i in range(RGB.shape[0]):
            for j in range(RGB.shape[1]):
                _lms = _LMS[i,j,:3]
                _RGB[i,j,:3] = np.dot(lms2rgb, _lms)
        
        # Save simulation how image is perceived by a color blind
        for i in range(RGB.shape[0]):
            for j in range(RGB.shape[1]):
                _RGB[i,j,0] = max(0, _RGB[i,j,0])
                _RGB[i,j,0] = min(255, _RGB[i,j,0])
                _RGB[i,j,1] = max(0, _RGB[i,j,1])
                _RGB[i,j,1] = min(255, _RGB[i,j,1])
                _RGB[i,j,2] = max(0, _RGB[i,j,2])
                _RGB[i,j,2] = min(255, _RGB[i,j,2])
        
        simulated = _RGB.astype('uint8')
        simulated_image = Image.fromarray(simulated, mode='RGB')
        buff = io.BytesIO()
        simulated_image.save(buff,format="JPEG")
        img_str[x] = base64.b64encode(buff.getvalue())

    return ""+str(img_str[0], 'utf-8'), ""+str(img_str[1], 'utf-8')
"""
def simulate(image, type):
    # CBFS Algorithm
    rgb = np.array(image.convert('RGB'))

    # Define color conversion matrices for each type of color blindness
    deuteranopia = np.array([[0.625, 0.375, 0], [0.7, 0.3, 0], [0, 0.3, 0.7]])
    protanopia = np.array([[0.567, 0.433, 0], [0.558, 0.442, 0], [0, 0.242, 0.758]])
    tritanopia = np.array([[0.95, 0.05, 0], [0, 0.433, 0.567], [0, 0.475, 0.525]])

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
    new_rgb = np.dot(flat_rgb, matrix.T).astype(np.uint8)

    # Reshape new RGB array back into a 3D array
    new_rgb = new_rgb.reshape(rgb.shape)

    # Create new image from RGB array
    new_image = Image.fromarray(new_rgb, 'RGB')

    buff = io.BytesIO()
    new_image.save(buff,format="JPEG")
    sim_image = base64.b64encode(buff.getvalue())

    return ""+str(sim_image, 'utf-8')

def simulate_Analous_Dichromacy(data):

    img = cv2.cvtColor(np.array(data), cv2.COLOR_RGB2BGR)

    # Apply deuteranomaly
    deuteranomaly = img.copy()
    deuteranomaly[:, :, 1] = 0.6 * img[:, :, 1] + 0.4 * img[:, :, 2]
    deuteranomaly[:, :, 2] = 0.7 * img[:, :, 2] + 0.3 * img[:, :, 1]

    # Apply protanomaly
    protanomaly = img.copy()
    protanomaly[:, :, 0] = 0.8 * img[:, :, 0] + 0.2 * img[:, :, 1]
    protanomaly[:, :, 1] = 0.6 * img[:, :, 1] + 0.4 * img[:, :, 2]
    #protanomaly[:, :, 1] = 0.25833 * img[:, :, 0] + 0.74167 * img[:, :, 1] + 0.00001 * img[:, :, 2]

    # Apply tritanomaly
    tritanomaly = img.copy()
    tritanomaly[:, :, 0] = 0.95 * img[:, :, 0] + 0.05 * img[:, :, 2]
    tritanomaly[:, :, 2] = 0.43333 * img[:, :, 0] + 0.56667 * img[:, :, 2] + 0.00001 * img[:, :, 1]

    # Convert the images to PIL format
    deuteranomaly_pil = Image.fromarray(cv2.cvtColor(deuteranomaly, cv2.COLOR_BGR2RGB))
    protanomaly_pil = Image.fromarray(cv2.cvtColor(protanomaly, cv2.COLOR_BGR2RGB))
    tritanomaly_pil = Image.fromarray(cv2.cvtColor(tritanomaly, cv2.COLOR_BGR2RGB))

    buff_0 = io.BytesIO()
    deuteranomaly_pil.save(buff_0,format="JPEG")
    sim_0 = base64.b64encode(buff_0.getvalue())

    buff_1 = io.BytesIO()
    protanomaly_pil.save(buff_1,format="JPEG")
    sim_1 = base64.b64encode(buff_1.getvalue())

    buff_2 = io.BytesIO()
    tritanomaly_pil.save(buff_2,format="JPEG")
    sim_2 = base64.b64encode(buff_2.getvalue())

    return ""+str(sim_0, 'utf-8'), ""+str(sim_1, 'utf-8'), ""+str(sim_2, 'utf-8')


def main(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data,np.uint8)
    img = cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    imgray = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img = Image.fromarray(img)

    # Monochromacy
    img_gray = cv2.cvtColor(imgray, cv2.COLOR_RGB2GRAY)
    pil_img = Image.fromarray(img_gray)

    buffs = io.BytesIO()
    pil_img.save(buffs,format="JPEG")
    greyscale = base64.b64encode(buffs.getvalue())

    deuteranopia_simulated, protanopia_simulated = simulate_dalto(img)

    #deuteranopia_simulated, protanopia_simulated = simulate_deu_and_pro(img)
    #deuteranopia_simulated = simulate(img,'deuteranopia')
    #protanopia_simulated = simulate(img,'protanopia')
    tritanopia_simulated = simulate(img,'tritanopia')

    deuteranomaly_simulated, protanomaly_simulated, tritanomaly_simulated = simulate_Analous_Dichromacy(img)

    return deuteranopia_simulated, protanopia_simulated, tritanopia_simulated, ""+str(greyscale, 'utf-8'), deuteranomaly_simulated, protanomaly_simulated, tritanomaly_simulated
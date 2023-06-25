
import numpy as np
import cv2
from PIL import Image
import base64
import io

def main(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data,np.uint8)
    img = cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    img_pil = Image.fromarray(img)
    image = img_pil.convert('RGB')
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

    # transformation to tritanopia
    tritanopia = np.array([[1,0,0],
                       [0,1,0],
                       [-0.395913,0.801109,0]])

    # transform to LMS space
    LMS = np.zeros_like(RGB)               
    for i in range(RGB.shape[0]):
        for j in range(RGB.shape[1]):
            rgb = RGB[i,j,:3]
            LMS[i,j,:3] = np.dot(rgb2lms, rgb)

    types = [deuteranopia,protanopia,tritanopia]
    
    img_str = ['','','']

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
    
    img_gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    pil_img = Image.fromarray(img_gray)

    buffs = io.BytesIO()
    pil_img.save(buffs,format="JPEG")
    grayscale = base64.b64encode(buffs.getvalue())

    return ""+str(img_str[0], 'utf-8'), ""+str(img_str[1], 'utf-8'), ""+str(img_str[2], 'utf-8'), ""+str(grayscale, 'utf-8')




    """# simulate colorblind
    _LMS = np.zeros_like(RGB)  
    for i in range(RGB.shape[0]):
        for j in range(RGB.shape[1]):
            lms = LMS[i,j,:3]
            _LMS[i,j,:3] = np.dot(deuteranopia, lms)

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
    img_str = base64.b64encode(buff.getvalue())
    return ""+str(img_str, 'utf-8')

       Monochromacy
        img_gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
        pil_img = Image.fromarray(img_gray)

        buff = io.BytesIO()
        pil_img.save(buff,format="JPEG")
        img_str = base64.b64encode(buff.getvalue())
        return ""+str(img_str, 'utf-8')
    """ 

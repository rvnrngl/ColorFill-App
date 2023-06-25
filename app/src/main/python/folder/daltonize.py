import numpy as np
from PIL import Image

def colorblind(image_path, type):

    #image = image.copy()
    image = image_path.convert('RGB')
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


    # image correction matrix
    """err2mod_deu = np.array([[1,0.7,0],
                        [0,0,0],
                        [0,0.7,1]])
        err2mod_tri = np.array([[1,0,0.7],
                        [0,1,0.7],
                        [0,0,0]])
    """

    err2mod = np.array([[0,0,0],
                        [0.7,1,0],
                        [0.7,0,1]])
    
    # Apply color conversion matrix based on the type of color blindness
    if type == 'deuteranopia':
        matrix = deuteranopia
    elif type == 'protanopia':
        matrix = protanopia
    elif type == 'tritanopia':
        matrix = tritanopia
    else:
        return "Invalid type of color blindness"

    # transform to LMS space
    LMS = np.zeros_like(RGB)               
    for i in range(RGB.shape[0]):
        for j in range(RGB.shape[1]):
            rgb = RGB[i,j,:3]
            LMS[i,j,:3] = np.dot(rgb2lms, rgb)

    # simulate colorblind
    _LMS = np.zeros_like(RGB)  
    for i in range(RGB.shape[0]):
        for j in range(RGB.shape[1]):
            lms = LMS[i,j,:3]
            _LMS[i,j,:3] = np.dot(matrix, lms)

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
    simulated_image_path = 'daltonize_images/sim_'+type+'.png'
    simulated_image.save(simulated_image_path)

    # calculate error between images
    error = (RGB-_RGB)

    #Correcting the image
    ERR = np.zeros_like(RGB) 
    for i in range(RGB.shape[0]):
        for j in range(RGB.shape[1]):
            err = error[i,j,:3]
            ERR[i,j,:3] = np.dot(err2mod, err)

    correct = ERR + RGB

    #save corrected image
    for i in range(RGB.shape[0]):
        for j in range(RGB.shape[1]):
            correct[i,j,0] = max(0, correct[i,j,0])
            correct[i,j,0] = min(255, correct[i,j,0])
            correct[i,j,1] = max(0, correct[i,j,1])
            correct[i,j,1] = min(255, correct[i,j,1])
            correct[i,j,2] = max(0, correct[i,j,2])
            correct[i,j,2] = min(255, correct[i,j,2])

    res = correct.astype('uint8')
    corrected_image = Image.fromarray(res, mode='RGB')
    corrected_image_path = 'daltonize_images/cor_'+type+'.png'
    corrected_image.save(corrected_image_path)

    return simulated_image_path, corrected_image_path


image_path = Image.open("daltonize_images/original_image/image_sample.png")

sim_deuteranopia, cor_deuteranopia = colorblind(image_path, 'deuteranopia')
sim_deuteranopia, cor_deuteranopia = colorblind(image_path, 'protanopia')
sim_deuteranopia, cor_deuteranopia = colorblind(image_path, 'tritanopia')

from PIL import Image
import numpy as np

def simulate_color_blindness(image_path, type):
    # Load image
    image = Image.open(image_path)

    # Convert image to RGB array
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

    # Save the new image with the suffix for the type of color blindness
    simulated_image_path = f"{image_path[:-4]}_{type}_simulated.png"
    new_image.save(simulated_image_path)

    return simulated_image_path

def correct_color_blindness(image_path, type):
    # Load image
    image = Image.open(image_path)

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
    new_image = Image.fromarray(new_rgb, 'RGB')

    # Save the new image with the suffix for the type of color blindness
    corrected_image_path = f"{image_path[:-4]}_{type}_corrected.png"
    new_image.save(corrected_image_path)

    return corrected_image_path

# Define the path to the image
image_path = 'cbfs_images/image_sample.png'

# Simulate deuteranopia and create a new image with the simulation
deuteranopia_simulated_path = simulate_color_blindness(image_path, 'deuteranopia')

# Correct the simulated deuteranopia and create a new image with the correction
deuteranopia_corrected_path = correct_color_blindness(deuteranopia_simulated_path, 'deuteranopia')

# Repeat the process for protanopia and tritanopia
protanopia_simulated_path = simulate_color_blindness(image_path, 'protanopia')
protanopia_corrected_path = correct_color_blindness(protanopia_simulated_path, 'protanopia')

tritanopia_simulated_path = simulate_color_blindness(image_path, 'tritanopia')
tritanopia_corrected_path = correct_color_blindness(tritanopia_simulated_path, 'tritanopia')
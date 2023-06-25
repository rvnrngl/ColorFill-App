import cv2
import numpy as np

# Load the image
img = cv2.imread('colorspace/imageTemp.jpg')

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

# Display the images
cv2.imshow('Original', img)
cv2.imshow('Deuteranomaly', deuteranomaly)
cv2.imshow('Protanomaly', protanomaly)
cv2.imshow('Tritanomaly', tritanomaly)
cv2.waitKey(0)
cv2.destroyAllWindows()
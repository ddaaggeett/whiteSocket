import cv2
import numpy

def create_blank(width, height, rgb_color=(255,255,255)):
    image = numpy.zeros((height, width, 3), numpy.uint8)
    color = tuple(reversed(rgb_color))
    image[:] = color
    return image

def filter(image):
    height, width, color = image.shape
    blank_image = create_blank(width,height)
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    lower_green = numpy.array([50,100,50])
    upper_green = numpy.array([70,255,255])
    mask = cv2.inRange(hsv, lower_green, upper_green)
    background_mask = cv2.bitwise_not(mask)
    background = cv2.bitwise_or(blank_image, blank_image, mask=background_mask)
    mask_green= cv2.bitwise_or(image, image, mask=mask)
    filtered = cv2.bitwise_or(mask_green, background)
    return(filtered)

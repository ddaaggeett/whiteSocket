import cv2
import numpy

def create_blank(width, height, rgb_color=(255,255,255)):
    image = numpy.zeros((height, width, 3), numpy.uint8)
    color = tuple(reversed(rgb_color))
    image[:] = color
    return image

def inkOnly(image):
    height, width, color = image.shape
    blank_image = create_blank(width,height)
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    # Every color except white
    low = numpy.array([0,0,0])
    high = numpy.array([359, 255, 255])
    maskInk = cv2.inRange(hsv, low, high)
    inkResult = cv2.bitwise_and(image, image, mask=maskInk)
    background_mask = cv2.bitwise_not(maskInk)
    background = cv2.bitwise_or(blank_image, blank_image, mask=background_mask)
    inkOnly = cv2.bitwise_or(inkResult, background)
    return(inkOnly)

def eraseAreas(image):
    # return mask
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    eraseAreas = cv2.Canny(gray, 30, 200)
    contours, hierarchy = cv2.findContours(eraseAreas, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
    cv2.fillPoly(eraseAreas,contours, color=(255,255,255))
    cv2.imshow('Contours', eraseAreas)
    return(eraseAreas)

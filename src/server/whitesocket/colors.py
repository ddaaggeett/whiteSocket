import cv2
import numpy
import colors

def getBlue(image,hsv):
    low_blue = numpy.array([90, 50,50])
    high_blue = numpy.array([135, 255, 255])
    blue_mask = cv2.inRange(hsv, low_blue, high_blue)
    blue = cv2.bitwise_and(image, image, mask=blue_mask)
    return blue

def getOrange(image,hsv):
    low_orange = numpy.array([10, 100, 20])
    high_orange = numpy.array([25, 255, 255])
    orange_mask = cv2.inRange(hsv, low_orange, high_orange)
    orange = cv2.bitwise_and(image, image, mask=orange_mask)
    return orange

def getRed(image,hsv):
    lower1 = numpy.array([0, 100, 20])
    upper1 = numpy.array([5, 255, 255])
    lower2 = numpy.array([160,100,20])
    upper2 = numpy.array([179,255,255])
    lower_mask = cv2.inRange(hsv, lower1, upper1)
    upper_mask = cv2.inRange(hsv, lower2, upper2)
    full_mask = lower_mask + upper_mask
    red = cv2.bitwise_and(image, image, mask=full_mask)
    return red

def getGreen(image,hsv):
    low_green = numpy.array([30, 52, 75])
    high_green = numpy.array([88, 255, 255])
    green_mask = cv2.inRange(hsv, low_green, high_green)
    green = cv2.bitwise_and(image, image, mask=green_mask)
    return green

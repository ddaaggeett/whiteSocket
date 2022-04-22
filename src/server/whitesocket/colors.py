import cv2
import numpy
import colors

def getBlue(image,hsv):
    low_blue = numpy.array([90, 50,50])
    high_blue = numpy.array([135, 255, 255])
    mask = cv2.inRange(hsv, low_blue, high_blue)
    return mask

def getOrange(image,hsv):
    low_orange = numpy.array([10, 100, 20])
    high_orange = numpy.array([25, 255, 255])
    mask = cv2.inRange(hsv, low_orange, high_orange)
    return mask

def getRed(image,hsv):
    lower1 = numpy.array([0, 100, 20])
    upper1 = numpy.array([5, 255, 255])
    lower2 = numpy.array([160,100,20])
    upper2 = numpy.array([179,255,255])
    lower_mask = cv2.inRange(hsv, lower1, upper1)
    upper_mask = cv2.inRange(hsv, lower2, upper2)
    mask = lower_mask + upper_mask
    return mask

def getGreen(image,hsv):
    low_green = numpy.array([30, 52, 75])
    high_green = numpy.array([88, 255, 255])
    mask = cv2.inRange(hsv, low_green, high_green)
    return mask

def getBlack(image,hsv):
    low_black = numpy.array([0,0,0])
    high_black = numpy.array([180,255,125])
    mask = cv2.inRange(hsv, low_black, high_black)
    return mask

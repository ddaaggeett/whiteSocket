import cv2
import numpy
import json

def warp(image,inputCorners):
    config = json.loads(open('config.json').read())
    outputWidth = config['outputWidth']
    outputHeight = config['outputHeight']
    inpoints = numpy.float32(inputCorners)
    outpoints = numpy.float32([[0,0],[outputWidth,0],[outputWidth,outputHeight],[0,outputHeight]])
    matrix = cv2.getPerspectiveTransform(inpoints,outpoints)
    warped = cv2.warpPerspective(image,matrix,(outputWidth,outputHeight))
    return(warped)

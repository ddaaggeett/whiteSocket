import cv2
import numpy
import json

def warp(img,inputCorners):
    config = json.loads(open('config.json').read())
    outputWidth = config['outputWidth']
    outputHeight = config['outputHeight']
    input = cv2.imread(img)
    inpoints = numpy.float32(inputCorners)
    outpoints = numpy.float32([[0,0],[outputWidth,0],[outputWidth,outputHeight],[0,outputHeight]])
    matrix = cv2.getPerspectiveTransform(inpoints,outpoints)
    output = cv2.warpPerspective(input,matrix,(outputWidth,outputHeight))
    # cv2.imshow('output',output)
    cv2.imwrite('./img/output.png', output)
    # cv2.waitKey(0)

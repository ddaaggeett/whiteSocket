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

def roi(image):
    mask = numpy.zeros(image.shape[:2], dtype="uint8")
    inputCorners = numpy.zeros((4,2))
    arucoDict = cv2.aruco.Dictionary_get(cv2.aruco.DICT_4X4_50)
    arucoParams = cv2.aruco.DetectorParameters_create()
    corners, ids, rejected = cv2.aruco.detectMarkers(image, arucoDict, parameters=arucoParams)
    if len(ids) == 4:
        ids = ids.flatten()
        for (markerCorner, markerID) in zip(corners, ids):
            corners = markerCorner.reshape((4, 2))
            (tl, tr, br, bl) = corners
            tl = [int(tl[0]), int(tl[1])]
            tr = [int(tr[0]), int(tr[1])]
            br = [int(br[0]), int(br[1])]
            bl = [int(bl[0]), int(bl[1])]
            # outer-most corner of each arcuro
            if markerID == 0: inputCorners[0] = tl
            if markerID == 1: inputCorners[1] = tr
            if markerID == 2: inputCorners[2] = br
            if markerID == 3: inputCorners[3] = bl
    contours = numpy.array([inputCorners[0],inputCorners[1],inputCorners[2],inputCorners[3]])
    cv2.fillPoly(mask, pts = numpy.int32([contours]), color =(255,255,255))
    return(mask)

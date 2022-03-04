import warp
import aruco

image = './img/aruco_corners.png'

# all corners read in following order - TL,TR,BR,BL
inputCorners = aruco.getInputCorners(image)
warp.warp(image,inputCorners)

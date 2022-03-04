import cv2
import warp
import filter
import aruco

# # all corners read in following order - TL,TR,BR,BL
img = './img/input.png'
image = cv2.imread(img)
inputCorners, image = aruco.getInputCorners(image)
warped = warp.warp(image,inputCorners)
filtered = filter.filter(warped)
cv2.imwrite('./img/output.png', filtered)
cv2.imshow('output',filtered)
cv2.waitKey(0)

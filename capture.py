
'''
/**
*   BLOOPRINT.XYZ: commoditizing hand-written design
*   Copyright (C) 2016 - Dave Daggett, EIT
*
*   This program is free software; you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation; either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program; if not, write to the Free Software Foundation,
*   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
*/

'''


# using python3.5
# make sure android camera is open and running (set phone to airplane mode)
# 12MP camera recommended (higher the better)

from subprocess import *

'''
triggered from blooprint source code
'''

def dirContains():
	print('dir contains what?.........')
	'''
	android camera input folder
	change according to camera usage
	'''
	cmd = 'adb shell ls /sdcard/DCIM/Camera/'
	process = Popen(cmd,shell=True,stdout=PIPE)
	out = process.communicate()[0].strip()
	return out

before = dirContains()

def shoot():
	print('shooting camera........')
	'''
	camera shutter
	'''
	cmd = 'adb shell input keyevent 66'
	process = Popen(cmd,shell=True,stdout=PIPE)
	out = process.communicate()[0].strip()

shoot()

def adb_pull():
	print('adb pull...........')
	'''
	TODO: set directories
			best to have script to draw these directory locations on blooprint.xyz program start
	'''
	cmd = 'adb pull [fromDirectory on android camera] [toDirectory on blooprint computer]'
	# cmd = 'adb pull /sdcard/DCIM/Camera /home/dave/Blooprint/in'
	process = Popen(cmd,shell=True,stdout=PIPE)
	out = process.communicate()[0].strip()

def adb_empty():
	print('adb empty...........')
	'''
	empty the camera images folder once done
	make ready for next bloop capture image transfer
	'''
	cmd = 'adb shell rm /sdcard/DCIM/Camera/*'
	process = Popen(cmd,shell=True,stdout=PIPE)
	out = process.communicate()[0].strip()


flag = True
while(flag):
	after = dirContains()
	if(before != after):
		adb_pull()
		adb_empty()
		flag = False

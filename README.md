# blooprint.xyz
commoditizing hand-written design

Project: 	BLOOPRINT.XYZ 
Creator:	DAVE DAGGETT, EIT
Owner:		BLOOPRINT, LLC
Licence:	GPLv3

Have fun, please consider getting involved.  I look forward to working with you!!!
____________________________________________________________________________

BIG PICTURE:
Handwriting/sketching/doodling are all crucial in the development of any intellectual project.  The unfortunate thing about handwriting is that it's analog.  There are technology projects currently out there that collect handwriting on a whiteboard to save its image state, which is awesome to share what someone writes down.  This, however, only produces static/non-editable data. (there’s also Smartboard technologies, which, I believe they’re using what they have in the worst way possible...just an extension of the desktop...C’MON!  Plus,  their base model product is around $5000, so if competition comes down to price in the marketplace, well, goodbye Smartboard)

Blooprint.xyz is a new input system.  It collects image data of handwriting/drawing, and not only does it save the written data -> it makes it EDITABLE.  This editable functionality is the foundation to what we can turn into a handwriting documentation system.  Significance: intellectual property profile maintenance.  Imagine the applications ... :)

Pixel manipulation is used to precisely align input image to output orientation.  Projected image and camera capture are not perfectly in line, so the output image needs to account for the transformation from the input image.  See mathematics demonstrating algebraic functionality of input to output transformation (see Calibration.java).  This transformation is what allows intended handwriting/drawing by the designer (user) to be maintained.

Text by typing on keyboard is an added convenience.  User is able to dictate position of new text location and edit text however necessary to supplement hand designs 

With every Blooprint.xyz account comes options to share work publicly, or keep work private. There are levels of privacy.  Shared work (open-sourced) is displayed to individuals with access to Blooprint.xyz accounts on the internet.  Designer works with Blooprint.xyz software with the data collecting hardware.  This data collected is then stored for redisplay on either another blooprint station, or on a basic internet browser with user's ability to comment endlessly on blooprints shared publicly. this is the part of the project that is free for the user who is not technically using the Blooprint.xyz software project.  They're indirectly contributing to the user base.

A single blooprint is an editable document containing hand-drawn detail with supporting text. the ultimate blooprint documentation system is based upon supportive text which is the measurable data in parallel to the handwriting, explaining exactly what's portrayed.

Think about it this way: if a picture is placed in front of an audience, and the only thing that had to be done was the group of people looking at it had to argue about it, you'd eventually get to a refined explanation, perhaps even an exact transcription of what was drawn by hand.  This is the measurement system that gives credibility to hand-drawn/written work.  Image sorting algorithms to apply to user generated content.
____________________________________________________________________________

NOTE:
This document is for direction purposes only.  It will be continuously altered.  Some components of Blooprint.xyz may not completed/running yet, so please copy whatever component description in this document and paste to that particular component's source code when in development.

Program start -> see BLOOPRINT.java
____________________________________________________________________________

GOAL:
to accelerate the advent of sustainable currency through commoditizing handwriting
to set the standard of intellectual collaboration methods 
to force open source collaboration across all industry – not just computer software collaboration
to lower the cost of a western education to that of a whiteboard marker (+room and board :-) lol) 
to employ 1,000,000,000 people -> lofty ;)  it can be done...
____________________________________________________________________________

BLOOPRINT USER MODEL: 
There are 2 types of users: (1) DIRECT USER operating Blooprint.xyz software at data collection point. (2) INDIRECT USER operating website

DIRECT USER -> designer position 
Blooprint.xyz whiteboard station component.  Hand-drawn work on any project is proof of involvement, and acts as a means of valuation of that individual designer.  These users own the work they design and are at all times verified and scrutinized by other designers they're collaborating with on other Blooprint.xyz devices AS WELL AS indirect users who have access to designers text for commenting on the website. This website will work as a (Wikipedia/Stack Overflow-type) democratic solution verification system. 

INDIRECT USER -> intern position 
Blooprint.xyz web browser component.  Accounts direct user straight to working blooprint of designer(s) where social-type commenting is kept per designer text input.  Indirect users play a very crucial role in the development of any shared design.  It's a type of open-sourcing details for everyone to tinker with.  It's even better if you're a direct user (plus I’m assuming all developers of Blooprint.xyz will be direct users considering they need the whiteboard/projector station to really develop this).
____________________________________________________________________________

WEBSITE: www.blooprint.xyz account
Free account giving indirect users accessibility to shared designer blooprints.  Platform gives this website user freedom to read designer details, whether it be in the Blooped image or in the Blipped text and cultivate argument among a participating community of users.  These users are the interns – they are not owners of the work they're involved with, although they are covered under Creative Commons licensing with whatever website posts they submit.  This substantiates the open-sourced atmosphere Blooprint is pursuing at its core.  All parties discussing further development of blooprint designs through the website are kept under the CC license.  Blooprint is aiming for true authoritarian parties of whatever new design topics/projects to be driven to obtain the hardware required to have their own Designer account with Blooprint.xyz software so they can directly contribute to blooprint designs (and its software development) that would have been restricted by only using the website.
____________________________________________________________________________

MONETIZATION:
First, let me say, I’m open sorcing Blooprint.xyz as a way for the world to freely adapt this new phenomena to their current work operation.  Monetization can happen in any number of ways.

End-users have intellectual property security options as part of the Blooprint.xyz documentation account settings.  Blooprint.xyz software will be free of charge for users who are openly designing new Blooprint documentation.  Blooprint.xyz software users for private Blooprint account documentation will pay a fee rate according to the amount of Blooprint documents created and maintained.

Note: the goal of Blooprint, LLC is to set the standard of open source documentation, so product licensing fees will only apply to accounts utilizing Blooprint as a method for maintaining PROPRIETARY designs/documentation – as in, Blooprint is globally taxing efforts to keep proprietary designs and methods close-sourced, these are Blooprint's paying customers.  We do agree that open-source is a good thing, right?

I’d rather the world flourish with this new technology and profit from it how it best serves itself.  Want to be happy?  Make the world around you better.
____________________________________________________________________________

BLOOPRINT.XYZ MODEL 1 DEVELOPED WITH: 
COST: MAX $ 2000.00 - hardware cost 

COMPUTER: 
64 BIT AMD 
8 GB RAM 
JDK 7
PYTHON3

PROJECTOR: 
4:3 - 740? (PREFERRABLY 16:9-1080p) 

CAMERA: 
android debug bridge - ADB with camera capture
Camera capture to match aspect ratio of projector

PROJECTOR LIGHT SHUTTER (reverse flash for minimal glare):
current : cardboard light-blocking flap – string pull
short-term goal: Raspberry Pi for stepper motor flap automation.
Consider for future development: substitute R-Pi component with internal electrical control of lamp ballast in the projector.  lamp should be LED.  Purpose: instantaneous lamp toggle/reverse flash on whiteboard for camera capture to avoid whiteboard reflection 

WHITEBOARD: 
2x3ft anti-glare surface 

ASSEMBLY: 
Whiteboard on tabletop face upward. Project image on whiteboard from above estimated 2.5-3ft from whiteboard surface. recommend surround/back-lighting for whiteboard visibility for capture.  Of course, assembly orientation is preferential.  This project started with literal blueprinting in mind, so the face-up orientation is to cater to traditional engineering teams.

Note:  I welcome parties (like a DELL) to come along and package a new harware system together that works with BLOOPRINT.XYZ software.  New hardware companies, start your engines!



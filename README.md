# www.blooprint.xyz
commoditizing hand-written design - world's most reliable think tank

Creator:	DAVE DAGGETT, EIT - BLOOPRINT, LLC

Licence:	GPLv3

Have fun, please consider getting involved.  I look forward to working with you!!!
____________________________________________________________________________

GOALS:

To set public education execution standards.

To set the standard of intellectual collaboration methods.

To force open source collaboration across all industry – not just computer software collaboration.

To employ 1,000,000,000 people.  Of course this won't be through Blooprint, LLC, but through whatever organizations that end up using Blooprint as a tool for team members to be relied upon to use and address new operations

To create a seamless way to incorporate different types of businesses together.  We want global interaction -> it's not like there's different types of hand-drawing.  It's not like a problem in a mathematics classroom is to be solved any differently that working on a solution with a particle accelerator.  The information of the problem doesn't change, only its approachability.  So, NO to smartphones and tablets attempting to make a hands-on approach.  They're a drag on the way the world really needs to extract and utilize pure intellect of human-kind.  Don't get me wrong...they're good for most everything in terms of communications, but not articulation.  Think bigger, back up and pick up a utility without an autocorrect in it.  That can be sprinkled in later, but at first can't mold (autocorrect) what isn't broken, and authenticity/articulation of designer work is not broken, and should be valued at first draw.  It's likely you'd pick up a pencil or pen.  Good, that's as analog as you can get.  The whiteboard marker is simply a way to make the writing and erasing as fast as possible.  Measuring iterative attempts and failures along the way through any problem prooves that individual's authoritarian value. -> what is open-source.

To rid the internet and economy of a 'like-based' value system -> see Douglas Rushkoff -> https://www.youtube.com/watch?v=6_n1Dro0Uec
____________________________________________________________________________

BLOOPRINT.XYZ MODEL 1 DEVELOPED WITH: 

HARDWARE COST: ROUGH MAX ~ $2000

COMPUTER: 
64 BIT AMD 
8 GB RAM 
JDK 7
PYTHON 3

PROJECTOR: 
ASPECT RATIO = 4:3 (PREFERRABLY 16:9-1080p) 

CAMERA: 
android debug bridge - ADB with camera capture
Camera capture to match aspect ratio of projector

PROJECTOR LIGHT SHUTTER (reverse flash for minimal glare):

CURRENT: cardboard light-blocking flap – string pull || SHORT TERM GOAL: Raspberry Pi for stepper motor flap automation || LONG-TERM GOAL: substitute R-Pi component with internal electrical control of lamp ballast in the projector.  lamp should be LED.  Purpose: instantaneous lamp toggle/reverse flash on whiteboard for camera capture to eliminate whiteboard reflection 

WHITEBOARD: 2x3ft anti-glare surface 

ASSEMBLY: Whiteboard on tabletop face upward. Project image on whiteboard from above estimated 3ft from whiteboard surface. Recommend surround/back-lighting for whiteboard visibility for capture.  Of course, assembly orientation is preferential.  This project started with literal blueprinting in mind, so the face-up orientation is to cater to traditional engineering teams.
____________________________________________________________________________

BIG PICTURE:

I'm not interested in personal handheld devices.  I don't see any handheld device with a screen able to retain the full authenticity of a designer's personal work.  This tool is for the individual who generates their best content on paper and would benefit most by it retaining a value of its worth and having the power of netowrking to distribute its use, whatever the designed content may be.  Bear with me, Model 1's prototype is a whole workstation, but there's plenty of room for increasing hardware beauty.

Blooprint.xyz is a new input system generating a database of image data of handwriting/drawing.  Not only does it save the written data -> it makes it EDITABLE.  This editable functionality is the foundation to what we can turn into a handwriting documentation system.  Significance: intellectual property profile maintenance.

Pixel manipulation is used to precisely align input image to output orientation.  Projected image and camera capture are not perfectly in line, so the output image needs to account for the transformation from the input image.  See Calibration.java -> Calibration.stretch() demonstrating algebraic functionality of input to output transformation.  More documentation on its derivation to come, but for now, know it just works.  This transformation is what turns the marker on the whiteboard into an exact replicated image but from the perspecive of the projector.  The projector then turns around and simply displays a pure white space plus whatever pixels that have been triggered by the user per their design work.

Text by typing on keyboard is an added convenience.  User is able to dictate position of new text location and edit text however necessary to supplement hand designs .

With every Blooprint.xyz account comes options to share work publicly, or keep work private. There are levels of privacy.  Shared work (open-sourced) is displayed to individuals with access to Blooprint.xyz accounts on the internet.  Designer works with Blooprint.xyz software with the data collecting hardware.  This data collected is then stored for redisplay on either another blooprint station, or on a basic internet browser with user's ability to comment endlessly on blooprints shared publicly. this is the part of the project that is free for the user who is not technically using the Blooprint.xyz software project.  They're indirectly contributing to the user base.

A single blooprint is an editable document containing hand-drawn detail with supporting text. the ultimate blooprint documentation system is based upon supportive text which is the measurable data in parallel to the handwriting, explaining exactly what's portrayed.

Think about it this way: if a picture is placed in front of an audience, and the only thing that had to be done was the group of people looking at it had to argue about it, you'd eventually get to a refined explanation, perhaps even an exact transcription of what was drawn by hand.  This is the measurement system that gives credibility to hand-drawn/written work.  Imagine sorting algorithms to apply to user generated content.
____________________________________________________________________________

NOTE:

This document is for direction purposes only.  It will be continuously altered.  Some components of Blooprint.xyz may not completed/running yet.  Please look through all the source and familiarize yourself with the code.  This is my first software ever written, so I'm looking forward to working with real-deal developers.

Program start -> see BLOOPRINT.java
____________________________________________________________________________

BLOOPRINT USER MODEL: 

There are 2 types of users: (1) DIRECT USER operating Blooprint.xyz software at data collection point. (2) INDIRECT USER operating website

DIRECT USER -> designer position 
-Blooprint.xyz whiteboard station component.  Hand-drawn work on any project is proof of involvement, and acts as a means of valuation of that individual designer.  These users own the work they design and are at all times verified and scrutinized by other designers they're collaborating with on other Blooprint.xyz devices AS WELL AS indirect users who have access to designers text for commenting on the website. This website will work as a (Wikipedia/Stack Overflow-type) democratic solution verification system. 

INDIRECT USER -> intern position 
-Blooprint.xyz web browser component.  Accounts direct user straight to working blooprint of designer(s) where social-type commenting is kept per designer text input.  Indirect users play a very crucial role in the development of any shared design.  It's a type of open-sourcing details for everyone to tinker with.  It's even better if you're a direct user (plus I’m assuming all developers of Blooprint.xyz will be direct users considering they need the whiteboard/projector station to really develop this).
____________________________________________________________________________

WEBSITE: 

Free account giving indirect users accessibility to shared designer blooprints.  Platform gives this website user freedom to read designer details, whether it be in the Blooped image or in the Blipped text and cultivate argument among a participating community of users.  These indeirect users are the 'interns' – they are not owners of the work they're involved with, although they are covered under Creative Commons licensing with whatever website posts they submit.  This substantiates the open-sourced atmosphere Blooprint is pursuing at its core.  All parties discussing further development of blooprint designs through the website are kept under the CC license.  Blooprint is aiming for true authoritarian parties of whatever new design topics/projects to be persuaded to obtain the hardware required to have their own Designer workspace with Blooprint.xyz software so they can directly contribute to blooprint designs (and its software development) that would have been restricted by only using the website.  Basically, indirect users can only debug, while direct users are able to debug as well as create [new] content.
____________________________________________________________________________

MONETIZATION:

I’m open sourcing Blooprint.xyz as a way for the world to freely adapt this new phenomenon to its current work operations/methods.  Monetization can happen in any number of ways.

End-users have intellectual property security options as part of the Blooprint.xyz documentation account settings.  Blooprint.xyz software will be free of charge for users who are openly designing new Blooprint documentation.  One way to make money -> Blooprint.xyz software users for private Blooprint account documentation will pay a fee rate according to the amount of Blooprint documents created and maintained.

I’d rather the world flourish with this new technology and have it profit from how it best serves itself, than aim to siphon off likes to sell to advertisers, and leave the world in an even bigger hole.

Want to be happy?  Make the world around you better.
____________________________________________________________________________

New development teams, start your engines!  University is the perfect test market for the entire Blooprint.xyz  project...from development to testing its functionality in society.
____________________________________________________________________________

Foreseeable applications:

EDUCATION - either public settings or private/more individual (autodidact) approach - Public education can place 4 students per machine's hardware setup at one time.  That's MAX $500/student for however long a projector lasts, of which it's usually the lamp that goes anyways -> cheap replacement.

Intelligent system methods to share design work.  (artificial intelligent systems -> remember, blooprint.xyz is a data collector...and AI exists by crawling data)

Student progression monitor (grading system)

Applicant/nominee verification system (are they really worth it?)

Also: VOTE IN YOUR SPECIAL DISTRICTS -> and as for one of them, get rid of useless spending in your public school districts!  Get rid of your SmartBoard and invest in Blooprint.  Save $3000 per machine MINIMUM!




# Handshakes 2010
A cooperation with [Brigitte Daetwyler](http://www.brigittedaetwyler.ch) and [Johnny Nia](http://johnnynia.ch).

## Idea
[Mobile phones are operating as virtual agents. These virtual Agents are sharing their explicit footage via Bluetooth. The more animations a mobile phone has, the faster it starts displaying them until it vibrates and displays a biting mouth. Then the search for new input starts again.](http://www.brigittedaetwyler.ch/mixed_media/handshakes)

## Execution
The installation consisted of several parts.

### Animated explicit content
Footage was searched on the WWW and further animated to be displayed on small screens. The generated footage consisted of PNG images, which could be played in a loop to create an animation on-screen.

### Physical installation with external power supply
Mobile phones were installed behind a wooden wall with peep holes in it. Interestingly displaying explicit content and exchanging footage over Bluetooth exhausted the battery faster than it could be charged. An external power supply instead of batteries was added to the phones.

### J2ME player to display images and exchange them over Bluetooth
A J2ME [player](player) written in Java exchanged images over bluetooth and displayed the animation on the mobile screen. It contains a simple heuristic to decide, when an image exchange should be triggered.

### Breaking the S40 operating system on the mobile phones to install un-signed J2ME software
Mobile phones do not just execute any J2ME software but only signed executables. To circumvent this limitation the S40 operating system was cracked and overwritten with a specific OS configuration to allow running any J2ME executable.

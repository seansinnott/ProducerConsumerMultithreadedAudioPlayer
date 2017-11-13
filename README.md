# The supplied applet plays audio data from the file toto.wav.
The applet also comes with a text area for entering commands.

To compile the applet:
$ javac *.java

To launch the applet:
$ appletviewer -J"-Djava.security.policy=all.policy" audio.html

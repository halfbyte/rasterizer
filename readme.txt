*The Great Rasterizer by JanKrutisch*

This piece of software is licensed using the GNU Public License (GPL). 
See Copying.Lib for the full license text.

*USAGE*

java -jar rasterizer.jar [-p pages] [-l] [-s papersize] [-d dotsize] [-h] [-v] inputfile [outputfile]
  -p : Number of horizontal pages (vertical will be chosen according to aspect ratio of source image)
  -l : Use Pages in Landscape
  -s : Paper Size. Allowed Values: A4, A3, LETTER, LEGAL. Default is A4.
  -c : Experimental color mode (the rasterbator approach). will change dramaticly.
  -m : Print Cropmarks intelligently. use -mall for forced cropmarks
  -d : Rasterdot size. Specified in pt (72pt make one inch). Default is 10 pt per dot.
  -h : The stuff you are reading right now. No Action.
  -v : Verbose output

  inputfile  : Input file (.jpeg, .gif, .png)
  outputfile : Output file (.pdf) defaults to out.pdf. '.pdf' is added if filename doesn't end on .pdf

  Remark on cropmarks: Using only -m Rasterizer will only print "inside" cropmarks, to help 
  aligning the pages. If using -mall (m all), Rasterizer will print all cropmarks (good if you
  want to cut it totally border free after printing.
  The cropmarks are printed "inside" the page margins to be able to print marks even on
  the outer borders (to make sure you have two cropmarks all the time to align your ruler.
  
  Remark on Color support: Currently I'm using the rasterbator approach. The dots are printed with
  the corresponding rgb color, but the dot size is nevertheless calculated from the gray scale value
  which means that light colors will appear much lighter than they should, since both parameters (dot
  size AND dot brightness (expressed by the color) will vary. I will try to find an approach to 
  eliminate the brightness effect on the color (printing all dots in the same size will probably
  ruin the effect), but that won't be easy. Then again, this would really set me apart from the
  rasterbator for the first time... :)

*EXAMPLE*

java -jar rasterizer.jar -p 4 -l -s A3 -d 20 -v schafbild.jpg schafbild.pdf

will generate 4xn pages (n depending on the height of the source image) on A3 
paper in landscape mode. The raster dots will be printed with a 20pt raster. All actions will
be commented in verbose output. The source image is schafbild.jpg, schafbild.pdf will be the
resulting pdf file.

*WEB RESOURCES*

The original Rasterbator @ http://homokaasu.org/rasterbator/
The official (but crappy) rasterizer homepage @ http://rasterizer.krutisch.de/

*AUTHOR*

Jan Krutisch is an Interactive Media Developer (IMD) at AOL Germany. He likes taking photos and
he loves to print 'em big. Real big. (Biggest up to date is a 40 sheets picture of the AOL dragonboat 
team) 

Contact Jan @ http://jan.krutisch.de/index.php?contact

Feel free to send suggestions, feature requests and such. If the response will be big enough, 
I would even create a mailing list :)

*USE CASES*

If you used this program for a commercial purpose (which is not forbidden), please drop me a line. 
I am also very interested in photos of rasterized images (please dont send images from the 
rasterbator gallery, though).
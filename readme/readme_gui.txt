Rasterizer-GUI V0.1

This release sports a very simple gui that offers the same basic
functionality as the CLI version.

Just execute java -jar rasterizergui.jar and the gui pops up. The GUI itself
should be pretty self explanatory.

 Remark on Color support: Currently I'm using the rasterbator approach. The dots are printed with
  the corresponding rgb color, but the dot size is nevertheless calculated from the gray scale value
  which means that light colors will appear much lighter than they should, since both parameters (dot
  size AND dot brightness (expressed by the color) will vary. I will try to find an approach to 
  eliminate the brightness effect on the color (printing all dots in the same size will probably
  ruin the effect), but that won't be easy. Then again, this would really set me apart from the
  rasterbator for the first time... :)



Use at your own risk.

Have fun.

Jan

*WEB RESOURCES*

The original Rasterbator @ http://homokaasu.org/rasterbator/
The official rasterizer homepage @ http://www.rasterizer.de/
The official support forums @ http://www.rasterizer.de/forum/

*AUTHOR*

Jan Krutisch is an Interactive Media Developer (IMD) at AOL Germany. He likes taking photos and
he loves to print 'em big. Real big. (Biggest up to date is a 40 sheets picture of the AOL dragonboat 
team) 

Contact Jan @ http://jan.krutisch.de/index.php?contact

Feel free to send suggestions, feature requests and such. 
Please use the forum @ http://www.rasterizer.de/forum/

*USE CASES*

If you used this program for a commercial purpose (which is not forbidden), please drop me a line. 
I am also very interested in photos of rasterized images (please dont send images from the 
rasterbator gallery, though).


*DISCLAIMER*

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    (see attached gpl.txt for the full license)
    
$Id$
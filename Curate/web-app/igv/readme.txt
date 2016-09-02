David Ma
3 March 2016

Notes on adding igv.js

I’ve made some changes to igv.js for our application here at Peter Mac.
BAM files now take indexURL (This change has been contributed to the igvteam github)
I’ve silenced some errors to the console. (This could be included as an option)
Changed igv.css at around line 450 so that it doesn’t override jQuery UI

Future work:
I want to allow people to flip between downsampled/non-downsampled
I want to allow people to hide/show the karyotype (this is implemented in our view as a shim)
I want to have drag+drop to load files




To get a new igv.js and iv.css
- Download the code from github
- `npm install` in the main directory, npm will use package.json to install the correct version of grunt and whatever dependencies igv.js needs
- `grunt` this will do a bunch of stuff on the code and spit out all the igv files you need into /dist
- Copy the files into our public folder




The files in this folder are:
cytoBand.txt - this is needed to show the karyotype ideogram
font-awesome.min.css - this is to show the -+ symbols and cogwheel (Note that fonts are stored in /fonts not in this folder)
igv.css
igv.js
hg19.fasta.fai - this shouldn’t be necessary anymore, but it’s nice to use our own version that we know will be served properly. The cloud versions often suffer from CORS problems.
img - folder containing a few logos for IGV.


I’ve stripped igv.js down to these necessary files to keep it as slim as possible. I hope I haven’t missed anything, but you can get any missing files by recompiling igv.js

Note that our css clashes a little with igv’s css. We will need to fix that.

Cheers
David
david.ma@petermac.org
3 March 2016
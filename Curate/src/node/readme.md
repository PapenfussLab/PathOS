# Thirst Quencher
#### A Simple, Satisfying Node/Gulp/Bower/SASS Boilerplate That Is Pretty Handy

## Features
* Compiles HTML partials
* Compiles and autoprefixes SASS, and will include any `bower_components` CSS/SASS
* Concatenates JS files and any `bower_components` JS, and runs JSHint
* Live reload with `watch` task
* Start your own local server for previewing as you build with `serve`
* Deploys to S3 bucket with `deploy` task, and will maintain a cache to speed up subsequent deployments
* Sets production environment on `build` or `deploy` and will minify HTML/CSS and uglify JS

## Quickstart

1. `npm install`
2. `bower install`
3. `cp .env.example .env`
4. `gulp build`

You can now write your files in /src/ and enjoy them in /dist/

Be sure to use `gulp watch` when coding, so that any time you change a file in /src/ it will be reflected in /dist/

## Customisation

Comments with the text _Editable_ have been added in the `gulpfile.js`, and indicate where you can change/add things to suit your project. Brief comments detailing what can be changed have also been added.

By default, Bourbon/Neat/jQuery have been disabled – to use them, uncomment the indicated lines in `main.scss`, as well as in `gulpfile.js` in the `Stylesheet` and `Concatenate JS` tasks.

## File/Folder Structure

A basic file/folder structure has been included as an example of how to organise your `/src` directory.

## Setting Up Deployment

Rename the included `.env.example` file to `.env` and update it with your AWS credentials.

*Note: An entry for `.env` is included in the project's `.gitignore` file – make sure you do not remove it*

Also make sure to include your S3 bucket `region` and `name` in `gulpfile.js` (locations are marked with the *Editable* comments).

# Guide
*This guide is a modified extract from a document I wrote for new front-end developers at my job, so please excuse anything that doesn't make sense in this context. I'll update it one day...*

## Node/NPM, Gulp & Bower

#### Sections
1. [Node & NPM](#11--node--npm)
1. [Gulp](#12--gulp)
1. [Bower](#13--bower)

[Node]:https://nodejs.org/en/
[NPM]:https://www.npmjs.com/

### [1.1](#11--node--npm) – Node & NPM

<a href="https://nodejs.org/en/" target="_blank">Node.js</a> is a JavaScript runtime environment for developing server-side web applications. It also does a bunch of other cool stuff (which is what we're after).

To install Node, visit the <a href="https://nodejs.org/en/" target="_blank">project website</a> and download the latest LTS release.

<a href="https://www.npmjs.com/" target="_blank">NPM</a> is a package manager for Node, and allows the installation and management of project dependancies. NPM comes bundled with Node, so no additional installation is required.

### [1.2](#12--gulp) – Gulp

<a href="http://gulpjs.com/" target="_blank">Gulp</a> is an automated, highly configurable streaming build system that is used to compile front-end source files into files readable by the browser. Gulp will:

- compile HTML partials,
- compile SASS into CSS,
- autoprefix any CSS properties that require vendor prefixes,
- concatenate and run JSHint on Javascript files,
- copy static files such as images and fonts,
- create a local server for live browser previewing,
- deploy projects to S3,
- and do pretty much whatever else you want it to do with <a href="https://www.npmjs.com/search?q=gulp-" target="_blank">the right Gulp package</a>.

As Gulp is built on Node, it is ready to go once `npm install` has been run from the project directory.

- [1.1.1](#1.1.1) <a name='1.1.1'></a> – **Gulp Tasks**

The boilerplate Gulp setup uses a variety of tasks to take care of the tricky transition from `/src` to `/dist`, letting you just focus on writing code. Run these tasks from your project directory with `gulp <taskname>`

- `build`: sets production environment and compiles to `/dist`, minifying HTML/CSS and uglifying JS

- `watch`: compiles to `/dist`, and will watch `/src` for any file changes, used in conjection with your own local server

- `serve`: identical to `watch`, but will also create a local server for live previewing within the browser

- `clean`: deletes `/dist`

- `cacheclear`: clears gulp's cache

- `deploy`: will run `build` and then publish to your specified S3 bucket

### [1.3](#13--bower) – Bower

<a href="http://bower.io/" target="_blank">Bower</a> is a package manager for web projects which allows you to utilise the most up-to-date versions of a large variety of frameworks, libraries, assets and utilities.

To install Bower globally, run `npm install -g bower` from any Terminal window.

- [1.3.1](#1.3.1) <a name='1.3.1'></a> – **Installing Packages**

Bower package dependancies are stored in `bower.json` in the project root.

When setting up a project for development, you must first install all required Bower packages by running `bower install` from the project directory.

To install additional dependancies, run `bower install <packagename> --save`. This will install the package, as well as adding it to the dependancies in `bower.json`. If you'd like to install a Bower package without adding it as a project dependancy, just omit the `--save` flag when running the `install` command.

To search for packages, either use the command `bower search <packagename>` or search on <a href="http://bower.io/search/" target="_blank">Bower.io</a>.

- [1.3.2](#1.3.2) <a name='1.3.2'></a> – **SASS/JS Imports**

Since the majority of Bower packages used for front-end dev require either stylesheets and/or javascript, it is important to understand how to correctly import them when the project is being compiled.

#### SASS

In order to import the SASS/CSS files from a Bower package, first an `@import` rule must be added to `main.scss` (see section [2.2](#2.2) for more information).

Next, you need to tell Gulp where to find the stylesheet (as it is not in the `css` folder). To do this, open `gulpfile.js` and find the **Stylesheets** task:

```js
// Stylesheets
gulp.task("stylesheets", function() {
	var paths = [
		'bower_components/normalize-scss/',
		'bower_components/bourbon/app/assets/stylesheets',
		'bower_components/neat/app/assets/stylesheets'
	];

	...
```

To add an additional Bower package stylesheet, just add the path to the stylesheet's folder to the `paths` array:

```js
// Stylesheets
gulp.task("stylesheets", function() {
	var paths = [
		'bower_components/normalize-scss/',
		'bower_components/bourbon/app/assets/stylesheets',
		'bower_components/neat/app/assets/stylesheets',
		'bower_components/path/to/component'
	];

	...
```

- **NOTE**: A recent change in `libsass` means that the import will fail if there are multiple SASS/CSS files in the specified Bower component folder. To fix this, just use the full file name when adding the `@import` rule to `main.scss` (i.e. instead of `@import "neat";`, use `@import "neat.scss";`.

#### JS

To import the JS files from a Bower package, open `gulpfile.js` and find the **Concatenate JS** task:

```js
// Concatenate JS
gulp.task("jsconcat", function() {
	return gulp.src([
			"bower_components/jquery/dist/jquery.min.js",
			"src/js/vendor/*.js"
		]).pipe( $.concat("vendor.min.js"))
		.pipe( gulp.dest("dist/js"));
});
```

To add an additional Bower package stylesheet, just add the path to the minified JS file to the `src` array:

```js
// Concatenate JS
gulp.task("jsconcat", function() {
	return gulp.src([
			"bower_components/jquery/dist/jquery.min.js",
			"bower_components/path/to/file.min.js",
			"src/js/vendor/*.js"
		]).pipe( $.concat("vendor.min.js"))
		.pipe( gulp.dest("dist/js"));
});
```

- **NOTE**: `src/js/vendor/*.js` should always be the last item being imported.

### Project Structure

- [2.1](#2.1) <a name='2.1'></a> – **File & Folder Structure**

Below is an example of the basic file/folder structure for a project's CSS.

<pre>
<b>css</b>
├── <b>base</b>
│   ├── _colours.scss
│   ├── _page.scss
│   └── _typography.scss
├── <b>components</b>
│   └── _component.scss
├── <b>sections</b>
│   └── _section.scss
├── _mixins.scss
└── <b>main.scss</b>
</pre>

- Additional folders can be added as needed, but make sure to also add a new import rule to `main.scss` (see section [2.2](#2.2) for more information).

--------------------

- [2.2](#2.2) <a name='2.2'></a> – **main.scss**

The `main.scss` file is used to import all other SASS project files. Here is a sampled `main.scss` file:

```scss
// Bower
@import "normalize.scss";
@import "bourbon.scss";
@import "neat.scss";

// Mixins
@import "mixins";

// Layout
@import "base/*";

// Sections
@import "sections/*";

// Components
@import "components/*";
```

- The standard syntax for a SASS import is `@import "path/to/file";` – however please note that Bower components are required to have the `.scss` file extension due to a recent change in `libsass`.
- Gulp is configured to use globbing, so the `*` wildcard can be used to import all SASS files within a specified folder.
- Importing additional **Bower** components requires further setup, see section [1.3.2](#1.3.2) for more information.

## SASS, Bourbon and Neat

#### Sections
1. [SASS](#31--sass)
1. [The Grid](#32--the-grid)
1. [Media Queries](#32--media-queries)


- <a href="http://sass-lang.com/" target="_blank">SASS</a> - Sass is the most mature, stable, and powerful professional grade CSS extension language in the world.
- <a href="http://rscss.io/" target="_blank">RSCSS</a> - Styling CSS without losing your sanity.
- <a href="http://bourbon.io/" target="_blank">Bourbon</a> - A simple and lightweight mixin library for Sass.
- <a href="http://neat.bourbon.io/" target="_blank">Neat</a> - A lightweight semantic grid framework for Sass and Bourbon.
- <a href="https://github.com/necolas/normalize.css/" target="_blank">Normalize</a> - A modern, HTML5-ready alternative to CSS resets.

### [3.1](#3.1--sass) – SASS

This section will give a brief rundown of some of the features and functionality of <a href="http://sass-lang.com/" target="_blank">SASS</a>. More in-depth documentation can be found <a href="http://sass-lang.com/documentation/file.SASS_REFERENCE.html)" target="_blank">here</a>.

- [3.1.1](#3.1.1) <a name='3.1.1'></a> – **Variables**

SASS variables can be defined as follows:
```scss
$width: 10px;
```

- At their most basic, SASS variables can be numbers (e.g. 1.2, 13, 10px), strings of text (e.g. "foo", 'bar', baz) or colours (e.g. blue, #04a3f9, rgba(255, 0, 0, 0.5)).
- SASS variables can also be booleans, lists of values and maps, which are handy when creating your own SASS functions.

Using a variable is as simple as referring to its name:
```scss
.element {
	width: $width;
}
```

--------------------

- [3.1.2](#3.1.2) <a name='3.1.2'></a> – **Nesting**

SASS allows for CSS rules to be nested, making your job of writing good, clean code much simpler. For example:

```scss
// SCSS

.sample-component {
	// Sample Component Styles

	.element {
		// Element Styles
	}
}
```

This will be compiled to:
```css
/* CSS */

.sample-component {
	/* Sample Component Styles */
}

.sample-component .element {
	/* Element Styles */
}
```

You can also use the incredibly useful `&` character to reference the parent selector, like so:
```scss
// SCSS

.sample-component {
	// Sample Component Styles

	.element {
		// Element Styles

		.hide & {
			display: none;
		}
	}

	&.-variant {
		// Variant Styles
	}
}
```

This will be compiled to:
```css
/* CSS */

.sample-component {
	/* Sample Component Styles */
}

.sample-component .element {
	/* Element Styles */
}

.hide .sample-component .element {
	display: none;
}

.sample-component.-variant {
	/* Variant Styles */
}
```

An even more advanced selector is the `at-root` selector – it enables you to traverse back up to the top-most parent selector append another selector to it (which is useful when you are using modifier classes).

```scss
// SCSS

.sample-component {
	// Sample Component Styles

	.button {
		// Default button styles

		@at-root {
			.-loaded#{&} {
				// Loaded button styles
			}
		}
	}
}
```

This will be compiled to:
```css
/* CSS */

.sample-component {
	/* Sample Component Styles */
}

.sample-component .button {
	/* Default button Styles */
}

.-loaded.sample-component .button {
	/* Loaded button styles */
}
```

--------------------

- [3.1.3](#3.1.3) <a name='3.1.3'></a> – **Operators**

Basic math operations can also be performed using SASS. Here are some examples:
```scss
// SCSS

$width: 100px;
$multiplier: 0.5;
$fontSize: 12px;


.element {
	width: $width;
	height: $width * $multiplier;
	margin-left: $width - ($fontSize/2);
	margin-right: $width * 0.25;

	font-size: $fontSize;
	line-height: $fontSize + 10px;
}
```

This will be compiled to:
```css
/* CSS */

.element {
	width: 100px;
	height: 50px;
	margin-left: 94px;
	margin-right: 25px;

	font-size: 12px;
	line-height: 22px;
}
```

--------------------

- [3.1.4](#3.1.4) <a name='3.1.4'></a> – **Mixins**

SASS mixins allow you to easily define a chunk of SASS that can easily be reused throughout your project. A mixin can be defined and used as follows:

```scss
// SCSS

$c-blue: #0000ff;

@mixin blue-text {
	color: $c-blue;
}

p {
	@include blue-text
}
```

This will be compiled to:
```css
/* CSS */

p {
	color: #0000ff;
}
```

For a more practical application:
```scss
// SCSS

@mixin vertical-center {
	position: absolute;
	top: 50%;
	transform: translateY(-50%);
}

.parent {
	position: relative;

	.child {
		// Child Styles

		@include vertical-center;
	}
}
```

This will be compiled to:
```css
/* CSS */

.parent {
	position: relative;
}

.parent .child {
	/* Child Styles */

	position: absolute;
	top: 50%;
	transform: translateY(-50%);
}
```

Or, for defining fonts:

```scss
@mixin font-helvetica-bold {
	font: {
		family: Helvetica, Arial, sans-serif;
		weight: 700;
		style: normal;
	}
}

// SCSS

.text {
	@include font-helvetica-bold;
}
```

This will be compiled to:
```css
/* CSS */

.text {
	font-family: Helvetica, Arial, sans-serif;
	font-weight: 700;
	font-style: normal;
}
```

- SASS mixins can also be written to accept arguments; more detailed information can be found <a href="http://sass-lang.com/documentation/file.SASS_REFERENCE.html#mixins" target=_blank>here</a>.

### [3.2](#3.2--the-grid) – The Grid

Our grids are made using <a href="http://neat.bourbon.io/" target="_blank">Neat</a>. This section will cover the basic Neat mixins used to create a grid structure, but more in-depth documentation can be found <a href="http://thoughtbot.github.io/neat-docs/latest/" target="_blank">here</a>.

- [3.2.1](#3.2.1) <a name='3.2.1'></a> – **Containers**

HTML elements that are required to contain [Grid Items](#3.2.2) must be made into containers using Neat's `outer-container` mixin.

```scss
// SCSS
$max-width = 1200px;

.container {
	@include outer-container($max-width);
}
```

This will be compiled to:
```css
/* CSS */

.container {
	max-width: 1200px;
	margin-left: auto;
	margin-right: auto;
}

.container:before,
.container:after {
	content: "";
	display: table;
}

.container:after {
	clear: both;
}
```

- Remember to also set a `width`.

--------------------

- [#3.2.2](##3.2.2) <a name='#3.2.2'></a> – **Grid Items**

Grid items may only be placed within an element that has been made into a [Container](##3.2.2) using the `outer-container` mixin.

#### span-columns

You can specify how many columns a grid item takes up using the `span-columns` mixin:
```scss
// SCSS

.grid-item {
	@include span-columns(6);
}
```

This will be compiled to:
```css
/* CSS */

.grid-item {
	float: left;
	display: block;
	margin-right: 2.35765%;
	width: 48.82117%;
}

.grid-item:last-child {
	margin-right: 0;
}
```

The result is an element that takes up 6 columns of a 12 column grid.

- By default, the number of total columns in the layout is **12**, although `span-columns` can accept custom grid layouts (e.g. `@include span-columns(49 of 100)` or `@include span-columns(3.5 of 10)`).

#### shift

To shift a column along the grid layout, use the `shift` mixin:
```scss
// SCSS

.grid-item {
	@include span-columns(6);
	@include shift(3);
}
```

This will be compiled to:
```css
/* CSS */

.grid-item {
	float: left;
	display: block;
	margin-right: 2.35765%;
	width: 48.82117%;
	margin-left: 258941%;
}

.grid-item:last-child {
	margin-right: 0;
}
```

The result is an element that takes up 6 columns of a 12 column grid that has been shifted along from the left by 3 columns.

- Much like the `span-columns` mixin, `shift` can accept custom grid layouts (e.g. `@include shift(14 of 55)` or `@include shift(2.25 of 7)`)

#### omega

The `omega` mixin is used to remove the gutter margin (or `margin-right`) of every nth element within a grid. For example, given a 12 column grid with 3 grid items per row, the grid item would be defined as:
```scss
// SCSS

.grid-item {
	@include span-columns(3);
	@include omega(3n);
}
```

This will be compiled to:
```css
/* CSS */

.grid-item {
	float: left;
	display: block;
	margin-right: 2.35765%;
	width: 23.23176%;
}

.grid-item:last-child {
	margin-right: 0;
}

.grid-item:nth-child(3n) {
	margin-right: 0;
}

.grid-item:nth-child(3n+1) {
	clear: left;
}
```

### [3.3](#3.3--media-queries) – Media Queries

Media queries are also handled by Neat, using the `media` mixin.

Firstly, media query breakpoints should be defined in `page.scss` using SASS variables as follows:
```scss
// - Breakpoints - //
$mobile: max-width 667px;
$tablet: min-width 768px max-width 1024px;
```

- The syntax to follow when definine a breakpoint variable is `$<device>: <media-feature> <breakpoint> ...`.
- Any number of media features and corresponding breakpoints can be defined.

Now, whenever you want to create a media query for a given device, just use the `media` mixin:
```scss
// SCSS

.element {
	// Desktop Styles

	@include media($tablet) {
		// Tablet Styles
	}
}
```

This will be compiled to:
```css
/* CSS */

.element {
	/* Desktop Styles */
}

@media screen and (min-width: 768px) and (max-width: 1024px) {
	.element {
		/* Tablet Styles */
	}
}
```

### Thanks

Special thanks to *@jmoggach* for creating an early version of the Gulp boilerplate – it's been extremely useful to me, and I wanted to share it with others who wanted a quick and easy way to setup Node/Gulp/Bower for their project.

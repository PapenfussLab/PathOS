# PathOS Demonstration Virtual Machine

PathOS is a complex system, integrating multiple data sources,
including the outputs of the NGS data processing (mapping, variant
calling, etc), variant classification and normalization, and so on.
This leads to a system with many local configuration dependencies.
To make it easier to try out the core PathOS system described in
the main paper, we have configured a CentOS EL 7 virtual machine
to enable those interested in trying out the system to experiment
with the various features on some real data, without having to go
through the rather large and involved effort to install and configure
the system.

We have built and tested the installation with Virtual Box
(virtualbox.org), but we provide it as an Open Virtualisation Archive
(.ova) file, and it should run without problems on other virtualisation
platforms.

The archive VM file is available at this URL:
    http://something/here

## Setup

A small amount of setup is required to get the virtual machine
running.  These instructions were written against Virtual Box
(version 5.0.32) running of OSX (10.12.3 Sierra). Other versions and
operating systems may require slight variations.

Import the PathOS-1.2.4.ova file using the "Import Appliance" 
in the "File" menu.

### Network

Open up the Settings for the virtual machine. In the "Network" tab
check that "Adapter 1" is enabled at set to "NAT".

Click to expand the "Advanced" options, and click "Port Forwarding".

In the Port Forwarding dialogue, check that guest port 80 is forwarded
to a free port on the host - we have used 8989, which is free on
our machine.

Close the dialogues and start the virtual machine. It will take a
minute or two to start up, and allow a minute or two for the PathOS
web application to start up.

### PathOS Login

Once you've started the the virtual machine, you can point your web
browser to the URL:
    http://localhost:8989/PathOS
changing the port (8989) if you altered the port forwarding
configuration above.

You should be presented with a login page, and you can log in to
the PathOS system with the username "pathosadmin" and the password
"pathos".

### Console Login

For those with an appropriate technical background, you may wish
to log in to the console to look at the configuration, and explore.
The username "pathos" and password "pathos" will get you in. The
user has sudo access, but be aware that the installation is complex
and sensitive to changes.


## LIMITATIONS

PathOS is a work in progress - we are continually adding features
based on the needs of our users. One of the consequences of this
is that some data and services are provided by other servers. We
are working on evolving the architecture to make it easier to wrap
up and deploy these, but the current demonstration version of PathOS
provided on the associated virtual machine only serves the core
PathOS curation platform.


### Data Limitations

For obvious reasons, identifying information has been removed from
records, so information about users and patients is not realistic.

### Links to external resources

A number of links in the application depend on external servers to
display information, in a site specific manner. In particular, PathOS expects that 
pipeline output files (VCFs, BAMs etc) are available to IGV through via URLs. Due to
the size of these files (a typical amplicon run is 8Gb while a typical hybrid capture run 
is 190Gb) they have not been included in the VM.
In addition, the following pages have some non-functioning links.

#### Sequence Run Page

* The bpipe log is not present
* The in-browser and external IGV links depend on an external http server which .

#### Sequenced Variants List

* The Gaffa1 and Gaffa2 links for showing copy number variation
depend on an external server.
* The in-browser and external IGV links depend on an external server.
* The Almut link depends on an external server.


# PathOS
PathOS is a decision support tool to manage, analyse and report on high throughput DNA sequencing variants.
PathOS is under active development at the [Peter MacCallum Cancer Centre in Melbourne](https://www.petermac.org/about/signature-centres/centre-clinical-cancer-genomics/molecular-diagnostic-software).

Authors: Ken Doig, Andre Seleznev, David Ma, Luis Lara, Tom Conway, Chris Love Date: February 2017 

## Introduction
Clinical diagnostics is being transformed by DNA sequencing technology capable of analysing patient samples at the nucleotide level.

Translating the data from this technology into clinically useful information requires decision support software that can analyse  data from sequencers and allow clinical scientists to interpret the DNA variations.
High throughput sequencing generates many technical artefacts from the chemical processing in the sequencing, these artefacts must be identified and filtered out of the data before further analysis.

The curation process requires identifying and annotating DNA changes, SNPs (single nucleotide polymorphisms), indels (insertions and deletions), CNVs (copy number variants) and SVs (structural variants) within a sample of patient DNA (either blood or tumour).

Once annotated, mutations are matched with internal and external databases to identify known pathogenic (disease causing) or actionable mutations (variants with an appropriate drug).
The resulting few variants are then rendered into a clinical diagnostic report suitable for the treating clinician incorporating clinical evidence and relevant publications.

PathOS carries out these tasks within a clinical laboratory setting where many patients must be reported on in a reliable, consistent and efficient manner.

## Technology Platform
PathOS takes advantage of many open-source and public Java libraries to implement an enterprise-grade application suitable for hospital use and secure storage of patient medical data. It interfaces to laboratory LIMS systems for input of patient demographic details and sample and assay registration data. An HL7 interface is currently being developed to interface with other hospital records systems.

The web application is implemented in Java, Javascript, Groovy and Grails deployable on any server supporting java servlet containers such as Tomcat or Jboss.
This allows for deployment in a wide range of environments.

Access to the system is controlled by the Spring Security Library which optionally uses an organisation's LDAP server for authentication or the internal database for authorisation and role assignment.
Web traffic is uses by Google Analytics to monitor user activity for workflow and user interface refinement.

The backend database is implemented with MariaDB, a MySQL compatible relational database, which stores the variant annotation cache and persistent java objects via Spring and Hibernate. The code base is managed in Atlassian Bitbucket and Git.

Build management uses Gradle to build system modules and create shared artifacts such as JARs, WARs and TAR files. Internally, Atlassian Bamboo is used for continuous integration to perform builds of system modules triggered by developer commits to the code repository.

The PathOS search engine is implemented in Apache Lucene. This is a powerful search framework allowing customised search capabilities over any text field in the PathOS domain model.

## Installation
PathOS has been deployed in a number of clinical environments but is a large complex application with a number of interfaces to external systems that need to be integrated for full featured operation. It has been built from the ground up to meet the clinical workflow needs of the Peter MacCallum Cancer Centre and this is reflected in some of the architectural decisions. 

The simplest way to experiment with PathOS is by accessing the [cloud instance](http://115.146.87.30:8746/PathOS) set up on the Nectar research cloud.

There are two ways to virtualise PathOS for easy deployment, Docker containers or a Virtual Machine. [Docker](http://www.docker.com) is the most powerful method as it allows lightweight containers to be built with minimal configuration files and then be deployed on a wide variety of operating systems such as: Lunix,MacOS,AWS,Azure and even Windows and Windows Server. Most PathOS deployment testing has been on Macs and Linux.
The instructions for Docker deployment can be found [here](https://github.com/PapenfussLab/PathOS/tree/master/Dockerish-PathOS).

To trial the system locally as a VM, a demonstration virtual machine has been implemented as an easy way to experiment with an operational PathOS instance. See [here](https://github.com/PapenfussLab/PathOS/tree/master/VirtualMachine).	

If building PathOS from scratch, build artifacts (PathosCore.jar,Loader.jar and PathOS.war) can be found under the Releases tab to minimise the effort of dealing with any build dependency issues.
This repository can be built using the pathos_deploy.sh bash script at the top level. This script runs on Linux or OSX but will need to be customised for local requirements and adapted for Windows environments. 



There are a number of dependencies including the following
- Java JDK 1.7 from [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html)
- Grails (we use 2.3.7 at time of writing) from https://github.com/grails/grails-core/releases/download/v2.3.7/grails-2.3.7.zip
- Gradle (we use 1.10 at time of writing) [Gradle 1.10](https://services.gradle.org/distributions/gradle-1.10-bin.zip)
- Git & a git ssh key with access to PathOS git repository
- MySql or MariaDB (currently 5.5.50 MariaDB)
- Tomcat (currently version 7.0)
- The report renderer uses a commercial package available as a JAR from http://www.aspose.com/downloads/words/java. Without a license, a "No License" message will appear on generated reports.
- Some of the pipeline utilities and HGVS libraries use the Genome Analysis Toolkit (Currently GATK 3.3) and the Sting utility JAR (currently 2.1.8) available from here https://software.broadinstitute.org/gatk/download/
- JNI wrapper to the striped Smith-Waterman alignment library SSW see https://github.com/mengyao/Complete-Striped-Smith-Waterman-Library



## Configuration
The following files can be edited post deployment to configure file locations, servers, databases and security servers.

**Configuration Files:**

|File/Path   | Purpose                                              |
|:-------:|-------------------------------------------------------|
|PathOSHome/etc/pathos.properties| Master properties file controlling servers, database access and schemas and most file locations|
|PathOSHome/etc/FilterRules.groovy| Rules for each PanelGroup. A panel group is a set of panels which share a common set of automatic filter thresholds |
|PathOSHome/lib/loader.properties| Log file properties for PathOS data loader                                 |
|PathOSHome/Report/Default Var Template.docx|Default variant report template. A MSWord mail megre template document|
|PathOSHome/Report/Default Neg Template.docx|Default negative report template. A MSWord mail megre template document|
|PathOSHome/Report/Default Fail Template.docx|Default failed sample report template. A MSWord mail megre template document|
|PathOSHome/war/PathOS.war|WAR file created by Grails in pathos_deploy.sh script. Copy to Java web server eg Tomcat|

## Contact
Ken Doig, Bioinformatics, Cancer Research Department, Data Scientist, Molecular Pathology Department
Peter MacCallum Cancer Centre, Victorian Comprehensive Cancer Centre Building
Grattan Street, Melbourne Victoria 3000 Australia
Ph: +61 411 225 178 Mail: ken.doig@petermac.org

## Demonstration Instances
A cloud instance of PathOS is maintained on the University of Melbourne Nectar cloud at 
    http://115.146.87.30:8746/PathOS
A virtual machine instance of PathOS is available at [VirtualMachine](https://github.com/PapenfussLab/PathOS/tree/master/VirtualMachine)

Please get in touch with the [authors](mailto:ken.doig@petermac.org) for a login account.

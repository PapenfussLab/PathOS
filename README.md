[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)](https://www.java.com/)
[![Language](http://img.shields.io/badge/language-groovy-orange.svg)](http://groovy-lang.org/)
[!(https://img.shields.io/github/v/tag/papenfussLab/PathOS)](https://github.com/PapenfussLab/PathOS/releases)

# PathOS
PathOS is a clinical decision support tool to manage, analyse and report on DNA sequencing variants.
PathOS is under active development at the [Peter MacCallum Cancer Centre in Melbourne](https://www.petermac.org/about/signature-centres/centre-clinical-cancer-genomics/molecular-diagnostic-software).

Authors: [Ken Doig](https://www.petermac.org/users/dr-kenneth-doig), Andrei Seleznev, David Ma, Luis Lara, Tom Conway, Chris Love, 2014-2019 

## Introduction
Clinical diagnostics is being transformed by DNA sequencing technology capable of analysing patient samples at the nucleotide level.

Translating the data from this technology into clinically useful information requires decision support software that can analyse  data from sequencers and allow clinical scientists to interpret the DNA variations.
High throughput sequencing generates many technical artefacts from the chemical processing in the sequencing, these artefacts must be identified and filtered out of the data before further analysis.

The curation process requires identifying and annotating DNA changes, SNPs (single nucleotide polymorphisms), indels (insertions and deletions), CNVs (copy number variants) and SVs (structural variants) within a sample of patient DNA (either blood or tumour).

Once annotated, mutations are matched with internal and external databases to identify known pathogenic (disease causing) or actionable mutations (variants with an appropriate drug).
The resulting few variants are then rendered into a clinical diagnostic report suitable for the treating clinician incorporating clinical evidence and relevant publications.

PathOS carries out these tasks within a clinical laboratory setting where many patients must be reported on in a reliable, consistent and efficient manner.

## Quick Start

The best way to experiment with PathOS is by accessing the [instance](http://115.146.85.170:8123/PathOS) on the University of Melbourne research cloud.
Get in touch with the [authors](mailto:ken.doig@petermac.org) for a login account.

[PathOS](https://cloud.docker.com/u/dockerpathos/repository/list) has been containerised in Docker for easy deployment. [Docker](http://www.docker.com) allows lightweight containers to be built with minimal configuration files and deployed on a variety of operating systems such as: Linux,MacOS,AWS,Azure and even Windows and Windows Server. 
The instructions and config files for Docker deployment can be found [here](https://github.com/PapenfussLab/PathOS/tree/master/Docker).

## Technology Platform
PathOS uses many open-source Java libraries to implement a clincal-grade application suitable for hospital use and secure storage of patient medical data. It interfaces to laboratory LIMS systems for input of patient demographic details and sample and assay registration data. An HL7 interface is used to interface with the hospital records systems.

The web application is implemented in Java, Javascript, Groovy and Grails deployable on any server supporting java servlet containers such as Tomcat or Jboss.
This allows for deployment in a wide range of environments.

Access to the system is controlled by the Spring Security Library which optionally uses an organisation's LDAP server for authentication or the internal database for authorisation and role assignment.
Web traffic is uses by Google Analytics to monitor user activity for workflow and user interface refinement.

The backend database is implemented with MariaDB, a MySQL compatible relational database, which stores the variant annotation cache and persistent java objects via Spring and Hibernate. The code base is managed in Atlassian Bitbucket and Git.

Build management uses Gradle to build system modules and create shared artifacts such as JARs, WARs and TAR files. Internally, Atlassian Bamboo is used for continuous integration to perform builds of system modules triggered by developer commits to the code repository.

The PathOS search engine is implemented in Apache Lucene allowing customised search capabilities over any text field in the PathOS domain model.

## Installation
PathOS has been deployed in a number of clinical environments but is a large complex application with a number of interfaces to external systems that need to be integrated for full featured operation. It has been built from the ground up to meet the clinical workflow needs of the Peter MacCallum Cancer Centre and this is reflected in some of the architectural decisions. 

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
|PathOSHome/Report/Default Var Template.docx|Default variant report template. A MSWord mail megre template document|
|PathOSHome/Report/Default Neg Template.docx|Default negative report template. A MSWord mail megre template document|
|PathOSHome/Report/Default Fail Template.docx|Default failed sample report template. A MSWord mail megre template document|

## Citation
Please consider citing [PathOS](https://genomemedicine.biomedcentral.com/articles/10.1186/s13073-017-0427-z) if you use it in your analysis.

> **PathOS: a decision support system for reporting high throughput sequencing of cancers in clinical diagnostic laboratories** <br/>
> _Kenneth D. Doig, Andrew Fellowes, Anthony H. Bell, Andrei Seleznev, David Ma, Jason Ellul, Jason Li, Maria A. Doyle, Ella R. Thompson, Amit Kumar, Luis Lara, Ravikiran Vedururu, Gareth Reid, Thomas Conway, Anthony T. Papenfuss and Stephen B. Fox_ <br/>
> Genome Medicine (2017) <br/>
> doi: [10.1186/s13073-017-0427-z](https://doi.org/10.1186/s13073-017-0427-z) <br/>
> PMID: [28438193](http://www.ncbi.nlm.nih.gov/pubmed/28438193)

## Contact
Dr. Kenneth Doig, Head Clinical Informatics Lab, Research Department
Peter MacCallum Cancer Centre, Victorian Comprehensive Cancer Centre,
Grattan Street, Melbourne VIC 3000, Australia
Ph: +61 411 225 178 Mail: ken.doig@petermac.org

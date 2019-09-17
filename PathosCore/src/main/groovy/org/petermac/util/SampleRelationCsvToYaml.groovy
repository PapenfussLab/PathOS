/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.util

import au.com.bytecode.opencsv.CSVParser
import au.com.bytecode.opencsv.CSVReader
import groovy.util.logging.Log4j
import org.petermac.yaml.YamlComposer
import org.yaml.snakeyaml.Yaml

/**
 * convert a PathOS Sample Relation CSV - as provided by Jason's website when runs are submitted to Richard's pipeline -
 * to a YAML file for loading SeqRelation files into Seqrun Loader
 * Created by seleznev andrei on 7/8/17.
 */
@Log4j
class SampleRelationCsvToYaml {

    //  strings fir

    YamlComposer y
    Integer relationNumber

    static void main(args) {
        def cli = new CliBuilder(
                usage: 'SampleRelationCsvToYaml [options]',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nTake a SeqLiner SampleRelation CSV convert it to YAML for loading into the PathOS API\n')

        cli.with
                {
                    h(longOpt: 'help', 'Usage Information', required: false)
                    f(longOpt: 'file', args: 1, 'Input file in CSV format', required: true)
                }
        def opt = cli.parse(args)

        if (!opt) return
        if (opt.h) {
            cli.usage()
            return
        }

        File infile = new File(opt.f.toString())
        if ( ! infile.exists() ) {
            log.error("File ${infile} does not exist, exiting")
            System.exit(1)
        }


        //  parse the file and print it to stdout
        //
        def parser = new SampleRelationCsvToYaml()
        def yaml = parser.parseRelationCsvToYaml(opt.f.toString())

        println yaml

    }

    /**
     * parse a relation csv from the seqliner pipeline into YAML format that's recognisabe by PathOS seqrun loader
     * @param filename
     * @return
     */
    def parseRelationCsvToYaml(String filename) {
        y = new YamlComposer()
        relationNumber = 0
        y[['domain']] = 'sequence'
        y[['action']] = 'create'

        CSVReader reader = new CSVReader(new FileReader(filename))

        String [] nextLine
        def first = true
        String type = ''

        while ((nextLine = reader.readNext()) != null) {
            if(first) {
                //  figure out type from header. TN: #Tumor RunName,Tumor SampleName,Normal RunName,Normal SampleName
                //  at time of writing we only ever parse a TN file. will
                if(nextLine[0] == '#Tumor RunName' || nextLine[0] == '#Tumour RunName') {
                    type = "TumourNormal"
                }
                first = false

            } else {
                switch(type) {
                    case "TumourNormal":
                        if( !nextLine[0].isEmpty() && !nextLine[1].isEmpty() && !nextLine[2].isEmpty() && !nextLine[3].isEmpty()  ) {
                            addRelation(type,[[seqRun:nextLine[0],sampleName:nextLine[1],sampleType:'Tumour'],[seqRun:nextLine[2],sampleName:nextLine[3],sampleType:'Normal']])
                        }
                        break;
                }
            }
        }

        def yaml = new Yaml()
        return yaml.dump(y.thing)



    }

    void addRelation(String relationType,ArrayList<Map> relationSamples) {
        y[['data',relationNumber,'seqRelation','relation']] = relationType
        int sampleNumber = 0
        for(sample in relationSamples) {
            y[['data',relationNumber,'seqRelation','samples',sampleNumber]] = sample
            sampleNumber++
        }
        relationNumber++
    }



}

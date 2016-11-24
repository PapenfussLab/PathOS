/*  mp_seqrun_stages.groovy
**
**  Test implementation of a Seqrun pipeline Stages
**
*/

mp_annotator = 
{
    doc title:  "Annotate VCFs",
        desc:   "Annotate a VCF file with VEP,Annovar,Mutalyzer",
        author: "Ken Doig, Molecular Pathology"

    requires DBNAME         : "Specify DBNAME"
    requires MP_PATHOS_HOME : "Specify MP_PATHOS_HOME"

    //  Output for annotation log - only used to flag completion currently
    //
    output.dir = "ANO"

    produce( "ano.log" )
    {
        if (DEBUG) println "In mp_annnotator in=$inputs.vcf out=$output"

        exec "${MP_PATHOS_HOME}/bin/Annotator --rdb $DBNAME --datasource mutalyzer,annovar,vep $inputs.vcf > $output"
    }
}

mp_loadPathOS =
{
    doc title:  "Load PathOS",
        desc:   "Upload a set of pipeline results into PathOS for curation",
        author: "Ken Doig, Molecular Pathology"

    requires DBNAME         : "Specify DBNAME"
    requires MP_PATHOS_HOME : "Specify MP_PATHOS_HOME"

    if (DEBUG) println "In mp_loadPathOS db=$DBNAME in=$sampleList"

    exec "${MP_PATHOS_HOME}/bin/PipelineLoadPathOS -d $DBNAME ${sampleList}"
}

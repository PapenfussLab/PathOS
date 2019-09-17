package org.petermac.util

import groovy.util.logging.Log4j
import org.apache.commons.lang.StringUtils

/**
 * Created for PathOS.
 *
 * Description:
 *
 * VCF loader class, Extends TSV class to load in data
 *
 * Author: Ken Doig
 * Date: 10/07/2014
 */

@Log4j
class Vcf extends Tsv
{
    //  Column meta data for VCF
    //
    private List<Map>    meta_cols   = []

    /**
     * Empty constructor
     */
    Vcf()
    {
        super()
    }

    /**
     * Constructor for URL
     *
     * @param url   URL to source VCF data from
     */
    Vcf( String url )
    {
        super( url )
    }

    /**
     * Constructor for File
     *
     * @param file
     */
    Vcf( File file )
    {
        super( file )
    }

    /**
     * Constructor for Tsv
     *
     * @param tsv   Tsv object to clone
     */
    Vcf( Tsv tsv )
    {
        setTsvMap( tsv.getTsvMap())

        //  Validate Tsv data
        //
        if ( ! validate())
        {
            this.filename = 'empty'
            this.inStream = null
            this.tsvMap   = [:]
        }
    }

    /**
     * Constructor for Vcf from a Vcf
     *
     * @param vcf   Vcf object to clone
     */
    Vcf( Vcf vcf )
    {
        setTsvMap( vcf.getTsvMap())

        //  Copy column meta data
        //
        this.meta_cols = vcf.getMetaCols()

        //  Validate Tsv data
        //
        if ( ! validate())
        {
            this.filename = 'empty'
            this.inStream = null
            this.tsvMap   = [:]
        }
    }

    /**
     * is the file multi allelic?
     * @return
     */
    boolean hasMultiAllele()
    {
        int       altcol = 4                  // ALT is always column 4
        List<Map> cols   = this.tsvMap.cols
        assert cols[altcol].name == 'ALT', "ALT column missing in VCF"

        for ( List row in this.tsvMap.rows )
        {
            //   get ALT column for row
            //
            String altallele = row[altcol]

            if ( altallele.contains(',')) return true
        }

        return false
    }

    /**
     * if file is multi-allelic, will return single-allelic version: that is, multi-allelic
     * lines split into individual ones
     * if file is already single-allelic, returns itself
     * @return
     */
    Vcf splitAlleles()
    {
        //  copy this VCF
        //
        Vcf splitVcf = new Vcf( this )

        //  ALT column is always 5th column for VCF
        //
        def altcol  = 4
        def newRows = []

        //  here we hard-define a set of fields that we will split for multi-allele.
        //  metadata in VCFheader should technically tell us about this, but in practice, it's horrible and wrong
        //
        def splittableInfoFields   = ['AC','AF','MLEAC','MLEAF']
        def splittableFormatFields = ['PMCBDIR',"PMCADR","PMCFREQ",'PMCAD','PMCADF']

        for ( List row in this.tsvMap.rows )
        {
            def altallele = row[altcol] as String

            if (altallele.contains(','))
            {  //this VCF row multi-allelic
                def allelenum = StringUtils.countMatches(altallele,',')    //number of alleles we have

                //we split this one row into allelenum rows. make somewhere to hold them:
                def newrows = new ArrayList<ArrayList>() //list of <allelenum> lists (rows)
                for (y in 0..allelenum)
                { //todo breaks if , number is mismatched, fix this
                    newrows[y] = []
                }

                def formatCol = -9
                //loop thru each column in row:
                for (int z = 0; z < row.size(); z++)
                {
                    // preliminaries
                    //
                    def element = row[z]

                    def col = this.tsvMap.cols[z]
                    if (col == null)
                    {   //Malformed, can't split
                        println "ERROR MALFORMED VCF"
                        return this
                    }
                    //we want to know the number of the col after FORMAT (that is, the one that holds the format values) so we can parse it when we come to it
                    if (col['name'] == 'FORMAT') {
                        formatCol = z
                    }

                    //
                    //SPLIT IF APPROPRIATE:
                    if(element.contains(',') && col['name'] == 'ALT')
                    { //if we're looking at the ALT column:
                        def splitelement = row[z].tokenize(',')

                        for (y in 0..allelenum)
                        { //write each different allele to each new row
                            newrows[y][z] = splitelement[y]
                        }
                    }
                    else if (element.contains(',') && this.tsvMap.cols[z]['name'] == 'INFO') 
                    {    //if we're looking at a split INFO field
                        def oldinfo = row[z]
                        HashMap newinfo = new HashMap() //from 0 to y fields
                        for (y in 0..allelenum) {
                            newinfo[y] = ''
                        }

                        //tokenize info, split thru it,
                        def splitinfo = oldinfo.tokenize(';')

                        for (infofield in splitinfo) {  //we are walking through

                            def tokenized = infofield.tokenize('=')
                            def fieldName = tokenized[0]
                            def fieldValue = tokenized[1]

                            if (fieldName in splittableInfoFields) {   //if this col is indeed multi-allele splittable
                                def fieldValueSplit = fieldValue.tokenize(',')
                                for (y in 0..allelenum) {
                                    newinfo[y] =  newinfo[y] + "${fieldName}=${fieldValueSplit[y]};"
                                }
                            } else {    //this col is not splittable
                                for (y in 0..allelenum) {
                                    newinfo[y] = newinfo[y] + "${fieldName}=${fieldValue};"
                                }
                            }
                        }

                        //set the new info field for all our new rows
                        for (y in 0..allelenum) {
                            String infostring = newinfo[y]
                            println "Adding to column ${z} INFOSTRING ${infostring.substring(0, infostring.length() - 1)}"
                            newrows[y][z] = infostring.substring(0, infostring.length() - 1) //. also hack trailing ';'
                        }

                    } else if (element.contains(',') && z == (formatCol+1)) {
                        //this is us splitting the FORMAT values much as above. | is used as a delimiter in the PMC fields.
                        HashMap newformat = new HashMap() //from 0 to y fields
                        for (y in 0..allelenum) {
                            newformat[y] = ''
                        }

                        //tokenize , split thru it,
                        def splitformat = row[z].tokenize(':')
                        def fieldnames  = row[z-1].tokenize(':')

                        def formatSize = splitformat.size()

                        for (j in 0..(formatSize-1)) {  //we are walking through both NAMES and VALUES of the FORMAT field/s

                            def formatfield = splitformat[j]
                            def fieldname   = fieldnames[j]

                            def tokenizedfield = formatfield.tokenize('|')
                            if (fieldname in splittableFormatFields) {  //if it's one of our multiallele fields, lets split it
                                for (y in 0..allelenum) {

                                    newformat[y] = newformat[y] + tokenizedfield[y] + ":"
                                }
                            }
                            else
                            {  //just write it to all our new fields
                                for (y in 0..allelenum)
                                {
                                    newformat[y] = newformat[y] + formatfield+ ":"
                                }
                            }
                        }

                        //set the new info field for all our new rows
                        for (y in 0..allelenum)
                        {
                            String fstring = newformat[y]

                            newrows[y][z] = fstring.substring(0, fstring.length() - 1) //hack trailing ':'
                        }
                    }
                    else
                    {  //ELSE: no splitting. just write.
                        for (y in 0..allelenum)
                        {
                            newrows[y][z] = row[z]   //set rows 0 to y (as we're splitting into multiple rows) to the same value
                        }
                    }
                }

                for (newrow in newrows)
                {
                    newRows.add(newrow)
                }
            }
            else
            {
                newRows.add(row)
            }
        }

        splitVcf.setRows(newRows)

        return splitVcf
    }

    /**
     * Load in VCF file from input stream
     * Validate columns to see they match VCF standard columns [0..8]
     * Subsequent columns varies with number of samples
     * Todo: map multiple samples to one
     * Todo: deal with multiple alleles
     * Todo: Normalise different forms of VCF file
     *
     * @return            lines read
     */
    int load()
    {
        int nlines = super.load( true, 'TSV' )

        //  Validate Tsv data
        //
        if ( ! validate())
        {
            this.filename = 'empty'
            this.inStream = null
            this.tsvMap   = [:]
            return 0
        }

        return nlines
    }

    /**
     * Return column meta data
     *
     * @return  List of column meta data Maps
     */
    public List<Map> getMetaCols()
    {
        //  Set meta data for columns
        //
        meta_cols = get_meta_cols()

        List  cols = new ArrayList( meta_cols as List )
        return cols
    }

    /**
     * Validate this VCF for format Todo: validate meta data in header
     *
     * @return  true if VCF is conforming
     */
    boolean validate()
    {
        if ( ! this.tsvMap ) return false

        //  Check columns in TSV file for a match with VCF columns
        //
        List cols = this.getCols()
        if ( cols.size() < 10 )
        {
            log.error( "Too few columns for VCF File, num cols=${cols.size()}")
            return false
        }
        if ( cols.size() > 10 )
        {
            log.warn( "Multiple samples in VCF File, samples=${cols[9..-1]}")
        }

        //  Check column names
        //
        List expect = ['CHROM','POS','ID','REF','ALT','QUAL','FILTER','INFO','FORMAT']
        if ( cols[0..8] != expect )
        {
            log.error( "Invalid columns for VCF File, actual cols=${cols[0..8]}, expected cols=${expect}")
            return false
        }

        return true
    }

    /**
     * Add a new column to a VCF
     *
     * @param colMeta   Map of column meta data
     * @param colData   Column data List (optional)
     * @return          modified Vcf
     */
    public void addColumn( Map colMeta, List colData = null )
    {
        //  Add meta data to preamble
        //
        this.setPreamble( addPreamble( colMeta ))

        //  Create a dummy list if no data supplied
        //
        if ( ! colData )
        {
            colData = new ArrayList()
            for ( int i=0; i < super.nrows(); ++i ) colData.add(null)
        }

        //  Add an additional column to each row
        //
        this.setRows( addRows( colMeta, colData ))
    }

    /**
     * Add a new row of fields
     *
     * @param   colMeta     Map of column metadata
     * @param   colData     List of fields to add
     * @return              List of rows
     */
    private List addRows( Map colMeta, List colData )
    {
        if ( super.nrows() != colData.size())
        {
            log.error( "Incorrect number of rows to add this=${super.nrows()} add=${colData.size()}")
            return tsvMap.rows
        }

        List<List> rows = []
        int idx = 0
        for ( List<String> row in tsvMap.rows )
        {
            switch (colMeta.cat)
            {
                case 'INFO':
                    String val = colData[idx++]
                    val = val?.replaceAll( /[; ]/, '_' )
                    if ( val && val != '' && val != 'null' )        // dont output empty values
                    {
                        def match = ( row[7] =~ /(.*${colMeta.name}=)([^;]+)(.*)/ )
                        if ( match.count == 1 )
                        {
                            //  Already exists, replace it
                            //
                            String prefix = match[0][1]
                            String oldval = match[0][2]
                            String suffix = match[0][3]
                            row[7] = "${prefix}${val}${suffix}"
                        }
                        else
                        {
                            //  New entry, add at end
                            //
                            row[7] = row[7] + ";${colMeta.name}=${val}"
                        }
                    }
                    break
                case 'FORMAT':
                    String val = colData[idx++]
                    val = val?.replaceAll( /[: ]/, '_' )
                    if ( row[8].contains(colMeta.name as String))
                    {
                        //  Column already exists
                        //
                        row[9] = replaceFormat( row[8], row[9], colMeta.name, val )
                    }
                    else
                    {
                        row[8] = row[8] + ":${colMeta.name}"
                        row[9] = row[9] + ":${val}"
                    }
                    break
                default:
                    log.error( "Unsupported column addition category ${colMeta.cat}")
            }
            rows << row
        }

        return rows
    }

    /**
     * Replace a value in the format string at the position matching the column
     *
     * @param keys  List of columns eg GT:AD:DP:GQ:PL
     * @param vals  List of values  eg 1/1:0,176:176:99:4972,373,0
     * @param col   Column name to replace
     * @param val   Value to use as replacement
     * @return      Updated values as String
     */
    private static String replaceFormat( String keys, String vals, String col, String val )
    {
        def vallst = vals.split( /:/ )
        def collst = keys.split( /:/ )

        //  Substitute val in list of vals
        //
        int idx = collst.findIndexOf { it == col }
        vallst[idx] = val

        return vallst.join(':')
    }

    /**
     * Add a meta data line for new column in the correct order in preamble
     *
     * @param   colMeta     Column meta data Map
     * @return              updated preamble lines List
     */
    private List addPreamble( Map colMeta )
    {
        boolean added = false   // have we added the new column yet
        List preamble = []      // list of modified preamble lines

        for ( pre in this.tsvMap.preamble )
        {
            //  Do we have a matching meta column, if so replace it
            //
            if ( pre.contains( "=<ID=${colMeta.name}" ))
            {
                preamble << toMeta( colMeta )
                added = true
            }
            else
            {
                preamble << pre
            }
        }

        if ( ! added ) preamble << toMeta( colMeta )

        return preamble
    }

    /**
     * Convert a column Map to a meta data string
     *
     * @param   col     Map of column [ name:, cat:, type:, description:]
     * @return
     */
    private static String toMeta( Map col )
    {
        return "##${col.cat}=<ID=${col.name},Number=1,Type=${col.type},Description=\"${col.description}\">"
    }

    /**
     *  Sort a VCF file by first column (chromosome) then second column (position)
     */
    void  sort()
    {
        List<List> rows     = this.getRows()

        //  Cute way of sorting two fields at once, col1 = chromosome, col2 = genomic pos
        //  Need to check if chromosome is 'X' or 'Y' (or something else) first
        //
        List<List>    sortRows = rows.sort { it[0].isInteger() ? String.format('%010d%010d', it[0] as int, it[1] as int ) : String.format('%s%010d', it[0], it[1] as int )}
        this.setRows( sortRows )
    }

    /**
     * Subtract one Vcf from another
     *
     * @param remove    Vcf to subtract from this Vcf
     * @return          New Vcf with set removed
     */
    Vcf minus( Vcf remove )
    {
        Tsv m = super.minus( remove as Tsv, [ 'CHROM', 'POS', 'REF', 'ALT' ])
        return new Vcf( m )
    }

    /**
     * Intersect one Vcf with another
     *
     * @param join      Vcf to intersect with this Vcf
     * @return          New Vcf with the intersection of rows
     */
    Vcf intersect( Vcf join )
    {
        Tsv i = super.intersect( join as Tsv, [ 'CHROM', 'POS', 'REF', 'ALT' ])
        return new Vcf( i )
    }

    /**
     * Unpack the VCF INFO and Sample data fields and return a Tsv object
     *
     * @return  Tsv object with all compound fields expanded
     */
    Tsv unpack()
    {
        //  Create new Tsv for output
        //
        Tsv unp = new Tsv()
        Map tm  = this.tsvMap

        //  Add to preamble
        //
        tm.preamble << '##unpack="expanded by org.petermac.util.Vcf.unpack()"'

        //  Set meta data for columns
        //
        meta_cols = get_meta_cols()
        tm.cols   = meta_cols

        //  Extract format columns for validating sample data
        //
        List<String> fmtCols = meta_cols.findAll { it.cat == 'FORMAT' }.name

        //  Transfer and unpack rows
        //
        List<List> rows = tsvMap.rows
        tm.rows = []
        int line = 0

        for ( List<String> row in rows )
        {
            ++line
            assert row.size() >= 10 , "Invalid number of columns in VCF cols=${row.size()} row=${line}"
            tm.rows << row[0..6] + unpack_format(row[9], row[8], fmtCols, line) + unpack_info( row[7], line )
        }

        //  Set tsvMap and return expanded TSV
        //
        unp.setTsvMap( tm )

        return unp
    }

    /**
     * Extract column headers from VCF preamble
     * This will determine the order data is displayed in TSV file
     *
     * @return  List of column meta data Maps
     */
    private List<Map> get_meta_cols()
    {
        List<Map> cols =
                [
                [cat: 'key',     name: 'CHROM',     type: 'String', description: 'Chromosome'],
                [cat: 'key',     name: 'POS',       type: 'Integer',description: 'Genomic position'],
                [cat: 'other',   name: 'ID',        type: 'String', description: 'Accession identifier'],
                [cat: 'key',     name: 'REF',       type: 'String', description: 'Reference allele'],
                [cat: 'key',     name: 'ALT',       type: 'String', description: 'Alternate allele'],
                [cat: 'other',   name: 'QUAL',      type: 'String', description: 'Quality'],
                [cat: 'other',   name: 'FILTER',    type: 'String', description: 'Filter'],

                // GT is always the first FORMAT field
                //
                [cat: 'FORMAT',  name: 'GT',        type: 'String', description: 'Genotype', number: '1', vcfType: 'String' ]
                ]

        //  output all FORMAT meta data first
        //
        for ( pre in tsvMap.preamble )
            if ( pre.startsWith( '##FORMAT'))
            {
                def col = toCol( pre )
                if ( col && col.name != 'GT' ) cols << col
            }

        //  output all INFO meta data next
        //
        for ( pre in tsvMap.preamble )
            if ( pre.startsWith( '##INFO'))
            {
                def col = toCol( pre )
                if ( isDuplicate(col.name,cols))
                {
                    col.name = 'INFO_' + col.name   // prepend "INFO_" if this column is a duplicate in FORMAT eg DP
                }
                if ( col ) cols << col
            }

        return cols
    }

    /**
     * Check if column name is duplicate
     *
     * @param colname   Name to check
     * @param cols      List<Map> of seen columns
     * @return          true if duplicate name
     */
    private static boolean isDuplicate( String colname, List<Map> cols )
    {
        for ( col in cols )
            if ( colname == col.name )
                return true

        return false
    }

    /**
     * Parse VCF meta data in the form of:
     * e.g. ##FORMAT=<ID=DP,Number=1,Type=Integer,Description="Approximate read depth (reads with MQ>=255 or with bad mates are filtered)">
     *
     * beware of ',' and '=' in quotes
     *
     * @param   meta    File metadata line
     * @return          Map of column meta data
     */
    private Map toCol( String meta )
    {
        Map col = [:]
        def match = ( meta =~ /^##([A-Z]+)=<(.*)>$/ )
        if ( match.count == 1 )
        {
            String cat  = match[0][1]
            String flds = match[0][2]

            if ( cat == 'INFO' )   col.cat = 'INFO'
            if ( cat == 'FORMAT' ) col.cat = 'FORMAT'

            col = splitFields( col, flds )
        }

        //  Override numeric type if Number != '1'
        //
        if ( col.number != '1' && (col.type == 'Integer' || col.type == 'Double'))
            col.type = 'String'

        return col
    }

    /**
     * Manually parse pseudo CSV VCF meta data line
     *
     * @param col   Column Map to populate
     * @param flds  Line to parse
     * @return
     */
    private Map splitFields( Map col, String flds )
    {
        def match = ( flds =~ /ID=([^,]+),/ )
        if ( match.count == 1 ) col.name = match[0][1]

        match = ( flds =~ /Number=([^,]+),/ )
        if ( match.count == 1 ) col.number = match[0][1]

        match = ( flds =~ /Type=([^,]+),/ )
        if ( match.count == 1 )
        {
            col.vcfType = match[0][1]
            col.type    = mapMetaType(match[0][1])
        }

        match = ( flds =~ /Description="([^"]+)"/ )
        if ( match.count == 1 ) col.description = match[0][1]

        return col
    }

    /**
     * Map VCF types to Groovy types
     *
     * @param   fld Field to map
     * @return      Mapped Groovy type
     */
    private static String mapMetaType( String fld )
    {
        if ( fld == 'Integer' || fld == 'String') return fld
        if ( fld == 'Flag' ) return 'Boolean'
        if ( fld == 'Float') return 'Double'
        return 'String'
    }

    /**
     * Unpack INFO columns. Columns may be missing but null fields will be output anyway.
     *
     * @param   info    INFO field of row
     * @return          List of data in this.info_cols order
     */
    private List unpack_info( String info, int lineno )
    {
        List cols = []

        for ( Map col in meta_cols.findAll{ it.cat == 'INFO' })
        {
            String colName = col.name
            if ( colName.startsWith('INFO_')) colName = colName[5..-1]  // Strip off "INFO_" prefix

            //  parse boolean flag INFO attribute
            //
            if ( col.type == 'Boolean' )
            {
                if ( info =~ /${colName}/ )
                    cols << "true"
                else
                    cols << "false"
                continue
            }

            //  Parse key=value pair in info field
            //
            def match = ( info =~ /(;|^)${colName}=([^;]+)/ )
            if ( match.count == 1 )
            {

                def val = match[0][2]
                cols << val
            }
            else
            {
                cols << ''
            }
        }

        return cols
    }

    /**
     * Unpack the sample FORMAT fields
     *
     * @param sample    Sample data eg "1/1:0,176:176:99:4972,373,0"
     * @param header    Sample fields eg "GT:AD:DP:GQ:PL"
     * @param outCols   List of columns from VCF preamble
     * @param lineno    Line number of VCF row
     * @return          List of FORMAT data in correct order
     */
    static private List unpack_format( String sample, String header, List<String> outCols, int lineno )
    {
        List cols = sample?.split(':')
        List head = header?.split(':')
        if ( ! cols || ! head )
        {
            log.error( "Missing FORMAT columns at line ${lineno}")
            return []
        }
        if (cols.size() != head.size()) log.debug( "Incorrect number of sample FORMAT columns: ${cols} header: ${head}" )

        //  Need to match sample data to required output columns
        //
        List out = []
        for (oc in outCols )
            out << (head.contains(oc) ? cols[head.indexOf(oc)] : '')

        return out
    }

    /**
     * Get all VCF rows as a List of Maps
     *
     * @return  List<Map> for each row as a Map keyed on columns name
     */
    public List<Map> getRowMaps()
    {
        Tsv  tsv  = unpack()
        Map  tm   = tsv.getTsvMap()
        List cols = tm.cols.name

        List<Map> mapRows = []
        for ( List<String> row in tm.rows )
        {
            Map mapRow = [:]

            cols.eachWithIndex { String col, int i -> mapRow << [(col): row[i]] }

            mapRows << mapRow
        }

        return mapRows
    }

    /**
     * Set rows by creating a new list from a row Map
     * Map must match the unpacked VCF columns
     *
     * @param rowMaps   rows to copy
     */
    public Vcf setRowMaps( List<Map> rows )
    {
        //  Create a column list from unpacked VCF
        //
        Tsv tsv = unpack()
        Map tm = tsv.getTsvMap()
        List<Map> cols = tm.cols

        //  Create a packed set of rows from the Map
        //
        List<List> lrows = []
        for ( row in rows )
        {
            //  Find novel column names by list subtraction
            //
            List newcols = (row.keySet() as List) - (cols.name as List)
            if ( newcols )
                log.warn( "Extra columns in row will be ignored=${newcols}")

            List<String> lrow = []
            List<String> fmtk = []
            List<String> fmtv = []
            List<String> info = []

            //  Process each column in row
            //
            for ( col in row )
            {
                Map vcfcol = cols.find { it.name == col.key }
                if ( vcfcol )
                {
                    switch( vcfcol.cat )
                    {
                        case 'FORMAT':
                            if ( col.value )
                            {
                                fmtv << col.value
                                fmtk << col.key
                            }
                            break
                        case 'INFO':
                            if ( col.value && col.value != '' )
                            {
                                info << "${col.key}=${col.value}"
                            }
                            break
                        default:
                            lrow << col.value
                            //log.debug( "Default ${col.key}")
                    }
                }
            }

            lrows << lrow + info.join(';') + fmtk.join(':') + fmtv.join(':')
        }

        setRows( lrows )

        return this
    }

    /**
     * Add new columns from another Vcf to this Vcf
     *
     * @param merge   Vcf with columns to add
     */
    public List<String> addColumns( Vcf merge )
    {
        List<Map> mrgCols = merge.getMetaCols()

        List newCols = mrgCols.name
        List oldCols = this.get_meta_cols().name
        List addCols = newCols - oldCols
        List<Map> addNewCols = mrgCols.findAll { it.name in addCols }

        for ( col in addNewCols )
            this.addColumn( col )

        return addCols
    }

    /**
     * Get sample name from VCF header
     *
     * @return  Sample name
     */
    String sampleName()
    {
        def is = this.getInStream()
        List<String> lines = is.readLines()
        for ( line in lines )
        {
            if ( line.startsWith('#CHROM'))
            {
                List cols = line.split('\t')
                if ( cols.size() >= 10 ) return cols[9]

                return null
            }
        }

        return null
    }

    /**
     * Return a canned VCF header
     *
     * @param   sample  Optional sample name to use
     * @return          Multiline header
     */
    static String header( String source, List<String> infoFields, String sample = 'Sample')
    {
        String infoLines = ''
        for ( infofld in infoFields )
        {
            infoLines += "##INFO=<ID=${infofld},Number=1,Type=String,Description=\"${infofld} INFO field\">\n"
        }

        return  "##fileformat=VCFv4.1\n" +
                "##source=${source}\n" +
                "##INFO=<ID=ADP,Number=1,Type=Integer,Description=\"Average per-sample depth of bases with Phred score >= 20\">\n" +
                "##INFO=<ID=WT,Number=1,Type=Integer,Description=\"Number of samples called reference (wild-type)\">\n" +
                "##INFO=<ID=HET,Number=1,Type=Integer,Description=\"Number of samples called heterozygous-variant\">\n" +
                "##INFO=<ID=HOM,Number=1,Type=Integer,Description=\"Number of samples called homozygous-variant\">\n" +
                "##INFO=<ID=NC,Number=1,Type=Integer,Description=\"Number of samples not called\">\n" +
                "##INFO=<ID=HGVSg,Number=1,Type=String,Description=\"HGVSg format of variant\">\n" +
                "##INFO=<ID=numAmps,Number=1,Type=String,Description=\"Number of amplicons with variant / amplicons including locus\">\n" +
                "##INFO=<ID=amps,Number=1,Type=String,Description=\"Amplicon names with variant\">\n" +
                "##INFO=<ID=gene,Number=1,Type=String,Description=\"gene of variant\">\n" +
                "##INFO=<ID=ampbias,Number=1,Type=String,Description=\"is there a bias across amplicons for variant\">\n" +
                "##INFO=<ID=fsRescue,Number=1,Type=String,Description=\"Can variant be rescued from a frameshift in phase\">\n" +
                "##INFO=<ID=homopolymer,Number=1,Type=String,Description=\"Is variant next to a homopolymer run\">\n" +
                infoLines +
                "##FILTER=<ID=str10,Description=\"Less than 10% or more than 90% of variant supporting reads on one strand\">\n" +
                "##FILTER=<ID=indelError,Description=\"Likely artifact due to indel reads at this position\">\n" +
                "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n" +
                "##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">\n" +
                "##FORMAT=<ID=SDP,Number=1,Type=Integer,Description=\"Raw Read Depth as reported by SAMtools\">\n" +
                "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Quality Read Depth of bases with Phred score >= 20\">\n" +
                "##FORMAT=<ID=RD,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases (reads1)\">\n" +
                "##FORMAT=<ID=AD,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases (reads2)\">\n" +
                "##FORMAT=<ID=FREQ,Number=1,Type=String,Description=\"Variant allele frequency\">\n" +
                "##FORMAT=<ID=PVAL,Number=1,Type=String,Description=\"P-value from Fisher's Exact Test\">\n" +
                "##FORMAT=<ID=RBQ,Number=1,Type=Integer,Description=\"Average quality of reference-supporting bases (qual1)\">\n" +
                "##FORMAT=<ID=ABQ,Number=1,Type=Integer,Description=\"Average quality of variant-supporting bases (qual2)\">\n" +
                "##FORMAT=<ID=RDF,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases on forward strand (reads1plus)\">\n" +
                "##FORMAT=<ID=RDR,Number=1,Type=Integer,Description=\"Depth of reference-supporting bases on reverse strand (reads1minus)\">\n" +
                "##FORMAT=<ID=ADF,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases on forward strand (reads2plus)\">\n" +
                "##FORMAT=<ID=ADR,Number=1,Type=Integer,Description=\"Depth of variant-supporting bases on reverse strand (reads2minus)\">\n" +
                "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t${sample}\n"
    }

    /**
     * Return a canned VCF header for VEP proccessing
     *
     * @return          Minimal VCF header
     */
    static String vepHeader()
    {
        return "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tSample\n"
    }
}
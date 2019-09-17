modules = {
    application {
        resource url: 'js/application.js'
    }
    'jqgrid-multiselect' {
        resource url: 'js/quasipartikel/ui.multiselect.js',
                disposition: 'head'

        resource url: [plugin: 'easygrid', file: "jquery.jqGrid-4.6.0/plugins/ui.multiselect.css"]
    }
    'google-charts' {
        resource url: 'js/jsapi.js',
                disposition: 'head'
    }
    'datatables' {
//        Note that the javascript is included in vendor.min.js, so we always have dataTables.
//        resource url: [file: 'datatables.min.css', dir: 'dist/css']
        resource url: [file: 'jquery.dataTables.min.css', dir: 'dist/css']
        resource url: [file: 'dataTables.bootstrap.min.css', dir: 'dist/css']
        resource url: [file: 'colReorder.dataTables.min.css', dir: 'dist/css']
        resource url: [file: 'select.dataTables.min.css', dir: 'dist/css']
    }
    'circos' {
        resource url: [file: 'circos/circos.js']
        resource url: [file: 'circos/karyotype.js']
    }
    'pmac-easygrid' {
        dependsOn 'easygrid-jquery-ui', 'easygrid-jqgrid-theme', 'jqgrid-multiselect', 'jquery-theme', 'easygrid-jqgrid-dev'

        resource url: [plugin: 'spring-security-ui', dir: 'js/jquery', file: "jquery.jgrowl.js" ]
    }
}
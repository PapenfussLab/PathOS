class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
        "/seqVariant/svlist/${seqrunName}/${sampleName}" (controller:"seqVariant",action: "svlist")
        "/seqrun/show/${seqrunName}" (controller:"seqrun",action:"show")
        "/sample/show/${sample}" (controller:"sample",action:"show")
        "/curVariant/show/${hgvsg}" (controller:"curVariant",action:"show")
		"/"(view:"/index")
		"500"(view:'/error')
	}
}

class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/seqSampleReport/${id}" {
			controller 	= "seqSampleReport"
			action 		= "inspect"
			constraints {
				id(matches:/\d+/)
			}
		}

		"/vcfUpload/status/${seqrunName}/${sampleName}/${tagName}" (controller:"vcfUpload",action: "status")

		"/seqVariant/svlist/${seqrunName}/${sampleName}" (controller:"seqVariant",action: "svlist")
        "/seqrun/show/${seqrunName}" (controller:"seqrun",action:"show")
        "/sample/show/${sample}" (controller:"sample",action:"show")
		"/igvSession/sessionXml/${seqrunName}/${sampleName}.xml" (controller:"igvSession",action:"sessionXml")
		"/"(view:"/index")
		"500"(view:'/error')
	}
}

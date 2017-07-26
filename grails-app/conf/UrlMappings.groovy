
class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/upload/uploadToDataset/$dataType/$dataset"(controller: "upload", action: "uploadToDataset")
        "/export/$type/$ds/$format?"(controller: "export",action:"export")
        "/viz/$name/$dataHandle"(controller: "showcase",action:"index")

        "/showcase/displayResultfile/$Uuid/$Filename"(controller: "showcase",action:"showResFileByName")

        "/"(controller: "showcase", action: "index")

        "500"(controller: 'error', action: "crash")
        "404"(controller: 'error', action: "notFound")
        "403"(controller: 'error', action: "accessDenied" )
        "401"(controller: 'error', action: "deniedPleaseLogIn" )

        "/data"(resources:"dataset")
	}
}

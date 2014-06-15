import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

/**
 * Date: 14/06/15
 * Time: 23:15
 */

final def nhkApiUrl = "http://api.nhk.or.jp"

final def programParameters  = [
	area	: "040",	// 宮城県
	service	: "tv",		// TVチャンネル全般
	genre	: "0102",	// サッカー
	date	: new Date().format("yyyy-MM-dd")	//NHK-API仕様に合わせた日付
]
final def genrePath = "/v1/pg/genre/${programParameters.area}/${programParameters.service}/${programParameters.genre}/${programParameters.date}.json"

final def apikey = "secret"

def http = new HTTPBuilder(nhkApiUrl)
http.request(Method.GET, ContentType.JSON) {
	uri.path = genrePath
	uri.query = [key : "${apikey}"]

	response.success = { resp, json ->
		println json.list

		Collection programs = []
		json.list.each { service ->
			service.getValue().each {
				programs.add(it)
			}
		}

		programs.sort {a, b ->
			a.start_time <=> b.start_time
		}

		programs.each {
			println "${it.title}"
			println " 放送局：${it.service.name}"
			println " 放送時間：${it.start_time} - ${it.end_time}"
			println " サブタイトル：${it.subtitle}"
			println ""
		}
	}

	response.failure = { resp ->
		println "Unexpected error : ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
	}
}


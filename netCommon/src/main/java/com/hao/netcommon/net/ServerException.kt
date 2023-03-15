package com.hao.netcommon.net

/**
 *@author raohaohao
 *@data 2023/3/10
 *@version 1.0
 */
class ServerException(what: Int, code: Int, message: String?, data: Any) : Exception(message) {
    var what = what
    var code = code
    override var message = message
    var data = data
}
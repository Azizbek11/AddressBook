package com.azizbek.myyandexmapapp.model

import java.io.Serializable

class Model  : Serializable {

    var id = 0
    var addressName: String
    var locationName: String

    constructor(id: Int, addressName: String, locationName: String) {
        this.id = id
        this.addressName = addressName
        this.locationName = locationName
    }

    constructor(addressName: String, locationName: String) {
        this.addressName = addressName
        this.locationName = locationName
    }
}